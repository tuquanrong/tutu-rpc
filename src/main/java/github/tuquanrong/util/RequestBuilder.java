package github.tuquanrong.util;

import java.lang.reflect.Method;
import java.util.UUID;

import github.tuquanrong.model.dto.RequestDto;

/**
 * @Author: tuquanrong
 * @Date: 2021/11/17 8:33 下午
 */
public class RequestBuilder {
    public static RequestDto genarateRequest(Method method, Object[] args) {
        RequestDto requestDto = new RequestDto();
        String[] classNamePathStr = method.getDeclaringClass().getName().split("\\.");
        String className = classNamePathStr[classNamePathStr.length - 1];
        requestDto.setInterfaceName(className);
        requestDto.setMethodName(method.getName());
        requestDto.setMethodParamType(method.getParameterTypes());
        requestDto.setParams(args);
        requestDto.setRequestId(UUID.randomUUID().toString());
        return requestDto;
    }
}
