package com.tfl.billing;

import com.tfl.billing.interfaces.Database;
import com.tfl.billing.journeyelements.JourneyEnd;
import com.tfl.billing.journeyelements.JourneyEvent;
import com.tfl.billing.journeyelements.JourneyStart;
import com.tfl.billing.legacyinteraction.DBHelper;
import com.tfl.external.Customer;
import com.tfl.underground.OysterReaderLocator;
import com.tfl.underground.Station;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static junit.framework.TestCase.assertTrue;

public class JourneyAssemblerTest {
    private Customer c;
    private Customer c1;
    private Customer c2;
    private List<JourneyEvent> eventLog;
    private JourneyAssembler journeyAssembler;

    public JourneyAssemblerTest() {
        Database database = new DBHelper();
        this.c = database.getCustomers().get(0);
        this.c1 = database.getCustomers().get(1);
        this.c2 = database.getCustomers().get(2);
        eventLog = Arrays.asList(
                new JourneyStart(c.cardId(), OysterReaderLocator.atStation(Station.PADDINGTON).id()),
                new JourneyStart(c1.cardId(), OysterReaderLocator.atStation(Station.PIMLICO).id()),
                new JourneyStart(c2.cardId(), OysterReaderLocator.atStation(Station.GREEN_PARK).id()),
                new JourneyEnd(c.cardId(), OysterReaderLocator.atStation(Station.VICTORIA_STATION).id()),
                new JourneyStart(c.cardId(), OysterReaderLocator.atStation(Station.OXFORD_CIRCUS).id()),
                new JourneyEnd(c2.cardId(), OysterReaderLocator.atStation(Station.MONUMENT).id()),
                new JourneyEnd(c.cardId(), OysterReaderLocator.atStation(Station.VICTORIA_STATION).id()),
                new JourneyEnd(c1.cardId(), OysterReaderLocator.atStation(Station.QUEENSWAY).id()),
                new JourneyStart(c.cardId(), OysterReaderLocator.atStation(Station.HOLBORN).id()),
                new JourneyEnd(c.cardId(), OysterReaderLocator.atStation(Station.LANCASTER_GATE).id())
        );
        journeyAssembler = new JourneyAssembler();
    }

    @Test
    public void getJourneyEventsFor() {
        List<JourneyEvent> expected = Arrays.asList(
                new JourneyStart(c.cardId(), OysterReaderLocator.atStation(Station.PADDINGTON).id()),
                new JourneyEnd(c.cardId(), OysterReaderLocator.atStation(Station.VICTORIA_STATION).id()),
                new JourneyStart(c.cardId(), OysterReaderLocator.atStation(Station.OXFORD_CIRCUS).id()),
                new JourneyEnd(c.cardId(), OysterReaderLocator.atStation(Station.VICTORIA_STATION).id()),
                new JourneyStart(c.cardId(), OysterReaderLocator.atStation(Station.HOLBORN).id()),
                new JourneyEnd(c.cardId(), OysterReaderLocator.atStation(Station.LANCASTER_GATE).id())
        );
        List<JourneyEvent> testResult = journeyAssembler.getJourneyEventsFor(c, eventLog);

        boolean flag = true;
        for (int i = 0; i < expected.size(); i++) {
            if (!(((expected.get(i) instanceof JourneyStart) && (testResult.get(i) instanceof JourneyStart))
                    || ((expected.get(i) instanceof JourneyEnd) && (testResult.get(i) instanceof JourneyEnd)))) {
                flag = false;
            }
            if (!((expected.get(i).cardId() == testResult.get(i).cardId()) && (expected.get(i).readerId() == testResult.get(i).readerId()))) {
                flag = false;
            }
        }
        assertTrue(flag);
    }

    @Test
    public void generateJourneyListIgnoresTimes() {
        List<Journey> testResult = null;
        List<Journey> expected = Arrays.asList(
                new Journey(new JourneyStart(c.cardId(), OysterReaderLocator.atStation(Station.PADDINGTON).id()), new JourneyEnd(c.cardId(), OysterReaderLocator.atStation(Station.VICTORIA_STATION).id())),
                new Journey(new JourneyStart(c.cardId(), OysterReaderLocator.atStation(Station.OXFORD_CIRCUS).id()), new JourneyEnd(c.cardId(), OysterReaderLocator.atStation(Station.VICTORIA_STATION).id())),
                new Journey(new JourneyStart(c.cardId(), OysterReaderLocator.atStation(Station.HOLBORN).id()), new JourneyEnd(c.cardId(), OysterReaderLocator.atStation(Station.LANCASTER_GATE).id()))
        );
        try {
            testResult = journeyAssembler.generateJourneyList(journeyAssembler.getJourneyEventsFor(c, eventLog));
        } catch (Exception e) {
            // If exception is thrown in this case there is something wrong
            assertTrue(false);
        }

        boolean flag = true;
        for (int i = 0; i < expected.size(); i++) {
            if (!(((expected.get(i) instanceof Journey) && (testResult.get(i) instanceof Journey))
                    || ((expected.get(i) instanceof Journey) && (testResult.get(i) instanceof Journey)))) {
                flag = false;
            }
            if (!((expected.get(i).originId() == ((Journey) testResult.get(i)).originId()) && (expected.get(i).destinationId() == testResult.get(i).destinationId()))) {
                flag = false;
            }
        }
        assertTrue(flag);
    }

}
