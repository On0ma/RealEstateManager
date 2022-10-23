package com.openclassrooms.realestatemanager.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.RawQuery;
import androidx.room.Update;
import androidx.sqlite.db.SupportSQLiteQuery;

import com.openclassrooms.realestatemanager.model.Property;

import java.util.List;

@Dao
public interface PropertyDao {
    @Query("SELECT * FROM Property")
    List<Property> getAllProperties();

    @Query("SELECT * FROM Property WHERE id = :propertyId")
    Property getProperty(long propertyId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long createProperty(Property property);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long[] createProperties(List<Property> properties);

    @Update
    void updateProperty(Property property);

    @RawQuery
    List<Property> searchProperties(SupportSQLiteQuery query);
}
