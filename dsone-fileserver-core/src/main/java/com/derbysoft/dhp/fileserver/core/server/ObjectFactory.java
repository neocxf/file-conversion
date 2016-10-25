package com.derbysoft.dhp.fileserver.core.server;

import java.io.IOException;

public interface ObjectFactory<T>{
	/**
	 *  create the service
     *
	 * @return
	 * @throws IOException
     */
	public T create() throws IOException;
	public boolean validate(T object);
	public void destroy(T object);
	public void activate(T object);
	public void passivate(T Object);
}