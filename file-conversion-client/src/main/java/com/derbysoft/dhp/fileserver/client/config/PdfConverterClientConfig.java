package com.derbysoft.dhp.fileserver.client.config;

import com.derbysoft.dhp.fileserver.config.EnablePdfConverter;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 * @author neo.fei {neocxf@gmail.com}
 * @version 2/24/17
 */
@Configuration
@EnablePdfConverter
@EnableWebMvc
public class PdfConverterClientConfig {
}
