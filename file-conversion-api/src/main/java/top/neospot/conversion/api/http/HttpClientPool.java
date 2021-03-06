package top.neospot.conversion.api.http;

import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 *  pool configuration of HttpClient, mainly for internal use
 *
 * @author neo.fei {neocxf@gmail.com} on 2017/9/29.
 */
class HttpClientPool {
    private static final Logger log = Logger.getLogger(InnerLoggerSetting.HTTP_LOGGER_NAME);

    // Single-element enum to implement Singleton.
    private enum Singleton {
        // Just one of me so constructor will be called once.
        Client(200, 20);
        // The thread-safe client.
        private final CloseableHttpClient threadSafeClient;
        // The pool monitor.
        private final IdleConnectionMonitorThread monitor;

        // The constructor creates it - thus late
        Singleton(int maxTotal, int maxRoute) {
            PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
            // Increase max total connection to 200
            cm.setMaxTotal(maxTotal);
            // Increase default max connection per route to 20
            cm.setDefaultMaxPerRoute(maxRoute);
            // Build the client.
            threadSafeClient = HttpClients.custom()
                    .setConnectionManager(cm)
                    .build();
            // Start up an eviction thread.
            monitor = new IdleConnectionMonitorThread(cm);
            // Don't stop quitting.
            monitor.setDaemon(true);
            monitor.start();
        }

        public CloseableHttpClient get() {
            return threadSafeClient;
        }

    }

    /**
     *  get singleton http client
     *
     * @return http client
     */
    public static CloseableHttpClient getClient() {
        // The thread safe client is held by the singleton.
        return Singleton.Client.get();
    }

    // Watches for stale connections and evicts them.
    private static class IdleConnectionMonitorThread extends Thread {
        // The manager to watch.
        private final PoolingHttpClientConnectionManager cm;
        // Use a BlockingQueue to stop everything.
        private final BlockingQueue<Stop> stopSignal = new ArrayBlockingQueue<Stop>(1);

        // Pushed up the queue.
        private static class Stop {
            // The return queue.
            private final BlockingQueue<Stop> stop = new ArrayBlockingQueue<Stop>(1);

            // Called by the process that is being told to stop.
            public void stopped() {
                // Push me back up the queue to indicate we are now stopped.
                stop.add(this);
            }

            // Called by the process requesting the stop.
            public void waitForStopped() throws InterruptedException {
                // Wait until the callee acknowledges that it has stopped.
                stop.take();
            }

        }

        IdleConnectionMonitorThread(PoolingHttpClientConnectionManager cm) {
            super();
            this.cm = cm;
        }

        @Override
        public void run() {
            try {
                // Holds the stop request that stopped the process.
                Stop stopRequest;
                // Every 5 seconds.
                while ((stopRequest = stopSignal.poll(5, TimeUnit.SECONDS)) == null) {
                    // Close expired connections
                    cm.closeExpiredConnections();
                    // Optionally, close connections that have been idle too long.
                    cm.closeIdleConnections(60, TimeUnit.SECONDS);
                    // Look at pool stats.
                    log.finer("Stats: {}" + cm.getTotalStats());
                }
                // Acknowledge the stop request.
                stopRequest.stopped();
            } catch (InterruptedException ex) {
                // terminate
            } finally {
                log.warning("IdleConnectionMonitorThread terminated");
            }
        }

        public void shutdown() throws InterruptedException {
//            log.trace("Shutting down client pool");
            // Signal the stop to the thread.
            Stop stop = new Stop();
            stopSignal.add(stop);
            // Wait for the stop to complete.
            stop.waitForStopped();
            // Close the pool - Added
//            Singleton.Client.threadSafeClient.close();
            HttpClientUtils.closeQuietly(HttpClientPool.getClient());
            // Close the connection manager.
            cm.close();
            log.warning("Http Client pool shut down");
        }

    }

    public static void shutdown() throws InterruptedException {
        // Shutdown the monitor.
        Singleton.Client.monitor.shutdown();
    }

}
