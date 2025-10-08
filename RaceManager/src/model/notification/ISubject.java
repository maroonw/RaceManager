package model.notification;

// Observable (Subject) interface (Observer pattern)
public interface ISubject {
    // Adds observers to be notified
    void attach(IObserver o);
    // Removes observers from being notified
    void detach(IObserver o);
    // Notifies attached observers
    void notify(String eventType, String data);
}
