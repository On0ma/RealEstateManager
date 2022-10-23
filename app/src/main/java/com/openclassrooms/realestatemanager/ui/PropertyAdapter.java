package com.openclassrooms.realestatemanager.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.openclassrooms.realestatemanager.databinding.FragmentListItemBinding;
import com.openclassrooms.realestatemanager.model.Property;

import java.util.List;

public class PropertyAdapter extends RecyclerView.Adapter<PropertyAdapter.ViewHolder> {

    private FragmentListItemBinding itemBinding;
    private List<Property> properties;
    private PropertyDisplayCallback callback;

    PropertyAdapter(List<Property> properties, PropertyDisplayCallback callback) {
        this.properties = properties;
        this.callback = callback;
    }

    void updateProperties(List<Property> properties) {
        this.properties = properties;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        itemBinding = FragmentListItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new PropertyAdapter.ViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindTo(properties.get(position), callback);
    }

    @Override
    public int getItemCount() {
        return properties.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView itemPicture;
        private TextView itemType;
        private TextView itemAdress;
        private TextView itemPrice;

        public ViewHolder(FragmentListItemBinding itemBinding) {
            super(itemBinding.getRoot());
            itemPicture = itemBinding.propertyItemImage;
            itemType = itemBinding.propertyItemType;
            itemAdress = itemBinding.propertyItemAdress;
            itemPrice = itemBinding.propertyItemPrice;
        }

        public void bindTo(Property property, PropertyDisplayCallback callback) {
            if (!property.getPhotos().isEmpty()) {
                Glide.with(itemPicture.getContext())
                        .load(property.getPhotos().get(0).getPhotoUrl())
                        .into(itemPicture);
            }

            itemType.setText(property.getType().getValue());
            itemAdress.setText(property.getAddress());
            int price = (int) property.getPrice();
            itemPrice.setText(String.valueOf(price)+" €");

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    callback.onPropertyClick(property);
                }
            });
        }
    }

    public interface PropertyDisplayCallback {
        void onPropertyClick(Property property);
    }
}
