package com.illidan.connection;

import com.illidan.*;
import com.illidan.config.Configuration;
import com.illidan.connection.proxy.ProxyConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author 甘明波
 * @date 2019-08-06
 */
public class ConnectionFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConnectionFactory.class);
    private static ConnectionFactory factory;
    private GantDataSource dataSource;
    private Configuration configuration;
    /**
     * 驱动：JDBC实现类
     */
    private Class driver;

    /**
     * 私有化构造方法
     */
    private ConnectionFactory(GantDataSource dataSource) {
        this.dataSource = dataSource;
        this.configuration = dataSource.getConfiguration();
        registerDriver();
        initConnectionInfo();
    }

    /**
     * 工厂模式获取单例对象
     */
    public static ConnectionFactory getInstance(GantDataSource dataSource) {
        if (factory == null) {
            synchronized (ConnectionFactory.class) {
                if (factory == null) {
                    factory = new ConnectionFactory(dataSource);
                }
            }
        }
        return factory;
    }

    /**
     * 生产连接
     */
    public PoolConnection getConnection() {
        return getGantConnection(new GantConnection(dataSource));
    }

    private PoolConnection getGantConnection(GantConnection connection) {
        ProxyConnection proxy = new ProxyConnection(connection);
        return (PoolConnection) proxy.getProxyObject();
    }

    /**
     * 注册驱动
     */
    private void registerDriver() {
        if (driver == null) {
            String driverStr = configuration.getDriver();
            try {
                driver = Class.forName(driverStr);
                LOGGER.debug("加载数据库驱动");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 初始化连接信息
     */
    private void initConnectionInfo() {
        Integer timeout = configuration.getConnectionTimeout();
        //将DriverManager内部的日志输出到控制台
        DriverManager.setLogWriter(new PrintWriter(System.out));
        try (Connection connection = DriverManager.getConnection(configuration.getUrl(), configuration.getUsername(),
                configuration.getPassword());
             Statement statement = connection.createStatement()) {
            statement.execute("set names utf8mb4");
            statement.execute("set global connect_timeout = " + timeout);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        LOGGER.debug("设置连接池获取连接等待最大的时间：" + timeout);
        LOGGER.debug("设置连接池各种字符集：utf8mb4");
    }
}
