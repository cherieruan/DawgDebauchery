package edu.uw.cruan.dawgdebauchery;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.NetworkImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private boolean hasEvent = false;
    private String userUID;
    private DatabaseReference mDatabase;
    private String fullName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        LinearLayout main_view_map = (LinearLayout) this.findViewById(R.id.main_view_map);
        LinearLayout main_view_events_list = (LinearLayout) this.findViewById(R.id.main_view_events_list);
        LinearLayout main_edit_profile = (LinearLayout) this.findViewById(R.id.main_edit_profile);
        final NetworkImageView main_profile_pic = (NetworkImageView) findViewById(R.id.main_profile_pic);

        // Set current user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        userUID = user.getUid();

        DatabaseReference userRef =  mDatabase.child("Users").child(userUID);

        userRef.child("imgUrl")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.v(TAG, "Profile pic changed -> updating locally");
                        String imageURL = (String) dataSnapshot.getValue();
                        main_profile_pic.setImageUrl(imageURL,
                                RequestSingleton.getInstance(getApplicationContext()).imageLoader);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        // Grab name from database
        mDatabase.child("Users").child(userUID).child("firstName").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                fullName = "Welcome, " + ((String) dataSnapshot.getValue()) + fullName + "!";
                TextView profileName = (TextView) findViewById(R.id.profile_name);
                profileName.setText(fullName);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        // Check if user is hosting event
        mDatabase.child("Users").child(userUID).child("eventKey").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                LinearLayout main_create_event = (LinearLayout) findViewById(R.id.main_create_event);
                TextView main_event_tv = (TextView) findViewById(R.id.main_event_tv);
                if (dataSnapshot.getValue() != null) {  // User has event
                    main_event_tv.setText("Manage Your Event");
                    hasEvent = true;
                    main_create_event.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(MainActivity.this, HostPartyActivity.class);
                            intent.putExtra("userID", userUID);
                            startActivity(intent);
                        }
                    });
                } else {  // user doesn't have event
                    main_create_event.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(MainActivity.this, CreateEventActivity.class);
                            startActivity(intent);
                        }
                    });

                    if (hasEvent) {
                        Toast.makeText(getApplicationContext(), "Event Successfully Canceled.",
                                Toast.LENGTH_SHORT).show();
                        hasEvent = false;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        main_view_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                startActivity(intent);
            }
        });

        main_view_events_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ViewEventListActivity.class);
                startActivity(intent);
            }
        });

        main_edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EditProfile.class);
                startActivity(intent);
            }
        });
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
