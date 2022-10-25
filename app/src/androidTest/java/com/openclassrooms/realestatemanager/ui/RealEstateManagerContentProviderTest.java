package com.openclassrooms.realestatemanager.ui;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.*;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.openclassrooms.realestatemanager.database.RealEstateManagerDatabase;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class RealEstateManagerContentProviderTest {

    private ContentResolver mContentResolver;

    @Before
    public void setUp() {
        final Context context = ApplicationProvider.getApplicationContext();
        Room.inMemoryDatabaseBuilder(context, RealEstateManagerDatabase.class).allowMainThreadQueries().build();
        mContentResolver = context.getContentResolver();
    }

    @Test
    public void contentProviderGetAllQuery() {
        final Cursor cursor = mContentResolver.query(RealEstateManagerContentProvider.URI_PROPERTY, new String[]{""},null, null, null);
        assertThat(cursor, notNullValue());
        assertThat(cursor.getCount(), is(3));
        cursor.close();
    }
}