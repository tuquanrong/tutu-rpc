package github.tuquanrong.generate.scan;

import github.tuquanrong.register.ServerRegister;
import github.tuquanrong.exception.BeanRpcException;
import github.tuquanrong.generate.ClientGenerate;
import github.tuquanrong.generate.ServerGenerate;
import github.tuquanrong.proxy.ClientProxy;
import github.tuquanrong.register.ServiceBeans;
import github.tuquanrong.transport.NettyClient;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

/**
 * tutu
 * 2021/1/15
 */
@Component
public class BeanProxyGenerate implements BeanPostProcessor {
    private NettyClient nettyClient;
    private ServiceBeans serviceBeans;
    private ServerRegister serverRegister;

    public BeanProxyGenerate() {
        nettyClient = new NettyClient();
        serviceBeans = ServiceBeans.getInstance();
        serverRegister = ServerRegister.getInstance();
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean.getClass().isAnnotationPresent(ServerGenerate.class)) {
            Class<?>[] interfaces = bean.getClass().getInterfaces();
            String className = interfaces[0].getName();
            if (interfaces.length != 1) {
                throw new BeanRpcException("服务提供方bean实现接口不为1");
            }
            serviceBeans.setService(className, bean);
            serverRegister.registerServer(className);
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> componentName = bean.getClass();
        for (Field field : componentName.getDeclaredFields()) {
            ClientGenerate clientGenerate = field.getAnnotation(ClientGenerate.class);
            if (clientGenerate != null) {
                ClientProxy clientProxy = new ClientProxy(nettyClient);
                Object proxyBean = clientProxy.getProxy(field.getType());
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
