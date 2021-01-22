package github.tuquanrong.proxy;

import github.tuquanrong.register.ServiceBeans;
import github.tuquanrong.exception.RpcException;
import github.tuquanrong.model.dto.RequestDto;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

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
            Object service = serviceBeans.getService(interfaceName.getName());
            if (service == null) {
                throw new RpcException("找不到相应的方法");
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
