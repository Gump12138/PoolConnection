package com.illidan.thread;

import com.illidan.GantDataSource;
import com.illidan.config.Configuration;
import com.illidan.connection.GantConnection;
import com.illidan.connection.PoolConnection;
import com.illidan.connection.proxy.ProxyConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author 甘明波
 * @date 2019-08-07
 */
public class ProducerConnection extends Thread {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProducerConnection.class);
    private static ProducerConnection producer;
    private GantDataSource dataSource;
    /**
     * 驱动：JDBC实现类
     */
    private static Class driver;
    private volatile static AtomicInteger count;

    public synchronized static ProducerConnection getInstance(String name, AtomicInteger count, GantDataSource dataSource) {
        if (producer == null) {
            producer = new ProducerConnection(name, dataSource);
            ProducerConnection.count = count;
        }
        return producer;
    }

    private ProducerConnection(String name, GantDataSource dataSource) {
        super(name);
        this.dataSource = dataSource;
        registerDriver();
    }

    private void registerDriver() {
        if (driver == null) {
            synchronized (ProducerConnection.class) {
                if (driver == null) {
                    try {
                        driver = Class.forName(dataSource.getConfiguration().getDriver());
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    public void run() {
        while (count.get() != 0) {
            dataSource.addConnection(getGantConnection(new GantConnection(dataSource)));
            count.decrementAndGet();
        }
    }

    private PoolConnection getGantConnection(GantConnection connection) {
        ProxyConnection proxy = new ProxyConnection(connection);
        return (PoolConnection) proxy.getProxyObject();
    }
}
