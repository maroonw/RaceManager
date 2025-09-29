package model;

import java.util.*;

public class RaceSystem {
    private final Map<String, User> users = new HashMap<>(); // key = email
    //private List<User> users = new ArrayList<>();
    private List<Race> availableRaces = new ArrayList<>();
    private List<RaceResult> raceHistory = new ArrayList<>();

    private static int nextRaceId = 0;
    private static final Map<String, Race> races = new HashMap<>(); //key = race Id



    // Register a user (Racer or other)
    public void registerUser(User user) {
        if (user == null || user.getEmail() == null) {
            throw new IllegalArgumentException("User or user email cannot be null");
        }
        users.put(user.getEmail(), user);
    }

    public User findUserByEmail(String email) {
        return users.get(email);
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }


    public List<Race> getAllRaces() {
        return new ArrayList<>(races.values());
    }

// updated this to make sure the races are all adding to the same container
    public void addRace(Race race) {
        if (race == null || race.getRaceID() == null) return;
        availableRaces.add(race);

        races.put(race.getRaceID(), race);
    }


    public void upgradeCategory(Racer racer) {
        if (racer == null) throw new IllegalArgumentException("Racer cannot be null");

        int currentLevel = racer.getCatLevel(); // 5 = beginner, 1 = best
        if (currentLevel > 1) {
            racer.setCatLevel(currentLevel - 1); // move to a better category
            System.out.println("Racer " + racer.getName() + " upgraded from category "
                    + currentLevel + " to category " + racer.getCatLevel());
        } else {
            System.out.println("Racer " + racer.getName() + " is already at the best category (1).");
        }
    }

    // getters/setters ...


    public Race createRace(Date date, String type, double miles, String route, boolean official, int limit, Date lastRegDate, int catRequired) {
        String id = "R" + (++nextRaceId);
        Race race = new Race(id);
        race.setRaceDate(date);
        race.setType(type);
        race.setMiles(miles);
        race.setRoute(route);
        race.setOfficialRace(official);
        race.setParticipantLimit(limit);
        race.setLastRegistrationDate(lastRegDate);
        race.setCatRequired(catRequired);

        races.put(id, race);

        // Observer hook (simplified for now)
        System.out.println("Notification: Race created -> " + id);

        return race;
    }







}
