package edu.uci.ics.fabflixmobile;

import java.util.ArrayList;

public class Movie {
    private String id;
    private String title;
    private String year;
    private String director;
    private String rating;
    private ArrayList<String> genres;
    private ArrayList<String> stars;

    public Movie(String id, String title, String year, String director, String rating, ArrayList<String> genres, ArrayList<String> stars) {
        this.id = id;
        this.title = title;
        this.year = year;
        this.director = director;
        this.rating = rating;
        this.genres = genres;
        this.stars = stars;
    }

    public String getId(){ return id; }

    public String getTitle() {
        return title;
    }

    public String getYear() {
        return year;
    }

    public String getDirector(){ return director; }

    public String getRating(){ return rating; }

    public ArrayList<String> getGenres(){ return genres; }

    public ArrayList<String> getStars(){ return stars; }
}