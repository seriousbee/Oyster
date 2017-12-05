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


    @Rule
    public JUnitRuleMockery context = new JUnitRuleMockery();

    //testing assets
    Date peakTime = new Date();
    Date offPeakTime = new Date();
    Customer c = CustomerDatabase.getInstance().getCustomers().get(0);
    Customer c1 = CustomerDatabase.getInstance().getCustomers().get(1);
    Customer c2 = CustomerDatabase.getInstance().getCustomers().get(2);

    //initilaize required object
    private List<JourneyEvent> eventLog = Arrays.asList(new JourneyEvent(c.cardId(), OysterReaderLocator.atStation(Station.PADDINGTON).id()), new JourneyEvent(c1.cardId(), OysterReaderLocator.atStation(Station.PADDINGTON).id()), new JourneyEvent(c.cardId(), OysterReaderLocator.atStation(Station.VICTORIA_STATION).id()), new JourneyEvent(c.cardId(), OysterReaderLocator.atStation(Station.OXFORD_CIRCUS).id()));
    private List<Journey> customerJourneys = Arrays.asList(new Journey(new JourneyEvent(c.cardId(), OysterReaderLocator.atStation(Station.PADDINGTON).id()), new JourneyEvent(c.cardId(), OysterReaderLocator.atStation(Station.VICTORIA_STATION).id())));
    JourneyCostCalculator costCalcuator = new JourneyCostCalculator(eventLog, customerJourneys);



    private long getTimeInMillis(int hour) {
        return hour*60*60*1000;
    }

    @Test
    public void getAllCardScansForCustomer() throws Exception {
        List<JourneyEvent> expected = Arrays.asList(new JourneyEvent(c.cardId(), OysterReaderLocator.atStation(Station.PADDINGTON).id()), new JourneyEvent(c.cardId(), OysterReaderLocator.atStation(Station.VICTORIA_STATION).id()), new JourneyEvent(c.cardId(), OysterReaderLocator.atStation(Station.OXFORD_CIRCUS).id()));
        List<JourneyEvent> testResult = costCalcuator.getJourneyEvents(c,eventLog);

        //assert by value using flag
        boolean flag=true;
        for (int i=0;i< testResult.size();i++){
            if(testResult.get(i).readerId()!=expected.get(i).readerId() || testResult.get(i).cardId()!=expected.get(i).cardId()){
                flag = false;
            }
        }
        assertTrue(flag);
    }

    @Test
    public void getAllCompletedJourneysForCustomerCurrentlyTravelling() throws Exception {
        List<Journey> expected = Arrays.asList(new Journey(new JourneyEvent(c.cardId(), OysterReaderLocator.atStation(Station.PADDINGTON).id()), new JourneyEvent(c.cardId(), OysterReaderLocator.atStation(Station.VICTORIA_STATION).id())));
        List<Journey> testResult = costCalcuator.getJourneys(eventLog);

        //assert by value using flag
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
        BigDecimal expected = BigDecimal.valueOf(2.4);
        assertThat(costCalcuator.roundToNearestPenny(costCalcuator.getTotal(customerJourneys, BigDecimal.ZERO)),is(costCalcuator.roundToNearestPenny(expected)));
    }

    @Test
    public void getTotalForCustomerCurrentlyTravelling() throws Exception {
        eventLog = Arrays.asList(new JourneyStart(c.cardId(), OysterReaderLocator.atStation(Station.PADDINGTON).id()), new JourneyStart(c1.cardId(), OysterReaderLocator.atStation(Station.HOLBORN).id()), new JourneyEnd(c.cardId(), OysterReaderLocator.atStation(Station.VICTORIA_STATION).id()), new JourneyEnd(c1.cardId(), OysterReaderLocator.atStation(Station.CHANCERY_LANE).id()), new JourneyStart(c.cardId(), OysterReaderLocator.atStation(Station.OXFORD_CIRCUS).id()), new JourneyEnd(c.cardId(), OysterReaderLocator.atStation(Station.VICTORIA_STATION).id()));
        BigDecimal expected = BigDecimal.valueOf(4.8);
        assertThat(costCalcuator.roundToNearestPenny(costCalcuator.getTotalForCustomer(c, eventLog)),is(costCalcuator.roundToNearestPenny(expected)));
    }

    @Test
    public void getTotalForNotTravellingCustomer() throws Exception {
        eventLog = Arrays.asList(new JourneyStart(c.cardId(), OysterReaderLocator.atStation(Station.PADDINGTON).id()), new JourneyStart(c1.cardId(), OysterReaderLocator.atStation(Station.HOLBORN).id()), new JourneyEnd(c.cardId(), OysterReaderLocator.atStation(Station.VICTORIA_STATION).id()), new JourneyEnd(c1.cardId(), OysterReaderLocator.atStation(Station.CHANCERY_LANE).id()), new JourneyStart(c.cardId(), OysterReaderLocator.atStation(Station.OXFORD_CIRCUS).id()), new JourneyEnd(c.cardId(), OysterReaderLocator.atStation(Station.VICTORIA_STATION).id()));
        BigDecimal expected = BigDecimal.ZERO;
        assertThat(costCalcuator.roundToNearestPenny(costCalcuator.getTotalForCustomer(c2, eventLog)),is(costCalcuator.roundToNearestPenny(expected)));
    }

    @Test
    public void peakTest() {
        peakTime.setTime(getTimeInMillis(6));
        offPeakTime.setTime(getTimeInMillis(12));

        assertTrue(costCalcuator.peak(peakTime));
        assertFalse(costCalcuator.peak(offPeakTime));

        peakTime.setTime(getTimeInMillis(18));
        offPeakTime.setTime(getTimeInMillis(22));

        assertTrue(costCalcuator.peak(peakTime));
        assertFalse(costCalcuator.peak(offPeakTime));

    }

    @Test
    public void correctlyRoundstoTheNearestPenny(){
        System.out.println();

        assertThat(costCalcuator.roundToNearestPenny(new BigDecimal(1.5110011)), is(new BigDecimal(1.51).setScale(2,BigDecimal.ROUND_HALF_UP)));
        assertThat(costCalcuator.roundToNearestPenny(new BigDecimal(1.0100010)), is(new BigDecimal(1.01).setScale(2,BigDecimal.ROUND_HALF_UP)));
    }

}