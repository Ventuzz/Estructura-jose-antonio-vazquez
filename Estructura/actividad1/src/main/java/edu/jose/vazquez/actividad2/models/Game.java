package edu.jose.vazquez.actividad2.models;

public class Game {
    private String name;
    private String genre;
    private int releaseYear;


    public Game(String name, String genre, int releaseYear) {
        this.name = name;
        this.genre = genre;
        this.releaseYear = releaseYear;
       
    }

    public String getName() {
        return name;
    }

    public String getGenre() {
        return genre;
    }

    public int getReleaseYear() {
        return releaseYear;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public void setReleaseYear(int releaseYear) {
        this.releaseYear = releaseYear;
    }
    

}
