package model.notification;

public interface ISubject {
    void attach(IObserver o);
    void detach(IObserver o);
    void notify(String eventType, String data);
}
