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
    public Uri websiteUri;
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
        pointOfInterest.websiteUri = place.getWebsiteUri();
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
        dest.writeParcelable(this.websiteUri, 0);
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
}
