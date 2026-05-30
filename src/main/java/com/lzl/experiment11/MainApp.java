package com.lzl.experiment11;

import com.lzl.experiment11.observer.ObserverTaskDemo;
import com.lzl.experiment11.state.Experiment11Demo;

public class MainApp {
    public static void main(String[] args) {
        System.out.println("========== 课堂任务：观察者模式 ==========");
        ObserverTaskDemo.run();

        System.out.println();
        System.out.println("========== 实验11：状态模式 ==========");
        Experiment11Demo.run();
    }
}
