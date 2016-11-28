package com.casumo.videorentalstore.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class RentalTransaction{

    private Integer id;
    private Customer customer;
    private int days;
    private List<Film> films;
    private BigDecimal price;
    private BigDecimal returningSurcharge;
    private LocalDate rentalDate;
    private LocalDate returningDate;

    public RentalTransaction(Customer customer, int days, List<Film> films, BigDecimal price, LocalDate rentalDate){
        this.customer = customer;
        this.days = days;
        this.films = films;
        this.price = price;
        this.returningSurcharge = BigDecimal.ZERO;
        this.rentalDate = rentalDate;
        this.returningDate = null;
    }

    public Integer getId(){
        return id;
    }

    public void setId(Integer id){
        this.id = id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Integer getDays() {
        return days;
    }

    public void setDays(Integer days) {
        this.days = days;
    }

    public List<Film> getFilms() {
        return films;
    }

    public void setFilms(List<Film> films) {
        this.films = films;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getReturningSurcharge() {
        return returningSurcharge;
    }

    public void setReturningSurcharge(BigDecimal returningSurcharge) {
        this.returningSurcharge = returningSurcharge;
    }

    public LocalDate getRentalDate() {
        return rentalDate;
    }

    public void setRentalDate(LocalDate rentalDate) {
        this.rentalDate = rentalDate;
    }

    public LocalDate getReturningDate() {
        return returningDate;
    }

    public void setReturningDate(LocalDate returningDate) {
        this.returningDate = returningDate;
    }

    public LocalDate getEndRentalDate(){
        return rentalDate.plusDays(days);
    }
}