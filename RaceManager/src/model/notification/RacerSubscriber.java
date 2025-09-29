package model.notification;

import model.Racer;

public class RacerSubscriber implements IObserver {
    private String racerName;
    private Racer racer;

    public RacerSubscriber(Racer racer) {
        this.racerName = racer.getName();
        this.racer = racer;
    }

    @Override
    public void update(String eventType, String data) {
        racer.receiveNotification("Event Type: " + eventType + "\nData: " + data + "\n");
    }
}
