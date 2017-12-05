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

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CreateEventActivity extends AppCompatActivity {

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

        final EditText create_event_description = (EditText) findViewById(R.id.create_event_description);

        Button create_event_next = (Button) findViewById(R.id.create_event_next);
        create_event_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String address = create_event_address.getText().toString();
                String description = create_event_description.getText().toString();

                TextView date = (TextView) findViewById(R.id.create_event_set_date_text_view);
                TextView time = (TextView) findViewById(R.id.create_event_set_time_text_view);

                Spinner spinner = (Spinner) findViewById(R.id.create_event_privacy);

                boolean privacy;
                if (spinner.getSelectedItem().toString() == "Private") {
                    privacy = true;
                } else {
                    privacy = false;
                }

                Event newEvent = new Event("temp Name", address, date.getText().toString(), time.getText().toString(), description, privacy);
                mDatabase.child("events").push().setValue(newEvent); //add to the list

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
