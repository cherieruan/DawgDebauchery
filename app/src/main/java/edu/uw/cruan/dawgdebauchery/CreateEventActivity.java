package edu.uw.cruan.dawgdebauchery;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import edu.uw.cruan.dawgdebauchery.Event;

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
                int year = create_event_date.getYear();
                int month = create_event_date.getMonth();
                int date = create_event_date.getDayOfMonth();
                Calendar formattedDate = Calendar.getInstance();
                formattedDate.set(year, month, date);

                String address = create_event_address.getText().toString();

                String formattedTime = "";
                int hour = create_event_time.getCurrentHour();
                String sHour = "00";
                if(hour < 10){
                    sHour = "0"+hour;
                } else {
                    sHour = String.valueOf(hour);
                }

                int minute = create_event_time.getCurrentMinute();
                String sMinute = "00";
                if(minute < 10){
                    sMinute = "0"+minute;
                } else {
                    sMinute = String.valueOf(minute);
                }

                formattedTime = sHour+":"+sMinute;

                String description = create_event_description.getText().toString();

                Event newEvent = new Event(address, formattedDate.getTime(), formattedTime, description);
                mDatabase.child("events").push().setValue(newEvent); //add to the list
            }
        });


//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
    }

}
