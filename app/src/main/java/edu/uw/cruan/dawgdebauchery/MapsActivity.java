package edu.uw.cruan.dawgdebauchery;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//TODO: Refactor code into service
//TODO: prompt user for file name at beginning and put into filename field or sharedpreference
//TODO: Change API Key to be in release mode instead of debug mode
//TODO: Prompt for runtime permission (and switch api level back to proper level)

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener    {

    public static final String TAG = "Map";
    private GoogleMap mMap;
    private boolean mRequestingLocationUpdates = false;
    private GoogleApiClient mGoogleApiClient;
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private LocationRequest mLocationRequest;
    private String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    //Firebase database reference
    private DatabaseReference mDatabase;

    private Location currentLocation;

    private Circle radiusOfCurrentLocation;

    private DataSnapshot eventsSnapshot;

    private boolean locationUpdatedFirstTime = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mapFragment.setRetainInstance(true);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        // Create the LocationRequest object
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(3 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(2 * 1000); // 1 second, in milliseconds

        mGoogleApiClient.connect();

        //get a reference to the database
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Launch location notification service
        Intent intent = new Intent(this, MyLocationService.class);
        startService(intent);

    }

    // Return true if all permissions are granted, false otherwise
    public boolean hasPermissions(Context context) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
            Log.v(TAG, permission);
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            onConnected(null); //do whatever we'd do when first connecting (try again)
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        return true;
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //For now we put all the below code into getInformationFromDataBase(), because we want to change
        //visibility of events every time a change of location is produced. So in this way, it is easier
        //to access the databse and display its information, instead of calling onMapReady()
        getInformationFromDataBase();


        // TODO When we are building out the marker clicking system we have to build our own view and inflate it in place of the other view
    }

    public void getInformationFromDataBase() {
        ValueEventListener valueListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                eventsSnapshot = dataSnapshot;

                if(currentLocation != null) {


                    for(DataSnapshot eventSnapShot: dataSnapshot.getChildren()) {
                        Event event = (Event) eventSnapShot.getValue(Event.class);
                        LatLng location = getLocationFromAddress(event.address);
                        if (location == null) {
                            continue;
                        }
                        if (event.private_party == false) {

                            //show events only under 1km radius
                            boolean visibility;
                            float[] distance = new float[5];

                            Location.distanceBetween(currentLocation.getLatitude(), currentLocation.getLongitude(),
                                    location.latitude, location.longitude, distance);
                            if (distance[0] < 250) {
                                visibility = true;
                            } else {
                                visibility = false;
                            }

                            StringBuilder description = new StringBuilder();
                            description.append("Date: " + event.date + "\n");
                            description.append("Time: " + event.time + "\n");
                            description.append("Address: " + event.address + "\n");
                            description.append("Click me to assist!");
                            mMap.addMarker(new MarkerOptions()
                                    .position(location)
                                    .title("Public: " + event.description)
                                    .snippet(description.toString())
                                    .visible(visibility));
                        } else {
                            //we draw the private events
                            Circle circle = mMap.addCircle(new CircleOptions()
                                    .center(location)
                                    .radius(350)
                                    .strokeColor(Color.RED)
                                    .strokeWidth(6)
                                    .fillColor(R.color.aquaRed)
                                    .visible(true));
                        }
                    }
                    //make each marker info window clickable
                    mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {

                        @Override
                        public void onInfoWindowClick(Marker marker) {
                            if(marker.getTitle().startsWith("Public")) {

                                //register the user to the event
                                registerEventToUserVersion2(marker);

                            }
                            Log.d(TAG, marker.getTitle());
                        }
                    });
                }

                mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

                    @Override
                    public View getInfoWindow(Marker arg0) {
                        return null;
                    }

                    @Override
                    public View getInfoContents(Marker marker) {

                        LinearLayout info = new LinearLayout(MapsActivity.this);
                        info.setOrientation(LinearLayout.VERTICAL);

                        TextView title = new TextView(MapsActivity.this);
                        title.setTextColor(Color.BLACK);
                        title.setGravity(Gravity.CENTER);
                        title.setTypeface(null, Typeface.BOLD);
                        title.setText(marker.getTitle());

                        TextView snippet = new TextView(MapsActivity.this);
                        snippet.setTextColor(Color.GRAY);
                        snippet.setText(marker.getSnippet());

                        info.addView(title);
                        info.addView(snippet);

                        return info;
                    }
                });


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadEvent:onCancelled", databaseError.toException());
            }



        };

        //add the listener to the events group of the database
        mDatabase.child("events").addValueEventListener(valueListener);
    }

    public void registerEventToUser(final Marker marker) {

        ValueEventListener valueListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot eventSnapShot : dataSnapshot.getChildren()) {
                    Event event = (Event) eventSnapShot.getValue(Event.class);
                    Log.w(TAG, marker.getTitle().substring(8));
                    if (event.description.equals(marker.getTitle().substring(8))) {
                        final String eventKey = eventSnapShot.getKey();
                        final String uID = FirebaseAuth.getInstance().getCurrentUser().getUid();

                        mDatabase.child("Users").child(uID).child("saved_events").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                if(dataSnapshot.getValue() == null || !(((Map<String, String>) dataSnapshot.getValue()).values().contains(eventKey))) {
                                    mDatabase.child("Users").child(uID).child("saved_events").push().setValue(eventKey);
                                    mDatabase.child("events").child(eventKey).child("attendees").push().setValue(uID);
                                    toast("Great! You just joined this event as attendee!");

                                } else {
                                    toast("You are already enrolled as attendee");
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadEvents:onCancelled", databaseError.toException());


            }
        };
        mDatabase.child("events").addValueEventListener(valueListener);

    }

    private void toast(String message) {
        Toast toast = Toast.makeText(MapsActivity.this, message, Toast.LENGTH_SHORT);
        TextView v = (TextView)toast.getView().findViewById(android.R.id.message);
        v.setGravity(Gravity.CENTER);
        toast.show();
    }

    public void registerEventToUserVersion2(Marker marker) {
        for(DataSnapshot eventSnapShot : eventsSnapshot.getChildren()) {
            Event event = (Event) eventSnapShot.getValue(Event.class);
            Log.w(TAG, marker.getTitle().substring(8));
            if (event.description.equals(marker.getTitle().substring(8))) {
                final String eventKey = eventSnapShot.getKey();
                final String uID = FirebaseAuth.getInstance().getCurrentUser().getUid();

                mDatabase.child("Users").child(uID).child("saved_events").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if(dataSnapshot.getValue() == null || !(((Map<String, String>) dataSnapshot.getValue()).values().contains(eventKey))) {
                            mDatabase.child("Users").child(uID).child("saved_events").push().setValue(eventKey);
                            mDatabase.child("events").child(eventKey).child("attendees").push().setValue(uID);
                            toast("Great! You just joined this event as attendee!");

                        } else {
                            toast("You are already enrolled as attendee");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        }

    }

    //Converts and address to a LatLng object
    private LatLng getLocationFromAddress(String address) {
        Geocoder coder = new Geocoder(this);
        List<Address> coordinates = new ArrayList<>();
        LatLng p = null;

        try {
            coordinates = coder.getFromLocationName(address, 5);

            if(coordinates.size() == 0) {
                return p;
            }
            Address location = coordinates.get(0);
            p = new LatLng(location.getLatitude(), location.getLongitude());

        } catch (IOException e) {
            e.printStackTrace();
        }
        return p;
    }

    @Override
    public void onConnected(Bundle bundle) {
        // Permission check
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if(permissionCheck == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
        else {
            //request permission
            ActivityCompat.requestPermissions(this, permissions, 1);
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, permissions, 1);
            return;
        }
        mRequestingLocationUpdates = true;
        mMap.setMyLocationEnabled(true);
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (location == null) {  // If location services are off
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
        else {  // Change location
            handleNewLocation(location);
        }
    }

    // Update map to given location
    private void handleNewLocation(Location position) {

        //Place current location marker
        LatLng latLng = new LatLng(position.getLatitude(), position.getLongitude());

        if(currentLocation == null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        }
        
        //get the current location based on gps service
        this.currentLocation = position;
        LatLng newLocation = new LatLng(position.getLatitude(), position.getLongitude());


        if(radiusOfCurrentLocation != null) {
            radiusOfCurrentLocation.remove();
        }
        radiusOfCurrentLocation = mMap.addCircle(new CircleOptions()
                .center(newLocation)
                .radius(250)
                .strokeColor(Color.TRANSPARENT)
                .strokeWidth(2)
                .fillColor(R.color.aliceblue)
                .visible(true));

        //getInformationFromDataBase();

        ///////new modification to read from local reference of database
        //we have to make interaction we database faster --> V2.0
        if(eventsSnapshot != null) {
            for (DataSnapshot eventSnapShot : eventsSnapshot.getChildren()) {
                Event event = (Event) eventSnapShot.getValue(Event.class);
                LatLng location = getLocationFromAddress(event.address);
                if (location == null) {
                    continue;
                }
                if (event.private_party == false) {

                    //show events only under 1km radius
                    boolean visibility;
                    float[] distance = new float[5];

                    Location.distanceBetween(currentLocation.getLatitude(), currentLocation.getLongitude(),
                            location.latitude, location.longitude, distance);
                    if (distance[0] < 250) {
                        visibility = true;
                    } else {
                        visibility = false;
                    }

                    StringBuilder description = new StringBuilder();
                    description.append("Date: " + event.date + "\n");
                    description.append("Time: " + event.time + "\n");
                    description.append("Address: " + event.address + "\n");
                    description.append("Click me to assist!");
                    mMap.addMarker(new MarkerOptions()
                            .position(location)
                            .title("Public: " + event.description)
                            .snippet(description.toString())
                            .visible(visibility));
                } else {
                    //we draw the private events
                    Circle circle = mMap.addCircle(new CircleOptions()
                            .center(location)
                            .radius(350)
                            .strokeColor(Color.RED)
                            .strokeWidth(6)
                            .fillColor(R.color.aquaRed)
                            .visible(true));
                }
            }
            //make each marker info window clickable
            mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {

                @Override
                public void onInfoWindowClick(Marker marker) {
                    if (marker.getTitle().startsWith("Public")) {

                        //register the user to the event
                        registerEventToUserVersion2(marker);

                    }
                    Log.d(TAG, marker.getTitle());
                }
            });


            mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

                @Override
                public View getInfoWindow(Marker arg0) {
                    return null;
                }

                @Override
                public View getInfoContents(Marker marker) {

                    LinearLayout info = new LinearLayout(MapsActivity.this);
                    info.setOrientation(LinearLayout.VERTICAL);

                    TextView title = new TextView(MapsActivity.this);
                    title.setTextColor(Color.BLACK);
                    title.setGravity(Gravity.CENTER);
                    title.setTypeface(null, Typeface.BOLD);
                    title.setText(marker.getTitle());

                    TextView snippet = new TextView(MapsActivity.this);
                    snippet.setTextColor(Color.GRAY);
                    snippet.setText(marker.getSnippet());

                    info.addView(title);
                    info.addView(snippet);

                    return info;
                }
            });
        }

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Log.i(TAG, "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Location services suspended. Please reconnect.");
    }


    // Callback for location changed
    @Override
    public void onLocationChanged(Location location) {

        handleNewLocation(location);
    }

    // Get Current Location
    @Override
    protected void onResume() {
        super.onResume();
        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    // Get Current Location
    private void startLocationUpdates() {
        // Check permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, permissions, 1);
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                mLocationRequest,
                this);
    }

    // Disconnect GoogleApiClient
    private void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        mGoogleApiClient.disconnect();
    }
}