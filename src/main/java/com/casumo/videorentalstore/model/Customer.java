package com.casumo.videorentalstore.model;

public class Customer {

    private String username;
    private int bonus;

    public Customer(String username){
        this.username = username;
        this.bonus = 0;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getBonus(){
        return bonus;
    }

    public void addBonus(int bonus){
        this.bonus = this.bonus + bonus;
    }

}