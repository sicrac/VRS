package com.casumo.videorentalstore.dal;

import com.casumo.videorentalstore.model.Customer;
import com.casumo.videorentalstore.model.Film;
import com.casumo.videorentalstore.model.FilmKind;
import com.casumo.videorentalstore.model.RentalTransaction;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.stereotype.Repository;

@Repository
public class InMemoryDAL implements VideoRentalStoreDAL {

    private final Map<Integer, Film> films;
    private final Map<String, Customer> customers;
    private final Map<Integer, RentalTransaction> transactions;
    private final Map<String, Integer> ongoningTransactions;
    private int rentalId;

    private void loadTestingData() throws IOException{
        ClassLoader classLoader = getClass().getClassLoader();
        String filePath = classLoader.getResource("films.csv").getFile();
        Stream<String> stream = Files.lines(Paths.get(filePath));
        stream.forEach((String t) -> {
            String[] items = t.split("\t");
            Integer id = new Integer(items[0]);
            films.put(id, Film.make(id, items[1], FilmKind.valueOf(items[3])));
        });
        for(int i=1; i<=3; i++){
            String username = String.format("customer%s", i);
            customers.put(username, new Customer(username));
            ongoningTransactions.put(username, null);
        }
    }

    public InMemoryDAL() throws IOException {
        films = new HashMap<Integer, Film>();
        customers = new HashMap<String, Customer>();
        transactions = new HashMap<Integer, RentalTransaction>();
        ongoningTransactions = new HashMap<String, Integer>();
        rentalId = 0;
        loadTestingData();
    }

    @Override
    public Customer findCustomerById(String id) {
        return customers.get(id);
    }

    @Override
    public Film findFilmById(Integer id) {
        return films.get(id);
    }

    @Override
    public List<Film> listFilms(String search, FilmKind kind, boolean availableOnly) {
        if (search != null && kind != null && availableOnly == true)
            return listFilms(f -> f.getTitle().contains(search) && f.getKind().equals(kind) && f.getAvailable());
        if (search != null && availableOnly == true)
            return listFilms(f -> f.getTitle().contains(search) && f.getAvailable());
        if (kind != null && availableOnly == true)
            return listFilms(f -> f.getKind().equals(kind));
        if (kind != null && search != null)
            return listFilms(f -> f.getKind().equals(kind));
        if (search != null)
            return listFilms(f -> f.getTitle().contains(search));
        if (kind != null)
            return listFilms(f -> f.getKind().equals(kind));
        if (availableOnly == true)
            return listFilms(f -> f.getAvailable());
        return new ArrayList(films.values());
    }

    private List<Film> listFilms(Predicate<? super Film> predicate) {
        return films.values().stream().filter(predicate).collect(Collectors.toList());
    }

    @Override
    public RentalTransaction newRentalTransaction(String username, List<Film> films, int days, BigDecimal price) {
        RentalTransaction rentalTransaction = new RentalTransaction(findCustomerById(username), days, films, price, LocalDate.now());
        rentalId++;
        rentalTransaction.setId(rentalId);
        transactions.put(rentalId, rentalTransaction);
        ongoningTransactions.put(username, rentalId);
        Customer customer = findCustomerById(username);
        return rentalTransaction;
    }

    @Override
    public RentalTransaction findOngoingTransaction(String username) {
        if (ongoningTransactions.get(username) != null)
            return transactions.get(ongoningTransactions.get(username));
        return null;
    }

    @Override
    public void closeOngoingTransaction(RentalTransaction rentalTransaction) {
        ongoningTransactions.put(rentalTransaction.getCustomer().getUsername(), null);
    }
}
