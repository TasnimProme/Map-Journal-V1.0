package com.askme.smart_map.activity.TaskToDoBuilder;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.yalantis.guillotine.sample.R;

public class Saved_option extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_option);

        final Button saved_notification = (Button) findViewById(R.id.saved_notification);
        Button saved_message = (Button) findViewById(R.id.saved_message);


        saved_notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Saved_option.this, Saved_notification.class);
                startActivity(intent);
            }
        });

        saved_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Saved_option.this, Saved_message.class);
                startActivity(intent);
            }
        });
    }

}
