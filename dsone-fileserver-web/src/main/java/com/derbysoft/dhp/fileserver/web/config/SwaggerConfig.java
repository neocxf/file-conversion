package com.derbysoft.dhp.fileserver.web.config;

import com.mangofactory.swagger.configuration.SpringSwaggerConfig;
import com.mangofactory.swagger.models.dto.ApiInfo;
import com.mangofactory.swagger.models.dto.builder.ApiInfoBuilder;
import com.mangofactory.swagger.plugin.EnableSwagger;
import com.mangofactory.swagger.plugin.SwaggerSpringMvcPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Xiaofei Chen <a href="mailto:neocxf@gmail.com">Email the author</a>
 * @version 1.0 1/10/2016
 */
@Configuration
@EnableSwagger
public class SwaggerConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(SwaggerConfig.class);

    @Autowired
    private SpringSwaggerConfig springSwaggerConfig;

    private ApiInfo getApiInfo() {

        return new ApiInfoBuilder().title("DHP FILESERVER API")
                .description("APi for pdf-converter for Derbysoft company")
                .termsOfServiceUrl("http://www.derbysoft.com")
                .contact("neo.chen@derbysoft.com")
                .license("MIT License")
                .licenseUrl("http://opensource.org/licenses/MIT")
                .build();
    }

    @Bean
    public SwaggerSpringMvcPlugin configureSwagger() {
        SwaggerSpringMvcPlugin swaggerSpringMvcPlugin = new SwaggerSpringMvcPlugin(this.springSwaggerConfig);

        LOGGER.debug(" inside SwaggerConfig configuration ");

        swaggerSpringMvcPlugin.apiInfo(getApiInfo()).apiVersion("1.0");

        return swaggerSpringMvcPlugin;
    }

    @Bean
    public SwaggerSpringMvcPlugin v1ApiConfiguration() {
        SwaggerSpringMvcPlugin swaggerSpringMvcPlugin = new SwaggerSpringMvcPlugin(this.springSwaggerConfig);
        swaggerSpringMvcPlugin.apiInfo(getApiInfo()).apiVersion("1.0")
                .includePatterns("/v1/*.*").swaggerGroup("v1");
        swaggerSpringMvcPlugin.useDefaultResponseMessages(false);

        return swaggerSpringMvcPlugin;
    }

    @Bean
    public SwaggerSpringMvcPlugin v2ApiConfiguration() {
        SwaggerSpringMvcPlugin swaggerSpringMvcPlugin = new SwaggerSpringMvcPlugin(this.springSwaggerConfig);
        swaggerSpringMvcPlugin.apiInfo(getApiInfo()).apiVersion("2.0")
                .includePatterns("/v2/*.*").swaggerGroup("v2");
        swaggerSpringMvcPlugin.useDefaultResponseMessages(false);

        return swaggerSpringMvcPlugin;
    }
}
