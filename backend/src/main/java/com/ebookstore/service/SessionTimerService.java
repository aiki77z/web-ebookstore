package com.ebookstore.service;

public   interface SessionTimerService {

    void start();

    long stopandGetElapsedTime();//停止计时器并返回当前时间

    long getElapsedTime();//不停止计时器返回当前时间

    boolean isRunning();//计时器是否运行
}