package com.illidan.thread;

import com.illidan.GantDataSource;
import com.illidan.connection.PoolConnection;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author 甘明波
 * @date 2019-08-02
 */
public class TestThread extends Thread {

    private GantDataSource dataSource;

    public TestThread(String name, GantDataSource dataSource) {
        super(name);
        this.dataSource = dataSource;
    }

    @Override
    public void run() {
        jdbc();
    }

    private void jdbc() {
        PoolConnection connection = null;
        Statement statement = null;
        ResultSet set = null;
        try {
            connection = dataSource.getConnection();
            statement = connection.createStatement();
            set = statement.executeQuery("show databases");
            set.next();
            System.out.println(Thread.currentThread().getName() + " result: " + set.getString(1));
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            dataSource.release(connection, statement, set);
            System.out.println(Thread.currentThread().getName() + " " + dataSource);
        }
    }
}
