package github.tuquanrong.register;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import github.tuquanrong.config.PropertiesConfig;
import github.tuquanrong.config.model.PropertiesEnum;

/**
 * tutu
 * 2021/1/19
 * pathContiner：作用为在删除节点的时候提供删除列表
 */
public class ZkController {
    private static final Logger logger = LoggerFactory.getLogger(ZkController.class);
    public static final String ZK_RPC_LINK = "/tutu-rpc";
    @SuppressWarnings("checkstyle:StaticVariableName")
    private static String DEFAULT_ZK_HOST = "127.0.0.1:2181";
    private static final ZkController ZK_CONTROLLER = new ZkController();
    private PropertiesConfig propertiesConfig;
    private CuratorFramework zkClient;
    private Map<String, List<String>> childerNodeMap = new ConcurrentHashMap<>();
    private Set<String> pathContiner = new ConcurrentHashMap<>().newKeySet();

    ZkController() {
        propertiesConfig = PropertiesConfig.getInstance();
    }

    public static ZkController getInstance() {
        return ZK_CONTROLLER;
    }

    private CuratorFramework getZkClient() {
        if (zkClient != null && zkClient.getState() == CuratorFrameworkState.STARTED) {
            return zkClient;
        }
        DEFAULT_ZK_HOST = PropertiesConfig.getInstance().get(PropertiesEnum.ZOOKEEPER);
        zkClient = CuratorFrameworkFactory.builder()
                .connectString(DEFAULT_ZK_HOST)
                .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                .build();
        zkClient.start();
        return zkClient;
    }

    public void createNode(String path) {
        CuratorFramework client = getZkClient();
        try {
            if (pathContiner.contains(path) || client.checkExists().forPath(path) != null) {
                return;
            } else {
                client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(path);
            }
            pathContiner.add(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<String> getChilderNode(String className) {
        List<String> list = childerNodeMap.get(className);
        if (list != null) {
            return list;
        }
        CuratorFramework client = getZkClient();
        String path = ZK_RPC_LINK + "/" + className;
        try {
            list = client.getChildren().forPath(path);
            childerNodeMap.put(path, list);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public void clearAllNode(InetSocketAddress inetSocketAddress) {
        CuratorFramework client = getZkClient();
        String suffix = inetSocketAddress.toString();
        pathContiner.stream().parallel().forEach(p -> {
            try {
                if (p.endsWith(suffix)) {
                    client.delete().forPath(p);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void deleteNode(String path) {
        CuratorFramework client = getZkClient();
        try {
            client.delete().forPath(path);
            pathContiner.remove(path);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void addServerListener(String path, CuratorFramework client) throws Exception {
        PathChildrenCache pathChildrenCache = new PathChildrenCache(client, path, true);
        PathChildrenCacheListener pathChildrenCacheListener = new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework curatorFramework, PathChildrenCacheEvent pathChildrenCacheEvent) throws Exception {
                List<String> list = curatorFramework.getChildren().forPath(path);
                childerNodeMap.put(path, list);
            }
        };
        pathChildrenCache.getListenable().addListener(pathChildrenCacheListener);
        pathChildrenCache.start();
    }
}
