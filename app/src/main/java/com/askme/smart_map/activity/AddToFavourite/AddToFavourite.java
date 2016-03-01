package com.askme.smart_map.activity.AddToFavourite;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.askme.smart_map.activity.DatabaseBuilder.DBadapter;
import com.yalantis.guillotine.sample.R;

import java.util.ArrayList;

public class AddToFavourite extends AppCompatActivity {

    ListView savedPlaceListView;
    DBadapter dBadapter;
    ArrayList<String[]> savedPlace;
    Dialog map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_to_favourite);

        dBadapter=new DBadapter(this);

        map=new Dialog(this);
        map.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dBadapter = new DBadapter(getApplicationContext());
        savedPlaceListView = (ListView) findViewById(R.id.savedFavPlaceListView);
        savedPlace = dBadapter.getFavPlaceDetails();

        savedPlaceListView.setAdapter(new AddToFavouriteAdapter(AddToFavourite.this, savedPlace));
        savedPlaceListView.setScrollContainer(true);

/*        savedPlaceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String[] x = (String[]) adapterView.getItemAtPosition(i);

                Intent MapIntent=new Intent(getApplicationContext(),MapDialog.class);
                MapIntent.putExtra("markerCOUNT",1);
                MapIntent.putExtra("markerNAME",x[1]);
                MapIntent.putExtra("markerLAT",x[2]);
                MapIntent.putExtra("markerLONG",x[3]);
                startActivity(MapIntent);
            }
        });*/


        savedPlaceListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                String[] x = (String[]) adapterView.getItemAtPosition(i);
                showDeleteConfirmation(x);
                return true;
            }
        });

    }

    private void showDeleteConfirmation(final String[] x) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Confirm Delete..");
        alertDialog.setMessage("Are you sure you want delete " + x[1] + " from your favorite place?");

        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                dBadapter.deleteFromFav(x[0]);
                finish();
                startActivity(getIntent());
                Toast.makeText(getApplicationContext(), x[1] + " Has been removed from favourites", Toast.LENGTH_SHORT).show();
            }
        });

        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();
    }

}