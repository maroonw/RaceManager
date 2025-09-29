package app;

import model.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public final class DataSeeder {

    public static final class Seed {
        public final RaceSystem raceSystem;
        public final Racer alice;
        public final Racer bob;
        public final Race crit;   // id "123"
        public final Race tt;     // id "456"

        private Seed(RaceSystem rs, Racer alice, Racer bob, Race crit, Race tt) {
            this.raceSystem = rs;
            this.alice = alice;
            this.bob = bob;
            this.crit = crit;
            this.tt = tt;
        }
    }

    private static final SimpleDateFormat SDF = new SimpleDateFormat("MM/dd/yyyy");

    private DataSeeder() {}

    public static Seed seed(RaceSystem rs) {
        // --- Racers ---
        Racer alice = new Racer();
        alice.setName("Alice Rider");
        alice.setEmail("alice@example.com");
        alice.setPassword("pass");
        alice.setUserType("RACER");
        alice.setCatLevel(5);
        alice.setCreditCardInfo("****1111");
        rs.registerUser(alice);

        Racer bob = new Racer();
        bob.setName("Bob Sprinter");
        bob.setEmail("bob@example.com");
        bob.setPassword("pass");
        bob.setUserType("RACER");
        bob.setCatLevel(4);
        bob.setCreditCardInfo("****2222");
        rs.registerUser(bob);

        // --- Races (numeric string IDs so your controllers that take int raceId are happy) ---
        Race crit = new Race("123");
        setRaceFields(crit, date("10/12/2025"), "Criterium", 25.0, "Downtown Loop",
                true, 100, date("10/10/2025"), 5);
        rs.addRace(crit);

        Race tt = new Race("456");
        setRaceFields(tt, date("11/02/2025"), "Time Trial", 10.0, "River Road",
                false, 80, date("10/31/2025"), 5);
        rs.addRace(tt);

        return new Seed(rs, alice, bob, crit, tt);
    }

    private static Date date(String mmddyyyy) {
        try { return SDF.parse(mmddyyyy); }
        catch (ParseException e) { return new Date(); }
    }

    private static void setRaceFields(Race r, Date raceDate, String type, double miles,
                                      String route, boolean official, int limit,
                                      Date lastReg, int catRequired) {
        r.setRaceDate(raceDate);
        r.setType(type);
        r.setMiles(miles);
        r.setRoute(route);
        r.setOfficialRace(official);
        r.setParticipantLimit(limit);
        r.setLastRegistrationDate(lastReg);
        r.setCatRequired(catRequired);
    }
}

