package com.derbysoft.dhp.fileserver.web;

import com.derbysoft.dhp.fileserver.web.config.SpringWebConfig;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;
import org.tuckey.web.filters.urlrewrite.UrlRewriteFilter;

import javax.servlet.*;
import java.util.EnumSet;

/**
 *  entry point of annotation-based servlet 3.0 container.
 *
 *  here, we start the spring bean container, and WebServletContext
 *
 *  http://www.rockhoppertech.com/blog/spring-mvc-configuration-without-xml/
 *
 *  good staff come here: http://www.robinhowlett.com/blog/2013/02/13/spring-app-migration-from-xml-to-java-based-config/
 */
public class FileServerInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

    @Override
    public void onStartup(ServletContext container) throws ServletException {
        super.onStartup(container);

//        container.addFilter("urlRewriteFilter", UrlRewriteFilter.class).addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD, DispatcherType.INCLUDE), false, "/*");
    }

    @Override
	protected Class<?>[] getServletConfigClasses() {
		System.out.println(" config the web initializer");
		return new Class[] { SpringWebConfig.class };
	}

	@Override
	protected String[] getServletMappings() {
		return new String[] { "/" };
	}

    @Override
	protected Class<?>[] getRootConfigClasses() {
		return null;
	}

    @Override
    protected Filter[] getServletFilters() {
        return new Filter[] {};
    }

}