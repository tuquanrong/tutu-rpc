package github.tuquanrong.exception;

import github.tuquanrong.model.enums.RpcServerStatusEnum;
import lombok.Getter;
import lombok.ToString;

/**
 * tutu
 * 2021/1/15
 */
@Getter
@ToString
public class RpcServerException extends RuntimeException {
    private RpcServerStatusEnum rpcServerStatusEnum;
    private String desc;

    public RpcServerException(RpcServerStatusEnum rpcServerStatusEnum) {
        this(rpcServerStatusEnum, rpcServerStatusEnum.getDesc());
    }

    public RpcServerException(RpcServerStatusEnum rpcServerStatusEnum, String desc) {
        this.rpcServerStatusEnum = rpcServerStatusEnum;
        this.desc = desc;
    }
}
