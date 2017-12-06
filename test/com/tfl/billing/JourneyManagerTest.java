package com.tfl.billing;

import com.tfl.external.Customer;
import com.tfl.external.CustomerDatabase;
import com.tfl.underground.OysterReaderLocator;
import com.tfl.underground.Station;
import org.junit.Test;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class JourneyManagerTest {

    Date peakTime;
    Date offPeakTime;
    Customer c;
    Customer c1;
    Customer c2;
    private List<JourneyEvent> eventLog;
    JourneyManager journeyManager;
    List<Journey> sampleJourneys;

    public JourneyManagerTest() {
        peakTime = new Date();
        offPeakTime = new Date();
        c = CustomerDatabase.getInstance().getCustomers().get(0);
        c1 = CustomerDatabase.getInstance().getCustomers().get(1);
        c2 = CustomerDatabase.getInstance().getCustomers().get(2);
        eventLog = Arrays.asList(new JourneyStart(c.cardId(), OysterReaderLocator.atStation(Station.PADDINGTON).id()), new JourneyStart(c1.cardId(), OysterReaderLocator.atStation(Station.HOLBORN).id()), new JourneyEnd(c.cardId(), OysterReaderLocator.atStation(Station.VICTORIA_STATION).id()), new JourneyEnd(c1.cardId(), OysterReaderLocator.atStation(Station.CHANCERY_LANE).id()), new JourneyStart(c.cardId(), OysterReaderLocator.atStation(Station.OXFORD_CIRCUS).id()), new JourneyEnd(c.cardId(), OysterReaderLocator.atStation(Station.VICTORIA_STATION).id()), new JourneyStart(c.cardId(), OysterReaderLocator.atStation(Station.HOLBORN).id()));
        journeyManager = new JourneyManager();
        sampleJourneys = Arrays.asList(new Journey(new JourneyStart(c.cardId(), OysterReaderLocator.atStation(Station.PADDINGTON).id()), new JourneyEnd(c.cardId(), OysterReaderLocator.atStation(Station.VICTORIA_STATION).id())), new Journey(new JourneyStart(c.cardId(), OysterReaderLocator.atStation(Station.OXFORD_CIRCUS).id()), new JourneyEnd(c.cardId(),  OysterReaderLocator.atStation(Station.VICTORIA_STATION).id())));
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

}
