package view;


import controller.RacerController;
import model.Race;
import model.Racer;
import model.License;


import java.util.Scanner;

/**
*
* Handles the presentation layer for all racer-related user interface interactions.
*
* Responsible for:
*   Displaying interactive menus and prompts
*   Collecting and validating user input
*   Showing racer profile and race lists
*   Handling license purchase workflow and confirmation
*   Managing raace review submission interface
*   Displaying success messages, errors and notifications
*
* @see RacerController
* @see Racer
* @see Race
* @see License
*
*/

public class RacerView {

    private final RacerController controller;
    private final Scanner scanner = new Scanner(System.in);


    public RacerView(RacerController controller) {
        this.controller = controller;
    }


    public void displayRacerMenu(Racer racer) {
        if (racer == null) {
            System.out.println("Error: No racer provided");
            return;
        }

        System.out.println("\n=== Racer Menu for " + racer.getName() + " ===");
        System.out.println("1. Browse Races");
        System.out.println("2. Sign Up for Race");
        System.out.println("3. Purchase License");
        System.out.println("4. Review Race");
        System.out.println("5. View Profile");
        System.out.println("6. Update Profile");
        System.out.println("7. Exit");
        System.out.print("Select option: ");
    }


    public void displayAvailableRaces() {
        System.out.println("\n=== Available Races ===");
        System.out.println("1. Mountain Bike Challenge - Category 3+");
        System.out.println("2. Road Race Championship - Category 2+");
        System.out.println("3. Beginner Criterium - Category 1+");
        System.out.println("4. Advanced Time Trial - Category 4+");




        System.out.println("Browse complete. Use race signup to register.");
    }


    public Race selectRaceForSignup() {
        System.out.println("\n=== Race Signup ===");
        System.out.print("Enter Race ID to sign up for: ");
        String raceId = scanner.nextLine();

        // Create a mock race for demonstration
        Race race = new Race(raceId);
        System.out.println("Selected race: " + race.getRaceID());
        return race;
    }


    public void displayLicensePurchase() {
        System.out.println("\n=== License Purchase ===");
        System.out.println("License Cost: $50.00");
        System.out.println("Valid for 1 year");
        System.out.print("Confirm purchase? (y/n): ");
    }


    public boolean confirmLicensePurchase() {
        String response = scanner.nextLine();
        return response.toLowerCase().startsWith("y");
    }


    public void displayLicenseSuccess(License license) {
        if (license != null) {
            System.out.println("✓ License purchased successfully!");
            System.out.println("You can now sign up for races.");
        } else {
            System.out.println("✗ License purchase failed.");
        }
    }


    public Race selectRaceForReview() {
        System.out.println("\n=== Race Review ===");
        System.out.print("Enter Race ID to review: ");
        String raceId = scanner.nextLine();
        return new Race(raceId);
    }


    public String getReviewComment() {
        System.out.print("Enter your review comment: ");
        return scanner.nextLine();
    }


    public int getReviewRating() {
        System.out.print("Enter rating (1-5): ");
        try {
            int rating = Integer.parseInt(scanner.nextLine());
            if (rating >= 1 && rating <= 5) {
                return rating;
            } else {
                System.out.println("Invalid rating. Using default rating of 3.");
                return 3;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Using default rating of 3.");
            return 3;
        }
    }


    public void displayRacerProfile(Racer racer) {
        System.out.println("\n=== Racer Profile ===");
        System.out.println("Name: " + (racer.getName() != null ? racer.getName() : "Not set"));
        System.out.println("Category Level: " + racer.getCatLevel());
        System.out.println("License: " + (racer.hasLicense() ? "Valid" : "None"));
        System.out.println("Credit Card: " + (racer.getCreditCardInfo() != null ? "On file" : "Not set"));
    }


    public void displayProfileUpdateForm() {
        System.out.println("\n=== Update Profile ===");
    }


    public String getCreditCardInfo() {
        System.out.print("Enter credit card info (or press Enter to skip): ");
        String input = scanner.nextLine();
        return input.trim().isEmpty() ? null : input;
    }


    public int getCategoryLevel() {
        System.out.print("Enter category level (1-5, or 0 to skip): ");
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return 0; // Skip update
        }
    }


    public void displayMessage(String message) {
        System.out.println(message);
    }


    public void displayError(String error) {
        System.err.println("Error: " + error);
    }


    public Racer promptCreateRacer() {
        System.out.println("\n=== Create Racer ===");
        System.out.print("Enter name: ");
        String name = scanner.nextLine().trim();

        System.out.print("Enter email: ");
        String email = scanner.nextLine().trim();

        System.out.print("Enter password: ");
        String password = scanner.nextLine().trim();

        System.out.print("Enter credit card info (or press Enter to skip): ");
        String cc = scanner.nextLine().trim();
        if (cc.isEmpty()) cc = null;

        System.out.print("Enter category level (1-5) [default 5]: ");
        int cat;
        try {
            String s = scanner.nextLine().trim();
            cat = s.isEmpty() ? 5 : Integer.parseInt(s);
        } catch (NumberFormatException e) {
            cat = 5;
        }

        Racer created = controller.handleRegisterRacer(name, email, password, cc, cat);
        if (created == null) {
            System.out.println("ERROR: Racer not created.");
        }
        return created;
    }




}
