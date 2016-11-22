package com.derbysoft.dhp.fileserver.client;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

@EnableWebMvc
@Configuration
@ComponentScan({ "com.derbysoft.dhp.fileserver.client" })
public class SpringWebConfig extends WebMvcConfigurerAdapter {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        System.out.println("addResourceHandlers ... ");

        registry.addResourceHandler("/css/**").addResourceLocations("/css/");
        registry.addResourceHandler("/js/**").addResourceLocations("/js/");
    }

    /**
     *  activate the @Value annotation
     *  <strong>ATTENTION</strong> for BeanFactoryPostProcessor-returning @Bean methods
     *  http://stackoverflow.com/questions/14942304/springs-javaconfig-and-customscopeconfigurer-issue
     */
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyConfigInDev() {
        System.out.println(" configure PropertySourcesPlaceholderConfigurer ...");
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public InternalResourceViewResolver viewResolver() {

        System.out.println("viewResolver ...");

        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setViewClass(JstlView.class);
        viewResolver.setPrefix("/WEB-INF/views/jsp/");
        viewResolver.setSuffix(".jsp");
        return viewResolver;
    }
}
