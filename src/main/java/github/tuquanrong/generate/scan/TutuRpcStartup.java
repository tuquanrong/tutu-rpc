package github.tuquanrong.generate.scan;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;

/**
 * tutu
 * 2021/1/15
 * 扫描
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import(BeanDirectoryImport.class)
@Component
public @interface TutuRpcStartup {
    String[] scanDir() default {""};
}
