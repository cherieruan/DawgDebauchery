package edu.uw.cruan.dawgdebauchery;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HostPartyActivity extends AppCompatActivity {

    private static final String TAG = "HostPartyActivity";
    private Event event;
    private String userID;
    private String eventKey;

    //firebase!
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_party);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            userID = extras.getString("userID");
            // Get event key
            mDatabase.child("Users").child(userID).child("eventKey").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    eventKey = (String) dataSnapshot.getValue();
                    if (eventKey == null) {
                        Intent intent = new Intent(HostPartyActivity.this, MainActivity.class);
                        startActivity(intent);
                    } else {
                        getEvent();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {}
            });
        }
    }

    private void getEvent() {
        // Get event object
        mDatabase.child("events").child(eventKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                event = (Event) dataSnapshot.getValue(Event.class);
                setInterface();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void setInterface() {
        // Event name
        TextView eventName = (TextView) findViewById(R.id.event_name);
        eventName.setText(event.name);

        // Event description
        TextView descr = (TextView) findViewById(R.id.event_description);
        descr.setText(event.description);

        findViewById(R.id.edit_party_details).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HostPartyActivity.this, EditEventActivity.class);
                intent.putExtra("eventKey", eventKey);
                startActivity(intent);
            }
        });

        // Event address
        TextView addr = (TextView) findViewById(R.id.event_address);
        addr.setText(event.address);

        // Event time
        TextView time = (TextView) findViewById(R.id.event_time);
        time.setText(event.time);

        // Buttons
        findViewById(R.id.view_guest_list).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HostPartyActivity.this, ViewGuestsActivity.class);
                intent.putExtra("eventKey", eventKey);
                startActivity(intent);
            }
        });

        findViewById(R.id.approve_guests).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HostPartyActivity.this, HostTinderActivity.class);
                intent.putExtra("eventKey", eventKey);
                startActivity(intent);
            }
        });

        findViewById(R.id.cancel_event).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("eventKey", eventKey);
                bundle.putString("uid", userID);
                CancelEventFragment alertdFragment = new CancelEventFragment();
                alertdFragment.setArguments(bundle);
                // Show Alert DialogFragment
                alertdFragment.show(getSupportFragmentManager(), "Alert Dialog Fragment");
            }
        });
    }

}
