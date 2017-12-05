package edu.uw.cruan.dawgdebauchery;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class HostPartyActivity extends AppCompatActivity {

    private static final String TAG = "HostPartyActivity";
    private Map<String, String> event;
    private String userID;
    private String eventKey;

    //firebase!
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_party);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            userID = extras.getString("userID");
            // Get event key
            mDatabase.child("Users").child(userID).child("eventKey").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    eventKey = (String) dataSnapshot.getValue();
                    getEvent();
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
                Log.v(TAG, ((HashMap) dataSnapshot.getValue()).toString());
                event = (HashMap<String, String>)dataSnapshot.getValue();
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
        eventName.setText(event.get("name"));

        // Event description
        TextView descr = (TextView) findViewById(R.id.event_description);
        descr.setText(event.get("description"));

        findViewById(R.id.edit_party_details).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HostPartyActivity.this, EditEventActivity.class);
                intent.putExtra("eventKey", eventKey);
                startActivity(intent);
            }
        });

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
    }

}
