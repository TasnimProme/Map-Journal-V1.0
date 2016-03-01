package com.askme.smart_map.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.askme.smart_map.activity.TaskToDoBuilder.Message_option;
import com.askme.smart_map.activity.TaskToDoBuilder.Remainder_option;
import com.askme.smart_map.activity.TaskToDoBuilder.Saved_option;
import com.yalantis.guillotine.sample.R;

public class TaskToDo extends Activity {

    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 999;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_to_do);


        Button saved = (Button) findViewById(R.id.go_back);
        Button notification = (Button) findViewById(R.id.notification);
        Button message = (Button) findViewById(R.id.message);


        checkSmsPermission();


        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TaskToDo.this, Message_option.class);
                startActivity(intent);
            }
        });

        notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TaskToDo.this, Remainder_option.class);
                startActivity(intent);
            }
        });

        saved.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TaskToDo.this, Saved_option.class);
                startActivity(intent);
            }
        });

    }

    private void checkSmsPermission() {

        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(TaskToDo.this,
                    Manifest.permission.SEND_SMS)) {

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(TaskToDo.this,
                        new String[]{Manifest.permission.SEND_SMS},
                        MY_PERMISSIONS_REQUEST_SEND_SMS);

                // MY_PERMISSIONS_REQUEST_SEND_SMS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_SEND_SMS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {


                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {
                    Toast.makeText(getApplicationContext(),"This feature won't work properly without sms sending permission",Toast.LENGTH_SHORT).show();
                    finish();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

        }
    }
}
