package com.openclassrooms.realestatemanager.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

@Entity
public class Property implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private long id;

    private PropertyType type;
    private float price;
    private float size;
    private int roomNb;
    private String description;
    private List<PropertyPhotos> photos;
    private String address;
    private SaleStatus status;
    private long dateAdded;
    private long dateSold;
    private String propertySeller;
    private boolean hasSchool;
    private boolean hasSuperMarket;
    private double longitude;
    private double latitude;

    public Property(PropertyType type, float price, float size, int roomNb, String description, List<PropertyPhotos> photos, String address, SaleStatus status, long dateAdded, String propertySeller, boolean hasSchool, boolean hasSuperMarket) {
        this.type = type;
        this.price = price;
        this.size = size;
        this.roomNb = roomNb;
        this.description = description;
        this.photos = photos;
        this.address = address;
        this.status = status;
        this.dateAdded = dateAdded;
        this.propertySeller = propertySeller;
        this.hasSchool = hasSchool;
        this.hasSuperMarket = hasSuperMarket;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public PropertyType getType() {
        return type;
    }

    public void setType(PropertyType type) {
        this.type = type;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public float getSize() {
        return size;
    }

    public void setSize(float size) {
        this.size = size;
    }

    public int getRoomNb() {
        return roomNb;
    }

    public void setRoomNb(int roomNb) {
        this.roomNb = roomNb;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<PropertyPhotos> getPhotos() {
        return photos;
    }

    public void setPhotos(List<PropertyPhotos> photos) {
        this.photos = photos;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public SaleStatus getStatus() {
        return status;
    }

    public void setStatus(SaleStatus status) {
        this.status = status;
    }

    public long getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(long dateAdded) {
        this.dateAdded = dateAdded;
    }

    public long getDateSold() {
        return dateSold;
    }

    public void setDateSold(long dateSold) {
        this.dateSold = dateSold;
    }

    public String getPropertySeller() {
        return propertySeller;
    }

    public void setPropertySeller(String propertySeller) {
        this.propertySeller = propertySeller;
    }

    public boolean isHasSchool() {
        return hasSchool;
    }

    public void setHasSchool(boolean hasSchool) {
        this.hasSchool = hasSchool;
    }

    public boolean isHasSuperMarket() {
        return hasSuperMarket;
    }

    public void setHasSuperMarket(boolean hasSuperMarket) {
        this.hasSuperMarket = hasSuperMarket;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Property property = (Property) o;
        return id == property.id && Float.compare(property.price, price) == 0 && Float.compare(property.size, size) == 0 && roomNb == property.roomNb && dateAdded == property.dateAdded && dateSold == property.dateSold && hasSchool == property.hasSchool && hasSuperMarket == property.hasSuperMarket && Double.compare(property.longitude, longitude) == 0 && Double.compare(property.latitude, latitude) == 0 && type == property.type && description.equals(property.description) && photos.equals(property.photos) && address.equals(property.address) && status == property.status && propertySeller.equals(property.propertySeller);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, type, price, size, roomNb, description, photos, address, status, dateAdded, dateSold, propertySeller, hasSchool, hasSuperMarket, longitude, latitude);
    }

    @Override
    public String toString() {
        return "Property{" +
                "id=" + id +
                ", type=" + type +
                ", price=" + price +
                ", size=" + size +
                ", roomNb=" + roomNb +
                ", description='" + description + '\'' +
                ", photos=" + photos +
                ", address='" + address + '\'' +
                ", status=" + status +
                ", dateAdded=" + dateAdded +
                ", dateSold=" + dateSold +
                ", propertySeller='" + propertySeller + '\'' +
                ", hasSchool=" + hasSchool +
                ", hasSuperMarket=" + hasSuperMarket +
                ", longitude=" + longitude +
                ", latitude=" + latitude +
                '}';
    }

    public enum PropertyType {
        APARTMENT("Appartement"),
        LOFT("Loft"),
        HOUSE("Maison");

        private String value;

        PropertyType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public static PropertyType fromString(String text) {
            for (PropertyType propertyType : PropertyType.values()) {
                if (propertyType.value.equals(text)) {
                    return propertyType;
                }
            }
            return null;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    public enum SaleStatus {
        AVAILABLE("Disponible"),
        SOLD("Vendu");

        private String value;

        SaleStatus(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public static SaleStatus fromString(String text) {
            for (SaleStatus saleStatus : SaleStatus.values()) {
                if (saleStatus.value.equals(text)) {
                    return saleStatus;
                }
            }
            return null;
        }

        @Override
        public String toString() {
            return value;
        }
    }
}
