package com.illidan;

import org.junit.Test;

/**
 * @author 甘明波
 * @date 2019-08-02
 */
public class JdbcTest {
    @Test
    public void selectTest() {
        GantDataSource dataSource = GantDataSource.getInstance("jdbc");
        for (int i = 0; i < 2; i++) {
            new Thread(new JdbcThread(dataSource)).start();
        }
    }
}
