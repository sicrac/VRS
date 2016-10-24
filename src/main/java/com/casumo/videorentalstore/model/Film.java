package com.casumo.videorentalstore.model;

public class Film {

    private int id;
    private String title;
    private FilmKind kind;
    private boolean available;
    
    public Film(String title, FilmKind kind, boolean available){
        this.title = title;
        this.kind = kind;
        this.available = available;
    }

    public void setId(int id){
        this.id = id;
    }
    public int getId(){
        return id;
    }
    
    public void setTitle(String title){
        this.title = title;
    }

    public String getTitle(){
        return title;
    }
    
    public void setKind(FilmKind kind){
        this.kind = kind;
    }
    
    public FilmKind getKind(){
        return kind;
    }

    public boolean getAvailable(){
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
    
    public static Film make(int id, String title, FilmKind kind){
        Film film = new Film(title, kind, true);
        film.setId(id);
        return film;
    }
}
