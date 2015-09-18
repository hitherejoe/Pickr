package com.hitherejoe.pickr.util;

import com.hitherejoe.pickr.data.model.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MockModelsUtil {

    public static String generateRandomString() {
        return UUID.randomUUID().toString();
    }

    public static Location createMockCharacter() {
        Location location = new Location();
        location.name = generateRandomString();
        return location;
    }

    public static List<Location> createListOfMockCharacters(int count) {
        List<Location> locations = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            locations.add(createMockCharacter());
        }
        return locations;
    }

}