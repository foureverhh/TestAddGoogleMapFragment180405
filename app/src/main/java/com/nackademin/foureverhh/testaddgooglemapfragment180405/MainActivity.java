package com.nackademin.foureverhh.testaddgooglemapfragment180405;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    //AppCompatActivity  FragmentActivity
    SupportMapFragment mapFragment;
    GoogleMap newMap;
    //To get location
    static final int REQUEST_LOCATION = 101;
    FusedLocationProviderClient mFusedLocationClint;
    LocationRequest mLocationRequest;
    LocationCallback mLocationCallback;
    boolean mRequestingLocationUpdates = true;

    double longitude ;
    double latitude ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //Add a google map fragment to main activity
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        /*
        //Add a map fragment in the coding way
        FragmentManager manager =getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        mapFragment = SupportMapFragment.newInstance();
        transaction.add(R.id.container,mapFragment);
        transaction.commit();
        mapFragment.getMapAsync(this);
        */

        //Location
        mFusedLocationClint = LocationServices.getFusedLocationProviderClient(this);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //ask for permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        } else {

            mFusedLocationClint.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            //Got last known location. In some rare situations this can be null
                            if (location != null) {
                                //logic to handle the location
                                 longitude = location.getLongitude();
                                 latitude = location.getLatitude();
                                Log.e("Location", "Longitude is : " + String.valueOf(longitude) + "Latitude is : " + String.valueOf(latitude));
                            }
                        }
                    });
        }

        createLocationRequest();

        mLocationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if(locationResult == null){
                    return;
                }
                for(Location location : locationResult.getLocations()){
                    //Update UI with location data
                     longitude = location.getLongitude();
                     latitude = location.getLatitude();
                    //Convey locations to firebase
                }
            }
        };

        updateValuesFromBundle(savedInstanceState);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        newMap = googleMap;
        newMap.addMarker(new MarkerOptions().position(new LatLng(latitude,longitude)).title("Sample"));

    }

    //Create a LocationRequest
    protected void createLocationRequest() {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        //To get the current LocationRequest from the device for Google play services
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        //Check whether the current location settings are satisfied
        //To get the local location settings
        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        //Prompt user to change location settings
        task.addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                //All location settings are satisfied. The clint can initialize
                //location request here
            }
        });
        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    //Location settings are not satisfied, but this can be fixed
                    //by showing the user a dialog
                    ResolvableApiException resolvable = (ResolvableApiException) e;
                    try {
                        resolvable.startResolutionForResult(MainActivity.this, 0);
                    } catch (IntentSender.SendIntentException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mRequestingLocationUpdates) {
        startLocationUpdates();
        }
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //To invoke Location updates
            mFusedLocationClint.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    private void stopLocationUpdates(){
        mFusedLocationClint.removeLocationUpdates(mLocationCallback);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean("Location_updates",mRequestingLocationUpdates);
        super.onSaveInstanceState(outState);

    }

    private void updateValuesFromBundle(Bundle bundle){
        if(bundle == null){
            return;
        }
        if(bundle.keySet().contains("Location_updates")){
            mRequestingLocationUpdates = bundle.getBoolean("Location_updates");
        }
    }
}
