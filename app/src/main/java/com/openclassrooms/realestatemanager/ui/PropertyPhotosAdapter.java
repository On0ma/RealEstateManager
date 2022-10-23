package com.openclassrooms.realestatemanager.ui;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.openclassrooms.realestatemanager.databinding.FragmentPropertyItemBinding;
import com.openclassrooms.realestatemanager.model.PropertyPhotos;

import java.util.List;

public class PropertyPhotosAdapter extends RecyclerView.Adapter<PropertyPhotosAdapter.ViewHolder> {

    private FragmentPropertyItemBinding itemBinding;
    private List<PropertyPhotos> propertyPhotos;

    PropertyPhotosAdapter(List<PropertyPhotos> propertyPhotos) {
        this.propertyPhotos = propertyPhotos;
    }

    void updatePhotos(List<PropertyPhotos> propertyPhotos) {
        this.propertyPhotos = propertyPhotos;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        itemBinding = FragmentPropertyItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new PropertyPhotosAdapter.ViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindTo(propertyPhotos.get(position));
    }

    @Override
    public int getItemCount() {
        return propertyPhotos.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView itemPicture;
        private TextView itemLegend;

        public ViewHolder(FragmentPropertyItemBinding itemBinding) {
            super(itemBinding.getRoot());
            itemPicture = itemBinding.propertyPicture;
            itemLegend = itemBinding.propertyPictureLegend;
        }

        public void bindTo(PropertyPhotos propertyPhoto) {
            Glide.with(itemPicture.getContext())
                    .load(propertyPhoto.getPhotoUrl())
                    .into(itemPicture);
            itemLegend.setText(propertyPhoto.getDescription());
        }
    }


}
