package controller;


import model.*;
import model.notification.*;
import model.payment.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.*;



public class RaceController {

    private static final double LICENSE_FEE = 100.00;


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
    public boolean trySelectPaymentAndPay(String method, String details, double amount, int raceId, int userId) {
        if (amount <= 0.0) return true;

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
    public boolean isEligible(model.Racer racer, model.Race race) {
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

    public boolean handleSignUpFlow(model.Racer racer, String raceId, String method, String details) {
        model.Race race = raceSystem.getRaceById(raceId);
        if (race == null) {
            System.out.println("Race not found: " + raceId);
            return false;
        }

        // Check eligibility and spots
        if (!isEligible(racer, race) || !raceSystem.hasSpots(race)) {
            raceNotifications.publishRaceEvent("SELECT_DIFFERENT_RACE",
                    "Not eligible or no spots for " + raceId);
            return false;
        }

        // calculate what the total due is
        double total = getTotalDue(racer, race);
        boolean needsLicense = race.isOfficialRace() && !racer.hasLicense();

        // unofficial or license already paid
        if (total <= 0.0) {
            boolean ok = finalizeRegistration(racer, raceId);
            if (ok) {
                raceNotifications.publishRaceEvent("RECEIPT_SENT",
                        "Receipt for " + racer.getName() + " — total $0.00");
            }
            return ok;
        }

        // pay for a license one time only
        boolean paid = trySelectPaymentAndPay(method, details, total, Integer.parseInt(raceId), /*userId*/ 0);
        if (!paid) {
            // caller (MainMenuView) will re-prompt the method on false
            return false;
        }

        // good payment, issue license, and notification
        if (needsLicense) {
            issueLicenseIfNeeded(racer, race);
        }

        boolean ok = finalizeRegistration(racer, raceId);
        if (ok) {
            raceNotifications.publishRaceEvent("RECEIPT_SENT",
                    "Receipt for " + racer.getName() + " — total $" + String.format("%.2f", total));
        }
        return ok;
    }


    //show official vs unofficial without breaking into the model classes directly from view
    public model.Race getRaceById(String id) {
        return raceSystem.getRaceById(id);
    }

    //issue license
    private void issueLicenseIfNeeded(Racer racer, Race race) {
        if (race.isOfficialRace() && !racer.hasLicense()) {
            model.License license = new License();
            license.setLicenseId("LIC" + System.currentTimeMillis());
            license.setExpiration(new Date(System.currentTimeMillis() + 365L*24*60*60*1000));
            racer.setLicense(license);
            System.out.println("License issed to " + racer.getName());
        }
    }

    // reserve spot and send confirmation and receipt notifications
    public boolean finalizeRegistration(model.Racer racer, String raceId) {
        model.Race race = raceSystem.getRaceById(raceId);
        if (race == null) return false;

        if (!raceSystem.reserveSpot(race)) {
            raceNotifications.publishRaceEvent("SELECT_DIFFERENT_RACE",
                    "Race " + raceId + " filled before finalization");
            return false;
        }

        raceNotifications.publishRaceEvent("SIGNUP_CONFIRMED",
                "Racer " + racer.getName() + " registered for " + raceId);
        return true;
    }


    // ge the total due
    public double getTotalDue(Racer racer, Race race) {
        if (racer == null || race == null) return 0;
        if (!race.isOfficialRace()) return 0;
        return racer.hasLicense() ? 0.0 : LICENSE_FEE;
    }
}

