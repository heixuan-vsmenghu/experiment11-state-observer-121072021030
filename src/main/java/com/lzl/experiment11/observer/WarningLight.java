package com.lzl.experiment11.observer;

public class WarningLight implements AlarmObserver {
    @Override
    public void update(double temperature) {
        System.out.printf("警示灯接收到 %.1f℃ 高温信号。%n", temperature);
        flash();
    }

    public void flash() {
        System.out.println("警示灯开始闪烁");
    }
}
