package com.xgj.phoneguardian.manager;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author 郭宝
 * @project： PhoneGuardian
 * @package： com.xgj.phoneguardian.manager
 * @date： 2017/7/31 10:51
 * @brief: 线程池
 */
public class ThreadManager {



    //利用单例模式中的懒汉式设计线程池（以确保内存中只有一个对象，节约系统资源同时提高系统的性能）
    //2.声明一个静态的ThreadPool类以确保对象唯一，private是为了防止外界修改ThreadPool类对象的值
    private static ThreadPool mThreadPool;

    //3.向外界暴露一个实例化ThreadPool类的方法
    public static ThreadPool getThreadPool() {
        //4.判断对象为空时就创建一个ThreadPool类的对象以确保该类只创建一次
        if (mThreadPool == null) {
            //6.同步锁是为了防止多个线程同时进入而导致同时创建多个对象的BUG
            synchronized (ThreadManager.class) {
                if (mThreadPool == null) {
                    int cpuCount = Runtime.getRuntime().availableProcessors();// 获取cpu数量
                    System.out.println("cup个数:" + cpuCount);

                    // int threadCount = cpuCount * 2 + 1;//线程个数
                    int threadCount = 10;
                    mThreadPool = new ThreadPool(threadCount, threadCount, 1L);
                }
            }
        }

        //5.如果mThreadPool对象不为空那么就返回全局已经创建了的对象，共享这一个对象即可
        return mThreadPool;
    }

    // 线程池
    public static class ThreadPool {

        private int corePoolSize;// 核心线程数
        private int maximumPoolSize;// 最大线程数
        private long keepAliveTime;// 休息时间

        private ThreadPoolExecutor executor;

        //1.ThreadPool类的构造私有化使外界无法实例化该类
        private ThreadPool(int corePoolSize, int maximumPoolSize,
                           long keepAliveTime) {
            this.corePoolSize = corePoolSize;
            this.maximumPoolSize = maximumPoolSize;
            this.keepAliveTime = keepAliveTime;
        }

        // 线程池几个参数的理解:
        // 比如去火车站买票, 有10个售票窗口, 但只有5个窗口对外开放. 那么对外开放的5个窗口称为核心线程数,
        // 而最大线程数是10个窗口.
        // 如果5个窗口都被占用, 那么后来的人就必须在后面排队, 但后来售票厅人越来越多, 已经人满为患, 就类似于线程队列已满.
        // 这时候火车站站长下令, 把剩下的5个窗口也打开, 也就是目前已经有10个窗口同时运行. 后来又来了一批人,
        // 10个窗口也处理不过来了, 而且售票厅人已经满了, 这时候站长就下令封锁入口,不允许其他人再进来, 这就是线程异常处理策略.
        // 而线程存活时间指的是, 允许售票员休息的最长时间, 以此限制售票员偷懒的行为.
        public void execute(Runnable r) {
            if (executor == null) {
                executor = new ThreadPoolExecutor(corePoolSize,
                        maximumPoolSize, keepAliveTime, TimeUnit.SECONDS,
                        new LinkedBlockingQueue<Runnable>(),
                        Executors.defaultThreadFactory(), new ThreadPoolExecutor.AbortPolicy());
                // 参1:核心线程数;参2:最大线程数;参3:线程休眠时间;参4:时间单位;参5:线程队列;参6:生产线程的工厂;参7:线程异常处理策略
            }

            // 线程池执行一个Runnable对象, 具体运行时机线程池说了算
            executor.execute(r);
        }

        // 取消任务
        public void cancel(Runnable r) {
            if (executor != null) {
                // 从线程队列中移除对象
                executor.getQueue().remove(r);
            }
        }

    }


}
