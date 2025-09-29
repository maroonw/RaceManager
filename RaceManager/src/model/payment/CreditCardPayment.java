package model.payment;

import model.User;

public class CreditCardPayment implements PaymentStrategy {
    @Override
    public boolean pay(double amount, String payerName, String reference, String details) {
        System.out.printf("Paid $%.2f for %s via Credit Card (%s)%n", amount, reference, details);

        return true;
    }
}
