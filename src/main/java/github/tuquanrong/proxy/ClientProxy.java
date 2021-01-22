package github.tuquanrong.proxy;

import github.tuquanrong.exception.RpcException;
import github.tuquanrong.model.dto.RequestDto;
import github.tuquanrong.model.dto.ResponseDto;
import github.tuquanrong.model.enums.CodeEnum;
import github.tuquanrong.transport.NettyClient;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * tutu
 * 2021/1/13
 */
public class ClientProxy implements InvocationHandler {
    private NettyClient nettyClient;

    public ClientProxy(NettyClient nettyClient) {
        this.nettyClient = nettyClient;
    }

    public <T> T getProxy(Class<T> proxyClass) {
        return (T) Proxy.newProxyInstance(proxyClass.getClassLoader(), new Class<?>[]{proxyClass}, this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RequestDto requestDto = new RequestDto();
        requestDto.setInterfaceName(method.getDeclaringClass().getName());
        requestDto.setMethodName(method.getName());
        requestDto.setMethodParamType(method.getParameterTypes());
        requestDto.setParams(args);
        requestDto.setRequestId(UUID.randomUUID().toString());
        System.out.println(requestDto);
        CompletableFuture<ResponseDto> completableFuture = nettyClient.sendMessage(requestDto);
        ResponseDto responseDto = completableFuture.get();
        System.out.println(responseDto);
        dealException(requestDto, responseDto);
        return responseDto.getData();
    }

    public void dealException(RequestDto requestDto, ResponseDto responseDto) {
        if (responseDto == null) {
            new RpcException("responseMessage=" + CodeEnum.SERVER_ERROR.getDes() + "interfaceName=" + requestDto.getInterfaceName());
        } else if (requestDto.getRequestId() != responseDto.getResponseId()) {
            new RpcException("responseMessage=" + CodeEnum.SERVER_ID_MISTAKE + "interfaceName=" + requestDto.getInterfaceName());
        } else {
            throw new RpcException("responseMessage=" + responseDto.getMessage() + "interfaceName=" + requestDto.getInterfaceName());
        }
    }
}
