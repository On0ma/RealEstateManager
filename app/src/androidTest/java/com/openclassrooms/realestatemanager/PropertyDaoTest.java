package com.openclassrooms.realestatemanager;

import static com.openclassrooms.realestatemanager.model.Property.PropertyType.LOFT;

import android.content.Context;

import androidx.room.Room;
import androidx.sqlite.db.SimpleSQLiteQuery;
import androidx.test.core.app.ApplicationProvider;

import com.openclassrooms.realestatemanager.database.RealEstateManagerDatabase;
import com.openclassrooms.realestatemanager.database.dao.PropertyDao;
import com.openclassrooms.realestatemanager.model.Property;
import com.openclassrooms.realestatemanager.model.PropertyPhotos;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PropertyDaoTest {

    // database
    private RealEstateManagerDatabase database;
    private PropertyDao propertyDao;

    // data set
    private Property PROPERTY_1 = new Property(
            Property.PropertyType.APARTMENT,
            980,
            35,
            2,
            "Description de l'appartement",
            Arrays.asList(
                    new PropertyPhotos("https://media.lesechos.com/api/v1/images/view/61b728368fe56f2a11726725/1280x720/070519160949-web-tete.jpg", "1ère photo"),
                    new PropertyPhotos("https://www.travaux.com/images/cms/original/ebcd4d3c-6a00-47d2-8165-6d9e192082af.jpeg", "2ème photo")
            ),
            "66 Boulevard Vincent Auriol, 75013",
            Property.SaleStatus.AVAILABLE,
            System.currentTimeMillis(),
            "Jean Jean",
            false,
            true);
    private List<Property> PROPERTIES = Arrays.asList(PROPERTY_1);

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        database = Room.inMemoryDatabaseBuilder(context, RealEstateManagerDatabase.class).allowMainThreadQueries().build();
        propertyDao = database.propertyDao();
    }

    @After
    public void closeDb() {
        database.close();
    }

    @Test
    public void createProperties() {
        Property expectedProperty = PROPERTY_1;
        expectedProperty.setId(1);
        long[] insertedIds = propertyDao.createProperties(Arrays.asList(expectedProperty));
        Property propertyFromDatabase = propertyDao.getProperty(insertedIds[0]);
        Assert.assertEquals(PROPERTY_1, propertyFromDatabase);
    }

    @Test
    public void createProperty() {
        Property expectedProperty = PROPERTY_1;
        expectedProperty.setId(1);
        long insertedId = propertyDao.createProperty(expectedProperty);
        Property propertyFromDatabase = propertyDao.getProperty(insertedId);
        Assert.assertEquals(expectedProperty, propertyFromDatabase);
    }

    @Test
    public void getAllProperties() {
        Property expectedProperty = PROPERTY_1;
        expectedProperty.setId(1);
        propertyDao.createProperty(expectedProperty);
        List<Property> propertiesFromDatabase = propertyDao.getAllProperties();
        Assert.assertEquals(PROPERTIES, propertiesFromDatabase);
    }

    @Test
    public void updateProperty() {
        Property expectedProperty = PROPERTY_1;
        expectedProperty.setId(1);
        propertyDao.createProperty(expectedProperty);

        Property.PropertyType propertyNewType = LOFT;
        Property newProperty = expectedProperty;
        newProperty.setType(propertyNewType);
        propertyDao.updateProperty(newProperty);
        Assert.assertEquals(propertyNewType, newProperty.getType());
    }

    @Test
    public void searchProperties() {
        Property expectedProperty = PROPERTY_1;
        expectedProperty.setId(1);
        propertyDao.createProperty(expectedProperty);
        String queryString = new String();
        List<Object> args = new ArrayList<>();
        queryString = "SELECT * FROM Property WHERE address LIKE '%' || ?";
        args.add("vincent");
        queryString += " || '%'";

        SimpleSQLiteQuery query = new SimpleSQLiteQuery(queryString, args.toArray());
        List<Property> result = database.propertyDao().searchProperties(query);
        Assert.assertEquals(expectedProperty, result.get(0));
    }
}
