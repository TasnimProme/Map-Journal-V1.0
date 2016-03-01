package com.askme.smart_map.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.askme.smart_map.activity.DatabaseBuilder.MyFile;
import com.squareup.picasso.Picasso;
import com.yalantis.guillotine.sample.R;

import java.io.File;

public class DetailGalaryActivity extends AppCompatActivity {

    String[] tempData = new String[7];
    MyFile data;
    ImageView placePic;
    ImageView backPreview;
    ImageButton placeLocation;
    TextView placeTitle;
    TextView placeDescription;
    TextView placeDateTime;


    //NEED TO FIX THIS CLASS AND CORRESPONDING XML
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_galary);
        tempData = getIntent().getStringArrayExtra("data");

        data = new MyFile(tempData);
        placePic = (ImageView) findViewById(R.id.placeImage);
        placeLocation = (ImageButton) findViewById(R.id.placeLocationButton);
        backPreview = (ImageView) findViewById(R.id.backPreview);

        placeTitle = (TextView) findViewById(R.id.placeTitle);
        placeDescription = (TextView) findViewById(R.id.placeDescription);
        placeDateTime = (TextView) findViewById(R.id.placeDateTime);


        Picasso.with(getApplicationContext())
                .load(new File(data.getPath()).getAbsoluteFile())
                .resize(1024, 600)
                .centerInside()
                .into(placePic);


        placeDateTime.setText(data.getDate());

        if (data.getType().equalsIgnoreCase("PIC")) {
            placeTitle.setVisibility(View.GONE);
            placeDescription.setVisibility(View.GONE);
        } else {
            placeTitle.setText(data.getTitle());

            if (data.getDescription().equalsIgnoreCase("NO DESCRIPTION"))
                placeDescription.setVisibility(View.GONE);
            else
                placeDescription.setText(data.getDescription());
        }

        backPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        placeLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

    }

}