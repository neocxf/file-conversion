package com.derbysoft.dhp.fileserver.config;

import com.derbysoft.dhp.fileserver.api.http.HttpClientAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 *
 *  various pdf-conversion service configuration
 *
 *  configure here for the operations
 *
 * @author neo.fei {neocxf@gmail.com}
 * @version 2/24/17
 */
@Configuration
@ComponentScan(basePackages = "com.derbysoft.dhp.fileserver.api.http")
public class PdfConverterConfig {

    @Bean
    HttpClientAdapter httpClientAdapter() {
        return new HttpClientAdapter();
    }
}
