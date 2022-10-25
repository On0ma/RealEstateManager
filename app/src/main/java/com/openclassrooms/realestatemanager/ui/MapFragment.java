package com.openclassrooms.realestatemanager.ui;

import static com.openclassrooms.realestatemanager.Utils.drawableToBitmap;
import static com.openclassrooms.realestatemanager.Utils.getLocationFromAddress;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mapbox.geojson.Point;
import com.mapbox.maps.CameraOptions;
import com.mapbox.maps.MapView;
import com.mapbox.maps.Style;
import com.mapbox.maps.plugin.annotation.AnnotationConfig;
import com.mapbox.maps.plugin.annotation.AnnotationPlugin;
import com.mapbox.maps.plugin.annotation.AnnotationPluginImplKt;
import com.mapbox.maps.plugin.annotation.generated.OnPointAnnotationClickListener;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotation;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManagerKt;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions;
import com.openclassrooms.realestatemanager.R;
import com.openclassrooms.realestatemanager.databinding.FragmentMapBinding;
import com.openclassrooms.realestatemanager.model.Property;

import java.util.ArrayList;
import java.util.List;

public class MapFragment extends Fragment {

    private MapView mapview;
    private List<Property> properties = new ArrayList<>();

    FragmentMapBinding binding;

    public MapFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMapBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        double longitude = getArguments().getDouble("longitude");
        double latitude = getArguments().getDouble("latitude");

        mapview = binding.mapView;

        ArrayList<Property> propertiesData = (ArrayList<Property>) getArguments().getSerializable("properties");
        properties.addAll(propertiesData);

        onMapReady(longitude, latitude, properties);

        binding.mapResetLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mapview.getMapboxMap().setCamera(new CameraOptions.Builder().center(Point.fromLngLat(longitude,latitude)).build());
            }
        });

        return view;
    }

    private void onMapReady(double longitude, double latitude, List<Property> properties) {
        CameraOptions cameraOptions = new CameraOptions.Builder().zoom(13.5).center(Point.fromLngLat(longitude,latitude)).build();
        mapview.getMapboxMap().setCamera(cameraOptions);
        mapview.getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {
                addMapAnnotations(properties);
            }
        });
    }

    private void addMapAnnotations(List<Property> properties) {
        Bitmap icon = drawableToBitmap(AppCompatResources.getDrawable(getContext(), R.drawable.pin_drop));
        AnnotationPlugin annotationApi = AnnotationPluginImplKt.getAnnotations(mapview);
        PointAnnotationManager pointAnnotationManager = PointAnnotationManagerKt.createPointAnnotationManager(annotationApi, new AnnotationConfig());
        pointAnnotationManager.deleteAll();
        List<Double> textOffset = new ArrayList<>();
        textOffset.add(0.00);
        textOffset.add(1.00);
        Gson gson = new Gson();

        for (Property property : properties) {
            LatLng propertyLoc = getLocationFromAddress(getActivity(), property.getAddress());
            if (propertyLoc != null) {
                JsonElement json = gson.toJsonTree(property);
                PointAnnotationOptions pointAnnotationOptions = new PointAnnotationOptions()
                        .withIconImage(icon)
                        .withTextSize(12.00)
                        .withData(json)
                        .withTextOffset(textOffset)
                        .withPoint(Point.fromLngLat(propertyLoc.longitude, propertyLoc.latitude));
                pointAnnotationManager.create(pointAnnotationOptions);
            }
        }

        pointAnnotationManager.addClickListener(new OnPointAnnotationClickListener() {
            @Override
            public boolean onAnnotationClick(@NonNull PointAnnotation pointAnnotation) {
                JsonElement json = pointAnnotation.getData();
                Property property = gson.fromJson(json, Property.class);
                Intent intent = new Intent(getActivity(), PropertyActivity.class);
                intent.putExtra("property", property);
                startActivity(intent);
                return false;
            }
        });
    }
}
