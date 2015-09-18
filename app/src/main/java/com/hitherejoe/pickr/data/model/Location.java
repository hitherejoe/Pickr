package com.hitherejoe.pickr.data.model;

import com.google.android.gms.location.places.Place;

public class Location {

    public String id;
    public String name;

    public static Location fromPlace(Place place) {
        Location location = new Location();
        location.id = place.getId();
        location.name = place.getName().toString();
        return location;
    }
}
