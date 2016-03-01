package com.askme.smart_map.activity.TaskToDoBuilder;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.askme.smart_map.activity.DatabaseBuilder.DBadapter;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.yalantis.guillotine.sample.R;

import java.util.GregorianCalendar;

public class Remainder_location extends AppCompatActivity {

    private static final long POINT_RADIUS = 1000; // in Meters
    private static final long PROX_ALERT_EXPIRATION = -1;


    Button sendSmsLocation;
    Button pickLocation;
    EditText phone_number;
    EditText message;
    LocationManager mLocationManager;
    DBadapter dBadapter;

    double pickedLat, pickedLong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remainder_location);
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


        dBadapter = new DBadapter(getApplicationContext());

        phone_number = (EditText) findViewById(R.id.phone_number_messageLocation);
        message = (EditText) findViewById(R.id.text_messageLocation);

        sendSmsLocation = (Button) findViewById(R.id.SendSMSLocation);
        pickLocation = (Button) findViewById(R.id.ChooseLocation);

        sendSmsLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (phone_number.getText().length() != 0 && message.getText().length() != 0 && !pickLocation.getText().toString().equalsIgnoreCase("Choose Location")) {

                    addProximityAlert();

                    //database
                    //dbadapter.insertfile();
                    finish();

                } else {
                    Toast.makeText(getApplicationContext(), "Something is missing", Toast.LENGTH_SHORT).show();
                }
            }
        });


        pickLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAutocompleteActivity();
            }
        });
    }

    private void addProximityAlert() {

        String number = phone_number.getText().toString();
        String messageBody = message.getText().toString();

        long time = new GregorianCalendar().getTimeInMillis();
        dBadapter.insertTask(number, messageBody, "NONE", "REMINDER+LOCATION", pickLocation.getText().toString(), "PENDING", time + "");

        int flag = dBadapter.getTaskID(time + "");

        Intent LocationIntent = new Intent(this, ProximityIntentReceiver.class);

        Bundle bundle = new Bundle();
        bundle.putBoolean("taskToDoSENDSMS", false);
        bundle.putString("taskToDoLocationNumber", number);
        bundle.putString("taskToDoLocationContent", messageBody);
        bundle.putString("taskToDoLocationLat", pickedLat + "");
        bundle.putString("taskToDoLocationLng", pickedLong + "");
        bundle.putString("taskToDoLocationFlag", flag + "");
        bundle.putString("taskToDoLocationName", pickLocation.getText().toString());
        LocationIntent.putExtras(bundle);


        PendingIntent proximityIntent = PendingIntent.getBroadcast(this, flag, LocationIntent, PendingIntent.FLAG_ONE_SHOT);

        mLocationManager.addProximityAlert(
                pickedLat, // the latitude of the central point of the alert region
                pickedLong, // the longitude of the central point of the alert region
                POINT_RADIUS, // the radius of the central point of the alert region, in meters
                PROX_ALERT_EXPIRATION, // time for this proximity alert, in milliseconds, or -1 to indicate no expiration
                proximityIntent // will be used to generate an Intent to fire when entry to or exit from the alert region is detected
        );

    }

    private void openAutocompleteActivity() {
        try {
            // The autocomplete activity_main requires Google Play Services to be available. The intent
            // builder checks this and throws an exception if it is not the case.
            Intent intent = new PlaceAutocomplete
                    .IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                    .build(this);
            startActivityForResult(intent, 999);
        } catch (GooglePlayServicesRepairableException e) {
            // Indicates that Google Play Services is either not installed or not up to date. Prompt
            // the user to correct the issue.
            GoogleApiAvailability.getInstance().getErrorDialog(this, e.getConnectionStatusCode(),
                    0 /* requestCode */).show();
        } catch (GooglePlayServicesNotAvailableException e) {
            // Indicates that Google Play Services is not available and the problem is not easily
            // resolvable.
            String message = "Google Play Services is not available: " +
                    GoogleApiAvailability.getInstance().getErrorString(e.errorCode);
        }
    }

    /**
     * Called after the autocomplete activity_main has finished to return its result.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check that the result was from the autocomplete widget.
        if (requestCode == 999) {
            if (resultCode == RESULT_OK) {
                // Get the user's selected place from the Intent.
                Place place = PlaceAutocomplete.getPlace(this, data);

                pickedLat=place.getLatLng().latitude;
                pickedLong=place.getLatLng().longitude;
                pickLocation.setText(place.getName());

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                Toast.makeText(getApplicationContext(), status.toString(), Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                // Indicates that the activity_main closed before a selection was made. For example if
                // the user pressed the back button.
            }
        }
    }

}