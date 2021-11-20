package github.tuquanrong.register;

import static github.tuquanrong.model.enums.RpcServerStatusEnum.NO_DISCOVER_SERVER;

import java.net.InetSocketAddress;
import java.util.List;

import github.tuquanrong.exception.RpcServerException;
import github.tuquanrong.loadbalance.ConsistenceHashLoadBalance;
import github.tuquanrong.loadbalance.ServerLoadBalance;

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
        zkController = ZkController.getInstance();
    }

    public InetSocketAddress discoverServer(String className) {
        List<String> list = zkController.getChilderNode(className);
        if (list == null || list.size() == 0) {
            throw new RpcServerException(NO_DISCOVER_SERVER);
        }
        String balanceInet = serverLoadBalance.selectServer(list, className);
        String[] ipPort = balanceInet.split(":");
        return new InetSocketAddress(ipPort[0], Integer.parseInt(ipPort[1]));
    }
}
