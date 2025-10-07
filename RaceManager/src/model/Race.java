package model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Race {
    private String raceID;
    private Date raceDate;
    private String type;
    private double miles;
    private String route;
    private boolean officialRace;
    private int participantLimit;
    private Date lastRegistrationDate;
    private int catRequired;
    private List<RaceReview> reviews;
    private static int counterRace = 1;

    public Race() {}
    public Race(String raceID) { this.raceID = raceID; }

    public String getRaceID() { return raceID; }

    public Date getRaceDate() {
        return raceDate;
    }

    public String getType() {
        return type;
    }

    public double getMiles() {
        return miles;
    }

    public String getRoute() {
        return route;
    }

    public boolean isOfficialRace() {
        return officialRace;
    }

    public int getParticipantLimit() {
        return participantLimit;
    }

    public Date getLastRegistrationDate() {
        return lastRegistrationDate;
    }

    public int getCatRequired() {
        //test high level
        //catRequired = 5;

        //test low level
        //catRequired = 0;
        return catRequired;
    }
    public void setRaceDate(Date raceDate) { this.raceDate = raceDate; }
    public void setType(String type) { this.type = type; }
    public void setMiles(double miles) { this.miles = miles; }
    public void setRoute(String route) { this.route = route; }
    public void setOfficialRace(boolean officialRace) { this.officialRace = officialRace; }
    public void setParticipantLimit(int participantLimit) { this.participantLimit = participantLimit; }
    public void setLastRegistrationDate(Date lastRegistrationDate) { this.lastRegistrationDate = lastRegistrationDate; }
    public void setCatRequired(int catRequired) { this.catRequired = catRequired; }

    @Override
    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        String raceDateStr = raceDate != null ? sdf.format(raceDate) : "N/A";
        String lastRegDateStr = lastRegistrationDate != null ? sdf.format(lastRegistrationDate) : "N/A";
        return String.format("Race %s on %s [%s, %.2f miles, route: %s, official: %s, limit: %d, reg closes: %s, cat: %d]",
                raceID,
                raceDateStr,
                type != null ? type : "",
                miles,
                route != null ? route : "",
                officialRace ? "Yes" : "No",
                participantLimit,
                lastRegDateStr,
                catRequired);
    }

}
