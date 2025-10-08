package controller;


import model.*;
import model.notification.*;
import model.payment.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.*;



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

    // handling payments for notification purposes
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

    //boolean to tell if paymetn is successful
    public boolean trySelectPaymentAndPay(String method, String details, int raceId, int userId) {
        if (method == null) return false;

        switch (method.toLowerCase()) {
            case "credit" -> paymentService.setStrategy(new CreditCardPayment());
            case "paypal" -> paymentService.setStrategy(new PayPalPayment());
            case "stripe" -> paymentService.setStrategy(new StripePayment());
            default -> {
                throw new IllegalArgumentException("Unknown payment method: " + method);
            }
        }

        String payer = "User#" + userId;
        boolean ok = paymentService.process(45.00, new Racer(), "Race#" + raceId, details);

        if (ok) {
            raceNotifications.publishRaceEvent("PAYMENT_SUCCESS", payer + " paid for Race " + raceId);
        } else {
            raceNotifications.publishRaceEvent("PAYMENT_DECLINED", payer + " failed payment for Race " + raceId);
        }
        return ok;
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

    // eligibility based on cat level of race and racer
    public boolean isEligble(model.Racer racer, model.Race race) {
        if (racer == null || race == null) {
            return false;
        }
        return racer.getCatLevel() >= race.getCatRequired();
    }

    // total fee, race fee + license fee
    public double calculateTotal(Racer racer, Race race) {
        double raceFee = 50.00;
        double licenseFee = (race.isOfficialRace() && !racer.hasLicense()) ? 100.0 : 0.0;
        return raceFee + licenseFee;
    }

    // sign up flow, we can change this around to match activity diagram if we need to change something

    public boolean handleSignUpFlow(Racer racer, String raceId, String method, String details) {
        Race race = raceSystem.getRaceByID(raceId);
        if (race == null) {
            System.out.println("ERROR: Race not found.");
            return false;
        }

        // check eligibility and spots available (notification pattern usage)
        if (!isEligble(racer, race) || !raceSystem.hasSpots(race)) {
            raceNotifications.publishRaceEvent("SELECT_DIFFERENT_RACE",
                    "Not eligible or no spots available for " + raceId);
            return false;
        }

        // official vs unofficial
        boolean licenseNeeded = race.isOfficialRace() && !racer.hasLicense();
        double total = calculateTotal(racer, race);

        //payment using PaymentService (builder patter usage)
        boolean pay;
        try {
            pay = trySelectPaymentAndPay(method, details, Integer.parseInt(raceId), 0);
        } catch (Throwable t) {
            pay = false;
        }

        if (!pay) {
            raceNotifications.publishRaceEvent("PAYMENT_DECLINED", "Payment declined for: " + raceId);
            return false;
        }

        // issue license if just paid for, if not this is skipped
        if (licenseNeeded) {
            License license = new License();
            license.setLicenseId("LIC" + System.currentTimeMillis());
            license.setExpiration(new Date(System.currentTimeMillis() + 365L*24*60*60*1000));
            racer.setLicense(license);
        }

        // finalize and notification split on activity diagram, both must happen
        if (!raceSystem.hasSpots(race)) {
            raceNotifications.publishRaceEvent("SELECT_DIFFERENT_RACE",
            "Race " + raceId + " filled up while you were typing.");
        return false;
        }

        raceNotifications.publishRaceEvent("SIGNUP_CONFIRMED", "Signup confirmed for: " + raceId);
        raceNotifications.publishRaceEvent("RECEIPT_CONFIRMED", "Receipt confirmed for: " + raceId);
        return true;
    }

    //show official vs unofficial without breaking into the model classes directly from view
    public model.Race getRaceById(String id) {
        return raceSystem.getRaceByID(id);
    }
}

