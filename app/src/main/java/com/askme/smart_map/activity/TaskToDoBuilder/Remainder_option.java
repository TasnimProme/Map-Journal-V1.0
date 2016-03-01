package com.askme.smart_map.activity.TaskToDoBuilder;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.yalantis.guillotine.sample.R;

public class Remainder_option extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remainder_option);

        Button time_notification = (Button) findViewById(R.id.time_notification);
        Button location_notification = (Button) findViewById(R.id.location_notification);

        time_notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Remainder_option.this, Remainder_time.class);
                startActivity(intent);
            }
        });


        location_notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Remainder_option.this, Remainder_location.class);
                startActivity(intent);
            }
        });
    }


}
