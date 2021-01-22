package github.tuquanrong.generate.scan;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * tutu
 * 2021/1/15
 */
@BeanScan(scanDir = "github.tuquanrong.bean")
public class NettyClient {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext=new AnnotationConfigApplicationContext(NettyClient.class);
    }
}
