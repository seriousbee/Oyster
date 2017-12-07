package com.tfl.billing;

import com.tfl.billing.journeyelements.JourneyEnd;
import com.tfl.billing.journeyelements.JourneyEvent;
import com.tfl.billing.journeyelements.JourneyStart;
import com.tfl.external.Customer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class JourneyManager {

    private List<JourneyEvent> eventLog;

    JourneyManager() {
        this.eventLog = new ArrayList<>();
    }

    void updateEvents(List<JourneyEvent> eventLog) {
        this.eventLog = Collections.unmodifiableList(eventLog);
    }

    private List<JourneyEvent> getJourneyEventsFor(Customer customer) {
        List<JourneyEvent> customerJourneyEvents = new ArrayList<>();

        for (JourneyEvent journeyEvent : eventLog) {
            if (journeyEvent.cardId().equals(customer.cardId())) {
                try {
                    customerJourneyEvents.add(journeyEvent.clone());
                } catch (Exception e){
                    System.out.println("JourneyEvent could not be cloned");
                    break;
                }
            }
        }
        return customerJourneyEvents;
    }

    List<Journey> generateJourneyList(List<JourneyEvent> customerJourneyEvents) throws Exception{
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

    public List<Journey> generateJourneyList(Customer customer) throws Exception {
        List<JourneyEvent> customerJourneyEvents = getJourneyEventsFor(customer);
        return Collections.unmodifiableList(generateJourneyList(customerJourneyEvents));
    }
}
