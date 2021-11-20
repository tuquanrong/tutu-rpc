# 项目文档

### 技术方案

- 通信框架：Netty
- 序列化：Protostuff
- 服务注册：Zookeeper
- 动态代理：JDK动态代理
- bean加载：Spring自带@import加载机制，通过bean初始化前和bean初始化后来实现bean的加载和bean的载入

### 待优化模块

- [ ] 客户端断开连接时服务端没有把对应的通道关闭缺乏心跳机制
- [ ] 通过sentinel引入流量控制能力（熔断、限流）
- [ ] 实现金丝雀、蓝绿、ab发布能力
- [ ] 接入本地缓存提高注册中心的容错性

### 使用方式

**框架启动注解**

1. @TutuServiceMark
2. @TutuRpcStartup

```java
//创建中间文件
@TutuServiceMark
public interface HelloService {
    String hello(String name);
}
```

```java
//创建服务方实现文件
@Component
public class HelloServerImpl implements HelloService {
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
    @Autowired
    private HelloService helloService;
    public String test(String name){
        return helloService.hello(name);
    }
}
```

```java
//服务端启动
@TutuRpcStartup
@SpringBootApplication
public class RpcServer {
    public static void main(String[] args) {
        SpringApplication.run(RpcServer.class,args);
        PropertiesConfig propertiesConfig=PropertiesConfig.getInstance();
        NettyServer nettyServer=new NettyServer();
        nettyServer.start();
    }
}
//客户端启动
@TutuRpcStartup
@SpringBootApplication
public class RpcClient {
    public static void main(String[] args) {
        SpringApplication.run(RpcClient.class, args);
     System.out.println(((HelloClient)SpringContextUtil.getBean("helloClient")).test("test"));
    }
}
```

**框架启动配置**

放在resources下tutuRPc.properties文件

```properties
serializer=1
zk=127.0.0.1:2181
```

