package model.payment;

import model.User;

public interface PaymentStrategy {
    boolean pay(double amount, model.User payer, String reference, String details);
}
