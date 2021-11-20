package github.tuquanrong.register;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

import github.tuquanrong.transport.NettyServer;

/**
 * tutu
 * 2021/1/20
 */
public class ZkShutdownHook {

    public static void clearServiceNode() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                ZkController.getInstance().clearAllNode(new InetSocketAddress(InetAddress.getLocalHost().getHostAddress(), NettyServer.getPort()));
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }));
    }
}
