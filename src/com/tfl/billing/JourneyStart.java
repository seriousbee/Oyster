package com.tfl.billing;

import java.util.UUID;
import org.joda.time.DateTime;


public class JourneyStart extends JourneyEvent {

    public JourneyStart(UUID cardId, UUID readerId) {
        super(cardId, readerId);
    }

    public JourneyStart(UUID cardId, UUID readerId, DateTime date) {
        super(cardId, readerId, date);
    }
}
