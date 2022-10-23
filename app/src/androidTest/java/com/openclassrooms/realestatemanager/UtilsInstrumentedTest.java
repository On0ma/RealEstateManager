package com.openclassrooms.realestatemanager;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class UtilsInstrumentedTest {

    @Test
    public void whenNoNetworkShouldReturnFalse() {
        Context context = ApplicationProvider.getApplicationContext();
        Assert.assertTrue(Utils.isInternetAvailable(context));
    }
}
