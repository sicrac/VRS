package com.casumo.videorentalstore.dal;

import com.casumo.videorentalstore.model.Customer;
import com.casumo.videorentalstore.model.Film;
import com.casumo.videorentalstore.model.FilmKind;
import com.casumo.videorentalstore.model.RentalTransaction;
import java.math.BigDecimal;
import java.util.List;

public interface VideoRentalStoreDAL {

    Customer findCustomerById(String id);

    Film findFilmById(Integer id);

    List<Film> listFilms(String search, FilmKind kind, boolean availableOnly);

    RentalTransaction newRentalTransaction(String username, List<Film> films, int days, BigDecimal price);

    RentalTransaction findOngoingTransaction(String username);

    void closeOngoingTransaction(RentalTransaction rentalTransaction);

}
