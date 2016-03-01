package com.askme.smart_map.activity.TaskToDoBuilder;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.askme.smart_map.activity.DatabaseBuilder.DBadapter;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;
import com.yalantis.guillotine.sample.R;

import java.util.Calendar;

public class Message_time extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener,
        DatePickerDialog.OnDateSetListener {

    private static final int IS_SMS = 999;
    Button select_time;

    Long timeDiff = Long.valueOf(0);
    String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
    String choosenDate = "";
    DBadapter dBadapter;
    int FinalYear, FinalMonth, FinalDay, FinalHour, FinalMinute;
    //dbadapter


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_time);

        dBadapter = new DBadapter(getApplicationContext());

        final EditText phone_number = (EditText) findViewById(R.id.phone_number_message);
        final EditText message = (EditText) findViewById(R.id.text_message);

        select_time = (Button) findViewById(R.id.select_time);
        Button send_button = (Button) findViewById(R.id.btnSendSMS);

        send_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (phone_number.getText().length() != 0 && message.getText().length() != 0 && !select_time.getText().toString().equalsIgnoreCase("Select Time")) {
                    dBadapter.insertTask(phone_number.getText().toString(), message.getText().toString(), select_time.getText().toString(), "SMS+TIME", "NONE", "PENDING", timeDiff + "");

                    int flag = dBadapter.getTaskID(timeDiff + "");


                    AlarmController alarmController = new AlarmController(getApplicationContext(), phone_number.getText().toString(), message.getText().toString(), timeDiff, flag,IS_SMS);


                    //database
                    //dbadapter.insertfile();
                    finish();

                } else {
                    Toast.makeText(getApplicationContext(), "Something is missing", Toast.LENGTH_SHORT).show();
                }
            }
        });


        select_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseDate(view);
            }
        });
    }


    public void chooseTime() {
        Calendar now = Calendar.getInstance();
        TimePickerDialog tpd = TimePickerDialog.newInstance(
                Message_time.this,
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE), false

        );
        tpd.show(getFragmentManager(), "Timepickerdialog");
    }

    public void chooseDate(View view) {
        Calendar now = Calendar.getInstance();
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                Message_time.this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        dpd.showYearPickerFirst(true);
        dpd.show(getFragmentManager(), "Datepickerdialog");
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {

        if (dayOfMonth < 10)
            choosenDate = "0" + dayOfMonth + " " + months[monthOfYear] + "," + year;
        else
            choosenDate = dayOfMonth + " " + months[monthOfYear] + "," + year;

        FinalDay = dayOfMonth;
        FinalMonth = monthOfYear + 1;
        FinalYear = year;

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


        FinalHour = Integer.parseInt(finalHour);
        FinalMinute = Integer.parseInt(finalMinute);


        Calendar calNow = Calendar.getInstance();
        Calendar calSet = (Calendar) calNow.clone();

        calSet.set(Calendar.YEAR, FinalYear);
        calSet.set(Calendar.MONTH, FinalMonth - 1);
        calSet.set(Calendar.DAY_OF_MONTH, FinalDay);
        calSet.set(Calendar.HOUR_OF_DAY, FinalHour);
        calSet.set(Calendar.MINUTE, FinalMinute);
        calSet.set(Calendar.SECOND, 0);
        calSet.set(Calendar.MILLISECOND, 0);

        if (AmPm.equalsIgnoreCase("am"))
            calSet.set(Calendar.AM_PM, Calendar.AM);
        else
            calSet.set(Calendar.AM_PM, Calendar.PM);

        if (calSet.compareTo(calNow) > 0) {

            timeDiff = calSet.getTimeInMillis();
            select_time.setText(choosenDate);

        } else {
            Toast.makeText(getApplicationContext(), "Your Selected Date has already Expired", Toast.LENGTH_SHORT);
        }
    }
}
