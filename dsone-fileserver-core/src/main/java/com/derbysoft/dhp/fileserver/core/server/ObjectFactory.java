package com.derbysoft.dhp.fileserver.core.server;

import java.io.IOException;

public interface ObjectFactory<T>{
	/**
	 *  create the service
     *
     *  TODO: not so good, cause this method is deeply coupled with the detail impl
	 * @param url
	 * @param destFile
	 * @return
	 * @throws IOException
     */
	public T create(String url, String destFile) throws IOException;
	public boolean validate(T object);
	public void destroy(T object);
	public void activate(T object);
	public void passivate(T Object);
}