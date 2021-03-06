package com.tfl.billing;

import com.tfl.billing.journeyelements.JourneyEnd;
import com.tfl.billing.journeyelements.JourneyEvent;
import com.tfl.billing.journeyelements.JourneyStart;
import com.tfl.external.Customer;

import java.util.ArrayList;
import java.util.List;

// Class in charge of building the journey objects required by the fare calculator.
// It is expected to handle assembling journeys of multiple customers that travel concurrently.

public class JourneyAssembler {

    public List<JourneyEvent> getJourneyEventsFor(Customer customer, List<JourneyEvent> eventLog) {
        List<JourneyEvent> customerJourneyEvents = new ArrayList<>();

        for (JourneyEvent journeyEvent : eventLog) {
            if (journeyEvent.cardId().equals(customer.cardId())) {
                customerJourneyEvents.add(journeyEvent);
            }
        }
        return customerJourneyEvents;
    }

    public List<Journey> generateJourneyList(List<JourneyEvent> customerJourneyEvents) throws Exception {
        List<Journey> journeys = new ArrayList<>();

        JourneyEvent start = null;
        for (JourneyEvent event : customerJourneyEvents) {
            if (event instanceof JourneyStart) {
                if (start != null) //the customer has not completed their earlier journey.
                    throw new Exception("Customer started a journey without finishing the previous one ");
                start = event;
            }
            if (event instanceof JourneyEnd && start != null) {
                journeys.add(new Journey(start, event));
                start = null;
            }
        }
        if (start != null)
            throw new Exception("Customer started a journey without finishing the previous one ");
        return journeys;
    }

}
