package com.lzl.experiment11.observer;

import java.util.ArrayList;
import java.util.List;

public class TemperatureSensor implements Subject {
    private double temperature;
    private final double threshold;
    private final List<AlarmObserver> observers = new ArrayList<>();

    public TemperatureSensor(double threshold) {
        this.threshold = threshold;
    }

    @Override
    public void attach(AlarmObserver observer) {
        observers.add(observer);
        System.out.println("已注册响应设备：" + observer.getClass().getSimpleName());
    }

    @Override
    public void detach(AlarmObserver observer) {
        observers.remove(observer);
        System.out.println("已移除响应设备：" + observer.getClass().getSimpleName());
    }

    @Override
    public void notifyObservers() {
        System.out.println("当前温度超过阈值，通知所有响应设备……");
        for (AlarmObserver observer : observers) {
            observer.update(temperature);
        }
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
        System.out.printf("温度传感器检测到当前温度：%.1f℃%n", temperature);
        checkTemperature();
    }

    public void checkTemperature() {
        if (temperature >= threshold) {
            notifyObservers();
            return;
        }
        System.out.printf("当前温度未达到报警阈值 %.1f℃，系统保持正常监控。%n", threshold);
    }
}
