package github.tuquanrong.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * tutu
 * 2021/1/5
 */
@Getter
@AllArgsConstructor
public enum CodeEnum {
    SERVER_SUCCESS("成功调用服务", 202),
    SERVER_REQUEST_ERROR("服务请求错误", 401),
    SERVER_NOT_FOUND("服务找不到", 404),
    SERVER_ERROR("服务出错", 501),
    SERVER_ID_MISTAKE("请求ID与应答ID不一致", 502);
    private int code;
    private String des;

    CodeEnum(String des, int code) {
        this.des = des;
        this.code = code;
    }
}
