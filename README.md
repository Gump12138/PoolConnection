# PoolConnection
## *Illidan PoolConnection*
# 伊利丹。怒风  数据库连接池子
*****
**1.管理数据库连接**

   1.1基本做到获得连接和最大连接最小连接
*****
**2.监控sql**

   2.1什么也没做，思路：使用静态代理Connection...对象然后收集其中的数据
*****
**3.基于消费者生产者模型**

   3.1什么也没做，思路：生成者负责生产连接，消费者负责吃掉连接。消费者吃完，唤醒生产者生产。生产者吃完，唤醒消费者
   
   ### 实现流程：
   1.初始化连接池
        
        1.通过加载jdbc.properties文件进内存，然后读取其中属性并存储在com.illidan.config.Configuration类中。
        2.然后调用生产者线程（线程生产工厂）来初始化数据库连接池
        3.加载驱动并调用DriverManneger.getConnection()存入LinkedList中
        
   2.获取连接
   
        1.每一个获取连接的方法中都有Synchronized同步关键字
        2.
