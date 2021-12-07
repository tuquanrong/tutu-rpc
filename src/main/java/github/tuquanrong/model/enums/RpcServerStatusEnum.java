package github.tuquanrong.model.enums;

import java.util.stream.Stream;

import lombok.Getter;

/**
 * @Author: tuquanrong
 * @Date: 2021/11/15 2:41 下午
 */
@Getter
public enum RpcServerStatusEnum {
    COMMON_ERROR(1000, "服务bean初始化创建失败"),
    DUPLICATE_REGISTER(1001, "注册服务重复"),
    IMPLEMENT_MULTIPLE_INTERFACE(1002, "服务提供方bean实现了多个接口"),
    REGISTER_FAIL(1003, "注册节点失败"),

    SERVER_SUCCESS(2000, "成功调用服务"),
    SERVER_ERROR(2001, "服务错误"),
    NO_DISCOVER_SERVER(2002, "没有该接口的注册服务"),
    TRANSPORT_CLOSED(2003, "传输通道被关闭"),
    CONNECT_ERROR(2004, "客户端连接错误"),
    SERVER_ID_MISTAKE(2005, "请求ID与应答ID不一致"),
    PROCOCOL_NOT_SUPPORT(2006, "当前协议不支持"),

    SENTINEL_ERROR(3000, "sentinel错误"),
    FLOW_ERROR(3001, "服务已开启流量控制"),
    DEGRADE_ERROR(3002, "服务已熔断");


    private int code;
    private String desc;

    RpcServerStatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static RpcServerStatusEnum fromValue(int status) {
        return Stream.of(values())
                .filter(e -> e.getCode() == status)
                .findFirst()
                .orElse(COMMON_ERROR);
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
