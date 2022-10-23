package com.openclassrooms.realestatemanager;

import static org.junit.Assert.assertEquals;

import com.openclassrooms.realestatemanager.ui.LoanActivity;

import org.junit.Test;

public class LoanActivityUnitTest {

    @Test
    public void loanMonthlyPaymentIsExact() {
        int expectedMontlyPayment = 1229;
        assertEquals(expectedMontlyPayment, LoanActivity.calculateLoan(250000, (float) 1.70, 240));
    }
}
