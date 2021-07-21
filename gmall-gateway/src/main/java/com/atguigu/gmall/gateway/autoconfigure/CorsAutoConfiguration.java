package com.atguigu.gmall.gateway.autoconfigure;

import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.*;
import org.springframework.beans.factory.support.*;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.List;


/**
 * @author wyl
 * @create 2020-06-27 15:26
 */
@Configuration
@Import(CorsAutoConfiguration.CorsRegistrar.class)
public class CorsAutoConfiguration {

    static class CorsRegistrar implements ImportBeanDefinitionRegistrar, EnvironmentAware {
        private List<String> corsList;
        private final String CORS_BEAN_NAME = "GmallCorsWebFilter";

        @Override
        public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
            for (int i = 0; i < corsList.size(); i++) {
                ConstructorArgumentValues constructorArgumentValues = new ConstructorArgumentValues();
                constructorArgumentValues.addGenericArgumentValue(new ConstructorArgumentValues
                        .ValueHolder(this.getCorsWebFilterConfiguration(corsList.get(i)), "org.springframework.web.cors.reactive.CorsConfigurationSource"));

                MutablePropertyValues propertyValues = new MutablePropertyValues();

                this.registerSyntheticBean(registry,CORS_BEAN_NAME + "[" + i + "]" ,
                        "org.springframework.web.cors.reactive.CorsWebFilter",
                        constructorArgumentValues,
                        propertyValues);
            }
        }

        @Override
        public void setEnvironment(Environment environment) {
            List<String> property = environment.getProperty("com.atguigu.cors.address", List.class);
            if (property != null && property.size() > 0)
                corsList = property;
        }

        // 配置cors的值
        private UrlBasedCorsConfigurationSource getCorsWebFilterConfiguration(String origin) {
            System.out.println(origin);
            CorsConfiguration corsConfig = new CorsConfiguration();
            corsConfig.addAllowedHeader("*");
            corsConfig.addAllowedMethod("*");
            corsConfig.addAllowedOrigin(origin);
            corsConfig.setAllowCredentials(true);

            UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
            urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", corsConfig);
            return urlBasedCorsConfigurationSource;
        }

        /**
         * 创建一个新的beanDefinition
         * @param registry
         * @param beanName
         * @param beanClass
         * @param constructorArgumentValues
         * @param propertyValues
         */
        private void registerSyntheticBean(BeanDefinitionRegistry registry,
                                                    String beanName, String beanClass,
                                                    ConstructorArgumentValues constructorArgumentValues,
                                                    MutablePropertyValues propertyValues) {
                RootBeanDefinition beanDefinition = new RootBeanDefinition(beanClass,constructorArgumentValues,propertyValues);

                beanDefinition.setSynthetic(true);
                // 注册BeanDefinition
                registry.registerBeanDefinition(beanName, beanDefinition);

        }
    }
}
