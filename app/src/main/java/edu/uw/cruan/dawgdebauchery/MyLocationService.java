package edu.uw.cruan.dawgdebauchery;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MyLocationService extends Service
{
    private static final String TAG = "MyLocationService";
    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 3000;
    private static final float LOCATION_DISTANCE = 10f;
    private DatabaseReference mDatabase;
    private Location currentLocation;
    private List<LatLng> notifiedAddresses = new ArrayList<LatLng>();
    private Context serviceThis = this;
    private static final String NOTIFICATION_CHANNEL_ID = "my_channel_01"; //channel ID
    private static final int DEMO_PENDING_ID = 1;
    private static final int DEMO_NOTIFICATION_ID = 2;

    LocationListener[] mLocationListeners = new LocationListener[] {
            new LocationListener(LocationManager.GPS_PROVIDER),
            new LocationListener(LocationManager.NETWORK_PROVIDER)
    };

    @Override
    public IBinder onBind(Intent arg0)
    {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Log.e(TAG, "onStartCommand");
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onCreate()
    {
        Log.e(TAG, "onCreate");

        mDatabase = FirebaseDatabase.getInstance().getReference();

        initializeLocationManager();
//        try {
//            mLocationManager.requestLocationUpdates(
//                    LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
//                    mLocationListeners[1]);
//        } catch (java.lang.SecurityException ex) {
//            Log.i(TAG, "fail to request location update, ignore", ex);
//        } catch (IllegalArgumentException ex) {
//            Log.d(TAG, "network provider does not exist, " + ex.getMessage());
//        }
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[0]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "gps provider does not exist " + ex.getMessage());
        }
    }

    @Override
    public void onDestroy()
    {
        Log.e(TAG, "onDestroy");
        super.onDestroy();
        if (mLocationManager != null) {
            for (int i = 0; i < mLocationListeners.length; i++) {
                try {
                    mLocationManager.removeUpdates(mLocationListeners[i]);
                } catch (Exception ex) {
                    Log.i(TAG, "fail to remove location listners, ignore", ex);
                }
            }
        }
    }

    private void initializeLocationManager() {
        Log.e(TAG, "initializeLocationManager");
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }

    // create LocationListener class to get location updates
    private class LocationListener implements android.location.LocationListener
    {
        Location mLastLocation;

        public LocationListener(String provider)
        {
            Log.e(TAG, "LocationListener " + provider);
            mLastLocation = new Location(provider);
        }

        @Override
        public void onLocationChanged(Location location)
        {
            Log.e(TAG, "onLocationChanged: " + location);
            mLastLocation.set(location);
            currentLocation = location;
            getInformationFromDataBase();
        }

        @Override
        public void onProviderDisabled(String provider)
        {
            Log.e(TAG, "onProviderDisabled: " + provider);
        }

        @Override
        public void onProviderEnabled(String provider)
        {
            Log.e(TAG, "onProviderEnabled: " + provider);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras)
        {
            Log.e(TAG, "onStatusChanged: " + provider);
        }
    }

    public void getInformationFromDataBase() {
        ValueEventListener valueListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(currentLocation != null) {

                    for(DataSnapshot eventSnapShot: dataSnapshot.getChildren()) {
                        Event event = (Event) eventSnapShot.getValue(Event.class);
                        LatLng location = getLocationFromAddress(event.address);
                        if (location == null || notifiedAddresses.contains(location)) {
                            continue;
                        }
                        if (event.private_party == false) {
                            float[] distance = new float[5];

                            Location.distanceBetween(currentLocation.getLatitude(), currentLocation.getLongitude(),
                                    location.latitude, location.longitude, distance);
                            Log.v(TAG, "Distance Between: "+distance[0]);
                            if (distance[0] < 250) { // TODO Change 250 to a SharedPreference

                                NotificationCompat.Builder builder = new NotificationCompat.Builder(serviceThis, NOTIFICATION_CHANNEL_ID)
                                        .setSmallIcon(R.drawable.ic_whatshot_black_24dp)
                                        .setContentTitle(event.name)
                                        .setContentText(event.description);

                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                    //Oreo support
                                    NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "Demo channel", NotificationManager.IMPORTANCE_HIGH);
                                    NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                                    notificationManager.createNotificationChannel(channel);
                                } else {
                                    //everything else!
                                    builder.setPriority(NotificationCompat.PRIORITY_HIGH);
                                    builder.setVibrate(new long[]{0, 500, 500, 5000});
                                    builder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);
                                }

                                Intent intent = new Intent(serviceThis, CreateEventActivity.class);
                                TaskStackBuilder stackBuilder = TaskStackBuilder.create(serviceThis);
                                stackBuilder.addParentStack(CreateEventActivity.class);
                                stackBuilder.addNextIntent(intent);
                                PendingIntent pendingIntent = stackBuilder.getPendingIntent(DEMO_PENDING_ID,
                                        PendingIntent.FLAG_UPDATE_CURRENT);

                                builder.setContentIntent(pendingIntent);

                                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                                notificationManager.notify(DEMO_NOTIFICATION_ID, builder.build()); //post the notification!

                                notifiedAddresses.add(location);
                                Log.v(TAG, "Notifying ["+location.toString()+"]!");
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadEvent:onCancelled", databaseError.toException());
            }



        };

        //add the listener to the events group of the database
        mDatabase.child("events").addValueEventListener(valueListener);
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
}
