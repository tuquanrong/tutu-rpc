package github.tuquanrong.generate.scan;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.stereotype.Component;

/**
 * tutu
 * 2021/1/15
 * 被ConfigurationClassPostProcessor处理
 */
public class BeanDirectoryImport implements ImportBeanDefinitionRegistrar, ResourceLoaderAware {
    private ResourceLoader resourceLoader;
    private String componentDir = "github.tuquanrong";

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    /**
     * @param importingClassMetadata 当前类的注册信息
     * @param registry 注册类
     */
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        //这是为了扫描proxyBean生成类生成BeanPostProcessor对象
        BeanDirectoryScan componentDirectoryScan = new BeanDirectoryScan(registry, Component.class);
        componentDirectoryScan.scan(componentDir);
    }
}
