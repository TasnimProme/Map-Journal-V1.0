package com.askme.smart_map.activity.TaskToDoBuilder;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.yalantis.guillotine.sample.R;

public class Message_option extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_option);

        Button time_message = (Button) findViewById(R.id.time_message);
        Button location_message = (Button) findViewById(R.id.location_message);

        time_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Message_option.this, Message_time.class);
                startActivity(intent);
            }
        });

        location_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Message_option.this, Message_location.class);
                startActivity(intent);
            }
        });
    }
}
