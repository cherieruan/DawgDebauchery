package edu.uw.cruan.dawgdebauchery;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewGuestsActivity extends AppCompatActivity {


    public static final String TAG = "ViewGuests";
    //firebase!
    private DatabaseReference mDatabase;
    private HashMap<String,String> attendees;
    private List<UserAccount> attendeeAccounts = new ArrayList<UserAccount>();
    private int i = 0;

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
                attendees = (HashMap<String, String>) dataSnapshot.getValue();
                if (attendees == null) {
                    findViewById(R.id.no_guests).setVisibility(View.VISIBLE);
                } else {
                    toView();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    private void toView() {
        for (String key : attendees.keySet()) {
            String UID = attendees.get(key);
            Log.v(TAG, UID);
            mDatabase.child("Users").child(UID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Map<String, String> userMap = (Map<String, String>) dataSnapshot.getValue();
                    attendeeAccounts.add(new UserAccount(userMap.get("firstName"), userMap.get("lastName"),
                            userMap.get("imgUrl")));
                    //Log.v(TAG, UID);
                    Log.v(TAG, ""+i);
                    if (i == attendees.keySet().size()) {
                        Log.v(TAG, "Setting adapter");
                        setAdapter();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {}
            });
            i++;
        }
    }

    private void setAdapter() {
        Log.v(TAG, attendeeAccounts.toString());
        ListAdapter adapter = new ListAdapter(getApplicationContext(), attendeeAccounts);
        AdapterView listView = (AdapterView) findViewById(R.id.list_view);
        listView.setAdapter(adapter);
    }

}
