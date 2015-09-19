package com.hitherejoe.pickr.data.model;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.Locale;

public class Location implements Parcelable {

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

    public static Location fromPlace(Place place) {
        Location location = new Location();
        location.id = place.getId();
        location.name = place.getName().toString();
        location.address = place.getAddress().toString();
        location.latLng = place.getLatLng();
        location.phoneNumber = place.getPhoneNumber().toString();
        location.priceLevel = place.getPriceLevel();
        location.rating = place.getRating();
        location.latLngBounds = place.getViewport();
        location.websiteUri = place.getWebsiteUri();
        location.locale = place.getLocale();
        return location;
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

    public Location() {
    }

    protected Location(Parcel in) {
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

    public static final Parcelable.Creator<Location> CREATOR = new Parcelable.Creator<Location>() {
        public Location createFromParcel(Parcel source) {
            return new Location(source);
        }

        public Location[] newArray(int size) {
            return new Location[size];
        }
    };
}
