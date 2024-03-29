package github.tuquanrong.generate.scan;

import java.lang.reflect.Field;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import github.tuquanrong.exception.RpcServerException;
import github.tuquanrong.generate.TutuServiceMark;
import github.tuquanrong.model.enums.RpcServerStatusEnum;
import github.tuquanrong.proxy.ClientProxy;
import github.tuquanrong.register.ServerRegisterLogout;
import github.tuquanrong.register.ServiceBeans;

/**
 * tutu
 * 2021/1/15
 * 对注入的类进行代理封装，服务注册
 */
@Component
public class BeanProxyGenerate implements BeanPostProcessor {
    private static final Logger logger = LoggerFactory.getLogger(BeanProxyGenerate.class);
    private ServiceBeans serviceBeans;
    private ServerRegisterLogout serverRegisterLogout;

    public BeanProxyGenerate() {
        serviceBeans = ServiceBeans.getInstance();
        serverRegisterLogout = ServerRegisterLogout.getInstance();
    }

    /**
     * 对服务端的服务进行注册
     *
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Class<?>[] interfaces = bean.getClass().getInterfaces();
        //排除异常实现
        Arrays.stream(interfaces).forEach(className -> {
            if (className.isAnnotationPresent(TutuServiceMark.class) && interfaces.length > 1) {
                throw new RpcServerException(RpcServerStatusEnum.IMPLEMENT_MULTIPLE_INTERFACE);
            }
        });
        if (interfaces.length == 1 && interfaces[0].isAnnotationPresent(TutuServiceMark.class)) {
            String classNamePath = interfaces[0].getName();
            String[] classNamePathStr = classNamePath.split("\\.");
            String className = classNamePathStr[classNamePathStr.length - 1];
            serviceBeans.setService(className, bean);
            serviceBeans.setClass(className, interfaces[0]);
            serverRegisterLogout.registerServer(className);
        }
        return bean;
    }

    /**
     * 对客户端的使用进行代理封装
     *
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> componentName = bean.getClass();
        logger.info(componentName.getName());
        for (Field field : componentName.getDeclaredFields()) {
            if (field.getType().isAnnotationPresent(TutuServiceMark.class)) {
                Object proxyBean = ClientProxy.getInstance().getProxy(field.getType());
                field.setAccessible(true);
                try {
                    field.set(bean, proxyBean);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return bean;
    }

}
