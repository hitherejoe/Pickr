package com.hitherejoe.pickr.util;

import com.google.android.gms.maps.model.LatLng;
import com.hitherejoe.pickr.data.model.PointOfInterest;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;

public class MockModelsUtil {

    public static String generateRandomString() {
        return UUID.randomUUID().toString();
    }

    public static PointOfInterest createMockPointOfInterest() {
        Random randomNumber = new Random();
        PointOfInterest pointOfInterest = new PointOfInterest();
        pointOfInterest.id = generateRandomString();
        pointOfInterest.name = generateRandomString();
        pointOfInterest.address = generateRandomString();
        pointOfInterest.latLng = new LatLng(randomNumber.nextInt(500), randomNumber.nextInt(500));
        pointOfInterest.phoneNumber = generateRandomString();
        pointOfInterest.priceLevel = randomNumber.nextInt(5);
        pointOfInterest.rating = randomNumber.nextInt(10);
        pointOfInterest.latLngBounds =
                DataUtils.latitudeLongitudeToBounds(pointOfInterest.latLng, randomNumber.nextInt(5));
        pointOfInterest.websiteUri = "www.medium.com/hitherejoe";
        pointOfInterest.locale = Locale.getDefault();
        return pointOfInterest;
    }


    public static List<PointOfInterest> createListOfMockCharacters(int count) {
        List<PointOfInterest> pointOfInterests = new ArrayList<>();
        for (int i = 0; i < count; i++) pointOfInterests.add(createMockPointOfInterest());
        return pointOfInterests;
    }

}