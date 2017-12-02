package com.tfl.billing;

import com.oyster.OysterCard;
import com.oyster.ScanListener;

import java.util.UUID;

public interface CardReader {
    void register(ScanListener scanListener);

    void touch(OysterCard card);

    UUID id();
}
