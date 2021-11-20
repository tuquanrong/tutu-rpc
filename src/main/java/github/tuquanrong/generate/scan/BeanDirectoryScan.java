package github.tuquanrong.generate.scan;

import java.lang.annotation.Annotation;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.filter.AnnotationTypeFilter;

/**
 * tutu
 * 2021/1/15
 * 将指定注解添加到扫描列表"此处可以进行扫描注解的自定义"
 */
public class BeanDirectoryScan extends ClassPathBeanDefinitionScanner {
    public BeanDirectoryScan(BeanDefinitionRegistry registry, Class<? extends Annotation> annotation) {
        super(registry);
        super.addIncludeFilter(new AnnotationTypeFilter(annotation));
    }

    @Override
    public int scan(String... basePackages) {
        return super.scan(basePackages);
    }
}
