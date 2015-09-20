package com.hitherejoe.pickr.data;

import com.hitherejoe.pickr.data.model.PointOfInterest;

public class BusEvent {
    public static class PlaceAdded {
        public PointOfInterest pointOfInterest;

        public PlaceAdded(PointOfInterest pointOfInterest) {
            this.pointOfInterest = pointOfInterest;
        }
    }
}
