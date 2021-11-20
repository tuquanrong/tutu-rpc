package github.tuquanrong.generate;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.stereotype.Service;

/**
 * tutu
 * 2021/1/15
 * 将需要提供给外部的服务都打上标记，在类加载阶段都给加载进去
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
@Service
public @interface TutuServiceMark {
}
