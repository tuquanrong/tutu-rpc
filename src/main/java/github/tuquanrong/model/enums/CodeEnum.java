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
    SERVER_SUCCESS("成功调用服务",202),
    SERVER_NOT_FOUND("服务找不到", 404),
    SERVER_ERROR("服务出错",501);
    private int code;
    private String des;

    CodeEnum(String des, int code) {
        this.des = des;
        this.code = code;
    }
}
