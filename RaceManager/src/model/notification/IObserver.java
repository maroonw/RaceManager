package model.notification;

// Observer interface for race notifications (Observer pattern)
public interface IObserver {
    // Updates for race events
    void update(String eventType, String data);
}
