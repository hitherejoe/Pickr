package com.hitherejoe.pickr.data;

import com.hitherejoe.pickr.data.model.Location;

public class BusEvent {
    public static class PlaceAdded {
        public Location location;

        public PlaceAdded(Location location) {
            this.location = location;
        }
    }
}
