package com.openclassrooms.realestatemanager;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class UtilsUnitTest {
    @Test
    public void dollarToEuroIsCorrect() {
        int expected = 101;
        assertEquals(expected, Utils.convertDollarToEuro(100));
    }

    @Test
    public void euroToDollarIsCorrect() {
        int expected = 99;
        assertEquals(expected, Utils.convertEuroToDollar(100));
    }

    @Test
    public void dateFromLongIsCorrect() {
        long timestamp = 1666432800000L;
        String expectedDate = "22/10/2022";
        assertEquals(expectedDate, Utils.getDateFromLong(timestamp));
    }
}
