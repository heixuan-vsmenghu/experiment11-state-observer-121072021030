package com.lzl.experiment11.observer;

public class AlarmBell implements AlarmObserver {
    @Override
    public void update(double temperature) {
        System.out.printf("报警器接收到 %.1f℃ 高温信号。%n", temperature);
        alarm();
    }

    public void alarm() {
        System.out.println("报警器开始报警");
    }
}
