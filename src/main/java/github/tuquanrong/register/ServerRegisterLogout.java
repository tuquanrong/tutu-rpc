package github.tuquanrong.register;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

import github.tuquanrong.transport.NettyServer;

/**
 * tutu
 * 2021/1/19
 */
public class ServerRegisterLogout {
    private static final ServerRegisterLogout SERVER_REGISTER_LOGOUT = new ServerRegisterLogout();
    private ZkController zkController;

    ServerRegisterLogout() {
        zkController = ZkController.getInstance();
    }

    public static ServerRegisterLogout getInstance() {
        return SERVER_REGISTER_LOGOUT;
    }

    public void registerServer(String className) {
        try {
            String ip = InetAddress.getLocalHost().getHostAddress();
            int port = NettyServer.getPort();
            zkController.createNode(ZkController.ZK_RPC_LINK + "/" + className + new InetSocketAddress(ip, port).toString());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public void logoutServer(String className) {
        try {
            String ip = InetAddress.getLocalHost().getHostAddress();
            int port = NettyServer.getPort();
            zkController.deleteNode(ZkController.ZK_RPC_LINK + "/" + className + new InetSocketAddress(ip, port).toString());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
}
