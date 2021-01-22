package github.tuquanrong.proxy;

import github.tuquanrong.model.dto.RequestDto;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * tutu
 * 2021/1/13
 */
public class ServerProxyTest {
    public static void main(String[] args) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InstantiationException, InvocationTargetException {
        RequestDto requestDto = new RequestDto();
        requestDto.setInterfaceName(Hello.class.getName());
        Method method = Hello.class.getMethods()[0];
        requestDto.setMethodName(method.getName());
        requestDto.setMethodParamType(method.getParameterTypes());
        requestDto.setParams(new Object[]{"tuquanrong"});

        Class interfaceName = Class.forName(requestDto.getInterfaceName());
        Method method1 = interfaceName.getMethod(requestDto.getMethodName(), requestDto.getMethodParamType());
        Object service = interfaceName.newInstance();
        Object object=method1.invoke(service,requestDto.getParams());
        System.out.println(object);;
    }
}
