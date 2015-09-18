package com.hitherejoe.pickr.data.remote;

import com.hitherejoe.pickr.data.model.Location;

import retrofit.http.GET;
import retrofit.http.Path;
import rx.Observable;

public interface AndroidBoilerplateService {

    String ENDPOINT = "http://swapi.co/api";

    @GET("/people/{personId}")
    Observable<Location> getCharacter(@Path("personId") int id);

}
