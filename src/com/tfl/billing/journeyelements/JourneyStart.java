package com.tfl.billing.journeyelements;

import org.joda.time.DateTime;

import java.util.UUID;


public class JourneyStart extends JourneyEvent {

    public JourneyStart(UUID cardId, UUID readerId) {
        super(cardId, readerId);
    }

    public JourneyStart(UUID cardId, UUID readerId, DateTime date) {
        super(cardId, readerId, date);
    }
}
