package com.illidan.connection.proxy;

import com.illidan.connection.GantStatement;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Statement;

/**
 * @author 甘明波
 * @date 2019-08-07
 */
public class ProxyStatement implements InvocationHandler {
    private GantStatement gantStatement;
    private Statement statement;

    public ProxyStatement(GantStatement gantStatement) {
        this.gantStatement = gantStatement;
        this.statement = gantStatement.getStatement();
    }

    public Object getProxyObject() {
        return Proxy.newProxyInstance(gantStatement.getClass().getClassLoader(),
                gantStatement.getClass().getInterfaces(),
                this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        final String methodName = method.getName();
        Object result;
        if ("setStartRunTime".equals(methodName) || "getStartRunTime".equals(methodName) || "setExecutionTime".equals(methodName) ||
                "getExecutionTime".equals(methodName)) {
            result = method.invoke(gantStatement, args);
        } else if (methodName.contains("execute")) {
            gantStatement.setStartRunTime(System.currentTimeMillis());
            result = method.invoke(statement, args);
            gantStatement.setExecutionTime(System.currentTimeMillis() - gantStatement.getStartRunTime());
            gantStatement.setStartRunTime(-1);
        } else {
            result = method.invoke(statement, args);
        }
        return result;
    }
}
