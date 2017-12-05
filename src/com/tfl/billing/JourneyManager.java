package com.tfl.billing;

import com.tfl.external.Customer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class JourneyManager implements TripManager {

    private final List<JourneyEvent> eventLog;

    public JourneyManager(List<JourneyEvent> events) {
        this.eventLog = events;
    }

    List<JourneyEvent> getJourneyEvents(Customer customer) {
        List<JourneyEvent> customerJourneyEvents = new ArrayList<>();
        for (JourneyEvent journeyEvent : eventLog) {
            if (journeyEvent.cardId().equals(customer.cardId())) {
                customerJourneyEvents.add(journeyEvent);
            }
        }
        return customerJourneyEvents;
    }

    public List<Journey> getJourneys(Customer customer) {

        List<Journey> journeys = new ArrayList<>();
        HashMap<UUID,JourneyEvent> startedJourneys = new HashMap<>();
        List<JourneyEvent> customerJourneyEvents = getJourneyEvents(customer);

        for (JourneyEvent event : customerJourneyEvents) {
            if (event instanceof JourneyStart) {
                startedJourneys.put(event.cardId(),event);
            }
            if (event instanceof JourneyEnd && startedJourneys.containsKey(event.cardId())) {
                journeys.add(new Journey(startedJourneys.get(event.cardId()), event));
                startedJourneys.remove(event.cardId());
            }
        }
        return journeys;
    }
}
