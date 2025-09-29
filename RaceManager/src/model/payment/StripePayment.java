package model.payment;

import model.User;

public class StripePayment implements PaymentStrategy {
    @Override
    public boolean pay(double amount, model.User payer, String reference, String details) {
        System.out.printf("Paid $%.2f for %s via Stripe (%s)%n", amount, reference, details);

        return true;
    }
}
