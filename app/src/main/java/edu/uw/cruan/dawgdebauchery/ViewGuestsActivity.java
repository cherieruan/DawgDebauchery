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
import java.util.Map;

public class ViewGuestsActivity extends AppCompatActivity {


    public static final String TAG = "ViewGuests";
    //firebase!
    private DatabaseReference mDatabase;
    private List<String> attendees;
    private List<UserAccount> attendeeAccounts = new ArrayList<UserAccount>();
    private int i;
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
        mDatabase.child("events").child(eventKey).child("attendees").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                attendees = (List<String>) dataSnapshot.getValue();
                toView();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    private void toView() {
        for (i = 0; i < attendees.size(); i++) {
            String UID = attendees.get(i);
            mDatabase.child("Users").child(UID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Map<String, String> userMap = (Map<String, String>) dataSnapshot.getValue();
                    attendeeAccounts.add(new UserAccount(userMap.get("firstName"), userMap.get("lastName"),
                            userMap.get("imgUrl")));
                    //Log.v(TAG, UID);
                    if (i == attendees.size()) {
                        setAdapter();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {}
            });
        }
    }

    private void setAdapter() {
        Log.v(TAG, attendeeAccounts.toString());
        ListAdapter adapter = new ListAdapter(getApplicationContext(), attendeeAccounts);
        AdapterView listView = (AdapterView) findViewById(R.id.list_view);
        listView.setAdapter(adapter);
    }

}
