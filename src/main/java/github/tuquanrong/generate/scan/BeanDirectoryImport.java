package github.tuquanrong.generate.scan;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.StandardAnnotationMetadata;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * tutu
 * 2021/1/15
 */
public class BeanDirectoryImport implements ImportBeanDefinitionRegistrar, ResourceLoaderAware {
    private ResourceLoader resourceLoader;
    private String componentDir = "github.tuquanrong";

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        Map<String, Object> map = importingClassMetadata.getAnnotationAttributes(BeanScan.class.getName());
        AnnotationAttributes annotationAttributes = AnnotationAttributes.fromMap(map);
        String[] dirs = null;
        if (annotationAttributes != null) {
            dirs = annotationAttributes.getStringArray("scanDir");
        }
        //如果没有输入目录采用beanscan的当前包
        if (dirs == null || dirs.length == 0) {
            dirs = new String[]{((StandardAnnotationMetadata) importingClassMetadata).getIntrospectedClass().getPackage().getName()};
        }

        BeanDirectoryScan serviceDirectoryScan = new BeanDirectoryScan(registry, BeanScan.class);
        //这是为了扫描proxyBean生成类生成BeanPostProcessor对象
        BeanDirectoryScan componentDirectoryScan = new BeanDirectoryScan(registry, Component.class);
        int serviceNumber = serviceDirectoryScan.scan(dirs);
        int componentNumber = componentDirectoryScan.scan(componentDir);
    }
}
