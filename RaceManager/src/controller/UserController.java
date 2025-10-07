package controller;

import model.RaceSystem;
import model.User;
import model.Racer;



public class UserController {
    private final RaceSystem raceSystem;
    private User currentUser;

    public UserController(RaceSystem raceSystem){
        this.raceSystem=raceSystem;
    }

    public void handleLogin(String email, String password) {
        User u = raceSystem.authenticate(email, password);
        if (u != null) {
            currentUser = u;
            System.out.println("Welcome, " + u.getName());
        } else {
            System.out.println("Wrong email or password");
        }
    }
    public void handleRegister(User user) {
        if (user == null || user.getEmail() == null || user.getEmail().isBlank()) {
            System.out.println("Misisng Email");
            return;
        }
        if (raceSystem.findUserByEmail(user.getEmail()) != null) {
            System.out.println("User already exists");
            return;
        }

        //already a racer
        if (user instanceof Racer) {
            raceSystem.registerUser(user);
            System.out.println("Account created");
            return;
        }

        // user says racer, but is actually a user
        // convert user to racer
        if ("RACER".equalsIgnoreCase(user.getUserType())) {
            model.Racer r = new model.Racer();
            copyUserFields(user, r);
            r.setCatLevel(5);
            raceSystem.registerUser(r);
            System.out.println("Account created for " + r.getEmail());
            return;
        }


        // correct type registration
        raceSystem.registerUser(user);
        System.out.println("User created");
    }

    public boolean userExists(String email) {
        return raceSystem.findUserByEmail(email) != null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void logout() {
        currentUser = null;
    }

    private static void copyUserFields(User src, User dst) {
        dst.setName(src.getName());
        dst.setEmail(src.getEmail());
        dst.setPassword(src.getPassword());
        dst.setUserType(src.getUserType());
    }

}



