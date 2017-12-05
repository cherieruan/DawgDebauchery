package edu.uw.cruan.dawgdebauchery;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class EditEventActivity extends AppCompatActivity {

    private final String TAG = "EditEventActivity";

    private DatabaseReference mDatabase;
    private Event event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_event);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        final EditText edit_event_address = (EditText) findViewById(R.id.edit_event_address);
        final EditText edit_event_name = (EditText) findViewById(R.id.edit_event_name);
        final EditText edit_event_description = (EditText) findViewById(R.id.edit_event_description);
        final TextView dateView = (TextView) findViewById(R.id.create_event_set_date_text_view);
        final TextView timeView = (TextView) findViewById(R.id.create_event_set_time_text_view);
        final Spinner spinner = (Spinner) findViewById(R.id.edit_event_privacy);

        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final String UID = currentFirebaseUser.getUid();
        if (UID == null) {
            throw new NullPointerException("There is no user currently logged in, log in a user.");
        }

        DatabaseReference databaseRef = mDatabase.child("Users").child(UID).child("eventKey");
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String eventKey = (String)dataSnapshot.getValue();
                DatabaseReference databaseRef = mDatabase.child("events").child(eventKey);
                databaseRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        event = dataSnapshot.getValue(Event.class);
                        edit_event_address.setText(event.address);
                        edit_event_name.setText(event.name);
                        edit_event_description.setText(event.description);
                        dateView.setText(event.date);
                        timeView.setText(event.time);
                        if (event.private_party == true) {
                            spinner.post(new Runnable() {
                                public void run() {
                                    spinner.setSelection(1);
                                }
                            });
                        } else {
                            spinner.post(new Runnable() {
                                public void run() {
                                    spinner.setSelection(0);
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "listener canceled", databaseError.toException());
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "listener canceled", databaseError.toException());
            }
        });

        final Button edit_event_next = (Button) findViewById(R.id.edit_event_next);
        edit_event_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String address = edit_event_address.getText().toString();
                String description = edit_event_description.getText().toString();
                String name = edit_event_name.getText().toString();

                boolean privacy;
                if (spinner.getSelectedItem().toString() == "Private") {
                    privacy = true;
                } else {
                    privacy = false;
                }

                Log.v(TAG, String.valueOf(privacy));

                final Event newEvent = new Event(name, address, dateView.getText().toString(), timeView.getText().toString(), description, privacy);

                DatabaseReference databaseRef = mDatabase.child("Users").child(UID).child("eventKey");
                databaseRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String eventKey = (String)dataSnapshot.getValue();
                        Log.v(TAG, eventKey);
                        mDatabase.child("events").child(eventKey).setValue(newEvent);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "listener canceled", databaseError.toException());
                    }
                });

                Intent intent = new Intent(EditEventActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }

    public void onSetTimeButtonClicked(View v){
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getFragmentManager(),"TimePicker");
    }

    public void onSetDateButtonClicked(View v){
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(),"DatePicker");
    }

}
