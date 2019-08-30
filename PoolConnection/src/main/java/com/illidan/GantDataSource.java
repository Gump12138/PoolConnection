package com.illidan;

import com.illidan.config.Configuration;
import com.illidan.connection.ConnectionFactory;
import com.illidan.connection.PoolConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.LinkedList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Gant数据库连接池
 * 管理数据库连接，创建关闭连接，取放连接，监控连接
 *
 * @author 甘明波
 * @date 2019-08-01
 */
public class GantDataSource implements AutoCloseable{

    /**
     * 日志
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(GantDataSource.class);

    /**
     * 单例模式：保存数据库连接池子对象
     * 并不需要过多的连接池，不好管理其次还浪费，直接设置最大连接数或是增强一下单项队列不久行了嘛
     */
    private volatile static GantDataSource dataSource;

    /**
     * 空闲连接队列
     * 这样做的目的
     * 1.如果写一个连接的封装类（包含Connection对象，是否正在被使用的标识）
     * 使用遍历然后判断是否正在被使用，这样十分浪费内存和时间，开发还很麻烦
     * 2.如果使用两个容器，一个是储存空闲一个储存正在使用的连接，
     * 当一个连接在被领走了之后就加入到正在使用的容器当中，
     * 当使用者调用close方法归还连接时再将连接从从正在使用队列中删除并加入到空闲队列当中，
     * 这样就能够分开管理这两种状态的连接，同时方便不需要遍历直接取值和储存就行了
     * 3.至于使用阻塞队列而不是使用其他数据结构，是因为
     * 3.1除了初始化容器的操作之外基本没有遍历容器的操作（Array-1分）
     * 3.2对于连接在内存中是连续还是离散，连接池并不关💗（All-1分）
     * 3.3着重考虑是否拥有阻塞或同步的线程安全功能——考虑下效率（concurrent包下+1分）
     * 3.4更多的增和删操作（Array-1分，LinkedList+1分）
     * 3.5在单端队列和双端的对比,暂时没有那么大的需求需要双向存取（Deque）
     * 综上所述需要用LinkedBlockingQueue
     */
    private LinkedList<PoolConnection> freeQueue;

    /**
     * 正在占用连接队列
     */
    private LinkedList<PoolConnection> busyQueue;

    /**
     * 数据库连接信息
     */
    private Configuration configuration;

    /**
     * 连接创建工厂
     */
    private ConnectionFactory factory;

    /**
     * 配置文件构造器
     */
    private GantDataSource(String propertiesFile) {
        setConfiguration(new Configuration(propertiesFile));
        initPoolConnectList();
    }

    /**
     * 获得数据库连接池单例对象
     */
    public static GantDataSource getInstance(String propertiesFile) {
        if (dataSource == null) {
            synchronized (GantDataSource.class) {
                if (dataSource == null) {
                    dataSource = new GantDataSource(propertiesFile);
                }
            }
        }
        return dataSource;
    }

    /**
     * 初始化连接池子
     */
    private void initPoolConnectList() {
        LOGGER.debug("初始化数据库连接池");
        factory = ConnectionFactory.getInstance(this);
        //初始化各个队列
        initQueue();
        //填满空闲队列
        fillQueue();
        LOGGER.debug("初始化空闲连接池：" + freeQueue.size());
        LOGGER.debug("初始化占用连接池：" + busyQueue.size());
    }

    private void initQueue() {
        int maxCount = configuration.getMaxCount();
        freeQueue = new LinkedList<>();
        busyQueue = new LinkedList<>();
    }

    private void fillQueue() {
        Integer maxCount = configuration.getMaxCount();
        int minCount = configuration.getMinCount();
        for (int i = 0; i < minCount; i++) {
            freeQueue.add(factory.getConnection());
        }
        LOGGER.debug("初始化空闲连接池：" + freeQueue.getClass().getSimpleName() + "大小" + freeQueue.size());
    }

    /**
     * 取得连接
     */
    public PoolConnection getConnection() {
        PoolConnection connection = freeQueue.poll();
        if (isNeededCreate(connection)) {
            connection = factory.getConnection();
            LOGGER.debug(connection + "连接创建");
        }
        busyQueue.add(connection);
        LOGGER.debug(connection + "连接取出");
        LOGGER.debug(toString());
        return connection;
    }

    /**
     * 检测连接是否需要被创建
     */
    private boolean isNeededCreate(PoolConnection connection) {
        boolean flag = false;
        //连接为空并且在最大连接数之内
        if (connection == null && (busyQueue.size() + freeQueue.size()) <= configuration.getMaxCount()) {
            flag = true;
        } else if (configuration.getTestWhileIdle() && connection != null) {
            //需要检测该连接是否被数据库回收了
            long time = System.currentTimeMillis() - connection.getFreeTime();
            if (time >= configuration.getTimeBetweenEvictionRunsMillis()) {
                if (!isValid(connection)) {
                    flag = true;
                }
            }
        }
        return flag;
    }

    /**
     * 将连接从占用队列到空闲队列
     */
    public void close(PoolConnection connection) {
        if (busyQueue.remove(connection)) {
            freeQueue.add(connection);
            LOGGER.debug(connection + "连接归还");
        }
    }

    /**
     * 是否正在被占用
     */
    public boolean isBusy(Connection connection) {
        return busyQueue.contains(connection);
    }

    /**
     * 关闭整个连接池子,包括其中的连接
     * 如果整个连接正在执行中
     */
    @Override
    public void close() throws SQLException {
        LOGGER.debug("正在关闭数据库");
        if (freeQueue.size() > 0) {
            for (PoolConnection next : freeQueue) {
                next.getConnection().close();
                LOGGER.debug("关闭连接:" + next);
            }
            freeQueue.clear();
            LOGGER.debug("清空空闲队列:" + dataSource);
        }
        if (busyQueue.size() > 0) {
            for (PoolConnection next : busyQueue) {
                next.getConnection().close();
                LOGGER.debug("关闭连接:" + next);
            }
            busyQueue.clear();
            LOGGER.debug("清空占用队列:" + dataSource);
        }
    }

    /**
     * 释放资源，归还连接
     */
    public void release(PoolConnection connection, Statement statement, ResultSet resultSet) {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (connection != null) {
            close(connection);
        }
    }

    public void release(PoolConnection connection, Statement statement) {
        release(connection, statement, null);
    }

    /**
     * 连接是否有效，是否没有被数据库回收
     */
    public boolean isValid(PoolConnection connection) {
        boolean flag = false;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            Connection con = connection.getConnection();
            statement = con.prepareStatement("select 1");
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                flag = true;
            }
        } catch (SQLException e) {
            return false;
        } finally {
            release(connection, statement, resultSet);
            connection.setFreeTime(System.currentTimeMillis());
        }
        return flag;
    }

    /**
     * 回收超时连接
     */
    public void removeAbandoned() {
        if (configuration.getRemoveAbandoned()) {
            try {
                final Integer abandonedTimeout = configuration.getRemoveAbandonedTimeout();
                for (PoolConnection connection : busyQueue) {
                    long startTime = connection.getStartRunTime();
                    if (startTime > 0 && connection.getExecutionTime() == -1) {
                        if (System.currentTimeMillis() - startTime > abandonedTimeout) {
                            LOGGER.debug("回收连接：" + connection);
                            connection.getConnection().close();
                            busyQueue.remove(connection);
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
        LOGGER.debug("设置configuration属性");
    }

    private final Lock lock = new ReentrantLock();

    public void addConnection(Connection connection) {

    }

    public int getFreeSize() {
        return freeQueue.size();
    }

    public int getBusySize() {
        return busyQueue.size();
    }

    @Override
    public String toString() {
        return "GantDataSource{" +
                "空闲队列=" + freeQueue.size() +
                ", 占用队列=" + busyQueue.size() +
                '}';
    }
}
