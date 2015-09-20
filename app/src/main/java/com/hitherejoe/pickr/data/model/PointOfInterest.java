package com.hitherejoe.pickr.data.model;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.Locale;

public class PointOfInterest implements Parcelable, Comparable<PointOfInterest> {

    public String id;
    public String name;
    public String address;
    public LatLng latLng;
    public String phoneNumber;
    public int priceLevel;
    public float rating;
    public LatLngBounds latLngBounds;
    public String websiteUri;
    public Locale locale;

    public static PointOfInterest fromPlace(Place place) {
        PointOfInterest pointOfInterest = new PointOfInterest();
        pointOfInterest.id = place.getId();
        pointOfInterest.name = place.getName().toString();
        pointOfInterest.address = place.getAddress().toString();
        pointOfInterest.latLng = place.getLatLng();
        pointOfInterest.phoneNumber = place.getPhoneNumber().toString();
        pointOfInterest.priceLevel = place.getPriceLevel();
        pointOfInterest.rating = place.getRating();
        pointOfInterest.latLngBounds = place.getViewport();
        pointOfInterest.websiteUri = place.getWebsiteUri().toString();
        pointOfInterest.locale = place.getLocale();
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
        dest.writeInt(this.priceLevel);
        dest.writeFloat(this.rating);
        dest.writeParcelable(this.latLngBounds, 0);
        dest.writeString(this.websiteUri);
        dest.writeSerializable(this.locale);
    }

    public PointOfInterest() { }

    protected PointOfInterest(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.address = in.readString();
        this.latLng = in.readParcelable(LatLng.class.getClassLoader());
        this.phoneNumber = in.readString();
        this.priceLevel = in.readInt();
        this.rating = in.readFloat();
        this.latLngBounds = in.readParcelable(LatLngBounds.class.getClassLoader());
        this.websiteUri = in.readParcelable(Uri.class.getClassLoader());
        this.locale = (Locale) in.readSerializable();
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

        if (priceLevel != that.priceLevel) return false;
        if (Float.compare(that.rating, rating) != 0) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (address != null ? !address.equals(that.address) : that.address != null) return false;
        if (latLng != null ? !latLng.equals(that.latLng) : that.latLng != null) return false;
        if (phoneNumber != null ? !phoneNumber.equals(that.phoneNumber) : that.phoneNumber != null)
            return false;
        if (latLngBounds != null ? !latLngBounds.equals(that.latLngBounds) : that.latLngBounds != null)
            return false;
        if (websiteUri != null ? !websiteUri.equals(that.websiteUri) : that.websiteUri != null)
            return false;
        return !(locale != null ? !locale.equals(that.locale) : that.locale != null);

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (address != null ? address.hashCode() : 0);
        result = 31 * result + (latLng != null ? latLng.hashCode() : 0);
        result = 31 * result + (phoneNumber != null ? phoneNumber.hashCode() : 0);
        result = 31 * result + priceLevel;
        result = 31 * result + (rating != +0.0f ? Float.floatToIntBits(rating) : 0);
        result = 31 * result + (latLngBounds != null ? latLngBounds.hashCode() : 0);
        result = 31 * result + (websiteUri != null ? websiteUri.hashCode() : 0);
        result = 31 * result + (locale != null ? locale.hashCode() : 0);
        return result;
    }
}
