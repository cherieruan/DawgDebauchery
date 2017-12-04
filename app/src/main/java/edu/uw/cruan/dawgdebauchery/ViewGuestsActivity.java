package edu.uw.cruan.dawgdebauchery;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.AdapterView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ViewGuestsActivity extends AppCompatActivity {


    public static final String TAG = "ViewGuests";
    //firebase!
    private DatabaseReference mDatabase;
    private Event event;
    private List<String> attendees;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_guests);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        Bundle extras = getIntent().getExtras();
        String eventKey = extras.getString("eventKey");
        // Get event object
        mDatabase.child("events").child(eventKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                event = (Event) dataSnapshot.getValue();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        attendees = event.confirmedGuests;

        final List<UserAccount> attendeeAccounts = new ArrayList<UserAccount>();
        mDatabase.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    attendeeAccounts.add((UserAccount) postSnapshot.getValue());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        });

        ListAdapter adapter = new ListAdapter(this, attendeeAccounts);
        AdapterView listView = (AdapterView) findViewById(R.id.list_view);
        listView.setAdapter(adapter);

    }

}
