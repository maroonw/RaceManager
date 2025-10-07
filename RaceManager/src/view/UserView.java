package view;

import controller.UserController;
import model.User;
import model.Racer;

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
                System.out.println("No account found. Let's create one.");

                System.out.print("Name: ");
                String name = scanner.nextLine().trim();

                System.out.print("Choose a password: ");
                String pw1 = scanner.nextLine().trim();

                // Create a Racer
                model.Racer u = new model.Racer();
                u.setName(name);
                u.setEmail(email);
                u.setPassword(pw1);
                u.setUserType("RACER");
                u.setCatLevel(5);
                u.setLicense(null);

                controller.handleRegister(u);
                System.out.println("(You can now log in.)");

                System.out.println("Please enter your password");
                String password = scanner.nextLine();
                controller.handleLogin(email, password);
            }
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

    public void signUpForRace() {
    }

    public controller.UserController getController() {
        return controller;
    }
}
