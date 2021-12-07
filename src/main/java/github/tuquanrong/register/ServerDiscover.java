package github.tuquanrong.register;

import static github.tuquanrong.model.enums.RpcServerStatusEnum.NO_DISCOVER_SERVER;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import github.tuquanrong.exception.RpcServerException;
import github.tuquanrong.loadbalance.ConsistenceHashLoadBalance;
import github.tuquanrong.loadbalance.ServerLoadBalance;

/**
 * tutu
 * 2021/1/19
 */
public class ServerDiscover {
    private static final Logger logger = LoggerFactory.getLogger(ServerDiscover.class);
    private static final ServerDiscover SERVER_DISCOVER = new ServerDiscover();
    private ServerLoadBalance serverLoadBalance;
    private ZkController zkController;

    private LoadingCache<String, List<String>> ipPortCache =
            CacheBuilder.newBuilder()
                    .refreshAfterWrite(3, TimeUnit.MINUTES)
                    .maximumSize(10000)
                    .expireAfterWrite(10, TimeUnit.MINUTES)
                    .build(new CacheLoader<String, List<String>>() {
                        public List<String> load(String serviceName) {
                            return getServicesByClass(serviceName);
                        }
                    });

    public static ServerDiscover getInstance() {
        return SERVER_DISCOVER;
    }

    public ServerDiscover() {
        serverLoadBalance = ConsistenceHashLoadBalance.getInstance();
        zkController = ZkController.getInstance();
    }

    public InetSocketAddress discoverServer(String className) {
        List<String> list = null;
        try {
            list = ipPortCache.get(className);
        } catch (ExecutionException e) {
            logger.error("提供服务的ipPort列表获取异常");
            e.printStackTrace();
        }
        if (list == null || list.size() == 0) {
            throw new RpcServerException(NO_DISCOVER_SERVER);
        }
        String balanceInet = serverLoadBalance.selectServer(list, className);
        String[] ipPort = balanceInet.split(":");
        return new InetSocketAddress(ipPort[0], Integer.parseInt(ipPort[1]));
    }

    public void refreshCache(String className) {
        ipPortCache.refresh(className);
    }

    public List<String> getServicesByClass(String className) {
        List<String> list = zkController.getChilderNode(className);
        return list;
    }
}
