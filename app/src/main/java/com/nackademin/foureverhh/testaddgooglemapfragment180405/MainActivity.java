package com.nackademin.foureverhh.testaddgooglemapfragment180405;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback{
//AppCompatActivity  FragmentActivity
    SupportMapFragment mapFragment;
    GoogleMap newMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        newMap = googleMap;
    }
}
