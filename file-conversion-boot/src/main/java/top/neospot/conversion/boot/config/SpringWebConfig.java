package top.neospot.conversion.boot.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.servlet.MultipartConfigElement;

@Configuration
@ComponentScan({ "top.neospot.conversion" })
public class SpringWebConfig extends WebMvcConfigurerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(SpringWebConfig.class);

    @Override
    public void addViewControllers( ViewControllerRegistry registry ) {
        registry.addViewController( "/" ).setViewName( "redirect:/swagger-ui.html" );
        registry.setOrder( Ordered.HIGHEST_PRECEDENCE );
        super.addViewControllers( registry );
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
