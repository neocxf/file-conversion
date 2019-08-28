package top.neospot.conversion.config;

import top.neospot.conversion.api.http.HttpClientAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

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
