package com.openclassrooms.realestatemanager.model;

import java.io.Serializable;
import java.util.Objects;

public class PropertyPhotos implements Serializable {

    private String photoUrl;
    private String description;

    public PropertyPhotos(String photoUrl, String description) {
        this.photoUrl = photoUrl;
        this.description = description;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PropertyPhotos that = (PropertyPhotos) o;
        return photoUrl.equals(that.photoUrl) && description.equals(that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(photoUrl, description);
    }

    @Override
    public String toString() {
        return "PropertyPhotos{" +
                "photoUrl='" + photoUrl + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
