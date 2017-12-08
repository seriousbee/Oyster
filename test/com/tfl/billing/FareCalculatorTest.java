package com.tfl.billing;

import com.oyster.OysterCard;
import com.tfl.billing.helpers.CostCalculatingUtil;
import com.tfl.billing.helpers.JourneyCosts;
import com.tfl.billing.journeyelements.JourneyEnd;
import com.tfl.billing.journeyelements.JourneyStart;
import com.tfl.external.Customer;
import com.tfl.underground.OysterReaderLocator;
import com.tfl.underground.Station;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
/**
 * Created by tomaszczernuszenko on 07/12/2017.
 */
public class FareCalculatorTest {


    FareCalculator fareCalculator;
    Customer c;
    
    @Before
    public void beforeEach(){
        fareCalculator = new FareCalculator();
        c = new Customer("Adam Testowy", new OysterCard());
    }

    private Journey createSingleJourneyFor (Customer customer, Station from, DateTime startTime, Station to, DateTime endTime){
        JourneyStart journeyStart = new JourneyStart(customer.cardId(), OysterReaderLocator.atStation(from).id(), startTime);
        JourneyEnd journeyEnd = new JourneyEnd  (customer.cardId(), OysterReaderLocator.atStation(to).id(), endTime);
        return new Journey(journeyStart, journeyEnd);
    }

    @Test
    public void shortOffPeakJourneyChargedCorrectly() {
        DateTime startTime = new DateTime();
        DateTime endTime = new DateTime();
        startTime = startTime.hourOfDay().setCopy(11);
        endTime = endTime.hourOfDay().setCopy(11);
        BigDecimal expected = JourneyCosts.OFF_PEAK_SHORT_JOURNEY_PRICE;
        List<Journey> shortPeakJourney = Arrays.asList(createSingleJourneyFor(c, Station.PADDINGTON, startTime, Station.VICTORIA_STATION, endTime));
        assertThat(fareCalculator.calculateFare(shortPeakJourney), is(CostCalculatingUtil.roundToNearestPenny(expected)));
    }

    @Test
    public void shortPeakJourneyChargedCorrectly() {
        DateTime startTime = new DateTime();
        DateTime endTime = new DateTime();
        startTime = startTime.hourOfDay().setCopy(7);
        endTime = endTime.hourOfDay().setCopy(7);
        BigDecimal expected = JourneyCosts.PEAK_SHORT_JOURNEY_PRICE;
        List<Journey> shortPeakJourney = Arrays.asList(createSingleJourneyFor(c, Station.PADDINGTON, startTime, Station.VICTORIA_STATION, endTime));
        assertThat(fareCalculator.calculateFare(shortPeakJourney), is(CostCalculatingUtil.roundToNearestPenny(expected)));
    }

    @Test
    public void longOffPeakJourneyChargedCorrectly() {
        DateTime startTime = new DateTime();
        DateTime endTime = new DateTime();
        startTime = startTime.hourOfDay().setCopy(11);
        endTime = endTime.hourOfDay().setCopy(12);
        BigDecimal expected = JourneyCosts.OFF_PEAK_LONG_JOURNEY_PRICE;
        List<Journey> shortPeakJourney = Arrays.asList(createSingleJourneyFor(c, Station.PADDINGTON, startTime, Station.VICTORIA_STATION, endTime));
        assertThat(fareCalculator.calculateFare(shortPeakJourney), is(CostCalculatingUtil.roundToNearestPenny(expected)));
    }

    @Test
    public void longPeakJourneyChargedCorrectly() {
        DateTime startTime = new DateTime();
        DateTime endTime = new DateTime();
        startTime = startTime.hourOfDay().setCopy(16);
        endTime = endTime.hourOfDay().setCopy(18);
        BigDecimal expected = JourneyCosts.PEAK_LONG_JOURNEY_PRICE;
        List<Journey> shortPeakJourney = Arrays.asList(createSingleJourneyFor(c, Station.PADDINGTON, startTime, Station.VICTORIA_STATION, endTime));
        assertThat(fareCalculator.calculateFare(shortPeakJourney), is(CostCalculatingUtil.roundToNearestPenny(expected)));
    }

    @Test
    public void offPeakCapAppliedCorrectly() {
        List<Journey> shortPeakJourney = new ArrayList<>();
        DateTime startTime = new DateTime();
        DateTime endTime = new DateTime();
        startTime = startTime.hourOfDay().setCopy(11);
        endTime = endTime.hourOfDay().setCopy(11);

        for(int i=0;i<=59;i+=6){
            startTime = startTime.minuteOfHour().setCopy(i);
            endTime = endTime.minuteOfHour().setCopy(i+5);
            shortPeakJourney.add(createSingleJourneyFor(c, Station.PADDINGTON, startTime, Station.VICTORIA_STATION, endTime));
        }

        BigDecimal expected = JourneyCosts.OFF_PEAK_DAILY_CAP_PRICE;
        assertThat(fareCalculator.calculateFare(shortPeakJourney), is(CostCalculatingUtil.roundToNearestPenny(expected)));
    }

    @Test
    public void peakCapAppliedCorrectly() {

        List<Journey> shortPeakJourney = new ArrayList<>();
        DateTime startTime = new DateTime();
        DateTime endTime = new DateTime();
        startTime = startTime.hourOfDay().setCopy(7);
        endTime = endTime.hourOfDay().setCopy(7);

        for(int i=0;i<=59;i+=6){
            startTime = startTime.minuteOfHour().setCopy(i);
            endTime = endTime.minuteOfHour().setCopy(i+5);
            shortPeakJourney.add(createSingleJourneyFor(c, Station.PADDINGTON, startTime, Station.VICTORIA_STATION, endTime));
        }

        BigDecimal expected = JourneyCosts.PEAK_DAILY_CAP_PRICE;
        assertThat(fareCalculator.calculateFare(shortPeakJourney), is(CostCalculatingUtil.roundToNearestPenny(expected)));
    }





}
