package github.tuquanrong.loadbalance;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * tutu
 * 2021/1/20
 */
public class ConsistenceHashLoadBalance implements ServerLoadBalance {
    private static final ConsistenceHashLoadBalance LOAD_BALANCE_INSTANCE = new ConsistenceHashLoadBalance();
    private Map<String, ConsistentHashSelector> selectorMap = new ConcurrentHashMap<>();

    public static ConsistenceHashLoadBalance getInstance() {
        return LOAD_BALANCE_INSTANCE;
    }

    @Override
    public String selectServer(List<String> inetList, String className) {
        if (inetList == null || inetList.size() == 0) {
            return null;
        }
        if (inetList.size() == 1) {
            return inetList.get(0);
        }
        long hashCode = System.identityHashCode(inetList);
        ConsistentHashSelector consistentHashSelector = selectorMap.get(className);
        if (consistentHashSelector == null || hashCode != consistentHashSelector.IP_LIST_HASHCODE) {
            selectorMap.put(className, new ConsistentHashSelector(hashCode, inetList, 160));
            consistentHashSelector = selectorMap.get(className);
        }
        return consistentHashSelector.select(className);
    }

    static class ConsistentHashSelector {
        private TreeMap<Long, String> treeMap;
        private long IP_LIST_HASHCODE;

        public ConsistentHashSelector(long hashCode, List<String> inetList, int replicaNumber) {
            treeMap = new TreeMap<>();
            IP_LIST_HASHCODE = hashCode;
            for (String inet : inetList) {
                for (int i = 0; i < replicaNumber / 4; i++) {
                    byte[] keyBytes = md5(inet + i);
                    for (int j = 0; j < 4; j++) {
                        long id = hash(keyBytes, j);
                        treeMap.put(id, inet);
//                        System.out.println(id+"   "+inet);
                    }
                }
            }
        }

        //生成每一个节点唯一key
        public byte[] md5(String key) {
            MessageDigest md;
            try {
                md = MessageDigest.getInstance("MD5");
                byte[] bytes = key.getBytes(StandardCharsets.UTF_8);
                md.update(bytes);
            } catch (NoSuchAlgorithmException e) {
                throw new IllegalStateException(e.getMessage(), e);
            }
            return md.digest();
        }

        //生成每一个虚拟节点在hash环上的位置,目的为将虚拟节点分散开
        private long hash(byte[] digest, int number) {
            return (((long) (digest[3 + number * 4] & 0xFF) << 24)
                    | ((long) (digest[2 + number * 4] & 0xFF) << 16)
                    | ((long) (digest[1 + number * 4] & 0xFF) << 8)
                    | (digest[number * 4] & 0xFF))
                    & 0xFFFFFFFFL;
        }

        public String select(String key) {
            byte[] digest = md5(key);
            return selectForKey(hash(digest, 0));
        }

        public String selectForKey(long index) {
            System.out.println(index);
            Map.Entry<Long, String> keyEntry = treeMap.tailMap(index, true).firstEntry();
            if (keyEntry == null) {
                keyEntry = treeMap.firstEntry();
            }
            return keyEntry.getValue();
        }
    }
}
