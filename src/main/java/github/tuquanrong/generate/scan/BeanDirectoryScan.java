package github.tuquanrong.generate.scan;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.lang.annotation.Annotation;

/**
 * tutu
 * 2021/1/15
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
