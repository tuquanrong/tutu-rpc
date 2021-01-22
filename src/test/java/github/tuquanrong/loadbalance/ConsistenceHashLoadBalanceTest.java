package github.tuquanrong.loadbalance;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * tutu
 * 2021/1/20
 */
public class ConsistenceHashLoadBalanceTest {
    public static void main(String[] args) {
        ServerLoadBalance serverLoadBalance = new ConsistenceHashLoadBalance();
        List<String> serviceUrlList = new ArrayList<>(Arrays.asList("127.0.0.1:9997", "127.0.0.1:9998", "127.0.0.1:9999"));
        String userRpcServiceName = "github.javaguide.UserServicetest1version1";
        String userServiceAddress = serverLoadBalance.selectServer(serviceUrlList, userRpcServiceName);
        System.out.println(userServiceAddress);
        //assertEquals("127.0.0.1:9999", userServiceAddress);
        String schoolRpcServiceName = "github.javaguide.SchoolServicetest1version1";
        String schoolServiceAddress = serverLoadBalance.selectServer(serviceUrlList, schoolRpcServiceName);
        System.out.println(schoolServiceAddress);
        //assertEquals("127.0.0.1:9997", schoolServiceAddress);
    }
}
