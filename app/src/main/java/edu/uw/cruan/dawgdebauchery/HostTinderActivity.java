package edu.uw.cruan.dawgdebauchery;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class HostTinderActivity extends AppCompatActivity {

    public static final String TAG = "HostTinderActivity";
    public Map<String, String> eventMap;
    public String eventKey;
    public Queue<String> pending = new LinkedList<>();
    public List<String> confirmed = new ArrayList<>();
    public String currUserID;
    public UserAccount user;
    public DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_tinder);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            eventKey = extras.getString("eventKey");
            mDatabase.child("events").child(eventKey).child("pending").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    HashMap<String,String> hashmap = (HashMap<String,String>) dataSnapshot.getValue();
                    if (hashmap != null) {
                        for (String key : hashmap.keySet()) {
                            Log.v(TAG, hashmap.get(key));
                            pending.add(hashmap.get(key));
                        }
                    }
                    if (pending.isEmpty()) {
                        displayEmptyPage();
                    } else {
                        displayUser();
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {}
            });

            Button noButton = (Button) findViewById(R.id.no_button);
            noButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mDatabase.child("events").child(eventKey).child("pending").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            HashMap<String,String> userMap = (HashMap<String, String>) dataSnapshot.getValue();
                            if (userMap != null) {
                                for (String key : userMap.keySet()) {
                                    Log.v(TAG, "Curr User ID: " + currUserID);
                                    if (userMap.get(key).equals(currUserID)) {
                                        Log.v(TAG, "Key:  " + key);
                                        mDatabase.child("events").child(eventKey).child("pending").child(key).removeValue();
                                        break;
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {}
                    });
                    displayUser();
                }
            });

            Button yesButton = (Button) findViewById(R.id.yes_button);
            yesButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    confirmed.add(currUserID);
                    mDatabase.child("events").child(eventKey).child("attendees").push().setValue(currUserID); //add to the list
                    mDatabase.child("events").child(eventKey).child("pending").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            HashMap<String,String> userMap = (HashMap<String, String>) dataSnapshot.getValue();
                            if (userMap != null) {
                                for (String key : userMap.keySet()) {
                                    Log.v(TAG, "Curr User ID: " + currUserID);
                                    if (userMap.get(key).equals(currUserID)) {
                                        Log.v(TAG, "Key:  " + key);
                                        mDatabase.child("events").child(eventKey).child("pending").child(key).removeValue();
                                        break;
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {}
                    });
                    displayUser();
                }
            });
        }
    }

    private void displayUser() {
        if (pending.isEmpty()) {
            displayEmptyPage();
        } else {
            currUserID = pending.remove();
            Log.v(TAG, "CurrentUserID: "+currUserID);
            mDatabase.child("Users").child(currUserID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    HashMap<String,String> userMap = (HashMap<String, String>) dataSnapshot.getValue();
                    //user = (UserAccount) dataSnapshot.getValue();
                    NetworkImageView imgView = (NetworkImageView) findViewById(R.id.user_img);
                    imgView.setImageUrl(userMap.get("imgUrl"), RequestSingleton.getInstance(getApplicationContext()).imageLoader);
                    TextView txtView = (TextView) findViewById(R.id.invite_text);
                    txtView.setText("Invite " + userMap.get("firstName").toString() + "?");
//                    // Remove user from database list
//                    dataSnapshot.getRef().removeValue();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {}
            });
        }
    }

    private void displayEmptyPage() {
        TextView txtView = (TextView) findViewById(R.id.invite_text);
        txtView.setVisibility(View.GONE);
        ImageView imgView = (ImageView) findViewById(R.id.user_img);
        imgView.setImageResource(0);
        Button noButton = (Button) findViewById(R.id.no_button);
        noButton.setVisibility(View.GONE);
        Button yesButton = (Button) findViewById(R.id.yes_button);
        yesButton.setVisibility(View.GONE);
        TextView noGuests = (TextView) findViewById(R.id.no_pending);
        noGuests.setVisibility(View.VISIBLE);
    }

}
