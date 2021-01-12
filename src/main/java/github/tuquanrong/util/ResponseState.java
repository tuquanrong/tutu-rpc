package github.tuquanrong.util;

import github.tuquanrong.model.dto.ResponseDto;
import github.tuquanrong.model.enums.CodeEnum;

/**
 * tutu
 * 2021/1/10
 */
public class ResponseState {
    public static ResponseDto success(String responseId, Object data) {
        return new ResponseDto(responseId, CodeEnum.SERVER_SUCCESS.getCode(), CodeEnum.SERVER_SUCCESS.getDes(), data);
    }
}
