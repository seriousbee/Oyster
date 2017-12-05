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
    private List<JourneyEvent> eventLog;
    private List<Journey> customerJourneys;
    JourneyCostCalculator costCalculator;
    JourneyManager journeyManager;
    List<Journey> sampleJourneys;

    public JourneyCostCalculatorTest() {
        peakTime = new Date();
        offPeakTime = new Date();
        c = CustomerDatabase.getInstance().getCustomers().get(0);
        c1 = CustomerDatabase.getInstance().getCustomers().get(1);
        c2 = CustomerDatabase.getInstance().getCustomers().get(2);
        eventLog = Arrays.asList(new JourneyStart(c.cardId(), OysterReaderLocator.atStation(Station.PADDINGTON).id()), new JourneyStart(c1.cardId(), OysterReaderLocator.atStation(Station.HOLBORN).id()), new JourneyEnd(c.cardId(), OysterReaderLocator.atStation(Station.VICTORIA_STATION).id()), new JourneyEnd(c1.cardId(), OysterReaderLocator.atStation(Station.CHANCERY_LANE).id()), new JourneyStart(c.cardId(), OysterReaderLocator.atStation(Station.OXFORD_CIRCUS).id()), new JourneyEnd(c.cardId(), OysterReaderLocator.atStation(Station.VICTORIA_STATION).id()), new JourneyStart(c.cardId(), OysterReaderLocator.atStation(Station.HOLBORN).id()));
        customerJourneys = Arrays.asList(new Journey(new JourneyStart(c.cardId(), OysterReaderLocator.atStation(Station.PADDINGTON).id()), new JourneyEnd(c.cardId(), OysterReaderLocator.atStation(Station.VICTORIA_STATION).id())));
        costCalculator = new JourneyCostCalculator();
        journeyManager = new JourneyManager();
        sampleJourneys = Arrays.asList(new Journey(new JourneyStart(c.cardId(), OysterReaderLocator.atStation(Station.PADDINGTON).id()), new JourneyEnd(c.cardId(), OysterReaderLocator.atStation(Station.VICTORIA_STATION).id())), new Journey(new JourneyStart(c.cardId(), OysterReaderLocator.atStation(Station.OXFORD_CIRCUS).id()), new JourneyEnd(c.cardId(),  OysterReaderLocator.atStation(Station.VICTORIA_STATION).id())));


    }

    private long hoursToMillis(int hour) {
        return hour*60*60*1000;
    }

    @Test
    public void getAllCardScansForCustomer()  {
        List<JourneyEvent> expected = Arrays.asList(new JourneyStart(c.cardId(), OysterReaderLocator.atStation(Station.PADDINGTON).id()), new JourneyEnd(c.cardId(), OysterReaderLocator.atStation(Station.VICTORIA_STATION).id()), new JourneyStart(c.cardId(), OysterReaderLocator.atStation(Station.OXFORD_CIRCUS).id()), new JourneyEnd(c.cardId(), OysterReaderLocator.atStation(Station.VICTORIA_STATION).id()), new JourneyStart(c.cardId(), OysterReaderLocator.atStation(Station.HOLBORN).id()));
        List<JourneyEvent> testResult = journeyManager.getCustomerJourneyEvents(c,eventLog);


        //assert by value using flag
        boolean flag = true;
        for (int i = 0; i < testResult.size(); i++) {
            if(testResult.get(i).readerId() != expected.get(i).readerId() || testResult.get(i).cardId() != expected.get(i).cardId()) {
                flag = false;
            }
        }
        assertTrue(flag);
    }

    @Test
    public void getAllCompletedJourneys()  {
        List<Journey> testResult = journeyManager.getCustomerJourneys(c,eventLog);

        //assert by value using flag
        boolean flag=true;
        for (int i = 0; i < testResult.size(); i++){
             if(testResult.get(i).durationSeconds()!=sampleJourneys.get(i).durationSeconds() || testResult.get(i).originId()!=sampleJourneys.get(i).originId() || testResult.get(i).destinationId()!=sampleJourneys.get(i).destinationId()) {
                 flag = false;
            }
        }
        assertTrue(flag);
    }

    @Test
    public void getTotalTest() {
        BigDecimal expected = BigDecimal.valueOf(2.4);
        assertThat(costCalculator.roundToNearestPenny(costCalculator.getTotalFromJourneyList(customerJourneys, BigDecimal.ZERO)), is(costCalculator.roundToNearestPenny(expected)));
    }

    @Test
    public void getTotalForCustomerCurrentlyTravelling() {
        BigDecimal expected = BigDecimal.valueOf(4.8);
        assertThat(costCalculator.roundToNearestPenny(costCalculator.getTotalFromJourneyList(sampleJourneys,BigDecimal.ZERO)),is(costCalculator.roundToNearestPenny(expected)));
    }

    @Test
    public void getTotalForNotTravellingCustomer() {
        BigDecimal expected = BigDecimal.ZERO;
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
