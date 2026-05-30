package com.lzl.experiment11.observer;

public class ObserverTaskDemo {
    private ObserverTaskDemo() {
    }

    public static void run() {
        TemperatureSensor sensor = new TemperatureSensor(60.0);
        AlarmObserver light = new WarningLight();
        AlarmObserver bell = new AlarmBell();
        AlarmObserver escapeDoor = new EscapeDoor();
        AlarmObserver insulationDoor = new InsulationDoor();

        sensor.attach(light);
        sensor.attach(bell);
        sensor.attach(escapeDoor);
        sensor.attach(insulationDoor);

        System.out.println();
        sensor.setTemperature(80.0);
    }
}
