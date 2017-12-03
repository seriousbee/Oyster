package com.tfl.billing;

import com.oyster.OysterCard;
import com.tfl.external.Customer;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.*;

public class TravelTrackerTest {

    @Rule
    public JUnitRuleMockery context = new JUnitRuleMockery();

    Set<UUID> cardsScanned = context.mock(Set.class);
    List<JourneyEvent> log = context.mock(List.class);
    public TravelTracker tracker = new TravelTracker(log, cardsScanned);

    static final BigDecimal OFF_PEAK_JOURNEY_PRICE = new BigDecimal(2.40);
    static final BigDecimal PEAK_JOURNEY_PRICE = new BigDecimal(3.20);

    @Test
    public void TestChargeAccounts() {
        List<Customer> testCustomers = new ArrayList<Customer>() {
            {
                this.add(new Customer("Customer1", new OysterCard()));
                this.add(new Customer("Customer2", new OysterCard()));
            }
        };
    }

    @Test
    public void CardScannedContainsTest() {
        UUID test = UUID.randomUUID();
        UUID reader = UUID.randomUUID();
        JourneyEnd end= new JourneyEnd(test, reader);

        context.checking(new Expectations() {{
            oneOf(cardsScanned).contains(test);
            will(returnValue(true));
            ignoring(log).add(end);
            ignoring(cardsScanned).remove(test);
        }});

        tracker.cardScanned(test, reader);
    }

    @Test
    public void TestConnectOne() {
        CardReader reader = context.mock(CardReader.class);
        context.checking(new Expectations() {{
            oneOf(reader).register(tracker);
        }});
        tracker.connect(reader);
    }

    @Test
    public void TestConnectMultiple() {
        CardReader reader = context.mock(CardReader.class);

        context.checking(new Expectations() {{
            exactly(3).of(reader).register(tracker);
        }});

        tracker.connect(reader,reader,reader);
    }
}
