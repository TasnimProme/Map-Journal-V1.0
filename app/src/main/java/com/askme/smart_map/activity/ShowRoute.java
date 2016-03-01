package com.askme.smart_map.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.directions.route.Route;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.SphericalUtil;
import com.yalantis.guillotine.sample.R;

import java.util.ArrayList;
import java.util.List;

public class ShowRoute extends AppCompatActivity {

    private final Handler mHandler = new Handler();
    EditText fromEdit, toEdit;
    GoogleMap googleMap;
    Animation slide_up, slide_down;
    Double fromLat, toLat, fromLng, toLng;
    Marker tempMarker;
    ImageButton addWaypoints;
    ImageButton animateRoute;
    ImageButton fullRouteWithoutAnimation;
    Dialog waypointsDialog;
    ArrayAdapter<String> adapter;
    ListView listWaypoints;
    ArrayList<String> waypointsName = new ArrayList<String>();
    String titleDestination;
    private int REQUEST_CODE_AUTOCOMPLETE;
    private List<LatLng> latLngs = new ArrayList<LatLng>();
    private List<LatLng> allPoints = new ArrayList<LatLng>();
    private List<Marker> markers = new ArrayList<Marker>();
    private int numOfWaypoints = 0;
    private Animator animator = new Animator();

    public static float bearingBetweenLatLngs(LatLng begin, LatLng end) {
        Location beginL = convertLatLngToLocation(begin);
        Location endL = convertLatLngToLocation(end);
        return beginL.bearingTo(endL);
    }


    //animating marker

    public static Location convertLatLngToLocation(LatLng latLng) {
        Location loc = new Location("someLoc");
        loc.setLatitude(latLng.latitude);
        loc.setLongitude(latLng.longitude);
        return loc;
    }

    public static void fixZoomForLatLngs(GoogleMap googleMap, List<LatLng> latLngs) {
        if (latLngs != null && latLngs.size() > 0) {
            LatLngBounds.Builder bc = new LatLngBounds.Builder();

            for (LatLng latLng : latLngs) {
                bc.include(latLng);
            }

            googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bc.build(), 50), 4000, null);
        }
    }

    public static void fixZoomForMarkers(GoogleMap googleMap, List<Marker> markers) {
        if (markers != null && markers.size() > 0) {
            LatLngBounds.Builder bc = new LatLngBounds.Builder();

            for (Marker marker : markers) {
                bc.include(marker.getPosition());
            }

            googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bc.build(), 50), 4000, null);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_route);


        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, waypointsName);

        fromEdit = (EditText) findViewById(R.id.fromLocation);
        toEdit = (EditText) findViewById(R.id.toLocation);
        animateRoute = (ImageButton) findViewById(R.id.animateRoute);
        fullRouteWithoutAnimation = (ImageButton) findViewById(R.id.fullRouteView);


        fromEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                REQUEST_CODE_AUTOCOMPLETE = 100;
                openAutocompleteActivity();

            }
        });

        toEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                REQUEST_CODE_AUTOCOMPLETE = 101;
                openAutocompleteActivity();
            }
        });


        addWaypoints = (ImageButton) findViewById(R.id.addWaypoints);
        slide_up = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);

        slide_down = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);

        fromLat = getIntent().getDoubleExtra("fromLat", 0.0);
        fromLng = getIntent().getDoubleExtra("fromLng", 0.0);
        toLat = getIntent().getDoubleExtra("toLat", 0.0);
        toLng = getIntent().getDoubleExtra("toLng", 0.0);

        titleDestination = getIntent().getStringExtra("toTitle");

        allPoints.add(new LatLng(fromLat, fromLng));
        allPoints.add(new LatLng(toLat, toLng));

        if (!isGooglePlayServicesAvailable()) {
            finish();
        }
        final SupportMapFragment fragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.ShowRouteMap);
        googleMap = fragment.getMap();
        googleMap.setMyLocationEnabled(true);

        addWaypoints.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                waypointsDialog = new Dialog(ShowRoute.this);
                waypointsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                waypointsDialog.setContentView(R.layout.waypoints_dialog);
                waypointsDialog.setCancelable(true);
                waypointsDialog.setCanceledOnTouchOutside(true);


                listWaypoints = (ListView) waypointsDialog.findViewById(R.id.waypointsListView);


                listWaypoints.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                        String removedWayPoints = waypointsName.get(i);
                        Toast.makeText(getApplicationContext(), removedWayPoints + " removed from waypoints", Toast.LENGTH_SHORT).show();

                        latLngs.remove(i);

                        numOfWaypoints--;
                        waypointsName.remove(i);

                        adapter.remove(removedWayPoints);

                        adapter.notifyDataSetChanged();

                        return false;
                    }
                });


                ImageButton addMoreWaypoints = (ImageButton) waypointsDialog.findViewById(R.id.addMoreWaypoints);
                ImageButton doneWaypoints = (ImageButton) waypointsDialog.findViewById(R.id.doneWaypoints);


                if (numOfWaypoints == 0) {
                    Toast t = Toast.makeText(getApplicationContext(), "No Waypoints Yet...", Toast.LENGTH_SHORT);
                    t.setGravity(Gravity.CENTER, 0, 0);
                    t.show();
                } else {
                    listWaypoints.setAdapter(adapter);
                }


                addMoreWaypoints.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        REQUEST_CODE_AUTOCOMPLETE = 102;
                        openAutocompleteActivity();

                    }
                });


                doneWaypoints.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        waypointsDialog.dismiss();
                    }
                });


                waypointsDialog.show();

            }
        });


        fromEdit.setText("Your Position");
        toEdit.setText(titleDestination);


/*        googleMap.addMarker(new MarkerOptions().position(new LatLng(fromLat,fromLng))).setTitle("Your Position");
        googleMap.addMarker(new MarkerOptions().position(new LatLng(toLat,toLng))).setTitle(titleDestination);


        CameraPosition cameraPosition =
                new CameraPosition.Builder()
                        .target(new LatLng(fromLat,fromLng))
                        .bearing(60)
                        .tilt(90)
                        .zoom(16f)
                        .build();

        googleMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition), null);*/


        showPath(allPoints, Routing.TravelMode.DRIVING, false, 0);

/*        cameraPosition=new CameraPosition.Builder()
                .target(new LatLng(toLat,toLng))
                .bearing(60)
                .tilt(90)
                .zoom(16f)
                .build();
        googleMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition), null);*/

        animateRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animator.stopAnimation();
                if (numOfWaypoints == 0)
                    showPath(allPoints, Routing.TravelMode.DRIVING, false, 1);
                else
                    showPath(allPoints, Routing.TravelMode.DRIVING, true, 1);
            }
        });


        fullRouteWithoutAnimation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animator.stopAnimation();

                if (numOfWaypoints == 0)
                    showPath(allPoints, Routing.TravelMode.DRIVING, false, 0);
                else
                    showPath(allPoints, Routing.TravelMode.DRIVING, true, 0);
            }
        });

    }

    private boolean isGooglePlayServicesAvailable() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            GooglePlayServicesUtil.getErrorDialog(status, this, 0).show();
            return false;
        }
    }

    ;

    public void showPath(List<LatLng> points, Routing.TravelMode travelMode, boolean optimize, final int animate) {

        googleMap.clear();

        List<LatLng> x = new ArrayList<LatLng>();

        x.add(new LatLng(fromLat, fromLng));

        googleMap.addMarker(new MarkerOptions().position(points.get(0)).title(fromEdit.getText().toString()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)));
        googleMap.addMarker(new MarkerOptions().position(points.get(1)).title(toEdit.getText().toString()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

        for (int i = 0; i < waypointsName.size(); i++) {
            x.add(points.get(i + 2));
            googleMap.addMarker(new MarkerOptions().position(points.get(i + 2)).title(waypointsName.get(i)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)));
        }

        x.add(new LatLng(toLat, toLng));

        final ProgressDialog progressDialog = ProgressDialog.show(this, "Please wait.",
                "Fetching route information.", true);
        final Routing routing = new Routing.Builder()
                .travelMode(travelMode)
                .waypoints(x)
                .optimize(optimize)
                .withListener(new RoutingListener() {
                    @Override
                    public void onRoutingFailure() {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Something went wrong, Try again", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onRoutingStart() {

                    }

                    @Override
                    public void onRoutingSuccess(ArrayList<Route> arrayList, int i) {
                        progressDialog.dismiss();
/*                        PolylineOptions polyoptions = new PolylineOptions();
                        polyoptions.color(Color.BLUE);
                        polyoptions.width(10);
                        polyoptions.addAll(arrayList.get(i).getPoints());
                        markerPolyline = googleMap.addPolyline(polyoptions);*/

                        latLngs = arrayList.get(0).getPoints();
                        addPolylineToMap(latLngs);

                        if (animate == 1) {
                            animator.startAnimation(false, latLngs);
                        } else {
                            fixZoomForLatLngs(googleMap, latLngs);
                        }
                    }

                    @Override
                    public void onRoutingCancelled() {
                        progressDialog.dismiss();
                    }
                })
                .build();
        routing.execute();
    }

    /**
     * Adds a list of markers to the map.
     */
    public void addPolylineToMap(List<LatLng> latLngs) {
        PolylineOptions options = new PolylineOptions();
        for (LatLng latLng : latLngs) {
            options.add(latLng);
        }
        options.color(Color.BLUE);
        options.width(10);
        googleMap.addPolyline(options);
    }

    /**
     * Clears all markers from the map.
     */
    public void clearMarkers() {
        googleMap.clear();
        markers.clear();
    }

    /**
     * Highlight the marker by index.
     */
    private void highLightMarker(int index) {
        if (markers.size() >= index + 1) {
            highLightMarker(markers.get(index));
        }
    }

    /**
     * Highlight the marker by marker.
     */
    private void highLightMarker(Marker marker) {
        if (marker != null) {
            marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
            marker.showInfoWindow();
        }

    }

    private void resetMarkers() {
        for (Marker marker : this.markers) {
            marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        }
    }

    private void openAutocompleteActivity() {
        try {
            // The autocomplete activity_main requires Google Play Services to be available. The intent
            // builder checks this and throws an exception if it is not the case.
            Intent intent = new PlaceAutocomplete
                    .IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                    .build(this);
            startActivityForResult(intent, REQUEST_CODE_AUTOCOMPLETE);
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
        if (requestCode == REQUEST_CODE_AUTOCOMPLETE) {
            if (resultCode == RESULT_OK) {
                // Get the user's selected place from the Intent.
                Place place = PlaceAutocomplete.getPlace(this, data);

                switch (REQUEST_CODE_AUTOCOMPLETE) {
                    case 100:
                        fromEdit.setText(place.getName());
                        fromLat = place.getLatLng().latitude;
                        fromLng = place.getLatLng().longitude;
                        allPoints.set(0, new LatLng(fromLat, fromLng));
                        break;
                    case 101:
                        toEdit.setText(place.getName());
                        toLat = place.getLatLng().latitude;
                        toLng = place.getLatLng().longitude;
                        allPoints.set(1, new LatLng(toLat, toLng));
                        break;
                    case 102:
                        waypointsName.add(place.getName().toString());
                        numOfWaypoints++;
                        allPoints.add(place.getLatLng());
                        adapter.notifyDataSetChanged();

                        if (numOfWaypoints == 1)
                            listWaypoints.setAdapter(adapter);
                        break;
                }

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                Toast.makeText(getApplicationContext(), status.toString(), Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                // Indicates that the activity_main closed before a selection was made. For example if
                // the user pressed the back button.
            }
        }
    }

    public class Animator implements Runnable {

        private static final int ANIMATE_SPEEED = 300;
        private static final int ANIMATE_SPEEED_TURN = 300;
        private static final int BEARING_OFFSET = 20;

        private final Interpolator interpolator = new LinearInterpolator();
        int currentIndex = 0;
        float tilt = 90;
        float zoom = 15.5f;
        boolean upward = true;
        long start = SystemClock.uptimeMillis();
        LatLng endLatLng = null;
        LatLng beginLatLng = null;
        boolean showPolyline = false;
        private boolean animating = false;
        private List<LatLng> latLngs = new ArrayList<LatLng>();
        private Marker trackingMarker;
        private Polyline polyLine;
        private PolylineOptions rectOptions = new PolylineOptions();

        public void reset() {
            resetMarkers();
            start = SystemClock.uptimeMillis();
            currentIndex = 0;
            endLatLng = getEndLatLng();
            beginLatLng = getBeginLatLng();

        }

        public void stopAnimation() {
            animating = false;
            mHandler.removeCallbacks(animator);

        }

        public void initialize(boolean showPolyLine) {
            reset();
            this.showPolyline = showPolyLine;

            highLightMarker(0);

            if (showPolyLine) {
                polyLine = initializePolyLine();
            }

            // We first need to put the camera in the correct position for the first run (we need 2 markers for this).....
            LatLng markerPos = latLngs.get(0);
            LatLng secondPos = latLngs.get(1);

            setInitialCameraPosition(markerPos, secondPos);

        }

        private void setInitialCameraPosition(LatLng markerPos,
                                              LatLng secondPos) {

            float bearing = bearingBetweenLatLngs(markerPos, secondPos);

            trackingMarker = googleMap.addMarker(new MarkerOptions().position(markerPos)
                    .title("title")
                    .snippet("snippet"));


            tempMarker = trackingMarker;

            float mapZoom = googleMap.getCameraPosition().zoom >= 16 ? googleMap.getCameraPosition().zoom : 16;

            CameraPosition cameraPosition =
                    new CameraPosition.Builder()
                            .target(markerPos)
                            .bearing(bearing + BEARING_OFFSET)
                            .tilt(90)
                            .zoom(mapZoom)
                            .build();

            googleMap.animateCamera(
                    CameraUpdateFactory.newCameraPosition(cameraPosition),
                    ANIMATE_SPEEED_TURN,
                    new GoogleMap.CancelableCallback() {

                        @Override
                        public void onFinish() {
                            System.out.println("finished camera");
                            animator.reset();
                            Handler handler = new Handler();
                            handler.post(animator);
                        }

                        @Override
                        public void onCancel() {
                            System.out.println("cancelling camera");
                        }
                    }
            );
        }

        private Polyline initializePolyLine() {
            //polyLinePoints = new ArrayList<LatLng>();
            rectOptions.add(latLngs.get(0));
            return googleMap.addPolyline(rectOptions);
        }

        /**
         * Add the marker to the polyline.
         */
        private void updatePolyLine(LatLng latLng) {
            List<LatLng> points = polyLine.getPoints();
            points.add(latLng);
            polyLine.setPoints(points);
        }

        public void startAnimation(boolean showPolyLine, List<LatLng> latLngs) {
            if (trackingMarker != null) {
                trackingMarker.remove();
            }
            this.animating = true;
            this.latLngs = latLngs;
            if (latLngs.size() > 2) {
                initialize(showPolyLine);
            }

        }

        public boolean isAnimating() {
            return this.animating;
        }


        @Override
        public void run() {

            long elapsed = SystemClock.uptimeMillis() - start;
            double t = interpolator.getInterpolation((float) elapsed / ANIMATE_SPEEED);
            LatLng intermediatePosition = SphericalUtil.interpolate(beginLatLng, endLatLng, t);

            Double mapZoomDouble = 18.5 - (Math.abs((0.5 - t)) * 5);
            float mapZoom = mapZoomDouble.floatValue();

            System.out.println("mapZoom = " + mapZoom);

            trackingMarker.setPosition(intermediatePosition);

            if (showPolyline) {
                updatePolyLine(intermediatePosition);
            }

            if (t < 1) {
                mHandler.postDelayed(this, 16);
            } else {

                System.out.println("Move to next marker.... current = " + currentIndex + " and size = " + latLngs.size());
                // imagine 5 elements -  0|1|2|3|4 currentindex must be smaller than 4
                if (currentIndex < latLngs.size() - 2) {

                    currentIndex++;

                    endLatLng = getEndLatLng();
                    beginLatLng = getBeginLatLng();


                    start = SystemClock.uptimeMillis();

                    Double heading = SphericalUtil.computeHeading(beginLatLng, endLatLng);

                    highLightMarker(currentIndex);

                    CameraPosition cameraPosition =
                            new CameraPosition.Builder()
                                    .target(endLatLng)
                                    .bearing(heading.floatValue() /*+ BEARING_OFFSET*/) // .bearing(bearingL  + BEARING_OFFSET)
                                    .tilt(tilt)
                                    .zoom(googleMap.getCameraPosition().zoom)
                                    .build();

                    googleMap.animateCamera(
                            CameraUpdateFactory.newCameraPosition(cameraPosition),
                            ANIMATE_SPEEED_TURN,
                            null
                    );

                    //start = SystemClock.uptimeMillis();
                    mHandler.postDelayed(this, 16);

                } else {
                    currentIndex++;
                    highLightMarker(currentIndex);
                    tempMarker.remove();
                    stopAnimation();
                }

            }
        }


        private LatLng getEndLatLng() {
            return latLngs.get(currentIndex + 1);
        }

        private LatLng getBeginLatLng() {
            return latLngs.get(currentIndex);
        }

    }


}
