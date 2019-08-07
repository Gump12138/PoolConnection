package com.illidan.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ResourceBundle;

/**
 * @author 甘明波
 * @date 2019-08-01
 */
public class Configuration {
    /**
     * 日志
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(Configuration.class);
    /**
     * 驱动类
     */
    private String driver;
    /**
     * 连接地址
     */
    private String url;
    /**
     * 登陆数据库用户名
     */
    private String username;
    /**
     * 登陆数据库密码
     */
    private String password;
    /**
     * 最大连接数
     */
    private Integer maxCount;
    /**
     * 最小连接数
     */
    private Integer minCount;
    /**
     * 最大获取连接等待时间
     */
    private Integer connectionTimeout;
    /**
     * 是否在获取连接时进行有效性检测
     */
    private Boolean testWhileIdle;
    /**
     * 检测连接是否有效的间隔时间
     */
    private Integer timeBetweenEvictionRunsMillis;
    /**
     * 是否需要回收执行时间过长的连接
     */
    private Boolean removeAbandoned;
    /**
     * 执行超时时间
     */
    private Integer removeAbandonedTimeout;

    /**
     * 传入配置文件文件名用于创建数据库连接
     */
    public Configuration(String file) {
        ResourceBundle bundle = ResourceBundle.getBundle(file);
        setDriver(bundle.getString("jdbc.driver"));
        setUrl(bundle.getString("jdbc.url"));
        setUsername(bundle.getString("jdbc.username"));
        setPassword(bundle.getString("jdbc.password"));
        setMaxCount(Integer.parseInt(bundle.getString("jdbc.maxCount")));
        setMinCount(Integer.parseInt(bundle.getString("jdbc.minCount")));
        setConnectionTimeout(Integer.parseInt(bundle.getString("jdbc.connectionTimeout")));
        setTimeBetweenEvictionRunsMillis(Integer.parseInt(bundle.getString("jdbc.timeBetweenEvictionRunsMillis")));
        setTestWhileIdle(Boolean.parseBoolean(bundle.getString("jdbc.testWhileIdle")));
        setRemoveAbandoned(Boolean.parseBoolean(bundle.getString("jdbc.removeAbandoned")));
        setRemoveAbandonedTimeout(Integer.parseInt(bundle.getString("jdbc.removeAbandonedTimeout")));
        LOGGER.debug("configuration对象创建by读取配置文件");
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getMaxCount() {
        return maxCount;
    }

    public void setMaxCount(Integer maxCount) {
        this.maxCount = maxCount;
    }

    public Integer getMinCount() {
        return minCount;
    }

    public void setMinCount(Integer minCount) {
        this.minCount = minCount;
    }

    public Integer getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(Integer connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public Integer getTimeBetweenEvictionRunsMillis() {
        return timeBetweenEvictionRunsMillis;
    }

    public void setTimeBetweenEvictionRunsMillis(Integer timeBetweenEvictionRunsMillis) {
        this.timeBetweenEvictionRunsMillis = timeBetweenEvictionRunsMillis;
    }

    public Boolean getTestWhileIdle() {
        return testWhileIdle;
    }

    public void setTestWhileIdle(Boolean testWhileIdle) {
        this.testWhileIdle = testWhileIdle;
    }

    public Boolean getRemoveAbandoned() {
        return removeAbandoned;
    }

    public void setRemoveAbandoned(Boolean removeAbandoned) {
        this.removeAbandoned = removeAbandoned;
    }

    public Integer getRemoveAbandonedTimeout() {
        return removeAbandonedTimeout;
    }

    public void setRemoveAbandonedTimeout(Integer removeAbandonedTimeout) {
        this.removeAbandonedTimeout = removeAbandonedTimeout;
    }
}
