package com.askme.smart_map.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.askme.smart_map.activity.AddToFavourite.AddToFavourite;
import com.askme.smart_map.activity.DatabaseBuilder.DBadapter;
import com.askme.smart_map.activity.HomePageBuilder.NearbyItem;
import com.askme.smart_map.activity.HomePageBuilder.NearbyRecyclerviewAdapter;
import com.askme.smart_map.activity.ShowingMarker.GetMyLocation;
import com.askme.smart_map.activity.ShowingMarker.GooglePlacesReadTask;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.yalantis.guillotine.animation.GuillotineAnimation;
import com.yalantis.guillotine.interfaces.GuillotineListener;
import com.yalantis.guillotine.sample.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MainActivity extends AppCompatActivity implements NearbyRecyclerviewAdapter.ClickListener {

    private static final int REQUEST_CODE_AUTOCOMPLETE = 999;
    private static final long RIPPLE_DURATION = 250;
    //used googles Web api key
    private static final String GOOGLE_API_KEY = "AIzaSyAnujWrP43wSm5jF0G944g5oP3uavhxBVA";
    private final int[] MAP_TYPES = {GoogleMap.MAP_TYPE_SATELLITE,
            GoogleMap.MAP_TYPE_NORMAL,
            GoogleMap.MAP_TYPE_HYBRID,
            GoogleMap.MAP_TYPE_TERRAIN};
    Animation slide_up, slide_down, move, move_in;
    RelativeLayout popUpLayout;
    RelativeLayout markerClickLayout;
    RecyclerView recList;
    LatLng pos;
    Marker showingMarker;
    ImageButton showRoute;
    ImageButton addToFav;
    ImageButton openStreetView;
    Button dialog_done;



    com.askme.smart_map.widget.CanaroTextView markerTitle;
    GoogleMap googleMap;
    Polyline markerPolyline;
    double latitude = 0;
    double longitude = 0;
    Location lastKnownLocation;
    String[] place_name = {"Airport", "ATM", "Bank", "Bus", "Church", "Ferry", "Food", "Fuel", "Hospital", "Market",
            "Mosque", "Movie", "Park", "Police", "Mail", "School", "University"};
    Context context;
    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.root)
    FrameLayout root;
    @InjectView(R.id.content_hamburger)
    View contentHamburger;
    TextView mPlaceDetailsText;
    Boolean isShowing = true;
    int x = 1;
    GetMyLocation myLocation;
    private NearbyRecyclerviewAdapter ca;
    private int PROXIMITY_RADIUS = 10000;
    private ImageButton menuButton, searchSuggestionWindowOpener;
    private TextView textView;

    DBadapter dBadapter;
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //show error dialog if GoolglePlayServices not available
        if (!isGooglePlayServicesAvailable()) {
            finish();
        }
        setContentView(R.layout.activity_main);
        context = getApplicationContext();

        dBadapter=new DBadapter(context);

        //search
        popUpLayout = (RelativeLayout) findViewById(R.id.needed);
        menuButton = (ImageButton) findViewById(R.id.Menu);
        searchSuggestionWindowOpener = (ImageButton) findViewById(R.id.placeSearch);
        mPlaceDetailsText = (TextView) findViewById(R.id.searchPlaceName);


        //marker click info window
        markerClickLayout = (RelativeLayout) findViewById(R.id.markerInfo);
        showRoute = (ImageButton) findViewById(R.id.showRouteMarker);
        markerTitle = (com.askme.smart_map.widget.CanaroTextView) findViewById(R.id.markerName);
        addToFav = (ImageButton) findViewById(R.id.saveLocation);
        openStreetView = (ImageButton) findViewById(R.id.showStreetView);


        searchSuggestionWindowOpener.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                openAutocompleteActivity();
            }
        });


        recList = (RecyclerView) findViewById(R.id.cardList);
        recList.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.HORIZONTAL);
        recList.setLayoutManager(llm);


        ca = new NearbyRecyclerviewAdapter(this, createlist(17));
        ca.setClickListener(this);
        recList.setAdapter(ca);


        move = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.move);

        move_in = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.move_in);


        slide_up = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);

        slide_down = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);


/*        popUpLayout.setVisibility(View.GONE);
        popUpLayout.setAnimation(slide_down);*/


        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (markerClickLayout.getVisibility() == View.VISIBLE) {
                    markerClickLayout.setAnimation(slide_down);
                    markerClickLayout.setVisibility(View.GONE);
                }

                menuButton.setAnimation(move);
                menuButton.setVisibility(View.GONE);

                popUpLayout.setVisibility(View.VISIBLE);
                popUpLayout.setAnimation(slide_up);
                recList.setVisibility(View.VISIBLE);
            }
        });


        context = getApplicationContext();
        ButterKnife.inject(this);
        SupportMapFragment fragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.googleMap);
        googleMap = fragment.getMap();


        myLocation = new GetMyLocation(getApplicationContext());

        if (myLocation.canGetLocation()) {
            lastKnownLocation = myLocation.getLocationValue();
        } else {
            showSettingsAlert();

        }


        initCamera(lastKnownLocation);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle(null);
            getSupportActionBar().show();
        }

        View guillotineMenu = LayoutInflater.from(this).inflate(R.layout.guillotine, null);
        root.addView(guillotineMenu);

        new GuillotineAnimation.GuillotineBuilder(guillotineMenu, guillotineMenu.findViewById(R.id.guillotine_hamburger), contentHamburger)
                .setStartDelay(RIPPLE_DURATION)
                .setActionBarViewForAnimation(toolbar)
                .setGuillotineListener(new GuillotineListener() {
                    @Override
                    public void onGuillotineOpened() {
                    }

                    @Override
                    public void onGuillotineClosed() {

                    }
                })
                .setClosedOnStart(true)
                .build();




        check_theme_color();

        LinearLayout galary_layout = (LinearLayout) findViewById(R.id.layoutGalary);
        galary_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, GalaryActivity.class));
            }
        });

        LinearLayout atlas_layout = (LinearLayout) findViewById(R.id.layoutAtlas);

        atlas_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, AtlasActivity.class));
            }
        });



        LinearLayout task_layout = (LinearLayout) findViewById(R.id.layoutTaskToDo);
        task_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, TaskToDo.class));
            }
        });




        LinearLayout favourites_layout = (LinearLayout) findViewById(R.id.layoutFavourites);
        favourites_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, AddToFavourite.class));
            }
        });


        LinearLayout personalize_layout = (LinearLayout) findViewById(R.id.layoutPersonalize);
        personalize_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                SharedPreferences bgshared= getSharedPreferences("background", MODE_PRIVATE);
                final SharedPreferences.Editor editor = bgshared.edit();


                dialog = new Dialog(MainActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.color_picker);
                dialog.show();
                Window window = dialog.getWindow();
                window.setLayout(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                dialog_done = (Button) dialog.findViewById(R.id.done);
                Button button1 = (Button) dialog.findViewById(R.id.color1);
                Button button2 = (Button) dialog.findViewById(R.id.color2);
                Button button3 = (Button) dialog.findViewById(R.id.color3);
                Button button4 = (Button) dialog.findViewById(R.id.color4);
                Button button5 = (Button) dialog.findViewById(R.id.color5);
                Button button6 = (Button) dialog.findViewById(R.id.color6);
                Button button7 = (Button) dialog.findViewById(R.id.color7);
                Button button8 = (Button) dialog.findViewById(R.id.color8);


                bgshared = getSharedPreferences("background", MODE_PRIVATE);
                String Bgfromshared = bgshared.getString("BKGRND", "Error");
                if(Bgfromshared.equals("bg1")) {
                    dialog_done.setBackgroundColor(Color.parseColor("#9CCC65"));
                    dialog_done.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });
                }

                else if(Bgfromshared.equals("bg2")) {
                    dialog_done.setBackgroundColor(Color.parseColor("#2D9FC9"));
                    dialog_done.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });
                }
                else if(Bgfromshared.equals("bg3")) {
                    dialog_done.setBackgroundColor(Color.parseColor("#FF9800"));
                    dialog_done.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });
                }
                else if(Bgfromshared.equals("bg4")) {
                    dialog_done.setBackgroundColor(Color.parseColor("#9575CD"));
                    dialog_done.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });
                }
                else if(Bgfromshared.equals("bg5")) {
                    dialog_done.setBackgroundColor(Color.parseColor("#e74c3c"));
                    dialog_done.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });
                }
                else if(Bgfromshared.equals("bg6")) {
                    dialog_done.setBackgroundColor(Color.parseColor("#009688"));
                    dialog_done.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });
                }
                else if(Bgfromshared.equals("bg7")) {
                    dialog_done.setBackgroundColor(Color.parseColor("#F06292"));
                    dialog_done.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });
                }
                else if(Bgfromshared.equals("bg8")) {
                    dialog_done.setBackgroundColor(Color.parseColor("#FF6F00"));
                    dialog_done.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });
                }

                button1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog_done.setBackgroundColor(Color.parseColor("#9CCC65"));

                        dialog_done.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                editor.putString("BKGRND", "bg1").commit();
                                //  editor.putString("BKGRND1", "bg1").commit();
                                check_theme_color();
                                dialog.dismiss();

                            }
                        });

                    }
                });


                button2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog_done.setBackgroundColor(Color.parseColor("#2D9FC9"));
                        dialog_done.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                editor.putString("BKGRND", "bg2").commit();
                                //    editor.putString("BKGRND1", "bg2").commit();
                                check_theme_color();
                                dialog.dismiss();

                            }
                        });

                    }
                });


                button3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog_done.setBackgroundColor(Color.parseColor("#FF9800"));

                        dialog_done.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                editor.putString("BKGRND", "bg3").commit();
                                //     editor.putString("BKGRND1", "bg3").commit();
                                check_theme_color();
                                dialog.dismiss();

                            }
                        });

                    }
                });


                button4.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog_done.setBackgroundColor(Color.parseColor("#9575CD"));

                        dialog_done.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                editor.putString("BKGRND", "bg4").commit();
                                //     editor.putString("BKGRND1", "bg4").commit();
                                check_theme_color();
                                dialog.dismiss();

                            }
                        });

                    }
                });

                button5.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog_done.setBackgroundColor(Color.parseColor("#e74c3c"));

                        dialog_done.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                editor.putString("BKGRND", "bg5").commit();
                                //     editor.putString("BKGRND1", "bg5").commit();
                                check_theme_color();
                                dialog.dismiss();


                            }
                        });

                    }
                });

                button6.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog_done.setBackgroundColor(Color.parseColor("#009688"));

                        dialog_done.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                editor.putString("BKGRND", "bg6").commit();
                                //     editor.putString("BKGRND1", "bg6").commit();
                                check_theme_color();
                                dialog.dismiss();

                            }
                        });

                    }
                });

                button7.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog_done.setBackgroundColor(Color.parseColor("#F06292"));

                        dialog_done.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                editor.putString("BKGRND", "bg7").commit();
                                //      editor.putString("BKGRND1", "bg7").commit();
                                check_theme_color();
                                dialog.dismiss();

                            }
                        });

                    }
                });

                button8.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog_done.setBackgroundColor(Color.parseColor("#FF6F00"));

                        dialog_done.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                editor.putString("BKGRND", "bg8").commit();
                                //      editor.putString("BKGRND1", "bg8").commit();
                                check_theme_color();
                                dialog.dismiss();

                            }
                        });

                    }
                });

            }





        });







        googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {

                googleMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                        .title(nameFromLatLng(latLng)));
            }
        });

        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                marker.hideInfoWindow();

                menuButton.setAnimation(move);
                menuButton.setVisibility(View.GONE);

                popUpLayout.setAnimation(slide_down);
                popUpLayout.setVisibility(View.GONE);


                markerClickLayout.setVisibility(View.VISIBLE);
                markerClickLayout.setAnimation(slide_up);

                markerTitle.setText(marker.getTitle());
                showingMarker = marker;
                pos = showingMarker.getPosition();
                return false;
            }
        });

//onclick methods for marker info window
        showRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String[] titleSplit = showingMarker.getTitle().split(":");


                Intent showRouteActivity = new Intent(MainActivity.this, ShowRoute.class);
                showRouteActivity.putExtra("toLat", pos.latitude);
                showRouteActivity.putExtra("toLng", pos.longitude);
                showRouteActivity.putExtra("fromLat", lastKnownLocation.getLatitude());
                showRouteActivity.putExtra("fromLng", lastKnownLocation.getLongitude());
                showRouteActivity.putExtra("toTitle", titleSplit[0]);
                startActivity(showRouteActivity);

                //showPath(showingMarker);
            }
        });

        openStreetView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), pos.latitude + " " + pos.longitude + showingMarker.getTitle(), Toast.LENGTH_SHORT).show();
                Intent streetViewActivity = new Intent(MainActivity.this, StreetViewActivity.class);
                streetViewActivity.putExtra("streetViewLat", pos.latitude);
                streetViewActivity.putExtra("streetViewLng", pos.longitude);
                streetViewActivity.putExtra("streetViewTitle", showingMarker.getTitle().split(":"));
                startActivity(streetViewActivity);
            }
        });

        addToFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dBadapter.insertFavPlace(showingMarker.getTitle(),showingMarker.getPosition().latitude+"",showingMarker.getPosition().longitude+"");
                startActivity(new Intent(MainActivity.this, AddToFavourite.class));
            }
        });


        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

                if (markerClickLayout.getVisibility() == View.VISIBLE) {
                    markerClickLayout.setAnimation(slide_down);
                    markerClickLayout.setVisibility(View.GONE);
                }
                if (menuButton.getVisibility() == View.GONE) {

                    popUpLayout.setAnimation(slide_down);
                    popUpLayout.setVisibility(View.GONE);

                    recList.setVisibility(View.GONE);

                    menuButton.setVisibility(View.VISIBLE);
                    menuButton.setAnimation(move_in);
                }

            }
        });
    }

    public void showSearchedLocation(Place searchedPlace) {


            /*used marker for show the location */
                googleMap.clear();

                googleMap.addMarker(new MarkerOptions()
                        .position(new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude()))
                        .title("Current Position")
                        .icon(BitmapDescriptorFactory
                                .fromResource(R.drawable.mypos)));


                googleMap.addMarker(new MarkerOptions()
                        .position(searchedPlace.getLatLng())
                        .title(searchedPlace.getName().toString())
                        .icon(BitmapDescriptorFactory
                                .fromResource(R.drawable.search_marker)));
                // Move the camera instantly to hamburg with a zoom of 15.
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(searchedPlace.getLatLng(), 15));

                // Zoom in, animating the camera.
                googleMap.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);

    }


    private List<NearbyItem> createlist(int size) {
        List<NearbyItem> result = new ArrayList<NearbyItem>();

        int[] icon = {R.drawable.airport, R.drawable.atm, R.drawable.bank, R.drawable.bus, R.drawable.church,
                R.drawable.ferry, R.drawable.food, R.drawable.fuel, R.drawable.hospital, R.drawable.market,
                R.drawable.mosque, R.drawable.movie, R.drawable.park, R.drawable.police, R.drawable.post_office,
                R.drawable.school, R.drawable.university};


        for (int i = 0; i < size; i++) {
            NearbyItem ci = new NearbyItem();
            ci.name = place_name[i];
            ci.image = icon[i];
            result.add(ci);
        }
        return result;
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

    private void initCamera(Location location) {
        CameraPosition position = CameraPosition.builder()
                .target(new LatLng(location.getLatitude(),
                        location.getLongitude()))
                .zoom(16f)
                .bearing(0.0f)
                .tilt(0.0f)
                .build();

        googleMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(position), null);
        googleMap.setMapType(MAP_TYPES[1]);
        googleMap.setTrafficEnabled(true);
        googleMap.getUiSettings().setAllGesturesEnabled(true);
        googleMap.getUiSettings().setTiltGesturesEnabled(true);
        googleMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())).title("Current Position").icon(BitmapDescriptorFactory.fromResource(R.drawable.mypos)));
    }

    public void showPath(Marker marker) {
        if (markerPolyline != null)
            markerPolyline.remove();
        LatLng start = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
        //LatLng waypoint= new LatLng(18.01455, -77.499333);
        LatLng end = marker.getPosition();

        final ProgressDialog progressDialog = ProgressDialog.show(this, "Please wait.",
                "Fetching route information.", true);
        final Routing routing = new Routing.Builder()
                .travelMode(Routing.TravelMode.WALKING)
                .waypoints(start, end)
                .alternativeRoutes(true)
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
                        PolylineOptions polyoptions = new PolylineOptions();
                        polyoptions.color(Color.BLUE);
                        polyoptions.width(10);
                        polyoptions.addAll(arrayList.get(i).getPoints());
                        markerPolyline = googleMap.addPolyline(polyoptions);
                    }

                    @Override
                    public void onRoutingCancelled() {

                    }
                })
                .build();
        routing.execute();
    }

    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                MainActivity.this);
        alertDialog.setTitle("SETTINGS");
        alertDialog.setMessage("Enable Location Provider! Go to settings menu?");
        alertDialog.setPositiveButton("Settings",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        MainActivity.this.startActivity(intent);
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

            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
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

                mPlaceDetailsText.setText(place.getName() + "," + place.getAddress());
                mPlaceDetailsText.setVisibility(View.VISIBLE);
                showSearchedLocation(place);
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                Toast.makeText(getApplicationContext(), status.toString(), Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                // Indicates that the activity_main closed before a selection was made. For example if
                // the user pressed the back button.
            }
        }
    }

    @Override
    public void itemClicked(View clickedView, int position) {

        popUpLayout.setAnimation(slide_down);
        popUpLayout.setVisibility(View.GONE);
        recList.setVisibility(View.GONE);

        String[] place_name = {"Airport", "ATM", "Bank", "Bus", "Church", "Ferry", "Food", "Fuel", "Hospital", "Market",
                "Mosque", "Movie", "Park", "Police", "Mail", "School", "University"};

        String result = "";
        switch (place_name[position].toLowerCase()) {
            case "bus":
                result = "bus_station";
                break;
            case "food":
                result = "restaurant";
                break;
            case "fuel":
                result = "gas_station";
                break;
            case "market":
                result = "shopping_mall";
                break;
            case "movie":
                result = "movie_theater";
                break;
            case "mail":
                result = "post_office";
                break;
            default:
                result = place_name[position].toLowerCase();
                break;
        }


        showNearbyPlaces(result);
    }

    public void showNearbyPlaces(String type) {
        Toast.makeText(getApplicationContext(), "Showing Nearby " + type + "'s", Toast.LENGTH_SHORT).show();
        StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlacesUrl.append("location=" + lastKnownLocation.getLatitude() + "," + lastKnownLocation.getLongitude());
        googlePlacesUrl.append("&radius=" + PROXIMITY_RADIUS);
        googlePlacesUrl.append("&types=" + type);
        googlePlacesUrl.append("&sensor=true");
        googlePlacesUrl.append("&key=" + GOOGLE_API_KEY);

        GooglePlacesReadTask googlePlacesReadTask = new GooglePlacesReadTask(getApplicationContext());


        Object[] toPass = new Object[2];
        toPass[0] = googleMap;
        toPass[1] = googlePlacesUrl.toString();
            /*toPass[2]=pd;*/
        googlePlacesReadTask.execute(toPass);


        googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude()))
                .title("Current Position")
                .icon(BitmapDescriptorFactory
                        .fromResource(R.drawable.mypos)));

        CameraPosition position = CameraPosition.builder()
                .target(new LatLng(lastKnownLocation.getLatitude(),
                        lastKnownLocation.getLongitude()))
                .zoom(16f)
                .bearing(0.0f)
                .tilt(0.0f)
                .build();
    }


    public void setupMenu(int selected) {
        switch (selected) {
            case 1:
        }
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

    private String nameFromLatLng(LatLng latLng) {

        if(!isAvailable()) return latLng.toString();

        String[]info=new String[5];
        Geocoder geoCoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        try {
            List<Address> addresses = geoCoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (addresses.size() > 0) {

                if(addresses.get(0).getFeatureName()!=null)
                {
                    return addresses.get(0).getFeatureName();
                }
                else if(addresses.get(0).getLocality()!=null)
                {
                    return addresses.get(0).getLocality();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    return latLng.toString();
    }

    private void check_theme_color() {



        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        Toolbar guillotine_tollbar = (Toolbar) findViewById(R.id.guillotine_top);

        LinearLayout guillotine_body = (LinearLayout) findViewById(R.id.guillotine_body);

        SharedPreferences bgshared= getSharedPreferences("background", MODE_PRIVATE);
        String Bgfromshared = bgshared.getString("BKGRND", "Error");
        if(Bgfromshared.equals("bg1")) {
            markerClickLayout.setBackgroundColor(Color.parseColor("#9CCC65"));
            toolbar.setBackgroundColor(Color.parseColor("#9CCC65"));
            toolbar.setBackgroundColor(Color.parseColor("#9CCC65"));
            recList.setBackgroundColor(Color.parseColor("#9CCC65"));
            guillotine_tollbar.setBackgroundColor(Color.parseColor("#9CCC65"));
            guillotine_body.setBackgroundColor(Color.parseColor("#9CCC65"));
            menuButton.setBackgroundResource(R.drawable.bg1);

        }
        else if(Bgfromshared.equals("bg2")) {
            markerClickLayout.setBackgroundColor(Color.parseColor("#2D9FC9"));
            toolbar.setBackgroundColor(Color.parseColor("#2D9FC9"));
            recList.setBackgroundColor(Color.parseColor("#2D9FC9"));
            guillotine_tollbar.setBackgroundColor(Color.parseColor("#2D9FC9"));
            guillotine_body.setBackgroundColor(Color.parseColor("#2D9FC9"));
            menuButton.setBackgroundResource(R.drawable.bg2);
        }
        else if(Bgfromshared.equals("bg3")) {
            markerClickLayout.setBackgroundColor(Color.parseColor("#FF9800"));
            toolbar.setBackgroundColor(Color.parseColor("#FF9800"));
            recList.setBackgroundColor(Color.parseColor("#FF9800"));
            guillotine_tollbar.setBackgroundColor(Color.parseColor("#FF9800"));
            guillotine_body.setBackgroundColor(Color.parseColor("#FF9800"));
            menuButton.setBackgroundResource(R.drawable.bg3);
        }
        else if(Bgfromshared.equals("bg4")) {
            markerClickLayout.setBackgroundColor(Color.parseColor("#9575CD"));
            toolbar.setBackgroundColor(Color.parseColor("#9575CD"));
            recList.setBackgroundColor(Color.parseColor("#9575CD"));
            guillotine_tollbar.setBackgroundColor(Color.parseColor("#9575CD"));
            guillotine_body.setBackgroundColor(Color.parseColor("#9575CD"));
            menuButton.setBackgroundResource(R.drawable.bg4);
        }
        else if(Bgfromshared.equals("bg5")) {
            markerClickLayout.setBackgroundColor(Color.parseColor("#e74c3c"));
            toolbar.setBackgroundColor(Color.parseColor("#e74c3c"));
            recList.setBackgroundColor(Color.parseColor("#e74c3c"));
            guillotine_tollbar.setBackgroundColor(Color.parseColor("#e74c3c"));
            guillotine_body.setBackgroundColor(Color.parseColor("#e74c3c"));
            menuButton.setBackgroundResource(R.drawable.bg5);
        }
        else if(Bgfromshared.equals("bg6")) {
            markerClickLayout.setBackgroundColor(Color.parseColor("#009688"));
            toolbar.setBackgroundColor(Color.parseColor("#009688"));
            recList.setBackgroundColor(Color.parseColor("#009688"));
            guillotine_tollbar.setBackgroundColor(Color.parseColor("#009688"));
            guillotine_body.setBackgroundColor(Color.parseColor("#009688"));
            menuButton.setBackgroundResource(R.drawable.bg6);
        }
        else if(Bgfromshared.equals("bg7")) {
            markerClickLayout.setBackgroundColor(Color.parseColor("#F06292"));
            toolbar.setBackgroundColor(Color.parseColor("#F06292"));
            recList.setBackgroundColor(Color.parseColor("#F06292"));
            guillotine_tollbar.setBackgroundColor(Color.parseColor("#F06292"));
            guillotine_body.setBackgroundColor(Color.parseColor("#F06292"));
            menuButton.setBackgroundResource(R.drawable.bg7);
        }
        else if(Bgfromshared.equals("bg8")) {
            markerClickLayout.setBackgroundColor(Color.parseColor("#FF6F00"));
            toolbar.setBackgroundColor(Color.parseColor("#FF6F00"));
            recList.setBackgroundColor(Color.parseColor("#FF6F00"));
            guillotine_tollbar.setBackgroundColor(Color.parseColor("#FF6F00"));
            guillotine_body.setBackgroundColor(Color.parseColor("#FF6F00"));
            menuButton.setBackgroundResource(R.drawable.bg8);
        }


    }

}