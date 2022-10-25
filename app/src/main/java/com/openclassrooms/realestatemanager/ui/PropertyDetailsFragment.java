package com.openclassrooms.realestatemanager.ui;

import static com.openclassrooms.realestatemanager.Utils.getLocationFromAddress;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.LatLng;
import com.mapbox.api.staticmap.v1.MapboxStaticMap;
import com.mapbox.api.staticmap.v1.StaticMapCriteria;
import com.mapbox.geojson.Point;
import com.openclassrooms.realestatemanager.R;
import com.openclassrooms.realestatemanager.databinding.FragmentPropertyDetailsBinding;
import com.openclassrooms.realestatemanager.model.Property;
import com.openclassrooms.realestatemanager.model.PropertyPhotos;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PropertyDetailsFragment extends Fragment {

    FragmentPropertyDetailsBinding binding;

    private List<PropertyPhotos> propertyPhotos = new ArrayList<>();
    private RecyclerView recyclerView;
    private PropertyPhotosAdapter adapter;

    public PropertyDetailsFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPropertyDetailsBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        Property property = (Property) getArguments().getSerializable("property");

        binding.propertyEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AddPropertyActivity.class);
                intent.putExtra("property", property);
                startActivity(intent);
            }
        });

        recyclerView = binding.fragmentPropertyRecyclerView;
        propertyPhotos.addAll(property.getPhotos());
        adapter = new PropertyPhotosAdapter(propertyPhotos);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(adapter);

        binding.propertyPrice.setText(String.valueOf(property.getPrice()) + " €");
        binding.propertyDescription.setText(property.getDescription());
        binding.propertyLocation.setText(property.getAddress());
        binding.propertySurface.setText(String.valueOf(property.getSize()) + "m²");
        binding.propertyRooms.setText(String.valueOf(property.getRoomNb()));

        Date date = new Date(property.getDateAdded());
        DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getActivity());
        binding.propertyDateAdded.setText(dateFormat.format(date));

        // Only has School
        if (property.isHasSchool() && !property.isHasSuperMarket()) {
            binding.poi.setText("Ecole");
            // Only has Supermarket
        } else if (property.isHasSuperMarket() && !property.isHasSchool()) {
            binding.poi.setText("Supermarché");
            // Has both school and supermarket
        } else if (property.isHasSchool() && property.isHasSuperMarket()) {
            binding.poi.setText("Ecole, supermarché");
            // Has none
        } else {
            binding.poi.setText("Aucun");
        }

        LatLng propertyLocation = getLocationFromAddress(getActivity(), property.getAddress());

        if (propertyLocation != null) {
        MapboxStaticMap staticMap = MapboxStaticMap.builder()
                .accessToken(getString(R.string.mapbox_access_token))
                .styleId(StaticMapCriteria.STREET_STYLE)
                .cameraPoint(Point.fromLngLat(propertyLocation.longitude, propertyLocation.latitude)) // Image's centerpoint on map
                .cameraZoom(13)
                .width(200) // Image width
                .height(200) // Image height
                .build();

        String staticMapUrl = staticMap.url().toString();
        Glide.with(this)
                .load(staticMapUrl)
                .into(binding.staticMapImage);
        } else {
            binding.staticMapImage.setVisibility(View.GONE);
        }
        return view;
    }
}
