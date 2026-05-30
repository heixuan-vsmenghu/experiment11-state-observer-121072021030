package com.lzl.experiment11.observer;

public class EscapeDoor implements AlarmObserver {
    @Override
    public void update(double temperature) {
        System.out.printf("安全逃生门接收到 %.1f℃ 高温信号。%n", temperature);
        open();
    }

    public void open() {
        System.out.println("安全逃生门自动开启");
    }
}
