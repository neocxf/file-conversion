package com.derbysoft.dhp.fileserver.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.Ordered;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

import javax.servlet.MultipartConfigElement;
import javax.servlet.annotation.MultipartConfig;

@EnableWebMvc
@Configuration
@ComponentScan({ "com.derbysoft.dhp.fileserver" })
//@MultipartConfig(maxFileSize = 1024 * 1024 * 10, maxRequestSize = 1024 * 1024 * 10)
public class SpringWebConfig extends WebMvcConfigurerAdapter {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        System.out.println("addResourceHandlers ... ");

        // http://stackoverflow.com/questions/1483063/how-to-handle-static-content-in-spring-mvc
        // http://www.baeldung.com/spring-mvc-static-resources
//        registry.addResourceHandler("/css/**").addResourceLocations("/css/");
//        registry.addResourceHandler("/js/**").addResourceLocations("/js/");
//        registry.addResourceHandler("/public/swagger/**").addResourceLocations("/swagger/");
//        registry.addResourceHandler("/swagger/**").addResourceLocations("/public/swagger/");
        registry.addResourceHandler("/**").addResourceLocations("/public/");
    }

    @Override
    public void addViewControllers( ViewControllerRegistry registry ) {
        registry.addViewController( "/" ).setViewName( "redirect:/swagger/index.html" );
//        registry.addViewController( "/" ).setViewName( "forward:/management" );
//        registry.addViewController( "/" ).setViewName( "redirect:/management" );
//        registry.addStatusController("/errors/404.html", HttpStatus.NOT_FOUND);
        registry.setOrder( Ordered.HIGHEST_PRECEDENCE );
        super.addViewControllers( registry );
    }

    /**
     *  activate the @Value annotation
     *  <strong>ATTENTION</strong> for BeanFactoryPostProcessor-returning @Bean methods
     *  http://stackoverflow.com/questions/14942304/springs-javaconfig-and-customscopeconfigurer-issue
     */
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyConfigInDev() {
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

    @Bean
    MultipartConfigElement multipartConfigElement() {
        System.out.println(" configure the MultipartConfigElement ...");
        return new MultipartConfigElement(null, 1024*1024*10, 1024*1024*10, 1024*1024*10);
    }

    @Bean
    public MultipartResolver multipartResolver() {
        System.out.println(" configure the MultipartResolver ...");
        return new StandardServletMultipartResolver();
    }
}
