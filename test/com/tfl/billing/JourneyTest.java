package com.tfl.billing;

import com.tfl.billing.journeyelements.JourneyEnd;
import com.tfl.billing.journeyelements.JourneyStart;
import com.tfl.external.Customer;
import com.tfl.external.CustomerDatabase;
import com.tfl.underground.OysterReaderLocator;
import com.tfl.underground.Station;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

/**
 * Created by tomaszczernuszenko on 15/11/2017.
 */
public class JourneyTest {

    private Customer c;
    private JourneyStart start;
    private JourneyEnd end;
    private Journey journey;

    @Before
    public void beforeEach(){
        c = CustomerDatabase.getInstance().getCustomers().get(0);
        start = new JourneyStart(c.cardId(), OysterReaderLocator.atStation(Station.PADDINGTON).id());
        end = new JourneyEnd(c.cardId(), OysterReaderLocator.atStation(Station.VICTORIA_STATION).id());
        journey = new Journey(start, end);
    }

    @Test
    public void journeyStartStationIdEqualsOriginId(){
        assertEquals(journey.originId(), start.readerId());
    }

    @Test
    public void journeyEndStationIdEqualsDestinationId() { assertEquals(journey.destinationId(), end.readerId()); }

    @Test
    public void correctlyCalculatesTheTime() {
        assertEquals(TimeUnit.MILLISECONDS.toSeconds(end.time()-start.time()),journey.durationSeconds());
    }

    @Test
    public void correctlyPreetifiesTheTime() {
        int durationSeconds = (int) TimeUnit.MILLISECONDS.toSeconds(end.time()-start.time());
        assertEquals(durationSeconds/60 + ":"+durationSeconds%60,journey.durationMinutes());
    }

}
