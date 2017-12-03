package com.tfl.billing;

import com.oyster.OysterCard;
import com.tfl.external.Customer;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class TravelTrackerTest {

    @Rule
    public JUnitRuleMockery context = new JUnitRuleMockery();

    Set<UUID> cardsScanned = context.mock(Set.class);
    List<JourneyEvent> log = context.mock(List.class);
    Database database = context.mock(Database.class);
    public TravelTracker tracker = new TravelTracker(log, cardsScanned, database);

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
            oneOf(log).add(end);
            oneOf(cardsScanned).remove(test);
        }});

        tracker.cardScanned(test, reader);
    }

    @Test
    public void CardScannedDoesNotContainTest() {
        UUID test = UUID.randomUUID();
        UUID reader = UUID.randomUUID();
        JourneyEnd end= new JourneyEnd(test, reader);

        context.checking(new Expectations() {{
            oneOf(cardsScanned).contains(test); will(returnValue(false));
            oneOf(database).isRegisteredId(test); will(returnValue(true));

            oneOf(cardsScanned).add(test);
            oneOf(log).add(with(same(end)));
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
