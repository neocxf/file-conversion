package com.derbysoft.dhp.fileserver.core.util;

import org.junit.Test;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.util.concurrent.TimeUnit;

/**
 *  get the host operating system CPU usage and Memory usage
 *
 * @author neo.fei {neocxf@gmail.com}
 * @version 3/23/17
 */
public class HostEnvTest {

    /**
     *  http://stackoverflow.com/questions/74674/how-to-do-i-check-cpu-and-memory-usage-in-java
     */
    @Test
    public void cpuUsage() throws InterruptedException {
        OperatingSystemMXBean operatingSystemMXBean = ManagementFactory.getOperatingSystemMXBean();
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        int availableProcessors = operatingSystemMXBean.getAvailableProcessors();
        long prevUpTime = runtimeMXBean.getStartTime();
        double prevProcessCpuTime = operatingSystemMXBean.getSystemLoadAverage();

        TimeUnit.MILLISECONDS.sleep(500);

        operatingSystemMXBean = ManagementFactory.getOperatingSystemMXBean();
        long upTime = runtimeMXBean.getUptime();
        double processCpuTime = operatingSystemMXBean.getSystemLoadAverage();

        double elapsedCpu = processCpuTime - prevProcessCpuTime;
        long elapsedTime = upTime - prevUpTime;

        double cpuUsage = Math.min(99F, elapsedCpu / (elapsedTime * 10000F * availableProcessors));
        System.out.println("Java CPU: " + cpuUsage);
//        System.out.println("available processors: {} " + availableProcessors);
//        System.out.println("uptime : {} " + prevUpTime);
//        System.out.println("systemLoadAverage : {} " + prevProcessCpuTime);
    }
}
