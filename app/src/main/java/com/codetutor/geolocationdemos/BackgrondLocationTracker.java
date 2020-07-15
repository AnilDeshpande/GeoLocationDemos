package com.codetutor.geolocationdemos;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.JobIntentService;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.logging.Handler;

class BackgrondLocationTracker extends JobIntentService {

    private static final String TAG = BackgrondLocationTracker.class.getSimpleName();

    private static final int UPDATE_INTERVAL = 5000; // 5 seconds

    FusedLocationProviderClient locationProviderClient;
    LocationRequest locationRequest;
    LocationCallback locationCallback;
    private Location currentLocation;

    static final int JOB_ID = 1000;

    static void enqueueWork(Context context, Intent work) {
        Log.i(TAG, "enqueueWork");
        enqueueWork(context, BackgrondLocationTracker.class, JOB_ID, work);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        Log.i(TAG, "onHandleWork");
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(UPDATE_INTERVAL);
        locationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        locationCallback = new LocationCallback(){
            @Override
            public void onLocationAvailability(LocationAvailability locationAvailability) {
                super.onLocationAvailability(locationAvailability);
                if(locationAvailability.isLocationAvailable()){
                    Log.i(TAG,"Location is available");
                }else {
                    Log.i(TAG,"Location is unavailable");
                }
            }

            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                Log.i(TAG,"Location result is available");
            }
        };

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED &&
           ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)== PackageManager.PERMISSION_GRANTED){
            Log.i(TAG,"As permissions are persent, Location request made");
            locationProviderClient.requestLocationUpdates(locationRequest,locationCallback, this.getMainLooper());
            locationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    currentLocation = location;
                    Log.i(TAG,"Location result is available: "+currentLocation.getLongitude()+", "+currentLocation.getLatitude());
                }
            });
        }
    }

    @Override
    public IBinder onBind(@NonNull Intent intent) {
        Log.i(TAG,"OnBind");
        return null;
    }

    @Override
    public void onDestroy() {
        Log.i(TAG,"onDestroy");
        super.onDestroy();
        locationProviderClient.removeLocationUpdates(locationCallback);
    }
}
