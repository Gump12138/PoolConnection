package com.illidan.connection.proxy;

import com.illidan.connection.GantConnection;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author 甘明波
 * @date 2019-08-06
 */
public class ProxyConnection implements InvocationHandler {

    private GantConnection gantConnection;
    private Connection connection;

    public ProxyConnection(GantConnection gantConnection) {
        this.gantConnection = gantConnection;
        try {
            this.connection = gantConnection.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Object getProxyObject() {
        return Proxy.newProxyInstance(gantConnection.getClass().getClassLoader(),
                gantConnection.getClass().getInterfaces(),
                this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String methodName = method.getName();
        Object result = null;
        if ("close".equals(methodName)) {
            gantConnection.getDataSource().close(gantConnection);
        } else if ("getFreeTime".equals(methodName) || "setFreeTime".equals(methodName) || "getStartRunTime".equals(methodName) || "createStatement".equals(methodName)) {
            result = method.invoke(gantConnection, args);
        } else {
            result = method.invoke(connection, args);
        }
        return result;
    }
}
