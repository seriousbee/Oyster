package com.tfl.billing.interfaces;

import com.oyster.ScanListener;

// Interface to handle client-database interactions related to cardReaders

public interface CardReader {
    void register(ScanListener scanListener);
}
