package github.tuquanrong.register;

import github.tuquanrong.exception.RpcException;
import github.tuquanrong.loadbalance.ConsistenceHashLoadBalance;
import github.tuquanrong.loadbalance.ServerLoadBalance;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * tutu
 * 2021/1/19
 */
public class ServerDiscover {
    private static final ServerDiscover SERVER_DISCOVER = new ServerDiscover();
    private ServerLoadBalance serverLoadBalance;
    private ZkController zkController;

    public static ServerDiscover getInstance() {
        return SERVER_DISCOVER;
    }

    public ServerDiscover() {
        serverLoadBalance = ConsistenceHashLoadBalance.getInstance();
        zkController=ZkController.getInstance();
    }

    public InetSocketAddress discoverServer(String className) {
        List<String> list = zkController.getChilderNode(className);
        if (list == null || list.size() == 0) {
            throw new RpcException("没有该接口的注册服务");
        }
        String balanceInet = serverLoadBalance.selectServer(list, className);
        String[] ipPort = balanceInet.split(":");
        return new InetSocketAddress(ipPort[0], Integer.parseInt(ipPort[1]));
    }
}
