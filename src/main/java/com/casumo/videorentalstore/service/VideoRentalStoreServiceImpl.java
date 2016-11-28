package com.casumo.videorentalstore.service;


import com.casumo.videorentalstore.model.FilmKind;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import static java.time.temporal.ChronoUnit.DAYS;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;


@Service
@EnableAutoConfiguration
@PropertySource("application.properties")
public class VideoRentalStoreServiceImpl implements VideoRentalStoreService {

    static public class  Payment {
        private final int dailyPriceThreshold;
        private final Money basePrice;
        public Payment(int daysBeforDailyFee, Money basePrice){
            this.dailyPriceThreshold = daysBeforDailyFee;
            this.basePrice = basePrice;
        }
        public static Payment make(int daysBeforDailyFee, CurrencyUnit currencyUnit, int price){
            return new Payment(daysBeforDailyFee, Money.of(currencyUnit, BigDecimal.valueOf(price)));
        }
    }

    public CurrencyUnit currencyUnit = CurrencyUnit.of("SEK");
    @Value("${service.currency}")
    private String currency;
    @Value("${service.premium_price}")
    private int premiumPrice;
    @Value("${service.regular_price}")
    private int regularPrice;
    @Value("${service.basic_price}")
    private int basicPrice;
    @Value("${service.premium_days}")
    private int premiumDays;
    @Value("${service.regular_days}")
    private int regularDays;
    @Value("${service.basic_days}")
    private int basicDays;
    @Value("${service.premium_bonus}")
    private int premiumBonus;
    @Value("${service.regular_bonus}")
    private int regularBonus;
    @Value("${service.basic_bonus}")
    private int basicBonus;

    private Map<FilmKind, Payment> payments;
    private Map<FilmKind, Integer> bonus;

    @PostConstruct
    private void init(){
        currencyUnit = CurrencyUnit.of(currency);
        payments = new HashMap<>();
        payments.put(FilmKind.NEW, Payment.make(premiumDays, currencyUnit, premiumPrice));
        payments.put(FilmKind.REGULAR, Payment.make(regularDays, currencyUnit, regularPrice));
        payments.put(FilmKind.OLD, Payment.make(basicDays, currencyUnit, basicPrice));
        bonus = new HashMap<>();
        bonus.put(FilmKind.NEW, premiumBonus);
        bonus.put(FilmKind.REGULAR, regularBonus);
        bonus.put(FilmKind.OLD, basicBonus);
    }

    /**
     * This method was meant to allow customers to specify a currency as well as
     * allowing the administrator to set a currency (other than SEK)
     * through the application.properties file.
     */
    private BigDecimal calculateConversionRate(CurrencyUnit currencyUnit){
        if (currencyUnit.equals(this.currencyUnit))
            return BigDecimal.ONE;
        throw new UnsupportedOperationException("The conversion rate should be got by querying an external service.");
    }

    @Override
    public BigDecimal calculatePrice(FilmKind filmKind, int days) {
        return calculatePrice(filmKind, days, currencyUnit);
    }

    @Override
    public BigDecimal calculatePrice(FilmKind filmKind, int days, CurrencyUnit currencyUnit) {
        Payment payment = payments.get(filmKind);
        Money money = payment.basePrice;
        if (days > payment.dailyPriceThreshold){
            money = money.multipliedBy(days - Math.max(0, payment.dailyPriceThreshold - 1));
        }
        money = money.convertedTo(currencyUnit, calculateConversionRate(currencyUnit), RoundingMode.UNNECESSARY);
        return money.getAmount();
    }

    @Override
    public BigDecimal calculateSurcharge(FilmKind filmKind, LocalDate rentalDate, LocalDate endRentalDate, LocalDate returningDate){
        return calculateSurcharge(filmKind, currencyUnit, rentalDate, endRentalDate, returningDate);
    }

    @Override
    public BigDecimal calculateSurcharge(FilmKind filmKind, CurrencyUnit currencyUnit, LocalDate rentalDate, LocalDate endRentalDate, LocalDate currentDate){
        if (currentDate.isEqual(endRentalDate) || currentDate.isBefore(endRentalDate)){
            return BigDecimal.ZERO;
        }
        long daysToSurcharge = DAYS.between(endRentalDate, currentDate);
        Payment payment = payments.get(filmKind);
        return payment.basePrice.multipliedBy(daysToSurcharge).getAmount();
    }

    @Override
    public int calculateBonus(FilmKind filmKind){
        return bonus.get(filmKind);
    }
}
