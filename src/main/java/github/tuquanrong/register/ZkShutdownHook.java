package github.tuquanrong.register;

import github.tuquanrong.transport.NettyServer;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

/**
 * tutu
 * 2021/1/20
 */
public class ZkShutdownHook {

    public static void clearServiceNode() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                ZkController.getInstance().clearAllNode(new InetSocketAddress(InetAddress.getLocalHost().getHostAddress(), NettyServer.PORT));
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }));
    }
}
