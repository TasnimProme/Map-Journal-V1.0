package com.askme.smart_map.activity;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.askme.smart_map.activity.DatabaseBuilder.DBadapter;
import com.askme.smart_map.activity.DatabaseBuilder.MyFile;
import com.askme.smart_map.activity.ShowingMarker.GetMyLocation;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.squareup.picasso.Picasso;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;
import com.yalantis.guillotine.sample.R;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class addFileActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener,
        DatePickerDialog.OnDateSetListener {
    private static final int PLACE_PICKER_REQUEST = 3;
    private static final int RESULT_LOAD_IMAGE = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;
    String choosenDate = "";
    Dialog dialog;
    EditText editTitle, editDescription;
    DBadapter dBadapter;
    GetMyLocation myLocation;
    String currentLocation;
    ImageButton saveFile, pickLocation;
    ImageView imagePreview;
    String picturePath = "";
    Boolean saveOnBackPress = false;
    String[] months = {"JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC"};
    private int x = 0;
    private Uri mCapturedImageURI;
    private String placeLocalName;

    public static String formateDateFromstring(String inputFormat, String outputFormat, String inputDate) {

        Date parsed = null;
        String outputDate = "";

        SimpleDateFormat df_input = new SimpleDateFormat(inputFormat, java.util.Locale.getDefault());
        SimpleDateFormat df_output = new SimpleDateFormat(outputFormat, java.util.Locale.getDefault());

        try {
            parsed = df_input.parse(inputDate);
            outputDate = df_output.format(parsed);

        } catch (ParseException e) {
        }

        return outputDate;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_image_activity);


        myLocation = new GetMyLocation(getApplicationContext());

        currentLocation = "";
        if (myLocation.canGetLocation()) {
            currentLocation = myLocation.getLatitude() + "," + myLocation.getLongitude();
            placeLocalName = myLocation.getLatitude() + "," + myLocation.getLongitude();
        } else {
            showSettingsAlert();
        }


        imagePreview = (ImageView) findViewById(R.id.previewUploadedImage);
        pickLocation = (ImageButton) findViewById(R.id.fileLocation);
        saveFile = (ImageButton) findViewById(R.id.fileSave);
        editTitle = (EditText) findViewById(R.id.addFileTitle);
        editDescription = (EditText) findViewById(R.id.addFileDescription);
        dialog = new Dialog(this);
        dBadapter = new DBadapter(getApplicationContext());
        // Construct the data source


        TextWatcher changeIcon = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (editTitle.getText().length() == 0 && editDescription.getText().length() == 0 && picturePath.length() == 0) {
                    saveOnBackPress = false;
                    saveFile.setBackgroundResource(R.drawable.back);
                } else

                {
                    saveOnBackPress = true;
                    saveFile.setBackgroundResource(android.R.drawable.ic_menu_save);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };

        editTitle.addTextChangedListener(changeIcon);
        editDescription.addTextChangedListener(changeIcon);

    }

    public void btnPickLocation(View view) {

        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

        try {
            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    public void chooseTime() {
        Calendar now = Calendar.getInstance();
        TimePickerDialog tpd = TimePickerDialog.newInstance(
                addFileActivity.this,
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE), false

        );
        tpd.show(getFragmentManager(), "Timepickerdialog");
    }

    public void chooseDate(View view) {
        Calendar now = Calendar.getInstance();
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                addFileActivity.this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        dpd.showYearPickerFirst(true);
        dpd.show(getFragmentManager(), "Datepickerdialog");

    }

    public void saveOnClick(View view) {

        if (saveOnBackPress) {
            MyFile image = new MyFile();
            if (editTitle.getText().length() == 0)
                image.setTitle("NO TITLE");
            else
                image.setTitle(editTitle.getText().toString());
            if (editDescription.getText().length() == 0)
                image.setDescription("NO DESCRIPTION");
            else
                image.setDescription(editDescription.getText().toString());

            if (picturePath.length() == 0) {
                image.setType("NOTE");
                image.setPath("NO IMAGE");
            } else {
                if (editTitle.getText().length() == 0 && editDescription.getText().length() == 0)
                    image.setType("PICTURE");
                else
                    image.setType("NOTE,PICTURE");
                image.setPath(picturePath);
            }

            image.setLocation(currentLocation);
            if (choosenDate.length() == 0) {
                Calendar c = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("dd MMM,yyyy hh:mm a");
                String strDate = sdf.format(c.getTime());
                image.setDate(strDate);
            } else
                image.setDate(choosenDate);

            String latLong[] = currentLocation.split(",");
            LatLng locationLatLng = new LatLng(Double.parseDouble(latLong[0]), Double.parseDouble(latLong[1]));


            image.setLocationLocalName(placeLocalName);

            dBadapter.insertFile(image);
            finish();
        }
    }

    public void btnAddOnClick(View view) {
        dialog.setContentView(R.layout.custom_dialog_box);
        dialog.setTitle("Upload A Photo");
        Button btnExit = (Button) dialog.findViewById(R.id.btnExit);
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.findViewById(R.id.btnChoosePath).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activeGallery();
            }
        });
        dialog.findViewById(R.id.btnTakePhoto).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activeTakePhoto();
            }
        });

        // show dialog on screen
        dialog.show();
    }

    /**
     * take a photo
     */
    private void activeTakePhoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            x++;
            String fileName = "SmartMapCapture" + x + ".jpg";
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, fileName);
            mCapturedImageURI = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    /**
     * to gallery
     */
    private void activeGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, RESULT_LOAD_IMAGE);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {

            case PLACE_PICKER_REQUEST:
                if (resultCode == RESULT_OK) {
                    Place place = PlacePicker.getPlace(data, this);
                    String toastMsg = String.format("Place: %s", place.getName());
                    Toast.makeText(this, toastMsg, Toast.LENGTH_SHORT).show();

                    placeLocalName = place.getName().toString();

                    currentLocation = place.getLatLng().latitude + "," + place.getLatLng().longitude;
                }

            case RESULT_LOAD_IMAGE:
                if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);

                    Picasso.with(getApplicationContext())
                            .load(new File(cursor.getString(columnIndex)).getAbsoluteFile())
                            .resize(1024, 600)
                            .centerInside()
                            .into(imagePreview);

                    if (picturePath.length() == 0) {
                        imagePreview.setVisibility(View.VISIBLE);
                        Animation animationFadeIn = AnimationUtils.loadAnimation(this, R.anim.abc_slide_in_bottom);
                        imagePreview.startAnimation(animationFadeIn);
                    }

                    picturePath = cursor.getString(columnIndex);
                    cursor.close();

                    saveOnBackPress = true;

                    saveFile.setBackgroundResource(android.R.drawable.ic_menu_save);
                    dialog.dismiss();

                }
            case REQUEST_IMAGE_CAPTURE:
                if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
                    String[] projection = {MediaStore.Images.Media.DATA};
                    Cursor cursor = managedQuery(mCapturedImageURI, projection, null, null, null);
                    int column_index_data = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    cursor.moveToFirst();

                    Picasso.with(getApplicationContext())
                            .load(new File(cursor.getString(column_index_data)).getAbsoluteFile())
                            .resize(1024, 600)
                            .centerInside()
                            .into(imagePreview);

                    if (picturePath.length() == 0) {
                        imagePreview.setVisibility(View.VISIBLE);
                        Animation animationFadeIn = AnimationUtils.loadAnimation(this, R.anim.abc_slide_in_bottom);
                        imagePreview.startAnimation(animationFadeIn);
                    }


                    picturePath = cursor.getString(column_index_data);
                    saveFile.setBackgroundResource(android.R.drawable.ic_menu_save);
                    saveOnBackPress = true;
                    dialog.dismiss();
                }
        }
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {

        choosenDate = dayOfMonth + " " + months[monthOfYear] + "," + year;
        chooseTime();
    }

    @Override
    public void onTimeSet(RadialPickerLayout radialPickerLayout, int hour, int minute, int second) {

        String AmPm = "AM";
        if (hour >= 12) {
            hour = hour - 12;
            AmPm = "PM";
        }
        if (hour == 0)
            hour = 12;

        String finalHour = hour + "";
        String finalMinute = minute + "";

        if (hour < 10) finalHour = "0" + finalHour;
        if (minute < 10) finalMinute = "0" + finalMinute;

        choosenDate = choosenDate + " " + finalHour + ":" + finalMinute + " " + AmPm;


        Toast.makeText(getApplicationContext(), choosenDate, Toast.LENGTH_SHORT).show();
    }

    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                addFileActivity.this);
        alertDialog.setTitle("SETTINGS");
        alertDialog.setMessage("Enable Location Provider! Go to settings menu?");
        alertDialog.setPositiveButton("Settings",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        addFileActivity.this.startActivity(intent);
                    }
                });
        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        alertDialog.show();
    }

    public Boolean isAvailable() {
        try {
            Process p1 = java.lang.Runtime.getRuntime().exec("ping -c 1    www.google.com");
            int returnVal = p1.waitFor();
            boolean reachable = (returnVal == 0);
            if (reachable) {
                System.out.println("Internet access");
                return reachable;
            } else {
                System.out.println("No Internet access");
            }

        } catch (Exception e) {

            e.printStackTrace();
        }
        return false;
    }

}
