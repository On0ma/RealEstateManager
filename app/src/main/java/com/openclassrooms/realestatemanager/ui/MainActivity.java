package com.openclassrooms.realestatemanager.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.CancellationToken;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnTokenCanceledListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.openclassrooms.realestatemanager.R;
import com.openclassrooms.realestatemanager.database.RealEstateManagerDatabase;
import com.openclassrooms.realestatemanager.databinding.ActivityMainBinding;
import com.openclassrooms.realestatemanager.model.Property;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    FusedLocationProviderClient mFusedLocationProviderClient;
    int PERMISSION_ID = 44;

    private ActivityMainBinding binding;

    private RealEstateManagerDatabase database;

    private double longitude;
    private double latitude;

    Fragment currentFragment;
    FragmentTransaction ft;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        setSupportActionBar(binding.toolbar);

        database = RealEstateManagerDatabase.getInstance(this);

        if (GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getApplicationContext()) == ConnectionResult.SUCCESS) {
            mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
            getLastLocation();
        } else {
            Bundle bundle = new Bundle();

            ArrayList<Property> properties = new ArrayList<>();
            ArrayList<Property> propertiesSearch = (ArrayList<Property>) getIntent().getSerializableExtra("properties");
            if (propertiesSearch == null) {
                properties.addAll(database.propertyDao().getAllProperties());
            } else {
                properties.addAll(propertiesSearch);
            }

            bundle.putSerializable("properties", properties);
            // Set Default Fragment
            currentFragment = new ListFragment();
            handleFragmentLoading(currentFragment, bundle, "MAP");
            handleBottomNav(bundle);
        }

        handleDrawerNav();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.nav_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.nav_add:
                startAddActivity();
                return true;
            case R.id.nav_search:
                startSearchActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void startAddActivity() {
        Intent intent = new Intent(getApplicationContext(), AddPropertyActivity.class);
        startActivity(intent);
    }

    private void startSearchActivity() {
        Intent intent = new Intent(getApplicationContext(), SearchActivty.class);
        startActivity(intent);
    }

    // Open the drawer on click on the menu button
    private void handleDrawerNav() {
        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.drawerLayout.open();
            }
        });

        binding.drawerNavigation.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.loan_button) {
                    Intent intent = new Intent(getApplicationContext(), LoanActivity.class);
                    startActivity(intent);
                }
                return true;
            }
        });
    }

    // Load a different fragment for each bottom tab
    private void handleBottomNav(Bundle bundle) {
        binding.bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    // Load the list view
                    case R.id.nav_list:
                        currentFragment = new ListFragment();
                        handleFragmentLoading(currentFragment, bundle, "LIST");
                        return true;
                    // Load the map view
                    case R.id.nav_map:
                        currentFragment = new MapFragment();
                        handleFragmentLoading(currentFragment, bundle, "MAP");
                        return true;
                }
                return false;
            }
        });
    }

    private void handleFragmentLoading(Fragment currFrag, Bundle bundle, String fragTag) {
        currentFragment.setArguments(bundle);
        ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container_view, currFrag, fragTag);
        ft.commit();
    }

    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                int priority = LocationRequest.PRIORITY_HIGH_ACCURACY;
                mFusedLocationProviderClient.getCurrentLocation(priority, new CancellationToken() {
                    @NonNull
                    @Override
                    public CancellationToken onCanceledRequested(@NonNull OnTokenCanceledListener onTokenCanceledListener) {
                        return null;
                    }

                    @Override
                    public boolean isCancellationRequested() {
                        return false;
                    }
                }).addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        Location location = task.getResult();
                        if (location != null) {
                            // Get user location
                            longitude = location.getLongitude();
                            latitude = location.getLatitude();
                            // Put Location in bundle
                            Bundle bundle = new Bundle();
                            bundle.putDouble("longitude", longitude);
                            bundle.putDouble("latitude", latitude);

                            ArrayList<Property> properties = new ArrayList<>();
                            ArrayList<Property> propertiesSearch = (ArrayList<Property>) getIntent().getSerializableExtra("properties");
                            if (propertiesSearch == null) {
                                properties.addAll(database.propertyDao().getAllProperties());
                            } else {
                                properties.addAll(propertiesSearch);
                            }

                            bundle.putSerializable("properties", properties);
                            // Set Default Fragment
                            currentFragment = new ListFragment();
                            handleFragmentLoading(currentFragment, bundle, "MAP");

                            handleBottomNav(bundle);
                        }
                    }
                });
            } else {
                Toast.makeText(this, "Please turn on your location", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            requestPermissions();
        }
    }

    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
        }, PERMISSION_ID);
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }
        }
    }
}
