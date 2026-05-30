package com.lzl.experiment11.observer;

public class InsulationDoor implements AlarmObserver {
    @Override
    public void update(double temperature) {
        System.out.printf("隔热门接收到 %.1f℃ 高温信号。%n", temperature);
        close();
    }

    public void close() {
        System.out.println("隔热门自动关闭");
    }
}
