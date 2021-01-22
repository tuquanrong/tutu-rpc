package github.tuquanrong.register;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * tutu
 * 2021/1/19
 */
public class ServerDiscoverTest {
    public static void main(String[] args) throws UnknownHostException {
//        ServerDiscover serverDiscover=new ServerDiscover();
//        System.out.println(serverDiscover.discoverServer("helloServer").toString());
        System.out.println(InetAddress.getLocalHost().getHostAddress());
    }
}
