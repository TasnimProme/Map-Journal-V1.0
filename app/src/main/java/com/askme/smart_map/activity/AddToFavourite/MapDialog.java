package com.askme.smart_map.activity.AddToFavourite;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.yalantis.guillotine.sample.R;

/**
 * Created by gadhaa on 2/17/2016.
 */
public class MapDialog extends AppCompatActivity {

    GoogleMap googleMap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mapview);
        //show error dialog if GoolglePlayServices not available
        if (!isGooglePlayServicesAvailable()) {
            finish();
        }
        SupportMapFragment fragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFavourite);
        googleMap = fragment.getMap();

        int markerCount=Integer.parseInt(getIntent().getStringExtra("markerCOUNT"));
        if(markerCount==1)
        {
            Double lat=Double.parseDouble(getIntent().getStringExtra("markerLAT"));
            Double lng=Double.parseDouble(getIntent().getStringExtra("markerLONG"));

            CameraPosition position = CameraPosition.builder()
                    .target(new LatLng(lat,
                            lng))
                    .zoom(16f)
                    .bearing(0.0f)
                    .tilt(0.0f)
                    .build();

            googleMap.animateCamera(CameraUpdateFactory
                    .newCameraPosition(position), null);
            googleMap.setTrafficEnabled(true);
            googleMap.getUiSettings().setAllGesturesEnabled(true);
            googleMap.getUiSettings().setTiltGesturesEnabled(true);
            googleMap.addMarker(new MarkerOptions().position(new LatLng(lat,lng)).title(getIntent().getStringExtra("markerNAME")).icon(BitmapDescriptorFactory.fromResource(R.drawable.mypos)));

        }
        else
        {

        }

        ((ImageButton) findViewById(R.id.mapViewBack)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }


    private boolean isGooglePlayServicesAvailable() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            GooglePlayServicesUtil.getErrorDialog(status, this, 0).show();
            return false;
        }
    }

}



