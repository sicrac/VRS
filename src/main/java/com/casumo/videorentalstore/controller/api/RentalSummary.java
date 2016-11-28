package com.casumo.videorentalstore.controller.api;

public class RentalSummary extends OperationSummary{

    private int totalBonus;
    private int rentalBonus;

    public RentalSummary(String errorMessage){
        super(errorMessage);
    }

    public RentalSummary(){
        super();
    }

    public void setTotalBonus(int totalBonus){
        this.totalBonus = totalBonus;
    }

    public int getTotalBonus(){
        return totalBonus;
    }

    public void setRentalBonus(int rentalBonus){
        this.rentalBonus = rentalBonus;
    }

    public int getRentalBonus(){
        return rentalBonus;
    }

}