package com.example.francisco.w4d4week.view.main_activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.example.francisco.w4d4week.Util.CONSTANTS;
import com.example.francisco.w4d4week.model.AddressResponse;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by FRANCISCO on 24/08/2017.
 */

public class MainActivityPresenter implements MainActivityContract.Presenter, LocationListener {
    MainActivityContract.View view;
    Context context;
    private static final String TAG = "MainActivityPresenter";

    @Override
    public void attachView(MainActivityContract.View view) {
        this.view = view;
    }

    @Override
    public void detachView() {
        this.view = null;
    }

    @Override
    public void setContext(Context context) {
        this.context = context;
    }

    public void MakeRestCall(Location currentLocation) {
        try {
            String currentLatLng = currentLocation.getLatitude() + "," + currentLocation.getLongitude();
            HttpUrl url = new HttpUrl.Builder()
                    .scheme("https")
                    .host(CONSTANTS.BASE_URL)
                    .addPathSegments(CONSTANTS.PATH)
                    .addQueryParameter("latlng", currentLatLng)
                    .addQueryParameter("key", CONSTANTS.KEY)
                    .build();
            System.out.println(url);
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    //Log.d(TAG, "onResponse: " + response.body().string());

                    // Get Gson object
                    Gson gson = new Gson();

                    final AddressResponse addressResponse = gson.fromJson(response.body().string(), AddressResponse.class);

                    ((Activity) view).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                view.SendResultRestCall(addressResponse.getResults().get(0).getFormattedAddress());
                            }catch (Exception ex){
                                Toast.makeText(context, "Introduce a valid latitude and longitude", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            });
        }catch(Exception ex){view.showError("No Internet/location, please turn on and open the app again");}
    }

    @Override
    public void checkPermissions(int MY_PERMISSIONS_REQUEST_LOCATION) {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(((Activity) view),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(((Activity) view),
                    Manifest.permission.READ_CONTACTS)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(((Activity) view),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},MY_PERMISSIONS_REQUEST_LOCATION);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
        else{
            //getLocation();
        }
    }

    public void getLocation() {
        try {
            Log.d(TAG, "getLocation: ");
            FusedLocationProviderClient fusedLocationProviderClient;
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener((Activity) view, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            view.sendLocation(location);
                            try {
                                Log.d(TAG, "onSuccess: " + location.toString());
                            }catch(Exception ex){
                                Toast.makeText(context, "For some reason we cannot obtain the location, awesome!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "onFailure: ");
                }
            });
        }catch(Exception ex){view.showError("No Internet/location, please turn on and open the app again");}

        LocationManager locationManager = (LocationManager) ((Activity) view).getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000,100, this);
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "onLocationChanged: " + location.toString());
        view.sendLocation(location);
    }

    @Override
    public void onStatusChanged(String s, int status, Bundle extras) {
        Log.d(TAG, "onStatusChanged: " + s);
        
        switch (status){
            case LocationProvider.AVAILABLE:
                Log.d(TAG, "onStatusChanged: ");
                break;
                
            case LocationProvider.OUT_OF_SERVICE:
                Log.d(TAG, "onStatusChanged: ");
                break;

            case LocationProvider.TEMPORARILY_UNAVAILABLE:
                Log.d(TAG, "onStatusChanged: ");
                break;
        }
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d(TAG, "onProviderEnabled: ");
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d(TAG, "onProviderDisabled: ");
    }
}
