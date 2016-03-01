package com.askme.smart_map.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.askme.smart_map.activity.AtlasBuilder.CustomClusterListView;
import com.askme.smart_map.activity.DatabaseBuilder.DBadapter;
import com.askme.smart_map.activity.DatabaseBuilder.MyFile;
import com.askme.smart_map.activity.ShowingMarker.GetMyLocation;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.yalantis.guillotine.sample.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.nlopez.clusterer.Cluster;
import io.nlopez.clusterer.Clusterer;
import io.nlopez.clusterer.MarkerAnimation;
import io.nlopez.clusterer.OnPaintingClusterListener;
import io.nlopez.clusterer.OnPaintingClusterableMarkerListener;

public class AtlasActivity extends FragmentActivity {

    private static final int CLUSTER_BASE_SIZE = 20;

    Dialog dialog;
    private GoogleMap map;
    private List<MyFile> pointsOfInterest;
    private Clusterer<MyFile> clusterer;
    DBadapter dBadapter;
    GetMyLocation myLocation;
    Location lastKnownLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atlas);



        dialog=new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dBadapter=new DBadapter(getApplicationContext());
        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.mapAtlas)).getMap();

        myLocation=new GetMyLocation(getApplicationContext());
        pointsOfInterest = dBadapter.getFileDetails();

        initClusterer();
        moveMap();



    }


    private void moveMap() {
        if(myLocation.canGetLocation())
        {
            lastKnownLocation=myLocation.getLocationValue();
        }
        CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(lastKnownLocation.getLatitude(),lastKnownLocation.getLongitude())).zoom(6).build();
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    private void initClusterer() {
        clusterer = new Clusterer<MyFile>(this, map);
        clusterer.addAll(pointsOfInterest);

        clusterer.setAnimationEnabled(false);
        clusterer.setMarkerAnimation(new MarkerAnimation() {
            @Override
            public void animateMarker(Marker marker, float interpolation) {
                // Basic fading animation
                //marker.setAlpha(interpolation);
            }
        });

        clusterer.setClustererListener(new Clusterer.ClustererClickListener<MyFile>() {
            @Override
            public void markerClicked(MyFile marker) {
/*                Intent openGalary=new Intent(getApplicationContext(), DetailGalaryActivity.class);
                openGalary.putExtra("data", myFile.getData());
                context.startActivity(openGalary);*/
            }

            @Override
            public void clusterClicked(Cluster position) {

                java.util.Collection<MyFile> userCollection=position.getMarkers();

                ArrayList<MyFile> userList = new ArrayList<MyFile>(userCollection);

                showdialog(userList);

            }
        });

        clusterer.setOnPaintingMarkerListener(new OnPaintingClusterableMarkerListener<MyFile>() {

            @Override
            public void onMarkerCreated(Marker marker, MyFile clusterable) {
                marker.hideInfoWindow();
            }

            @Override
            public MarkerOptions onCreateMarkerOptions(MyFile poi) {

                View marker = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker, null);

                Bitmap temp=scaleBitmap(new File(poi.getPath()).getAbsolutePath());
                ((ImageView)marker.findViewById(R.id.marker_pic)).setImageBitmap(temp);

                return new MarkerOptions().position(poi.getPosition()).title(poi.getTitle()).snippet(poi.getDescription()).icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(AtlasActivity.this, marker)));

            }
        });

        clusterer.setOnPaintingClusterListener(new OnPaintingClusterListener<MyFile>() {

            @Override
            public void onMarkerCreated(Marker marker, Cluster<MyFile> cluster) {
                marker.hideInfoWindow();

            }

            @Override
            public MarkerOptions onCreateClusterMarkerOptions(Cluster<MyFile> cluster) {
                return new MarkerOptions()
                        .title("Clustering " + cluster.getWeight() + " items")
                        .position(cluster.getCenter())
                        .icon(BitmapDescriptorFactory.fromBitmap(getClusteredLabel(cluster.getWeight(),
                                getApplicationContext())));
            }
        });

    }

    private Bitmap getClusteredLabel(Integer count, Context ctx) {

        float density = getResources().getDisplayMetrics().density;

        Resources r = ctx.getResources();
        Bitmap res = BitmapFactory.decodeResource(r, R.drawable.cluster_image);
        res = res.copy(Bitmap.Config.ARGB_8888, true);
        Canvas c = new Canvas(res);

        Paint textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTypeface(Typeface.DEFAULT_BOLD);
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(20 * density);

        c.drawText(String.valueOf(count.toString()), res.getWidth() / 2, res.getHeight() / 2, textPaint);

        return res;
    }

    private Bitmap scaleBitmap(String imagePath) {
        int targetW = 200;
        int targetH = 200;
        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(imagePath, bmOptions);
        return bitmap;
    }

    // Convert a view to bitmap
    public static Bitmap createDrawableFromView(Context context, View view) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        view.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        return bitmap;
    }


    private void showdialog(ArrayList<MyFile> userList)
    {
        LayoutInflater li = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = li.inflate(R.layout.custom_dialog_cluster_clicked, null, false);
        dialog.setContentView(v);
        dialog.setCancelable(true);

        ListView list = (ListView) dialog.findViewById(R.id.listViewCluster);
        list.setAdapter(new CustomClusterListView(this,userList));
        list.setScrollContainer(true);
        //now that the dialog is set up, it's time to show it
        dialog.show();
    }



}
