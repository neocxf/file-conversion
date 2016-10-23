package com.derbysoft.dhp.fileserver.web.config;

import org.springframework.context.annotation.Bean;
import org.tuckey.web.filters.urlrewrite.UrlRewriteFilter;

import javax.servlet.Filter;
import javax.servlet.FilterRegistration;

/**
 * @author neo.fei {neocxf@gmail.com}
 */
//@Configuration
public class FilterConfig {

    @Bean
    Filter urlRewriteFilter() {
        UrlRewriteFilter rewriteFilter = new UrlRewriteFilter();

        return rewriteFilter;
    }

//    @Bean
//    Filter jsonpFilter() {
//        return new JsonpCallbackFilter();
//    }

//    @Bean
//    public FilterRegistrationBean corsFilterReg() {
//        LOGGER.trace("Setting up SecurityCorsFilter with " + corsFilter());
//
//        FilterRegistrationBean filterRegBean = new FilterRegistrationBean();
//
//        filterRegBean.setFilter(corsFilter());
//        filterRegBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
//
//        return filterRegBean;
//    }
}
