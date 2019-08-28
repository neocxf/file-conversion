package top.neospot.conversion.boot.config;

import com.google.gson.Gson;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author neo.fei {neocxf@gmail.com}
 */
@Configuration
public class AppConfig {

    @Bean
    Gson gson() {
        return new Gson();
    }


}
