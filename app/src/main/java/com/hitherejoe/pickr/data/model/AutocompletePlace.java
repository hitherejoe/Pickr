package com.hitherejoe.pickr.data.model;

public class AutocompletePlace {

    public CharSequence placeId;
    public CharSequence description;

    public AutocompletePlace(CharSequence placeId, CharSequence description) {
        this.placeId = placeId;
        this.description = description;
    }

    @Override
    public String toString() {
        return description.toString();
    }
}