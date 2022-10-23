package com.openclassrooms.realestatemanager.ui;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.openclassrooms.realestatemanager.databinding.ActivityAddPropertyPhotosItemBinding;
import com.openclassrooms.realestatemanager.model.PropertyPhotos;

import java.util.List;

public class AddPropertyPhotosAdapter extends RecyclerView.Adapter<AddPropertyPhotosAdapter.ViewHolder> {

    private ActivityAddPropertyPhotosItemBinding itemBinding;
    private List<PropertyPhotos> propertyPhotos;
    private PropertyPhotoCallback callback;

    AddPropertyPhotosAdapter(List<PropertyPhotos> propertyPhotos, PropertyPhotoCallback callback) {
        this.propertyPhotos = propertyPhotos;
        this.callback = callback;
    }

    void updatePhotos(List<PropertyPhotos> propertyPhotos) {
        this.propertyPhotos = propertyPhotos;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        itemBinding = ActivityAddPropertyPhotosItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new AddPropertyPhotosAdapter.ViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindTo(propertyPhotos.get(position), callback);
    }

    @Override
    public int getItemCount() {
        return propertyPhotos.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView itemPicture;
        private EditText itemLegend;
        private ImageView itemDelete;

        public ViewHolder(ActivityAddPropertyPhotosItemBinding itemBinding) {
            super(itemBinding.getRoot());
            itemPicture = itemBinding.propertyPicture;
            itemLegend = itemBinding.propertyPictureLegend;
            itemDelete = itemBinding.propertyPictureDelete;
        }

        public void bindTo(PropertyPhotos propertyPhoto, PropertyPhotoCallback callback) {
            Glide.with(itemPicture.getContext())
                    .load(propertyPhoto.getPhotoUrl())
                    .into(itemPicture);
            itemLegend.setText(propertyPhoto.getDescription());
            itemLegend.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    propertyPhoto.setDescription(editable.toString());
                }
            });
            itemDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    callback.onPropertyDelete(propertyPhoto);
                }
            });
        }
    }

    public interface PropertyPhotoCallback {
        void onPropertyDelete(PropertyPhotos propertyPhoto);
    }
}

