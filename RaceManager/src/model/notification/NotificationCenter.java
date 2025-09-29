package model.notification;

import java.util.ArrayList;
import java.util.List;

public class NotificationCenter implements ISubject {
    private final List<IObserver> observers = new ArrayList<>();

    @Override
    public void attach(IObserver o) {
        observers.add(o);
    }

    @Override
    public void detach(IObserver o) {
        observers.remove(o);
    }

    @Override
    public void notify(String eventType, String data) {
        for (IObserver o : observers) o.update(eventType, data);
    }

    public void publish(String eventType, String data) {
        notify(eventType, data);
    }
}
