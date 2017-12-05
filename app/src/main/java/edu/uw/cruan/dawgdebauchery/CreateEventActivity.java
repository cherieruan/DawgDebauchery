package edu.uw.cruan.dawgdebauchery;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CreateEventActivity extends AppCompatActivity {

    private final String TAG = "CreateEventActivity";

    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Get field references

        final EditText create_event_address = (EditText) findViewById(R.id.create_event_address);
        final EditText create_event_name = (EditText) findViewById(R.id.create_event_name);
        final EditText create_event_description = (EditText) findViewById(R.id.create_event_description);

        Button create_event_next = (Button) findViewById(R.id.create_event_next);
        create_event_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String address = create_event_address.getText().toString();
                String description = create_event_description.getText().toString();
                String name = create_event_name.getText().toString();

                TextView date = (TextView) findViewById(R.id.create_event_set_date_text_view);
                TextView time = (TextView) findViewById(R.id.create_event_set_time_text_view);

                Spinner spinner = (Spinner) findViewById(R.id.create_event_privacy);

                boolean privacy;
                if (spinner.getSelectedItem().toString().equals("Private")) {
                    privacy = true;
                } else {
                    privacy = false;
                }

                Event newEvent = new Event(name, address, date.getText().toString(), time.getText().toString(), description, privacy);
                //get the push key value
                String eventKey = mDatabase.child("events").push().getKey();
                //then you can write in that node in this way
                mDatabase.child("events").child(eventKey).setValue(newEvent);

                FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                String UID = currentFirebaseUser.getUid();
                if (UID == null) {
                    throw new NullPointerException("There is no user currently logged in, log in a user.");
                }

                mDatabase.child("Users").child(UID).child("event_key").push().setValue(eventKey);

//                DatabaseReference databaseRef;
//                databaseRef = FirebaseDatabase.getInstance().getReference()
//                        .child("users").child(UID).child("event_key").push().setValue();
//                databaseRef.addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        String value = (String)dataSnapshot.getValue();
//                        Log.v(TAG, value);
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//                        Log.w(TAG, "listener canceled", databaseError.toException());
//                    }
//                });

                Intent intent = new Intent(CreateEventActivity.this, MainActivity.class);
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
