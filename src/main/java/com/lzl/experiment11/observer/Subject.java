package com.lzl.experiment11.observer;

public interface Subject {
    void attach(AlarmObserver observer);

    void detach(AlarmObserver observer);

    void notifyObservers();
}
