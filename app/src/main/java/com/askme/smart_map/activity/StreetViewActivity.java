package com.askme.smart_map.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;

import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.SupportStreetViewPanoramaFragment;
import com.google.android.gms.maps.model.LatLng;
import com.yalantis.guillotine.sample.R;


public class StreetViewActivity extends AppCompatActivity {

    Double lat;
    Double lng;
    String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.streetview);


        lat = getIntent().getDoubleExtra("streetViewLat", 0.0);
        lng = getIntent().getDoubleExtra("streetViewLng", 0.0);
        title = getIntent().getStringExtra("streetViewTitle");


        final SupportStreetViewPanoramaFragment streetViewPanoramaFragment = (SupportStreetViewPanoramaFragment) getSupportFragmentManager().findFragmentById(R.id.markerStreetView);
        streetViewPanoramaFragment.getStreetViewPanoramaAsync(new OnStreetViewPanoramaReadyCallback() {
            @Override
            public void onStreetViewPanoramaReady(StreetViewPanorama panoramaView) {
                panoramaView.setPosition(new LatLng(lat, lng));
                panoramaView.setStreetNamesEnabled(true);
                panoramaView.setPanningGesturesEnabled(true);
                panoramaView.setZoomGesturesEnabled(true);
                panoramaView.setUserNavigationEnabled(true);
            }
        });


        ((ImageButton) findViewById(R.id.streetViewBack)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

}