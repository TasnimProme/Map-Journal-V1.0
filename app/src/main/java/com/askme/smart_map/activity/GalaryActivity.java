package com.askme.smart_map.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.askme.smart_map.activity.DatabaseBuilder.DBadapter;
import com.askme.smart_map.activity.DatabaseBuilder.MyFile;
import com.askme.smart_map.activity.GalaryBuilder.GalaryListAadapter;
import com.yalantis.guillotine.sample.R;

import java.util.ArrayList;

//NEED TO LOAD PICTURES FROM DATABASE
public class GalaryActivity extends AppCompatActivity {
    ArrayList<MyFile> galaryInfo;
    private static final long RIPPLE_DURATION = 250;
    private RecyclerView mRecyclerView;
    private StaggeredGridLayoutManager mStaggeredLayoutManager;
    private GalaryListAadapter mAdapter;
    private boolean isListView;
    DBadapter dBadapter;
    private Menu menu;
    private ImageButton addFile;
    private ImageButton switchView;



    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_galary);





        dBadapter=new DBadapter(getApplicationContext());


        addFile=(ImageButton)findViewById(R.id.addFile);
        addFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(GalaryActivity.this, addFileActivity.class));
            }
        });






        mStaggeredLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);

        mRecyclerView = (RecyclerView) findViewById(R.id.list);
        mRecyclerView.setLayoutManager(mStaggeredLayoutManager);
        loadData();


        isListView = true;





        /*************************************************

         Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

         SharedPreferences bgshared= getSharedPreferences("background", MODE_PRIVATE);
         String Bgfromshared = bgshared.getString("BKGRND", "Error");

         if(Bgfromshared.equals("bg1")) {
         toolbar.setBackgroundColor(Color.parseColor("#9CCC65"));

         }
         else if(Bgfromshared.equals("bg2")) {
         toolbar.setBackgroundColor(Color.parseColor("@android:color/holo_blue_dark"));
         }
         else if(Bgfromshared.equals("bg3")) {
         toolbar.setBackgroundColor(Color.parseColor("#FF9800"));
         }
         else if(Bgfromshared.equals("bg4")) {
         toolbar.setBackgroundColor(Color.parseColor("#9575CD"));
         }
         else if(Bgfromshared.equals("bg5")) {
         toolbar.setBackgroundColor(Color.parseColor("#e74c3c"));
         }
         else if(Bgfromshared.equals("bg6")) {
         toolbar.setBackgroundColor(Color.parseColor("#009688"));
         }
         else if(Bgfromshared.equals("bg7")) {
         toolbar.setBackgroundColor(Color.parseColor("#F06292"));
         }
         else if(Bgfromshared.equals("bg8")) {
         toolbar.setBackgroundColor(Color.parseColor("#FF6F00"));
         }
         }

         ********************************************************************/
    }

    @Override
    protected void onResume() {
        loadData();
        super.onResume();
    }

    private void loadData() {
        galaryInfo=dBadapter.getFileWithImageDetails();
        if(galaryInfo.size()==0)
        {
            mRecyclerView.setVisibility(View.INVISIBLE);
            Toast t=Toast.makeText(getApplicationContext(), "No image to Display!!please add some image", Toast.LENGTH_SHORT);
            t.setGravity(Gravity.CENTER, 0, 0);
            t.show();
        }
        else
        {
            mAdapter = new GalaryListAadapter(this,galaryInfo);
            mRecyclerView.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
            mAdapter.setOnItemClickListener(onItemClickListener);
        }
    }

    GalaryListAadapter.OnItemClickListener onItemClickListener = new GalaryListAadapter.OnItemClickListener() {
        @Override
        public void onItemClick(View v, int position) {
            Intent transitionIntent = new Intent(GalaryActivity.this, DetailGalaryActivity.class);

            transitionIntent.putExtra("data", galaryInfo.get(position).getData());
            startActivity(transitionIntent);
        }
    };

}