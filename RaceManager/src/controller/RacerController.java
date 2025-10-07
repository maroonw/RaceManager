package controller;

/**
* Handles all racer-related oprations
*
* Responsible for:
*   Racer registration and profile management
*   Race browsing and sign-up coordination with license validation
*   License purchase processing
*   Race review submission and validation
*   Category upgrade management
* 
* Uses the following notification systems:
*   {@link NotificationCenter} - for general notifications
*   (@link RaceNotifications) - for race-specific event notifications
*
* @see Racer
* @see RaceSystem
* @see NotificationCenter
* @see RaceNotifications
*
*/

import model.Race;
import model.Racer;
import model.License;
import model.RaceReview;
import model.RaceSystem;
import model.notification.NotificationCenter;
import model.notification.RaceNotifications;


import java.util.List;


public class RacerController {

    //private final RaceSystem raceSystem = new RaceSystem();
    private final RaceSystem raceSystem;
    private final NotificationCenter notificationCenter = new NotificationCenter();
    private final RaceNotifications raceNotifications = new RaceNotifications();

    public RacerController(RaceSystem raceSystem){
        this.raceSystem=raceSystem;
    }

    public void handleBrowseRaces(Racer racer) {
        if (racer != null) {
            racer.browseRaces();
            // Could return filtered races based on racer's category level
            notificationCenter.publish("BROWSE_RACES", "Racer " + racer.getName() + " browsing races");
        }
    }


    public void handleRaceSignUp(Racer racer, Race race) {
        if (racer != null && race != null) {
            if (racer.hasLicense()) {
                racer.raceSignUp(race);
                raceNotifications.publishRaceEvent("RACE_SIGNUP",
                        "Racer " + racer.getName() + " signed up for " + race.getRaceID());
            } else {
                raceNotifications.publishRaceEvent("SIGNUP_FAILED",
                        "Racer " + racer.getName() + " needs a license to sign up");
            }
        }
    }


    public License handlePurchaseLicense(Racer racer) {
        if (racer != null) {
            License license = racer.purchaseLicense();
            if (license != null) {
                notificationCenter.publish("LICENSE_PURCHASED",
                        "Racer " + racer.getName() + " purchased a license");
            }
            return license;
        }
        return null;
    }


    public void handleReviewRace(Racer racer, Race race, String comment, int rating) {
        if (racer != null && race != null) {
            racer.reviewRace(race, comment, rating);

            // Create and validate the review
            RaceReview review = new RaceReview();
            review.setReviewer(racer);
            review.setComment(comment);
            review.setRating(rating);

            if (review.isValidReview()) {
                raceNotifications.publishRaceEvent("REVIEW_SUBMITTED",
                        "Racer " + racer.getName() + " reviewed " + race.getRaceID());
            } else {
                raceNotifications.publishRaceEvent("REVIEW_FAILED",
                        "Invalid review submitted by " + racer.getName());
            }
        }
    }


    public void handleCategoryUpgrade(Racer racer) {
        if (racer != null) {
            int oldLevel = racer.getCatLevel();
            raceSystem.upgradeCategory(racer);
            int newLevel = racer.getCatLevel();

            if (newLevel > oldLevel) {
                notificationCenter.publish("CATEGORY_UPGRADED",
                        "Racer " + racer.getName() + " upgraded to category " + newLevel);
            }
        }
    }


    public void handleUpdateProfile(Racer racer, String creditCardInfo, int catLevel) {
        if (racer != null) {
            if (creditCardInfo != null) {
                racer.setCreditCardInfo(creditCardInfo);
            }
            if (catLevel > 0 && catLevel <= 5) {
                racer.setCatLevel(catLevel);
            }
            notificationCenter.publish("PROFILE_UPDATED",
                    "Racer " + racer.getName() + " updated profile");
        }
    }


    // Getters for notification wiring
    public NotificationCenter getNotificationCenter() {
        return notificationCenter;
    }

    public RaceNotifications getRaceNotifications() {
        return raceNotifications;
    }



    /**
     * Register a Racer directly (we know we're creating a Racer).
     * Returns the created Racer or null on error.
     */
    public Racer handleRegisterRacer(String name, String email, String password,
                                     String creditCardInfo, int catLevel) {
        if (name == null || email == null || password == null) {
            System.err.println("ERROR: name, email and password are required");
            return null;
        }
        // Basic validation (extend as needed)
        if (catLevel < 1 || catLevel > 5) catLevel = 5; // default beginner if invalid

        Racer racer = new Racer();
        // set User fields (inherited)
        racer.setName(name);
        racer.setEmail(email);
        racer.setPassword(password);
        racer.setUserType("racer");

        // set Racer specific fields
        racer.setCreditCardInfo(creditCardInfo);
        racer.setCatLevel(catLevel);

        // register in model
        raceSystem.registerUser(racer);
        System.out.println("SUCCESS: Racer registered: " + racer.getName() + " (" + racer.getEmail() + ")");
        return racer;
    }




}
