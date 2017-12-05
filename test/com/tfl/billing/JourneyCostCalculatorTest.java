package com.tfl.billing;

import com.tfl.external.Customer;
import com.tfl.external.CustomerDatabase;
import com.tfl.underground.OysterReaderLocator;
import com.tfl.underground.Station;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.*;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;


public class JourneyCostCalculatorTest {

    List<JourneyEvent> eventLog;
    Set<UUID> currentlyTravelling = new HashSet<UUID>();
    Database database;

    Date peakTime = new Date();
    Date offPeakTime = new Date();
    Customer c = CustomerDatabase.getInstance().getCustomers().get(0);
    Customer c1 = CustomerDatabase.getInstance().getCustomers().get(1);
    Customer c2 = CustomerDatabase.getInstance().getCustomers().get(2);

    private List<Journey> customerJourneys = Arrays.asList(new Journey(new JourneyEvent(c.cardId(), OysterReaderLocator.atStation(Station.PADDINGTON).id()), new JourneyEvent(c.cardId(), OysterReaderLocator.atStation(Station.VICTORIA_STATION).id())));
    JourneyCostCalculator testCalc = new JourneyCostCalculator(eventLog, customerJourneys);


    @Rule
    public JUnitRuleMockery context = new JUnitRuleMockery();

    private long getTimeInMillis(int hour) {
        return hour*60*60*1000;
    }

    @Test
    public void getAllCardScansForCustomer() throws Exception {
        List<JourneyEvent> eventLog = Arrays.asList(new JourneyEvent(c.cardId(), OysterReaderLocator.atStation(Station.PADDINGTON).id()), new JourneyEvent(c1.cardId(), OysterReaderLocator.atStation(Station.PADDINGTON).id()), new JourneyEvent(c.cardId(), OysterReaderLocator.atStation(Station.VICTORIA_STATION).id()), new JourneyEvent(c.cardId(), OysterReaderLocator.atStation(Station.OXFORD_CIRCUS).id()));
        List<JourneyEvent> expected = Arrays.asList(new JourneyEvent(c.cardId(), OysterReaderLocator.atStation(Station.PADDINGTON).id()), new JourneyEvent(c.cardId(), OysterReaderLocator.atStation(Station.VICTORIA_STATION).id()), new JourneyEvent(c.cardId(), OysterReaderLocator.atStation(Station.OXFORD_CIRCUS).id()));
        JourneyCostCalculator costCalcuator = new JourneyCostCalculator(eventLog, customerJourneys);
        eventLog=costCalcuator.getJourneyEvents(c,eventLog);
        boolean flag=true;
        for (int i=0;i< eventLog.size();i++){
            if(eventLog.get(i).readerId()!=expected.get(i).readerId() || eventLog.get(i).cardId()!=expected.get(i).cardId()){
                flag = false;
            }
        }
        assertTrue(flag);
    }

    @Test
    public void getAllCompletedJourneysForCustomerCurrentlyTravelling() throws Exception {
        eventLog = Arrays.asList(new JourneyStart(c.cardId(), OysterReaderLocator.atStation(Station.PADDINGTON).id()), new JourneyStart(c1.cardId(), OysterReaderLocator.atStation(Station.PADDINGTON).id()), new JourneyEnd(c.cardId(), OysterReaderLocator.atStation(Station.VICTORIA_STATION).id()), new JourneyStart(c.cardId(), OysterReaderLocator.atStation(Station.OXFORD_CIRCUS).id()));
        List<Journey> expected = Arrays.asList(new Journey(new JourneyEvent(c.cardId(), OysterReaderLocator.atStation(Station.PADDINGTON).id()), new JourneyEvent(c.cardId(), OysterReaderLocator.atStation(Station.VICTORIA_STATION).id())));
        JourneyCostCalculator costCalcuator = new JourneyCostCalculator(eventLog, customerJourneys);

        List<Journey> testResult = costCalcuator.getJourneys(eventLog);

        boolean flag=true;
        for (int i=0;i< testResult.size();i++){
                 if(testResult.get(i).durationSeconds()!=expected.get(i).durationSeconds() ||
                    testResult.get(i).originId()!=expected.get(i).originId() ||
                    testResult.get(i).destinationId()!=expected.get(i).destinationId()){
                flag = false;
            }
        }
        assertTrue(flag);
    }

    @Test
    public void getTotalTest() throws Exception {
        eventLog = Arrays.asList(new JourneyStart(c.cardId(), OysterReaderLocator.atStation(Station.PADDINGTON).id()), new JourneyStart(c1.cardId(), OysterReaderLocator.atStation(Station.HOLBORN).id()), new JourneyEnd(c.cardId(), OysterReaderLocator.atStation(Station.VICTORIA_STATION).id()), new JourneyEnd(c1.cardId(), OysterReaderLocator.atStation(Station.CHANCERY_LANE).id()), new JourneyStart(c.cardId(), OysterReaderLocator.atStation(Station.OXFORD_CIRCUS).id()), new JourneyEnd(c.cardId(), OysterReaderLocator.atStation(Station.VICTORIA_STATION).id()));
        BigDecimal expected = BigDecimal.valueOf(4.8);
        JourneyCostCalculator costCalcuator = new JourneyCostCalculator(eventLog, customerJourneys);
        assertThat(costCalcuator.roundToNearestPenny(costCalcuator.getTotal(customerJourneys, BigDecimal.ZERO)),is(costCalcuator.roundToNearestPenny(expected)));
    }

    @Test
    public void getTotalForCustomerCurrentlyTravelling() throws Exception {
        eventLog = Arrays.asList(new JourneyStart(c.cardId(), OysterReaderLocator.atStation(Station.PADDINGTON).id()), new JourneyStart(c1.cardId(), OysterReaderLocator.atStation(Station.HOLBORN).id()), new JourneyEnd(c.cardId(), OysterReaderLocator.atStation(Station.VICTORIA_STATION).id()), new JourneyEnd(c1.cardId(), OysterReaderLocator.atStation(Station.CHANCERY_LANE).id()), new JourneyStart(c.cardId(), OysterReaderLocator.atStation(Station.OXFORD_CIRCUS).id()), new JourneyEnd(c.cardId(), OysterReaderLocator.atStation(Station.VICTORIA_STATION).id()));
        BigDecimal expected = BigDecimal.valueOf(4.8);
        JourneyCostCalculator costCalcuator = new JourneyCostCalculator(eventLog, customerJourneys);
        assertThat(costCalcuator.roundToNearestPenny(costCalcuator.getTotalForCustomer(c, eventLog)),is(costCalcuator.roundToNearestPenny(expected)));
    }

    @Test
    public void getTotalForNotTravellingCustomer() throws Exception {
        eventLog = Arrays.asList(new JourneyStart(c.cardId(), OysterReaderLocator.atStation(Station.PADDINGTON).id()), new JourneyStart(c1.cardId(), OysterReaderLocator.atStation(Station.HOLBORN).id()), new JourneyEnd(c.cardId(), OysterReaderLocator.atStation(Station.VICTORIA_STATION).id()), new JourneyEnd(c1.cardId(), OysterReaderLocator.atStation(Station.CHANCERY_LANE).id()), new JourneyStart(c.cardId(), OysterReaderLocator.atStation(Station.OXFORD_CIRCUS).id()), new JourneyEnd(c.cardId(), OysterReaderLocator.atStation(Station.VICTORIA_STATION).id()));
        BigDecimal expected = BigDecimal.ZERO;
        JourneyCostCalculator costCalcuator = new JourneyCostCalculator(eventLog, customerJourneys);
        assertThat(costCalcuator.roundToNearestPenny(costCalcuator.getTotalForCustomer(c2, eventLog)),is(costCalcuator.roundToNearestPenny(expected)));
    }

    @Test
    public void peakTest() {
        peakTime.setTime(getTimeInMillis(6));
        offPeakTime.setTime(getTimeInMillis(12));

        assertTrue(testCalc.peak(peakTime));
        assertFalse(testCalc.peak(offPeakTime));

        peakTime.setTime(getTimeInMillis(18));
        offPeakTime.setTime(getTimeInMillis(22));

        assertTrue(testCalc.peak(peakTime));
        assertFalse(testCalc.peak(offPeakTime));

    }

    @Test
    public void correctlyRoundstoTheNearestPenny(){
        System.out.println();

        assertThat(testCalc.roundToNearestPenny(new BigDecimal(1.5110011)), is(new BigDecimal(1.51).setScale(2,BigDecimal.ROUND_HALF_UP)));
        assertThat(testCalc.roundToNearestPenny(new BigDecimal(1.0100010)), is(new BigDecimal(1.01).setScale(2,BigDecimal.ROUND_HALF_UP)));
    }

}