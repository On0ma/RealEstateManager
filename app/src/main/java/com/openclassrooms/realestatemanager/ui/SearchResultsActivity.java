package com.openclassrooms.realestatemanager.ui;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.openclassrooms.realestatemanager.R;
import com.openclassrooms.realestatemanager.databinding.ActivitySearchResultsBinding;
import com.openclassrooms.realestatemanager.model.Property;

import java.util.ArrayList;

public class SearchResultsActivity extends AppCompatActivity {

    private ActivitySearchResultsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchResultsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Résultats de recherche");

        ArrayList<Property> properties = (ArrayList<Property>) getIntent().getSerializableExtra("properties");

        Bundle bundle = new Bundle();
        bundle.putSerializable("properties", properties);

        Fragment fragment = new ListFragment();
        FragmentTransaction ft;
        fragment.setArguments(bundle);
        ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.activity_search_results_fragment,fragment, "LIST");
        ft.commit();
    }
}
