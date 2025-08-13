package edu.jose.vazquez.actividades.actividad1.models;

public class Song {
    private String tittle;
    private String artist;
    private double duration;

    public Song(String tittle, String artist, double duration) {
        this.tittle = tittle;
        this.artist = artist;
        this.duration = duration;
    }

    public String getTittle() {
        return tittle;
    }
    public void setTittle(String tittle) {
        this.tittle = tittle;
    }
    public String getArtist() {
        return artist;
    }
    public void setArtist(String artist) {
        this.artist = artist;
    }
    public double getDuration() {
        return duration;
    }
    public void setDuration(double duration) {
        this.duration = duration;
    }
    

}
