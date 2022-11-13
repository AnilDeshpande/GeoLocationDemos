package com.codetutor.geolocationdemos;

import android.location.Location;
import android.location.LocationManager;

import com.google.android.gms.maps.model.LatLng;

public class Utils {

    public static Location getLocationFromLatLong(LatLng latLng){
        Location tempLocation = new Location(LocationManager.GPS_PROVIDER);
        tempLocation.setLongitude(latLng.longitude);
        tempLocation.setLatitude(latLng.latitude);
        return tempLocation;
    }
}
