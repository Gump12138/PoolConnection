package com.illidan;

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
    private Integer maxWait;

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
        setMaxWait(Integer.parseInt(bundle.getString("jdbc.maxWait")));
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

    public Integer getMaxWait() {
        return maxWait;
    }

    public Configuration setMaxWait(Integer maxWait) {
        this.maxWait = maxWait;
        return this;
    }
}
