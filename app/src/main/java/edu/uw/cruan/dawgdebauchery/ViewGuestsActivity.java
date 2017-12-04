package edu.uw.cruan.dawgdebauchery;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.AdapterView;

import com.google.gson.Gson;

import java.util.List;

public class ViewGuestsActivity extends AppCompatActivity {


    public static final String TAG = "ViewGuests";
    private List<UserAccount> attendees;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_guests);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle extras = getIntent().getExtras();
        String jsonQueue = extras.getString("event");
        Event event = new Gson().fromJson(jsonQueue, Event.class);
        attendees = event.confirmedGuests;

        ListAdapter adapter = new ListAdapter(this, attendees);

        AdapterView listView = (AdapterView) findViewById(R.id.list_view);

        Log.v(TAG, ((UserAccount) attendees.get(0)).toString());

        listView.setAdapter(adapter);

    }

}
