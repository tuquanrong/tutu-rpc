package github.tuquanrong.loadbalance;

import java.util.List;

/**
 * tutu
 * 2021/1/20
 */
public interface ServerLoadBalance {
    String selectServer(List<String> ipList, String className);
}
