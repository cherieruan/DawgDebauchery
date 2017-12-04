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

    private Event event;
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
            mDatabase.child("users").child(userID).child("user_event").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    eventKey = (String) dataSnapshot.getValue();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {}
            });

            // Get event object
            mDatabase.child("events").child(eventKey).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    event = (Event) dataSnapshot.getValue();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
            setInterface();
        }
    }

    /*private void setEvent() {
        event = new Event("address", "1/1/1997", "12:00", "LIT");
        // Set up prospective guests
        Queue<UserAccount> prospective = new LinkedList<>();
        for (int i = 0; i < 5; i++) {
            UserAccount acc = new UserAccount(i + "", i + "", R.drawable.ic_favorite_black_24dp);
            prospective.add(acc);
        }
        event.pendingGuests = prospective;

        // Set up curr guests
        List<UserAccount> guests = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            UserAccount acc = new UserAccount(i + "f", i + "l", R.drawable.ic_favorite_black_24dp);
            guests.add(acc);
        }
        event.confirmedGuests = guests;
    }*/

    private void setInterface() {
        // Event name
        TextView eventName = (TextView) findViewById(R.id.event_name);
        eventName.setText(event.name);

        // Event description
        TextView descr = (TextView) findViewById(R.id.event_description);
        descr.setText(event.description);

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
