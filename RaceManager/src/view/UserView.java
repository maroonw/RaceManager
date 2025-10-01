package view;

import controller.UserController;
import model.User;

import java.util.Scanner;

public class UserView {
    private final UserController controller;

    public UserView(UserController controller) {
        this.controller = controller;
    }

    public void displayLoginForm() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("-----Login Form-----");
        while (controller.getCurrentUser() == null) {
            System.out.println("Please enter your email");
            String email = scanner.nextLine();

            if (!controller.userExists(email)) {
                System.out.println("Email not found");
                System.out.println("Let's get you registered");

                System.out.println("Please enter your name");
                String name = scanner.nextLine();

                System.out.println("Please enter your password");
                String password = scanner.nextLine();

                User u = new User();
                u.setEmail(email);
                u.setName(name);
                u.setPassword(password);
                u.setUserType("Racer");

                controller.handleRegister(u);
                System.out.println("You are now registered");
            }

            System.out.println("Please enter your password");
            String password = scanner.nextLine();
            controller.handleLogin(email, password);
        }

    }
    public void displayProfile(User user) {
        System.out.println("-----Profile-----");
        System.out.println("Name: " + user.getName());
        System.out.println("Email: " + user.getEmail());
        System.out.println("Type: " + user.getUserType());
    }

    public void logoutToLogin() {
        try {
            controller.logout();
        } catch (Exception e) {
            e.printStackTrace();
        }
        displayLoginForm();
    }
}
