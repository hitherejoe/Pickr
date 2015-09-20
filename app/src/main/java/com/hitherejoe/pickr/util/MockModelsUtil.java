package com.hitherejoe.pickr.util;

import com.hitherejoe.pickr.data.model.PointOfInterest;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MockModelsUtil {

    public static String generateRandomString() {
        return UUID.randomUUID().toString();
    }

    public static PointOfInterest createMockCharacter() {
        PointOfInterest pointOfInterest = new PointOfInterest();
        pointOfInterest.name = generateRandomString();
        return pointOfInterest;
    }

    public static List<PointOfInterest> createListOfMockCharacters(int count) {
        List<PointOfInterest> pointOfInterests = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            pointOfInterests.add(createMockCharacter());
        }
        return pointOfInterests;
    }

}