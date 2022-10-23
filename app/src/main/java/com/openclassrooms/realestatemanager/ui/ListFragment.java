package com.openclassrooms.realestatemanager.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.openclassrooms.realestatemanager.R;
import com.openclassrooms.realestatemanager.databinding.FragmentListBinding;
import com.openclassrooms.realestatemanager.model.Property;

import java.util.ArrayList;
import java.util.List;

public class ListFragment extends Fragment implements PropertyAdapter.PropertyDisplayCallback {

    FragmentListBinding binding;

    private List<Property> properties = new ArrayList<>();
    private PropertyAdapter adapter;
    private RecyclerView recyclerView;

    public ListFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentListBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        recyclerView = binding.fragmentListRecyclerView;

        ArrayList<Property> propertiesData = (ArrayList<Property>) getArguments().getSerializable("properties");
        properties.addAll(propertiesData);

        adapter = new PropertyAdapter(properties, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);

        updateProperties();

        return view;
    }

    private void updateProperties() {
        if (properties.size() == 0) {
            binding.fragmentListRecyclerView.setVisibility(View.GONE);
            binding.fragmentListNoProperties.setVisibility(View.VISIBLE);
        } else {
            binding.fragmentListRecyclerView.setVisibility(View.VISIBLE);
            binding.fragmentListNoProperties.setVisibility(View.GONE);
            adapter.updateProperties(properties);
        }
    }

    @Override
    public void onPropertyClick(Property property) {
        int width = (int) (getResources().getDisplayMetrics().widthPixels / getResources().getDisplayMetrics().density);
        if (width > 600) {
            Bundle bundle = new Bundle();
            bundle.putSerializable("property", property);
            Fragment detailsFrag = new PropertyDetailsFragment();
            FragmentTransaction ft;
            detailsFrag.setArguments(bundle);
            ft = getChildFragmentManager().beginTransaction();
            ft.replace(R.id.fragment_property_details_tablet ,detailsFrag, "DETAILS");
            ft.commit();
        } else {
            Intent intent = new Intent(getActivity(), PropertyActivity.class);
            intent.putExtra("property", property);
            startActivity(intent);
        }

    }
}
