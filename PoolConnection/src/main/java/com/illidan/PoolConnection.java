package com.illidan;

import javax.sql.PooledConnection;
import java.sql.Connection;

/**
 * @author 甘明波
 * @date 2019-08-06
 */
public interface PoolConnection extends Connection, PooledConnection {
}
