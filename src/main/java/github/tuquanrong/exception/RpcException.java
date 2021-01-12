package github.tuquanrong.exception;

import lombok.Getter;

/**
 * tutu
 * 2021/1/7
 */
@Getter
public class RpcException extends RuntimeException {
    private String message;

    public RpcException(){
        super();
    }

    public RpcException(String message) {
        super(message);
        this.message = message;
    }
}
