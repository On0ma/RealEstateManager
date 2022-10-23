package com.openclassrooms.realestatemanager.ui;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.openclassrooms.realestatemanager.R;
import com.openclassrooms.realestatemanager.databinding.ActivityPropertyBinding;
import com.openclassrooms.realestatemanager.model.Property;

public class PropertyActivity extends AppCompatActivity {

    private ActivityPropertyBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPropertyBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Property property = (Property) getIntent().getSerializableExtra("property");

        getSupportActionBar().setTitle(property.getType().getValue());

        Bundle bundle = new Bundle();
        bundle.putSerializable("property", property);

        Fragment fragment = new PropertyDetailsFragment();
        FragmentTransaction ft;
        fragment.setArguments(bundle);
        ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.activity_property_fragment,fragment, "DETAILS");
        ft.commit();
    }
}
