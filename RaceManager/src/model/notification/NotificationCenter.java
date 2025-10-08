package model.notification;

import java.util.ArrayList;
import java.util.List;

// Notifications for global subscribers (observers)
public class NotificationCenter implements ISubject {
    // List of all global subscribers (observers)
    private final List<IObserver> observers = new ArrayList<>();

    // Adds global subscribers (observers) to be notified
    @Override
    public void attach(IObserver o) {
        // Validates that a new global subscriber (observer) is added
        if (o != null && !observers.contains(o))
            observers.add(o);
    }

    // Removes global subscribers (observers) from being notified
    @Override
    public void detach(IObserver o) {
        observers.remove(o);
    }

    // Notifies attached global subscribers (observers)
    @Override
    public void notify(String eventType, String data) {
        // Checks for null eventTypes and data
        if (eventType == null || data == null)
            return;

        for (IObserver o : observers)
            o.update(eventType, data);
    }

    // Publishes event to global subscribers (observers)
    public void publish(String eventType, String data) {
        notify(eventType, data);
    }
}
