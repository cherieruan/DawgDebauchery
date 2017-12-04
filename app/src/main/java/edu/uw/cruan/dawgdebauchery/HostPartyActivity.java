package edu.uw.cruan.dawgdebauchery;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;

public class HostPartyActivity extends AppCompatActivity {

    private Event event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_party);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle extras = getIntent().getExtras();

        // DEBUGGING PURPOSES
        event = new Event("5039 18th Ave NE", "1/1/1997", "7:00PM", "LIT");

        //if (extras != null) {
            //String jsonQueue = extras.getString("event");
            //event = new Gson().fromJson(jsonQueue, Event.class);
            setInterface();
        //}
    }

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
                intent.putExtra("attendees", new Gson().toJson(event.confirmedGuests));
                startActivity(intent);
            }
        });

        findViewById(R.id.approve_guests).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HostPartyActivity.this, HostTinderActivity.class);
                intent.putExtra("event", new Gson().toJson(event));
                startActivity(intent);
            }
        });
    }

}
