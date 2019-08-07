package com.illidan.thread;

import com.illidan.GantDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    public synchronized static ProducerConnection getInstance(String name, GantDataSource dataSource) {
        if (producer == null) {
            producer = new ProducerConnection(name, dataSource);
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
        super.run();
    }

    /**
     * 主方法
     */
    public void createConnection() {

    }
}
