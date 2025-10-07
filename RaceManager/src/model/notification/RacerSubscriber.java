package model.notification;

import model.Racer;

// Racers who are subscribed to receive notifications
public class RacerSubscriber implements IObserver {
    private final String racerName;
    private final Racer racer;

    public RacerSubscriber(Racer racer) {
        this.racerName = racer.getName();
        this.racer = racer;
    }

    // Update to notify global or race-scoped subscribers (observers) of an event
    @Override
    public void update(String eventType, String data) {
        racer.receiveNotification("Event Type: " + eventType + "\nData: " + data + "\n");
    }
}
