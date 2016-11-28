package com.casumo.videorentalstore.controller.api;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

class OperationSummary {

    static final String DATE_PATTERN = "yyyy-MM-dd";

    static class FilmSummary {

        private final int filmId;
        private final String title;
        private final FilmFlag flag;
        private final BigDecimal price;
        public FilmSummary(int filmId, String title, FilmFlag flag, BigDecimal price){
            this.filmId = filmId;
            this.title = title;
            this.flag = flag;
            this.price = price;
        }

        int getFilmId() {
            return filmId;
        }

        String getTitle() {
            return title;
        }

        FilmFlag getFlag() {
            return flag;
        }

        BigDecimal getPrice() {
            return price;
        }
    }

    private int rentalId;
    private BigDecimal price;
    private List<FilmSummary> details;
    private boolean success;
    private String errorMessage;

    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern=DATE_PATTERN)
    private LocalDate endRentalDate;

    public OperationSummary(String errorMessage){
        super();
        this.errorMessage = errorMessage;
    }

    public OperationSummary(){
        price = BigDecimal.ZERO;
        details = new ArrayList<>();
        success = false;
        errorMessage = null;
    }

    public int getRentald(){
        return rentalId;
    }

    public void setRentalId(int rentalId){
        this.rentalId = rentalId;
    }

    public void setPrice(BigDecimal price){
        this.price = price;
    }

    public BigDecimal getPrice(){
        return price;
    }

    public void addFilmDetail(int filmId, String title, FilmFlag flag, BigDecimal price){
        details.add(new FilmSummary(filmId, title, flag, price));
    }

    public List<FilmSummary> getDetails(){
        return details;
    }

    public void setEndRentalDate(LocalDate endRentalDate){
        this.endRentalDate = endRentalDate;
    }

    public LocalDate getEndRentalDate(){
        return endRentalDate;
    }

    public void setSuccess(boolean success){
        this.success = success;
    }

    public boolean getSuccess(){
        return success;
    }

    public void setErrorMessage(String errorMessage){
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage(){
        return errorMessage;
    }
}