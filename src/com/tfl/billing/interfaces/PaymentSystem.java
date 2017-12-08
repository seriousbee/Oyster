package com.tfl.billing.interfaces;

import com.tfl.billing.Journey;
import com.tfl.external.Customer;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by tomaszczernuszenko on 08/12/2017.
 */
public interface PaymentSystem {
    void charge(Customer customer, List<Journey> journeys, BigDecimal totalBill);
    void chargeAllAccounts();
}
