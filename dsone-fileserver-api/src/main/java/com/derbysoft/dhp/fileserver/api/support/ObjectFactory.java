package com.derbysoft.dhp.fileserver.api.support;

import java.io.IOException;

public interface ObjectFactory<T>{
	/**
	 *  create the service
     *
	 * @return the newly created service object
	 * @throws IOException
     */
	public T create() throws IOException;

    /**
     *  validate the service
     *
     * @param service the service object
     * @return true if valid, false if not
     */
	public boolean validate(T service);

    /**
     *  destroy the service object
     *
     * @param service the service object
     */
	public void destroy(T service);

    /**
     *  activate the service
     *
     * @param service the service object
     */
	public void activate(T service);

    /**
     *  not used as soon
     *
     * @param service the service object
     */
	public void passivate(T service);
}