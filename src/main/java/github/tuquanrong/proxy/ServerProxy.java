package github.tuquanrong.proxy;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import github.tuquanrong.exception.RpcServerException;
import github.tuquanrong.model.dto.RequestDto;
import github.tuquanrong.model.enums.RpcServerStatusEnum;
import github.tuquanrong.register.ServiceBeans;

/**
 * tutu
 * 2021/1/13
 */
public class ServerProxy {
    private static final ServerProxy SERVER_PROXY = new ServerProxy();
    private ServiceBeans serviceBeans;

    public ServerProxy() {
        serviceBeans = ServiceBeans.getInstance();
    }

    public static ServerProxy getInstance() {
        return SERVER_PROXY;
    }

    public Object invoke(RequestDto requestDto) {
        System.out.println(requestDto);
        Object data = null;
        try {
            Class interfaceName = Class.forName(requestDto.getInterfaceName());
            Method method1 = interfaceName.getMethod(requestDto.getMethodName(), requestDto.getMethodParamType());
            System.out.println("one" + method1);
            Object service = serviceBeans.getService(interfaceName.getName());
            System.out.println("two" + service);
            if (service == null) {
                throw new RpcServerException(RpcServerStatusEnum.NO_DISCOVER_SERVER);
            }
            data = method1.invoke(service, requestDto.getParams());
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return data;
    }
}
