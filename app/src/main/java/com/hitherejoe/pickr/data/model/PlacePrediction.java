package com.hitherejoe.pickr.data.model;

public class PlacePrediction {

    public CharSequence placeId;
    public CharSequence description;

    public PlacePrediction(CharSequence placeId, CharSequence description) {
        this.placeId = placeId;
        this.description = description;
    }

    @Override
    public String toString() {
        return description.toString();
    }
}