package com.openclassrooms.realestatemanager.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.sqlite.db.SimpleSQLiteQuery;

import com.openclassrooms.realestatemanager.database.RealEstateManagerDatabase;
import com.openclassrooms.realestatemanager.databinding.ActivitySearchBinding;
import com.openclassrooms.realestatemanager.model.Property;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SearchActivty extends AppCompatActivity {

    private ActivitySearchBinding binding;
    private RealEstateManagerDatabase database;

    private int roomsNbValue = 0;
    private int photosNbvalue = 0;
    private String typeValue = "";
    private String TYPE_VALUE_RESET = "Tous";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        database = RealEstateManagerDatabase.getInstance(this);

        Property.PropertyType[] propertyTypeValues = Property.PropertyType.values();
        List<String> propertyTypeList = new ArrayList<>();
        for (Property.PropertyType type : propertyTypeValues) {
            if (type.ordinal() == 0) {
                propertyTypeList.add(TYPE_VALUE_RESET);
            }
            propertyTypeList.add(type.toString());
        }

        ArrayAdapter<String> roomsNbAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, Arrays.asList("","1","2","3","4","5","6"));
        roomsNbAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.searchRooms.setAdapter(roomsNbAdapter);

        ArrayAdapter<String> typeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, propertyTypeList);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.searchType.setAdapter(typeAdapter);

        /*ArrayAdapter<String> photosNbAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, Arrays.asList("","1","2","3","4","5","6"));
        photosNbAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.searchPhotos.setAdapter(photosNbAdapter);*/

        binding.searchRooms.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String value = String.valueOf(adapterView.getItemAtPosition(i));
                if (!value.isEmpty()) {
                    roomsNbValue = Integer.parseInt(value);
                } else {
                    roomsNbValue = 0;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        /*binding.searchPhotos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String value = String.valueOf(adapterView.getItemAtPosition(i));
                if (!value.isEmpty()) {
                    photosNbvalue = Integer.parseInt(value);
                } else {
                    photosNbvalue = 0;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });*/

        binding.searchType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String value = String.valueOf(adapterView.getItemAtPosition(i));
                if (!value.equals(TYPE_VALUE_RESET)) {
                    Property.PropertyType result = Property.PropertyType.fromString(value);
                    typeValue = result.name();
                }
                else typeValue = TYPE_VALUE_RESET;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        binding.searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchProperties();
            }
        });
    }

    private void searchProperties() {
        float priceMin = binding.searchPriceMinimum.getText().toString().isEmpty() ? 0 : Float.parseFloat(binding.searchPriceMinimum.getText().toString());
        float priceMax = binding.searchPriceMaximum.getText().toString().isEmpty() ? 0 : Float.parseFloat(binding.searchPriceMaximum.getText().toString());
        float sizeMin = binding.searchSizeMinimum.getText().toString().isEmpty() ? 0 : Float.parseFloat(binding.searchSizeMinimum.getText().toString());
        float sizeMax = binding.searchSizeMaximum.getText().toString().isEmpty() ? 0 : Float.parseFloat(binding.searchSizeMaximum.getText().toString());
        String address = binding.searchAddress.getText().toString().isEmpty() ? "" : binding.searchAddress.getText().toString();
        boolean hasSchool = binding.searchSchool.isChecked();
        boolean hasSuperMarket = binding.searchMarket.isChecked();

        String queryString = new String();
        List<Object> args = new ArrayList<>();
        boolean containsCondition = false;

        queryString += "SELECT * FROM Property";

        if (priceMin != 0 && priceMax == 0) {
            queryString += " WHERE price > ?";
            args.add(priceMin);
            containsCondition = true;
        }

        if (priceMin == 0 && priceMax != 0) {
            queryString += " WHERE price < ?";
            args.add(priceMax);
            containsCondition = true;
        }

        if (priceMin != 0 && priceMax != 0) {
            queryString += " WHERE price BETWEEN ?";
            args.add(priceMin);
            queryString += " AND ?";
            args.add(priceMax);
            containsCondition = true;
        }

        if (sizeMin != 0 && sizeMax == 0) {
            if (containsCondition) {
                queryString += " AND";
            } else {
                queryString += " WHERE";
                containsCondition = true;
            }
            queryString += " size > ?";
            args.add(sizeMin);
        }

        if (sizeMin == 0 && sizeMax != 0) {
            if (containsCondition) {
                queryString += " AND";
            } else {
                queryString += " WHERE";
                containsCondition = true;
            }
            queryString += " size < ?";
            args.add(sizeMax);
        }

        if (sizeMin != 0 && sizeMax != 0) {
            if (containsCondition) {
                queryString += " AND";
            } else {
                queryString += " WHERE";
                containsCondition = true;
            }
            queryString += " size BETWEEN ?";
            args.add(sizeMin);
            queryString += " AND ?";
            args.add(sizeMax);
        }

        if (roomsNbValue != 0) {
            if (containsCondition) {
                queryString += " AND";
            } else {
                queryString += " WHERE";
                containsCondition = true;
            }
            queryString += " roomNb IS ?";
            args.add(roomsNbValue);
        }

        if (!typeValue.equals(TYPE_VALUE_RESET)) {
            if (containsCondition) {
                queryString += " AND";
            } else {
                queryString += " WHERE";
                containsCondition = true;
            }
            queryString += " type IS ?";
            args.add(typeValue);
        }

        if (!address.isEmpty()) {
            if (containsCondition) {
                queryString += " AND";
            } else {
                queryString += " WHERE";
                containsCondition = true;
            }
            queryString += " address LIKE '%' || ?";
            args.add(address);
            queryString += " || '%'";
        }

        if (hasSchool) {
            if (containsCondition) {
                queryString += " AND";
            } else {
                queryString += " WHERE";
                containsCondition = true;
            }
            queryString += " hasSchool is ?";
            args.add(hasSchool);
        }

        if (hasSuperMarket) {
            if (containsCondition) {
                queryString += " AND";
            } else {
                queryString += " WHERE";
                containsCondition = true;
            }
            queryString += " hasSuperMarket is ?";
            args.add(hasSuperMarket);
        }

        SimpleSQLiteQuery query = new SimpleSQLiteQuery(queryString, args.toArray());
        List<Property> result = database.propertyDao().searchProperties(query);

        if (!result.isEmpty()) {
            ArrayList<Property> properties = new ArrayList<>();
            properties.addAll(result);
            Intent intent = new Intent(getApplicationContext(), SearchResultsActivity.class);
            intent.putExtra("properties", properties);
            startActivity(intent);
        } else {
            Toast.makeText(getApplicationContext(), "Aucun résultat trouvé", Toast.LENGTH_SHORT).show();
        }
    }
}
