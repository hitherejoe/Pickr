package com.hitherejoe.pickr.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

public class PointOfInterest implements Parcelable, Comparable<PointOfInterest> {

    public String id;
    public String name;
    public String address;
    public LatLng latLng;
    public String phoneNumber;
    public LatLngBounds latLngBounds;

    public static PointOfInterest fromPlace(Place place) {
        PointOfInterest pointOfInterest = new PointOfInterest();
        pointOfInterest.id = place.getId();
        pointOfInterest.name = place.getName().toString();
        pointOfInterest.address = place.getAddress().toString();
        pointOfInterest.latLng = place.getLatLng();
        pointOfInterest.phoneNumber = place.getPhoneNumber().toString();
        pointOfInterest.latLngBounds = place.getViewport();
        return pointOfInterest;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeString(this.address);
        dest.writeParcelable(this.latLng, 0);
        dest.writeString(this.phoneNumber);
        dest.writeParcelable(this.latLngBounds, 0);
    }

    public PointOfInterest() { }

    protected PointOfInterest(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.address = in.readString();
        this.latLng = in.readParcelable(LatLng.class.getClassLoader());
        this.phoneNumber = in.readString();
        this.latLngBounds = in.readParcelable(LatLngBounds.class.getClassLoader());
    }

    public static final Parcelable.Creator<PointOfInterest> CREATOR = new Parcelable.Creator<PointOfInterest>() {
        public PointOfInterest createFromParcel(Parcel source) {
            return new PointOfInterest(source);
        }

        public PointOfInterest[] newArray(int size) {
            return new PointOfInterest[size];
        }
    };

    @Override
    public int compareTo(PointOfInterest pointOfInterest) {
        return name.compareToIgnoreCase(pointOfInterest.name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PointOfInterest that = (PointOfInterest) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (address != null ? !address.equals(that.address) : that.address != null) return false;
        if (latLng != null ? !latLng.equals(that.latLng) : that.latLng != null) return false;
        if (phoneNumber != null ? !phoneNumber.equals(that.phoneNumber) : that.phoneNumber != null)
            return false;
        return !(latLngBounds != null ? !latLngBounds.equals(that.latLngBounds) : that.latLngBounds != null);

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (address != null ? address.hashCode() : 0);
        result = 31 * result + (latLng != null ? latLng.hashCode() : 0);
        result = 31 * result + (phoneNumber != null ? phoneNumber.hashCode() : 0);
        result = 31 * result + (latLngBounds != null ? latLngBounds.hashCode() : 0);
        return result;
    }
}
