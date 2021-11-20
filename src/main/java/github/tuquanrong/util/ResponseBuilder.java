package github.tuquanrong.util;

import github.tuquanrong.model.dto.ResponseDto;
import github.tuquanrong.model.enums.RpcServerStatusEnum;

/**
 * tutu
 * 2021/1/10
 */
public class ResponseBuilder {
    public static ResponseDto success(String responseId, Object data) {
        return new ResponseDto(responseId, RpcServerStatusEnum.SERVER_SUCCESS.getCode(), RpcServerStatusEnum.SERVER_SUCCESS.getDesc(), data);
    }
}
