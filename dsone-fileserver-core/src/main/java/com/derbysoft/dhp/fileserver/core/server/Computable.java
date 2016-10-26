package com.derbysoft.dhp.fileserver.core.server;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author neo.fei {neocxf@gmail.com}
 */
public interface Computable<A, V> {

    V compute(A arg) throws InterruptedException, IOException, TimeoutException;
}
