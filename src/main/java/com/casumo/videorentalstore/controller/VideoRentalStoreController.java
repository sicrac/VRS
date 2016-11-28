package com.casumo.videorentalstore.controller;

import com.casumo.videorentalstore.model.Film;
import com.casumo.videorentalstore.model.FilmKind;
import com.casumo.videorentalstore.model.RentalTransaction;
import com.casumo.videorentalstore.controller.api.FilmFlag;
import com.casumo.videorentalstore.controller.api.RentalSummary;
import com.casumo.videorentalstore.controller.api.ReturningSummary;
import com.casumo.videorentalstore.dal.InMemoryDAL;
import com.casumo.videorentalstore.model.Customer;
import com.casumo.videorentalstore.service.VideoRentalStoreServiceImpl;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/api")
public class VideoRentalStoreController {

    @Autowired
    private VideoRentalStoreServiceImpl videoRentalStoreService;

    @Autowired
    private InMemoryDAL videoRentalStoreDAL;

    @RequestMapping(path="/films", method=RequestMethod.GET)
    public @ResponseBody List<Film> listFilms(
            @RequestParam(value="search", required=false) String search,
            @RequestParam(value="kind", required=false) FilmKind kind,
            @RequestParam(value="availableOnly", defaultValue="false") boolean availableOnly) {

        return videoRentalStoreDAL.listFilms(search, kind, availableOnly);
    }

    @RequestMapping(path="/films/{username}", method=RequestMethod.POST)
    public @ResponseBody ResponseEntity<RentalSummary> renting(
            @PathVariable(value = "username") String username,
            @RequestParam(value="filmId", required=true) List<Integer> filmIds,
            @RequestParam(value="days", required=true) int days) {

        // It can only happen by writing the payload by hand (with curl)
        filmIds.removeIf(Objects::isNull);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();
        if (!user.getUsername().equals(username))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new RentalSummary("You tried to perform an forbidden action"));

        if (videoRentalStoreDAL.findOngoingTransaction(user.getUsername()) != null)
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new RentalSummary("You have an ongoing rental"));

        List<Film> films = new ArrayList<>();
        BigDecimal totalPrice = BigDecimal.ZERO;
        RentalSummary rentalSummary = new RentalSummary();
        int bonus = 0;
        for(Integer filmId: filmIds){
            Film film = videoRentalStoreDAL.findFilmById(filmId);
            if (film == null)
                rentalSummary.addFilmDetail(filmId, null, FilmFlag.NOT_FOUND, BigDecimal.ZERO);
            else if (!film.getAvailable())
                rentalSummary.addFilmDetail(filmId, film.getTitle(), FilmFlag.UNAVAILABLE, BigDecimal.ZERO);
            else{
                films.add(film);
                film.setAvailable(false);
                BigDecimal price = videoRentalStoreService.calculatePrice(film.getKind(), days);
                rentalSummary.addFilmDetail(filmId, film.getTitle(), FilmFlag.AVAILABLE, price);
                totalPrice = totalPrice.add(price);
                bonus += videoRentalStoreService.calculateBonus(film.getKind());
            }
        }

        if (films.size() > 0){
            Customer customer = videoRentalStoreDAL.findCustomerById(user.getUsername());
            customer.addBonus(bonus);
            RentalTransaction rentalTransaction = videoRentalStoreDAL.newRentalTransaction(user.getUsername(), films, days, totalPrice);
            rentalSummary.setRentalId(rentalTransaction.getId());
            rentalSummary.setPrice(totalPrice);
            rentalSummary.setSuccess(true);
            rentalSummary.setEndRentalDate(rentalTransaction.getEndRentalDate());
            rentalSummary.setRentalBonus(bonus);
            rentalSummary.setTotalBonus(customer.getBonus());
        }
        return ResponseEntity.status(HttpStatus.OK).body(rentalSummary);
    }

    @RequestMapping(path="/films/{username}/{rentalId}", method=RequestMethod.PUT)
    public @ResponseBody ResponseEntity<ReturningSummary> returning(
            @PathVariable(value = "username") String username,
            @PathVariable(value = "rentalId") Integer rentalId,
            @RequestParam(value="currentDate", required=false)
            @DateTimeFormat(pattern="yyyy-MM-dd") LocalDate currentDate) {

        if (currentDate == null)
            currentDate = LocalDate.now();

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();
        if (!user.getUsername().equals(username))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ReturningSummary("You tried to perform a forbidden action"));

        RentalTransaction rentalTransaction = videoRentalStoreDAL.findOngoingTransaction(user.getUsername());
        if (rentalTransaction == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ReturningSummary("You haven't an ongoing rental"));

        if (!rentalTransaction.getId().equals(rentalId))
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ReturningSummary(String.format("You haven't an ongoing transaction with id='%s'", rentalId)));

        ReturningSummary returningSummary = new ReturningSummary();
        BigDecimal totalPrice = BigDecimal.ZERO;
        for(Film film: rentalTransaction.getFilms()){
            BigDecimal surcharge = videoRentalStoreService.calculateSurcharge(film.getKind(), rentalTransaction.getRentalDate(), rentalTransaction.getEndRentalDate(), currentDate);
            film.setAvailable(true);
            returningSummary.addFilmDetail(film.getId(), film.getTitle(), FilmFlag.RETURNED, surcharge);
            totalPrice = totalPrice.add(surcharge);
        }
        returningSummary.setRentalId(rentalTransaction.getId());
        returningSummary.setSuccess(true);
        returningSummary.setPrice(totalPrice);
        returningSummary.setEndRentalDate(rentalTransaction.getEndRentalDate());
        returningSummary.setReturningDate(currentDate);
        rentalTransaction.setReturningDate(currentDate);
        videoRentalStoreDAL.closeOngoingTransaction(rentalTransaction);
        return ResponseEntity.status(HttpStatus.OK).body(returningSummary);
    }


}