package com.casumo.videorentalstore.controller.api;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import java.math.BigDecimal;
import java.time.LocalDate;

public class ReturningSummary extends OperationSummary{

    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern=DATE_PATTERN)
    private LocalDate returningDate;
    
    public ReturningSummary(String errorMessage){
        super(errorMessage);
    }
    
    public ReturningSummary(){
        super();
    }

    @Override
    @JsonProperty("surcharge")
    public BigDecimal getPrice(){
        return super.getPrice();
    }

    public LocalDate getReturningDate() {
        return returningDate;
    }

    public void setReturningDate(LocalDate returningDate) {
        this.returningDate = returningDate;
    }
    
}