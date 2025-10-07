package model.notification;

import java.util.ArrayList;
import java.util.List;

// Notifications for race-scoped subscribers (observers)
public class RaceNotifications implements ISubject {
    private final List<IObserver> observers = new ArrayList<>();

    // Adds race-scoped subscribers (observers) to be notified
    @Override
    public void attach(IObserver o) {
        // Validates that a new race-scoped subscriber (observer) is added
        if (o != null && !observers.contains(o))
            observers.add(o);
    }

    // Removes race-scoped subscribers (observers) from being notified
    @Override
    public void detach(IObserver o) {
        observers.remove(o);
    }

    // Notifies attached race-scoped subscribers (observers)
    @Override
    public void notify(String eventType, String data) {
        // Checks for null eventTypes and data
        if (eventType == null || data == null)
            return;

        for (IObserver o : observers)
            o.update(eventType, data);
    }

    // Publishes a race event to race-scoped subscribers (observers)
    public void publishRaceEvent(String eventType, String data) {
        notify(eventType, data);
    }
}
