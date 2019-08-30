package com.illidan.connection;

import java.sql.Statement;
import java.util.PriorityQueue;

/**
 * @author 甘明波
 * @date 2019-08-07
 */
public interface PoolStatement extends Statement {
    void setStartRunTime(long startRunTime);

    long getStartRunTime();

    void setStatement(Statement statement);

    Statement getStatement();

    void setExecutionTime(long executionTime);

    long getExecutionTime();
}
