package com.askme.smart_map.activity.TaskToDoBuilder;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.askme.smart_map.activity.DatabaseBuilder.DBadapter;
import com.yalantis.guillotine.sample.R;

import java.util.ArrayList;

public class Saved_message extends AppCompatActivity {

    ListView savedSmsListView;
    DBadapter dBadapter;
    ArrayList<String[]> savedMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_message);

        dBadapter = new DBadapter(getApplicationContext());
        savedSmsListView = (ListView) findViewById(R.id.savedSmsListView);
        savedMsg = dBadapter.getSmsTaskDetails();

        savedSmsListView.setAdapter(new Saved_sms_listview_adapter(this, savedMsg));
        savedSmsListView.setScrollContainer(true);

    }
}
