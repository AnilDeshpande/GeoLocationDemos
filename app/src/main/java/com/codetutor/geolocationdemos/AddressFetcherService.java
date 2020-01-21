package com.codetutor.geolocationdemos;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import androidx.annotation.Nullable;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class AddressFetcherService extends IntentService {

    private static final String TAG = AddressFetcherService.class.getSimpleName();

    private ResultReceiver resultReceiver;

    public AddressFetcherService(){
        super(TAG);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        resultReceiver = intent.getParcelableExtra(Constants.RECEIVER);
        if(resultReceiver==null){
            Log.i(TAG,"resultReceiver not received");
            return;
        }

        Location latestLocation = intent.getParcelableExtra(Constants.LOCATION_DATA_EXTRA);
        if(latestLocation==null){
            Log.i(TAG,"resultReceiver not received");
            respondWithResilt(Constants.FAILURE_RESULT, "Location not available, can't fetch address");
            return;
        }

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;
        try{
            addresses = geocoder.getFromLocation(latestLocation.getLatitude(), latestLocation.getLongitude(),1);
            if(addresses==null || addresses.size()==0){
                Log.i(TAG,"Address not avaialable");
                respondWithResilt(Constants.FAILURE_RESULT, "Address not avaialabl");
                return;
            }else {
                StringBuilder addressString = new StringBuilder();
                Address address = addresses.get(0);
                for(int i = 0;i<=address.getMaxAddressLineIndex();i++){
                    addressString.append(address.getAddressLine(i)+"\n");
                }
                respondWithResilt(Constants.SUCCESS_RESULT, addressString.toString());
            }
        }catch (IOException e){
            Log.i(TAG,"Exception while getting address");
            respondWithResilt(Constants.FAILURE_RESULT, "Exception while getting address");
        }

    }

    private void respondWithResilt(int resultCode, String resultMessage) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.RESULT_DATA_KEY, resultMessage);
        resultReceiver.send(resultCode, bundle);
    }
}
