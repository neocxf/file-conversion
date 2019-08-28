package top.neospot.conversion.web;

import top.neospot.conversion.web.config.SpringWebConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;

/**
 *  entry point of annotation-based servlet 3.0 container.
 *
 *  here, we start the spring bean container, and WebServletContext
 *
 */
@WebServlet
public class FileServerInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {
    private static final Logger logger = LoggerFactory.getLogger(FileServerInitializer.class);

    @Override
    public void onStartup(ServletContext container) throws ServletException {
        super.onStartup(container);
    }

    @Override
	protected Class<?>[] getServletConfigClasses() {
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

    @Override
    protected void customizeRegistration(ServletRegistration.Dynamic registration) {
        super.customizeRegistration(registration);
        registration.setMultipartConfig(new MultipartConfigElement(LOCATION, MAX_FILE_SIZE, MAX_REQUEST_SIZE, FILE_SIZE_THRESHOLD));
    }


    private static final String LOCATION = "/tmp/"; // Temporary location where files will be stored

    private static final long MAX_FILE_SIZE = 5242880; // 5MB : Max file size.
    // Beyond that size spring will throw exception.
    private static final long MAX_REQUEST_SIZE = 20971520; // 20MB : Total request size containing Multi part.

    private static final int FILE_SIZE_THRESHOLD = 0; // Size threshold after which files will be written to disk
}