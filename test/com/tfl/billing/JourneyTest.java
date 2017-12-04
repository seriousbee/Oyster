package com.tfl.billing;

import com.tfl.external.Customer;
import com.tfl.external.CustomerDatabase;
import com.tfl.underground.OysterReaderLocator;
import com.tfl.underground.Station;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

/**
 * Created by tomaszczernuszenko on 15/11/2017.
 */
public class JourneyTest {
    static Customer c;
    static JourneyStart start;
    static JourneyEnd end;
    static Journey journey;


    @BeforeClass
    public static void beforeAll(){
        c = CustomerDatabase.getInstance().getCustomers().get(0);
        start = new JourneyStart(c.cardId(), OysterReaderLocator.atStation(Station.PADDINGTON).id());
        end = new JourneyEnd(c.cardId(), OysterReaderLocator.atStation(Station.VICTORIA_STATION).id());
        journey = new Journey(start, end);
    }

    @Before
    public void beforeEach(){
        c = CustomerDatabase.getInstance().getCustomers().get(0);
        start = new JourneyStart(c.cardId(), OysterReaderLocator.atStation(Station.PADDINGTON).id());
        end = new JourneyEnd(c.cardId(), OysterReaderLocator.atStation(Station.VICTORIA_STATION).id());
        journey = new Journey(start, end);

        //Don't worry about commiting stuff to memory - all data is removed after the program is run.
    }

    @Test
    public void firstTest(){
        assertTrue(true);
    }

    @Test
    public void journeyStartStationIdEqualsOriginId(){
        assertEquals(journey.originId(), start.readerId());
    }

    @Test
    public void journeyEndStationIdEqualsDestinationId() { assertEquals(journey.destinationId(), end.readerId()); }

    @Test
    public void journeyTimeCalculatedCorrectlyForVeryShortJourneys(){

    }

    @Test
    public void journeyTimeWith0ofLessTravelTimeRaisesException(){

    }

    @Test
    public void durationSecondsTest() {
        assertEquals(TimeUnit.MILLISECONDS.toSeconds(end.time()-start.time()),journey.durationSeconds());
    }

    @Test
    public void durationMinutesTest() {
        int durationSeconds = (int) TimeUnit.MILLISECONDS.toSeconds(end.time()-start.time());
        assertEquals(durationSeconds/60 + ":"+durationSeconds%60,journey.durationMinutes());
    }

}
