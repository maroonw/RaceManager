package view;

import app.DataSeeder;
import controller.RaceController;
import controller.ResultController;
import model.Race;
import view.PaymentView;
import model.RaceResult;
import model.Racer;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

public class MainMenuView {
    private final UserView userView;
    private final RaceView raceView;
    private final ResultView resultView;
    private final ReviewView reviewView;
    private final RacerView racerView;

    // start demo wire, optional only used if setDemoContext is used
    private DataSeeder.Seed seed;
    private RaceController demoRaceController;
    private ResultController demoResultController;
    private PaymentView demoPaymentView;

    public void setDemoContext(DataSeeder.Seed seed,
                               RaceController raceController,
                               ResultController resultController,
                               PaymentView paymentView) {
        this.seed = seed;
        this.demoRaceController = raceController;
        this.demoResultController = resultController;
        this.demoPaymentView = paymentView;
    }
    // end of demo wiring

    // start demo methods

    private void demoBrowseRaces() {
        if (seed == null || demoRaceController == null || demoPaymentView == null) {
            System.out.println("[demo] Not configured");
            return;
        }
        System.out.println("\n[Demo] Browse Races + Pay");

        // show two seeded races
        raceView.displayRaceList(Arrays.asList(seed.crit, seed.tt));

        // Handle Payment
        String method = demoPaymentView.selectMethod();
        String details;
        if ("credit".equalsIgnoreCase(method)) {
            details = demoPaymentView.enterCardNumber();
        } else if ("paypal".equalsIgnoreCase(method)) {
            details = demoPaymentView.enterPayPalEmail();
        } else {
            details = demoPaymentView.enterStripeToken();
        }

        int raceIdAsInt = Integer.parseInt(seed.crit.getRaceID());
        int userId = 1; // demo user
        demoRaceController.handleSelectPaymentAndPay(method, details, raceIdAsInt, userId);
        demoRaceController.handleRaceSignUp(raceIdAsInt, userId);
    }

    private void demoViewResults() {
        if (seed == null || demoResultController == null) {
            System.out.println("[demo] Not configured");
            return;
        }

        System.out.println("\n[Demo] Post & View Results");

        RaceResult rr = new RaceResult(seed.crit);
        Map<String, String> times = new LinkedHashMap<>();
        times.put(seed.alice.getName(), "00:42:10");

        rr.setTimes(times);
        rr.setPodium(new Racer[] { seed.alice, null, null });

        // notify subscribers and display
        demoResultController.handleEnterResults(rr);
        resultView.displayResults(rr);
    }
// end demo methods


    public MainMenuView(UserView userView, RaceView raceView, ResultView resultView, ReviewView reviewView, RacerView racerView) {
        this.userView = userView;
        this.raceView = raceView;
        this.resultView = resultView;
        this.reviewView = reviewView;
        this.racerView=racerView;
    }

    public void showMenu() {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;
        while (running) {
            System.out.println("Menu:");

            System.out.println("0. Create Race");
            System.out.println("1. Browse Races");
            System.out.println("2. Create Racer");
            System.out.println("3. View Results");
            System.out.println("4. Submit Review");
            System.out.println("5. View Reviews");
            System.out.println("6. Logout");
            System.out.println("7. Strategy Payment demo");
            System.out.println("8. Observer Notification demo");
            System.out.print("Enter your choice: ");
            String input = scanner.nextLine();
            int choice;
            try {
                choice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
                continue;
            }
            switch (choice) {
                case 0:
                    raceView.promptCreateRace();
                    break;
                case 1:
                    raceView.displayRaceList();
                    String raceId = raceView.enterRaceId();
                    if (raceId == null) {
                        break;
                    }

                    // must be logged in to proceed
                    var current = userView.getController().getCurrentUser();
                    if (!(current instanceof model.Racer racer)) {
                        System.out.println("Please log in as a Racer to sign up.");
                        break;
                    }

                    // make sure set up demoPaymentView and demoRaceController
                    if (demoPaymentView == null || demoRaceController == null) {
                        System.out.println("Payment or Race Controller not set up");
                        break;
                    }

                    // show official vs unofficial (let th user know why they are putting in payment info)
                    Race selected = demoRaceController.getRaceById(raceId);
                    if (selected == null) {
                        System.out.println("Race not found");
                        break;
                    }

                    boolean isOfficial = selected.isOfficialRace();

                    System.out.println("Selected Race: " + selected.getRaceID());
                    System.out.println("Race is official: " + isOfficial);

                    if (isOfficial) {
                        System.out.println("A license is required.");
                        System.out.println("You have needed license: " + (racer.hasLicense() ? "Yes" : "No"));
                    } else {
                        System.out.println("This is an unoffical race.");
                    }

                    // payments process (loop)
                    boolean paid = false;
                    while (!paid) {
                        String method = demoPaymentView.selectMethod();
                        String details;
                        if ("credit".equalsIgnoreCase(method)) {
                            details = demoPaymentView.enterCardNumber();
                        } else if ("paypal".equalsIgnoreCase(method)) {
                            details = demoPaymentView.enterPayPalEmail();
                        } else {
                            details = demoPaymentView.enterStripeToken();
                        }

                        paid = demoRaceController.handleSignUpFlow(racer, raceId, method, details);
                        if (!paid) {
                            System.out.println("Payment declined or race unavailable");
                            System.out.print("Try another payment method? (y to retry, else cancel");
                            Scanner scr = new Scanner(System.in);
                            String tryAgain = scr.nextLine().trim();
                            if (tryAgain.equalsIgnoreCase("y")) {
                                break;
                            }
                        }
                    }
                    break;

                case 2:
                    racerView.promptCreateRacer();
                    System.out.print("Would you like to sign up for a race? (y/n)");
                    String response = scanner.nextLine();
                    if (response.equalsIgnoreCase("y")) {
                        userView.signUpForRace();
                    } else {
                        break;
                    }
                    break;
                case 3:
                    //System.out.println("Enter raceID: ");
                    // Controller needs to handle converting raceID String to actual RaceResult
                    //resultView.displayResults(...);
                    break;
                case 4:
                    //reviewView.submitReview();
                    var race1 = racerView.selectRaceForReview();
                    String comment = racerView.getReviewComment();
                    int rating = racerView.getReviewRating();
                    break;
                case 5:
                    // Reviews need to be obtained
                    //reviewView.displayReviews(getReviews);
                    break;
                case 6:
                    //running = false;
                    System.out.println("Logging Out... Goodbye!");
                    // goes back to login screen, to go back to just exiting un comment above, comment below
                    userView.logoutToLogin();
                    break;
                case 7:
                    demoBrowseRaces();
                    break;
                case 8:
                    demoViewResults();
                    break;
                default:
                    System.out.println("Invalid choice. Please select a valid option.");
            }
            System.out.println();
        }
    }
}
