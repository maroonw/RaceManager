package model;

/**
* Feedback submitted by a racer for a completed race.
*
* Valid review components:
*   Non-empty comment describing the race experience
*   Rating between 1 and 5
*   Reference to {@link Racer} who submitted the review
*   
* @see Racer
* @see Race
*
*/

public class RaceReview {
    private String comment;
    private int rating;
    private Racer reviewer;


    // Getters
    public String getComment() { return comment; }
    public int getRating() { return rating; }
    public Racer getReviewer() { return reviewer; }

    // Setters
    public void setComment(String comment) { this.comment = comment; }
    public void setRating(int rating) { this.rating = rating; }
    public void setReviewer(Racer reviewer) { this.reviewer = reviewer; }

    // Optional: Validation method
    public boolean isValidReview() {
        return comment != null && !comment.trim().isEmpty()
                && rating >= 1 && rating <= 5
                && reviewer != null;
    }
}

