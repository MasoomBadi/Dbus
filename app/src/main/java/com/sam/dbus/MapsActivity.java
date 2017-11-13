package com.sam.dbus;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity {


    private GoogleMap googleMap;

    public Double camlat;
    public Double camlon;

    public ArrayList<String> coordinatearrlat;
    public ArrayList<String> coordinatearrlon;
    public ArrayList<String> pickupnamesmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        setUpMapIfNeeded();
        googleMap.clear();


        coordinatearrlat = getIntent().getStringArrayListExtra("coordinatearrlat");
        coordinatearrlon = getIntent().getStringArrayListExtra("coordinatearrlon");
        pickupnamesmap = getIntent().getStringArrayListExtra("pickupnamesmap");

        camlat = Double.valueOf(coordinatearrlat.get(1));
        camlon = Double.valueOf(coordinatearrlon.get(1));


        for (int i = 0; i < coordinatearrlat.size(); i++) {
            Log.d("Data", coordinatearrlat.get(i));
            Log.d("Data", coordinatearrlon.get(i));
        }

        for (int k = 0; k < coordinatearrlat.size(); k++) {
            Double Lat = Double.parseDouble(coordinatearrlat.get(k));
            Double Lon = Double.parseDouble(coordinatearrlon.get(k));
            String PickupName = (pickupnamesmap.get(k));
            googleMap.addMarker(new MarkerOptions().position(new LatLng(Lat, Lon)).title(k+"." + " " + PickupName).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

        }
        LatLng CameraZoom = new LatLng(camlat, camlon);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(CameraZoom, 13));

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void setUpMapIfNeeded() {
        // check if we have got the googleMap already
        if (googleMap == null) {
            googleMap = ((SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map)).getMap();

        }

    }


}
