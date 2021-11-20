package github.tuquanrong.register;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import github.tuquanrong.exception.RpcServerException;
import github.tuquanrong.model.enums.RpcServerStatusEnum;

/**
 * tutu
 * 2021/1/15
 */
public class ServiceBeans {
    private static final ServiceBeans SERVICE_BEANS = new ServiceBeans();
    //服务端提供的bean对象
    private Map<String, Object> serviceMap = new ConcurrentHashMap();

    public static ServiceBeans getInstance() {
        return SERVICE_BEANS;
    }

    public void setService(String classname, Object object) {
        if (serviceMap.keySet().contains(classname)) {
            throw new RpcServerException(RpcServerStatusEnum.DUPLICATE_REGISTER);
        }
        System.out.println(classname);
        serviceMap.put(classname, object);
    }

    public Object getService(String className) {
        return serviceMap.get(className);
    }
}
