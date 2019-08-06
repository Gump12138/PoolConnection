package com.illidan;

import org.junit.Test;

import java.sql.SQLException;

/**
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
                new JdbcThread("线程" + i,dataSource).start();
            }
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
