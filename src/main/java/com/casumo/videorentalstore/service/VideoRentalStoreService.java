/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.casumo.videorentalstore.service;

import com.casumo.videorentalstore.model.FilmKind;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.joda.money.CurrencyUnit;

public interface VideoRentalStoreService {

    int calculateBonus(FilmKind filmKind);

    BigDecimal calculatePrice(FilmKind filmKind, int days);

    BigDecimal calculatePrice(FilmKind filmKind, int days, CurrencyUnit currencyUnit);

    BigDecimal calculateSurcharge(FilmKind filmKind, LocalDate rentalDate, LocalDate endRentalDate, LocalDate returningDate);

    BigDecimal calculateSurcharge(FilmKind filmKind, CurrencyUnit currencyUnit, LocalDate rentalDate, LocalDate endRentalDate, LocalDate currentDate);

}
