package app;

import controller.RaceController;
import controller.ResultController;
import model.RaceResult;
import model.Racer;
import model.notification.RacerSubscriber;
/**import view.PaymentView;
 import view.RaceView;
 import view.ResultView;*/
import view.*;
import model.*;
import controller.*;

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

        // Instantiate controllers
        UserController userController = new UserController(raceSystem);
        RaceController raceController = new RaceController(raceSystem);
        ResultController resultController = new ResultController();
        ReviewController reviewController = new ReviewController();
        RacerController racerController=new RacerController(raceSystem);

        // Instantiate views
        UserView userView = new UserView(userController);
        RaceView raceView = new RaceView(raceController, raceSystem);
        ResultView resultView = new ResultView(resultController);
        //ReviewView reviewView = new ReviewView(reviewController);
        RacerView racerView=new RacerView(racerController);
        PaymentView paymentView = new PaymentView(raceController);


        // fake login demo
        // fakeLoginDemo();
        // Show login form first
        userView.displayLoginForm();

        // Show main menu
        //UserController userController=new UserController();
        //UserView userView=new UserView(userController);
        resultController=new ResultController();
        resultView=new ResultView(resultController);
        reviewController=new ReviewController();
        ReviewView reviewView=new ReviewView(reviewController);
        MainMenuView mainMenuView = new MainMenuView(userView, raceView, resultView, reviewView, racerView);

        // subscribe Alice so notifications show up during demos
        RacerSubscriber aliceSub = new RacerSubscriber(seed.alice);
        raceController.getRaceNotifications().attach(aliceSub);
        resultController.getRaceNotifications().attach(aliceSub);

        // hand the menu what it needs for the demos
        mainMenuView.setDemoContext(seed, raceController, resultController, paymentView);

        mainMenuView.showMenu();

        /**      //************

         // Controllers
         RaceController raceController = new RaceController();


         // Views

         RaceView raceView = new RaceView(raceController);

         Scanner sc = new Scanner(System.in);
         int option;
         do {
         System.out.println("\n--- Bike Racing System ---");
         System.out.println("1. Create Race");
         System.out.println("2. Create Racer (TODO)");
         System.out.println("3. Exit");
         System.out.print("Choose option: ");
         option = sc.nextInt();
         sc.nextLine(); // clear buffer

         switch(option) {1
         case 1 -> raceView.promptCreateRace();
         case 2 -> System.out.println("Create Racer not implemented yet.");
         case 3 -> System.out.println("Exiting...");
         default -> System.out.println("Invalid option.");
         }
         } while(option != 3);


         //************************************
         */
    }

    private static void fakeLoginDemo() {

        Scanner sc = new Scanner(System.in);
        System.out.print("Login (any email): ");
        String email = sc.nextLine().trim();
        System.out.print("Password: ");
        String password = sc.nextLine().trim();

    }
}
