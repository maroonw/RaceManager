package view;

import controller.RaceController;

import java.util.Scanner;

public class PaymentView {
    private final RaceController controller;
    private Scanner scnr = new Scanner(System.in);
    public PaymentView(RaceController controller) { this.controller = controller; }

    public String selectMethod() {
        while (true) {
            System.out.println("Select Payment Type: [1] Credit\t [2] PayPal\t [3] Stripe");
            try {
                int num = Integer.parseInt(scnr.nextLine());
                String method = switch (num) {
                    case 1 -> "credit";
                    case 2 -> "paypal";
                    case 3 -> "stripe";
                    default -> {
                        System.out.println("Invalid number, try again.");
                        yield null;
                    }
                };
                if (method != null) {
                    return method; // only returns a valid method
                }
            } catch (NumberFormatException e) {
                System.out.println("Not a number. Try again.");
            }
        }
    }

    public String enterCardNumber() {
        while (true) {
            System.out.print("Enter card number: ");
            String input = scnr.nextLine();

            // Remove spaces and dashes
            String cleaned = input.replaceAll("[\\s-]", "");

            try {
                // Check if it’s numeric
                long num = Long.parseLong(cleaned);

                // Check length
                if (cleaned.length() != 16) {
                    System.out.println("A card number should be 16 digits.");
                } else {
                    return cleaned; // valid card number
                }
            } catch (NumberFormatException e) {
                System.out.println("Card number must contain only digits. Try again.");
            }
        }
    }

    public String enterPayPalEmail() {
        System.out.print("Enter PayPal Email: ");
        return scnr.nextLine();
    }
    public String enterStripeToken() {
        System.out.print("Enter Stripe Token: ");
        return scnr.nextLine();
    }

    public void showPaymentResult(boolean ok, String message) {
        System.out.println(ok ? "[OK] " + message : "[FAIL] " + message);
    }
}

