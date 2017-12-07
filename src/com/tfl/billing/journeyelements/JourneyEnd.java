package com.tfl.billing.journeyelements;

import org.joda.time.DateTime;

import java.util.UUID;

public class JourneyEnd extends JourneyEvent {

    public JourneyEnd(UUID cardId, UUID readerId) {
        super(cardId, readerId);
    }

    public JourneyEnd(UUID cardId, UUID readerId, DateTime date) {
        super(cardId, readerId, date);
    }
}
