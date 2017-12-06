package com.tfl.billing;

import com.tfl.external.Customer;
import com.tfl.external.CustomerDatabase;
import com.tfl.underground.OysterReaderLocator;
import com.tfl.underground.Station;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class JourneyCostCalculatorTest {

    Date peakTime;
    Date offPeakTime;
    Customer c;
    Customer c1;
    Customer c2;
    private List<Journey> customerJourneys;
    JourneyCostCalculator costCalculator;
    List<Journey> sampleJourneys;

    public JourneyCostCalculatorTest() {
        peakTime = new Date();
        offPeakTime = new Date();
        c = CustomerDatabase.getInstance().getCustomers().get(0);
        c1 = CustomerDatabase.getInstance().getCustomers().get(1);
        c2 = CustomerDatabase.getInstance().getCustomers().get(2);
        customerJourneys = Arrays.asList(new Journey(new JourneyStart(c.cardId(), OysterReaderLocator.atStation(Station.PADDINGTON).id()), new JourneyEnd(c.cardId(), OysterReaderLocator.atStation(Station.VICTORIA_STATION).id())));
        costCalculator = new JourneyCostCalculator();
        sampleJourneys = Arrays.asList(new Journey(new JourneyStart(c.cardId(), OysterReaderLocator.atStation(Station.PADDINGTON).id()), new JourneyEnd(c.cardId(), OysterReaderLocator.atStation(Station.VICTORIA_STATION).id())), new Journey(new JourneyStart(c.cardId(), OysterReaderLocator.atStation(Station.OXFORD_CIRCUS).id()), new JourneyEnd(c.cardId(),  OysterReaderLocator.atStation(Station.VICTORIA_STATION).id())));
    }

    private long hoursToMillis(int hour) {
        return hour*60*60*1000;
    }
    @Test
    public void getTotalTest() {
        BigDecimal expected = BigDecimal.valueOf(1.6);
        assertThat(costCalculator.roundToNearestPenny(costCalculator.getTotalFromJourneyList(customerJourneys, BigDecimal.ZERO)), is(costCalculator.roundToNearestPenny(expected)));
    }

    @Test
    public void getTotalForCustomerCurrentlyTravelling() {
        BigDecimal expected = BigDecimal.valueOf(3.2);
        assertThat(costCalculator.roundToNearestPenny(costCalculator.getTotalFromJourneyList(sampleJourneys,BigDecimal.ZERO)),is(costCalculator.roundToNearestPenny(expected)));
    }

    @Test
    public void getTotalForNotTravellingCustomer() {
        BigDecimal expected = BigDecimal.valueOf(3.2);
        assertThat(costCalculator.roundToNearestPenny(costCalculator.getTotalFromJourneyList(sampleJourneys,BigDecimal.ZERO)),is(costCalculator.roundToNearestPenny(expected)));
    }

    @Test
    public void morningPeakTimeIsMarkedAsPeak() {
        peakTime.setTime(hoursToMillis(6));
        assertTrue(costCalculator.isPeak(peakTime));
    }

    @Test
    public void anythingBetweenPeakTimesIsMarkedAsOffPeak() {
        offPeakTime.setTime(hoursToMillis(12));
        assertFalse(costCalculator.isPeak(offPeakTime));
    }

    @Test
    public void afternoonPeakTimeIsMarkedAsPeak() {
        peakTime.setTime(hoursToMillis(18));
        assertTrue(costCalculator.isPeak(peakTime));
    }

    @Test
    public void eveningTimeIsMarkedAsOffPeak() {
        offPeakTime.setTime(hoursToMillis(22));
        assertFalse(costCalculator.isPeak(offPeakTime));
    }

    @Test
    public void correctlyRoundstoTheNearestPennyFloor(){
        assertThat(costCalculator.roundToNearestPenny(new BigDecimal(1.0100010)), is(new BigDecimal(1.01).setScale(2,BigDecimal.ROUND_HALF_UP)));
    }

    @Test
    public void correctlyRoundstoTheNearestPennyCeil(){
        assertThat(costCalculator.roundToNearestPenny(new BigDecimal(1.5190011)), is(new BigDecimal(1.52).setScale(2,BigDecimal.ROUND_HALF_UP)));
    }

}
