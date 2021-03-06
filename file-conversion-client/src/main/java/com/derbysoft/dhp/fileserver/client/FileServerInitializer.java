package com.derbysoft.dhp.fileserver.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import javax.servlet.Filter;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

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
    private static final Logger logger = LoggerFactory.getLogger(FileServerInitializer.class);

    @Override
    public void onStartup(ServletContext container) throws ServletException {
        super.onStartup(container);

//        container.addFilter("urlRewriteFilter", UrlRewriteFilter.class).addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD, DispatcherType.INCLUDE), false, "/*");
    }

    @Override
	protected Class<?>[] getServletConfigClasses() {
        logger.info(" config the web initializer");
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