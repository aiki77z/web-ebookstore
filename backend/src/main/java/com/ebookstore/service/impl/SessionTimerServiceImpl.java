package com.ebookstore.service.impl;

import com.ebookstore.service.SessionTimerService;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

@Service
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class SessionTimerServiceImpl implements SessionTimerService {
private Instant startTime;

    @Override
    public void start() {
        this.startTime=Instant.now();
    }

    @Override
    public long stopandGetElapsedTime() {
        if(startTime==null){
            return 0L;
        }
        Duration duration=Duration.between(startTime,Instant.now());
        long elapsedTime=duration.toMillis();
        this.startTime=null;
        return elapsedTime;
    }

    @Override
    public long getElapsedTime() {
        if (startTime == null) {
            return 0L;
        }
        return Duration.between(startTime, Instant.now()).toMillis();
    }

    @Override
    public boolean isRunning() {
        return startTime != null;
    }
}
