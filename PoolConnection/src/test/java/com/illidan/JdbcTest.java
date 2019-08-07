package com.illidan;

import com.illidan.connection.PoolConnection;
import com.illidan.thread.TestThread;
import org.junit.Test;

import java.sql.SQLException;

/**
 * 需要配合数据库关闭
 *
 * @author 甘明波
 * @date 2019-08-02
 */
public class JdbcTest {
    @Test
    public void selectTest() {
        GantDataSource dataSource = null;
        try {
            dataSource = GantDataSource.getInstance("jdbc");
            for (int i = 0; i < 2; i++) {
                new TestThread("线程" + i, dataSource).start();
            }
            dataSource.removeAbandoned();
            Thread.sleep(1000);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (dataSource != null) {
                try {
                    dataSource.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 查看数据库连接的超时时间（再获取连接之后，数据库会在指定的超时时间之后将连接自动断开——经典8小时问题）
     * show global variables like "%timeout%";
     * 修改了数据库的wait_timeout的值为3秒
     * set global wait_timeout = 3;
     * 连接池子新增方法：测试连接是否被断开
     */
    @Test
    public void heartBomBom() {
        int time = 2000;
        GantDataSource dataSource = null;
        try {
            dataSource = GantDataSource.getInstance("jdbc");
            PoolConnection connection = dataSource.getConnection();
            System.out.println("获得连接，并等待数据库关闭连接");
            Thread.sleep(time);
            System.out.println("等待时间为：" + time);
            boolean valid = dataSource.isValid(connection);
            System.out.println("连接是否还有效" + valid);
            Thread.sleep(1000);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (dataSource != null) {
                try {
                    dataSource.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
