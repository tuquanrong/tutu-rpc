package github.tuquanrong.config.model;

/**
 * @Author: tuquanrong
 * @Date: 2021/11/30 4:47 下午
 */
public enum PropertiesEnum {
    ZOOKEEPER("zk", "127.0.0.1:2181"),
    SERIALIZER("serializer", "1"),
    FLOWRULE("flowRule", "[]"),
    DEGRADERULE("degradeRule", "[]");


    private String name;
    private String defaultValue;

    PropertiesEnum(String name, String defaultValue) {
        this.name = name;
        this.defaultValue = defaultValue;
    }

    public String getName() {
        return name;
    }

    public String getDefaultValue() {
        return defaultValue;
    }
}
