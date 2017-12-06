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
    public void shortOffPeakJourneyChargedCorrectly() {
        BigDecimal expected = JourneyCosts.OFF_PEAK_SHORT_JOURNEY_PRICE; //TODO: needs adjusting based on time of the day
        //TODO: fill out
    }

    @Test
    public void shortPeakJourneyChargedCorrectly() {
        BigDecimal expected = JourneyCosts.PEAK_SHORT_JOURNEY_PRICE; //TODO: needs adjusting based on time of the day
        //TODO: fill out
    }

    @Test
    public void longOffPeakJourneyChargedCorrectly() {
        BigDecimal expected = JourneyCosts.OFF_PEAK_LONG_JOURNEY_PRICE; //TODO: needs adjusting based on time of the day
        //TODO: fill out
    }

    @Test
    public void longPeakJourneyChargedCorrectly() {
        BigDecimal expected = JourneyCosts.PEAK_LONG_JOURNEY_PRICE; //TODO: needs adjusting based on time of the day
        //TODO: fill out
    }

    @Test
    public void customerWhoDidNotTapOutIsChargedAPenaltyFare() {
        BigDecimal expected = BigDecimal.valueOf(9);
        //TODO: fill out
    }

    @Test
    public void offPeakCapAppliedCorrectly() {
        BigDecimal expected = BigDecimal.valueOf(7);
        //TODO: fill out
    }

    @Test
    public void peakCapAppliedCorrectly() {
        BigDecimal expected = BigDecimal.valueOf(9);
        //TODO: fill out
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
    public void correctlyRoundsToTheNearestPennyFloor(){
        assertThat(costCalculator.roundToNearestPenny(new BigDecimal(1.0100010)), is(new BigDecimal(1.01).setScale(2,BigDecimal.ROUND_HALF_UP)));
    }

    @Test
    public void correctlyRoundsToTheNearestPennyCeil(){
        assertThat(costCalculator.roundToNearestPenny(new BigDecimal(1.5190011)), is(new BigDecimal(1.52).setScale(2,BigDecimal.ROUND_HALF_UP)));
    }

}
