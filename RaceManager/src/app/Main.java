package app;


import view.*;
import model.*;
import controller.*;
import model.notification.RacerSubscriber;

import java.util.Map;
import java.util.Scanner;
import java.util.LinkedHashMap;

public class Main {
    public static void main(String[] args) {


        // test pull request from maroon on intellij

        // fakeLoginDemo();
        // Instantiate the RaceSystem
        RaceSystem raceSystem = new RaceSystem();

        DataSeeder.Seed seed = DataSeeder.seed(raceSystem);

        // Controllers
        UserController userController     = new UserController(raceSystem);
        RaceController raceController     = new RaceController(raceSystem);
        ResultController resultController = new ResultController();
        ReviewController reviewController = new ReviewController();
        RacerController racerController   = new RacerController(raceSystem);

        // Views
        UserView userView     = new UserView(userController);
        RaceView raceView     = new RaceView(raceController, raceSystem);
        ResultView resultView = new ResultView(resultController);
        ReviewView reviewView = new ReviewView(reviewController);
        RacerView racerView   = new RacerView(racerController);
        PaymentView paymentV  = new PaymentView(raceController);
        MainMenuView menu     = new MainMenuView(userView, raceView, resultView, reviewView, racerView);

        // demo wiring
        menu.setDemoContext(seed, raceController, resultController, paymentV);
        raceController.getRaceNotifications().attach(new RacerSubscriber(seed.alice));
        resultController.getRaceNotifications().attach(new RacerSubscriber(seed.alice));

        // Show login form first
        userView.displayLoginForm();
        menu.showMenu();

    }
}
