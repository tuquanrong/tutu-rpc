# 项目文档

### 技术方案

- 通信框架：Netty
- 序列化：Protostuff
- 服务注册：Zookeeper
- 动态代理：JDK动态代理
- bean加载：Spring自带@import加载机制，通过bean初始化前和bean初始化后来实现bean的加载和bean的载入

### 待优化模块

- [ ] 服务提供强依赖于Spring需要通过注解导入服务bean这一部分需要使用者显式处理，应该对这部分再封装隐藏起来
- [ ] 使用**SimpleChannelInboundHandler**替代**ChannelInboundHandlerAdapter**使得channelHandler可以热抽拔
- [ ] 客户端断开连接时服务端没有把对应的通道关闭缺乏心跳机制

### 待解决Bug

- [ ] 服务端启动时有java.net.BindException: Can't assign requested address错误

### 使用方式

```java
//创建中间文件
public interface HelloService {
    String hello(String name);
}
```

```java
//创建服务方实现文件
@ServerGenerate
@Component
public class HelloServer implements HelloService {
    @Override
    public String hello(String name) {
        return name+"tututu";
    }
}
```

```java
//创建客户端使用文件
@Component
public class HelloClient {
    @ClientGenerate
    private HelloService helloService;
    public String test(String name){
        return helloService.hello(name);
    }
}

```

```java
//服务端启动
@BeanScan(scanDir = "tuturpc")
public class RpcServer {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(RpcServer.class);
        PropertiesConfig propertiesConfig=PropertiesConfig.getInstance();
        NettyServer nettyServer=new NettyServer();
        nettyServer.start();
    }
}
//客户端启动
@BeanScan(scanDir ={"tuturpc"})
public class RpcClient {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(RpcClient.class);
        HelloClient helloClient = (HelloClient) applicationContext.getBean("helloClient");
        System.out.println(helloClient.test("test"));
    }
}
```

