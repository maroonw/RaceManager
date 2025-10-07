package controller;

import model.Race;
import model.RaceSystem;
import model.Racer;
import model.payment.*;
import model.notification.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import java.util.List;
import model.Race;
import model.RaceSystem;



public class RaceController {

    private final PaymentService paymentService = new PaymentService();
    private final NotificationCenter notificationCenter = new NotificationCenter();
    // For per-race events, you’d manage a map<RaceId, RaceNotifications>
    private final RaceNotifications raceNotifications = new RaceNotifications();

    //private final RaceSystem raceSystem = new RaceSystem();
    private final RaceSystem raceSystem;
    private final SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

    public RaceController(RaceSystem raceSystem) {
        sdf.setLenient(false);
        this.raceSystem=raceSystem;

    }



    public void handleBrowseRaces() { }

    public void handleRaceSignUp(int raceId, int userId) {
        // TODO: fetch Race, Racer; register; notify:
        raceNotifications.publishRaceEvent("SIGNUP_CONFIRMED", "Racer " + userId + " -> Race " + raceId);
    }

    public void handleSelectPaymentAndPay(String method, String details, int raceId, int userId) {
        // choose strategy
        switch (method.toLowerCase()) {
            case "credit" -> paymentService.setStrategy(new CreditCardPayment());
            case "paypal" -> paymentService.setStrategy(new PayPalPayment());
            case "stripe" -> paymentService.setStrategy(new StripePayment());
            default -> throw new IllegalArgumentException("Unknown payment method: " + method);
        }
        // minimal demo values
        boolean ok = paymentService.process(45.00, new Racer(), "Race#" + raceId, details);

        if (ok) {
            raceNotifications.publishRaceEvent("PAYMENT_SUCCESS", "User " + userId + " paid for Race " + raceId);
        } else {
            raceNotifications.publishRaceEvent("PAYMENT_FAILED", "User " + userId + " failed payment for Race " + raceId);
        }
    }

    // exposure for wiring subscribers (e.g., from View)
    public NotificationCenter getNotificationCenter() { return notificationCenter; }
    public RaceNotifications getRaceNotifications() { return raceNotifications; }

    //******************
    //private final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    public boolean handleCreateRace(String dateStr, String type, String milesStr, String route,
                                    String officialStr, String limitStr, String lastRegStr, String catStr) {
        try {
            Date raceDate = sdf.parse(dateStr);
            if (raceDate.before(new Date())) {
                System.out.println("ERROR: Race date must be in the future.");
                return false;
            }
            double miles = Double.parseDouble(milesStr);
            if (miles <= 0) {
                System.out.println("ERROR: Miles must be greater than 0.");
                return false;
            }
            int limit = Integer.parseInt(limitStr);
            if (limit <= 0) {
                System.out.println("ERROR: Participant limit must be greater than 0.");
                return false;
            }
            Date lastRegDate = sdf.parse(lastRegStr);
            if (lastRegDate.after(raceDate)) {
                System.out.println("ERROR: Last registration date must be before race date.");
                return false;
            }
            int catRequired = Integer.parseInt(catStr);
            if (catRequired < 1 || catRequired > 5) {
                System.out.println("ERROR: Category must be between 1 and 5.");
                return false;
            }
            boolean official = Boolean.parseBoolean(officialStr);

            Race race = raceSystem.createRace(raceDate, type, miles, route,
                    official, limit, lastRegDate, catRequired);
            System.out.println("SUCCESS: " + race);
            return true;

        } catch (ParseException e) {
            System.out.println("ERROR: Invalid date format. Use MM/dd/yyyy.");
            return false;
        } catch (NumberFormatException e) {
            System.out.println("ERROR: Invalid number format.");
            return false;
        }
    }
    //******************


    // gettter to return all races
    public List<model.Race> getAllRaces() {
            return raceSystem.getAllRaces();
    }

}

