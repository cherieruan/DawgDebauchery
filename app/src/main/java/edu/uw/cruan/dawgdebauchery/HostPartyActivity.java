package edu.uw.cruan.dawgdebauchery;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class HostPartyActivity extends AppCompatActivity {

    private Event event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_party);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // DEV
        setEvent();


        //Bundle extras = getIntent().getExtras();

        //if (extras != null) {
        //    String jsonQueue = extras.getString("event");
        //    event = new Gson().fromJson(jsonQueue, Event.class);
            setInterface();
        //}
    }

    private void setEvent() {
        event = new Event("address", "1/1/1997", "12:00", "LIT", false);
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
                intent.putExtra("event", new Gson().toJson(event));
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
