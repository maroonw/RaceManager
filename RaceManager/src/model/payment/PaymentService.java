package model.payment;

import model.User;

public class PaymentService {
    private PaymentStrategy strategy;

    public void setStrategy(PaymentStrategy s) {
        this.strategy = s;
    }

    public boolean process(double amount, model.User payer, String reference, String details) {
        if (strategy == null) {
            throw new IllegalStateException("No payment strategy selected.");
        }

        return strategy.pay(amount, payer, reference, details);
    }
}
