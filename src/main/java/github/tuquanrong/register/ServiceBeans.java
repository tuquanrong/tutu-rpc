package github.tuquanrong.register;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import github.tuquanrong.exception.RpcServerException;
import github.tuquanrong.model.enums.RpcServerStatusEnum;

/**
 * tutu
 * 2021/1/15
 */
public class ServiceBeans {
    private static final Logger logger = LoggerFactory.getLogger(ServiceBeans.class);
    private static final ServiceBeans SERVICE_BEANS = new ServiceBeans();
    //服务端提供的bean对象
    private Map<String, Object> serviceMap = new ConcurrentHashMap();

    private Map<String, Class<?>> classMap = new ConcurrentHashMap();

    public static ServiceBeans getInstance() {
        return SERVICE_BEANS;
    }

    public void setService(String className, Object object) {
        if (serviceMap.keySet().contains(className)) {
            throw new RpcServerException(RpcServerStatusEnum.DUPLICATE_REGISTER);
        }
        serviceMap.put(className, object);
    }

    public Object getService(String className) {
        return serviceMap.get(className);
    }

    public void setClass(String className, Class<?> classType) {
        if (classMap.keySet().contains(className)) {
            throw new RpcServerException(RpcServerStatusEnum.DUPLICATE_REGISTER);
        }
        classMap.put(className, classType);
    }

    public Class<?> getClass(String className) {
        return classMap.get(className);
    }
}
