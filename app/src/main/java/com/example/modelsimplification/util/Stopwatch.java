package com.example.modelsimplification.util;

/**
 * 秒表类，用来测量程序运行的时间
 * Created by lenovo on 2017/5/21.
 */

public class Stopwatch {

    private final long start;

    /**
     * 初始化一个秒表并开始计时
     */
    public Stopwatch() {
        start = System.currentTimeMillis();
    }

    /**
     * 返回从秒表创建开始到现在的时间，单位：ms
     * @return
     */
    public int elapsedTimeMs() {
        long now =  System.currentTimeMillis();
        return (int) (now - start);
    }

    /**
     * 返回从秒表创建开始到现在的时间，单位：s
     * @return
     */
    public double elapsedTime() {
        return elapsedTimeMs() / 1000.0;
    }
}
