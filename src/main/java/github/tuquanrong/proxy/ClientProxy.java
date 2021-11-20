package github.tuquanrong.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.CompletableFuture;

import github.tuquanrong.exception.RpcServerException;
import github.tuquanrong.model.dto.RequestDto;
import github.tuquanrong.model.dto.ResponseDto;
import github.tuquanrong.model.enums.RpcServerStatusEnum;
import github.tuquanrong.transport.NettyClient;
import github.tuquanrong.util.RequestBuilder;

/**
 * tutu
 * 2021/1/13
 */
public class ClientProxy implements InvocationHandler {
    private static final ClientProxy CLIENT_PROXY = new ClientProxy();
    private NettyClient nettyClient;

    public ClientProxy() {
        this.nettyClient = NettyClient.getInstance();
    }

    public static ClientProxy getInstance() {
        return CLIENT_PROXY;
    }

    public <T> T getProxy(Class<T> proxyClass) {
        return (T) Proxy.newProxyInstance(proxyClass.getClassLoader(), new Class<?>[]{proxyClass}, this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RequestDto requestDto = RequestBuilder.genarateRequest(method, args);
        System.out.println(requestDto);
        CompletableFuture<ResponseDto> completableFuture = nettyClient.sendMessage(requestDto);
        ResponseDto responseDto = completableFuture.get();
        System.out.println(responseDto);
        dealException(requestDto, responseDto);
        return responseDto.getData();
    }

    public void dealException(RequestDto requestDto, ResponseDto responseDto) {
        if (responseDto == null) {
            new RpcServerException(RpcServerStatusEnum.SERVER_ERROR
                    , "responseMessage=" + RpcServerStatusEnum.SERVER_ERROR + "interfaceName=" + requestDto.getInterfaceName());
        } else if (requestDto.getRequestId() != responseDto.getResponseId()) {
            new RpcServerException(RpcServerStatusEnum.SERVER_ID_MISTAKE
                    , "responseMessage=" + RpcServerStatusEnum.SERVER_ID_MISTAKE + "interfaceName=" + requestDto.getInterfaceName());
        } else {
            throw new RpcServerException(RpcServerStatusEnum.SERVER_ERROR
                    , "responseMessage=" + responseDto.getMessage() + "interfaceName=" + requestDto.getInterfaceName());
        }
    }
}
