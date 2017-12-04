package edu.uw.cruan.dawgdebauchery;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.List;
import java.util.Queue;

public class HostTinderActivity extends AppCompatActivity {

    public static final String TAG = "Tinder Activity";
    public Event event;
    public Queue<UserAccount> pending;
    public List<UserAccount> confirmed;
    public UserAccount currUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_tinder);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String jsonQueue = extras.getString("event");
            event = new Gson().fromJson(jsonQueue, Event.class);
            pending = event.pendingGuests;
            confirmed = event.confirmedGuests;

            Button noButton = (Button) findViewById(R.id.no_button);
            noButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    displayUser();
                }
            });

            Button yesButton = (Button) findViewById(R.id.yes_button);
            yesButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    confirmed.add(currUser);
                    displayUser();
                }
            });
            displayUser();
        }


    }

    private void populateAttendees() {
        UserAccount acc1 = new UserAccount("Taylor", "Swift", R.drawable.ic_audiotrack_black_24dp);
        UserAccount acc2 = new UserAccount("Taylor1", "Swift2", R.drawable.ic_brightness_low_black_24dp);
        UserAccount acc3 = new UserAccount("Taylor2", "Swift3", R.drawable.ic_favorite_black_24dp);

        pending.add(acc1);
        pending.add(acc2);
        pending.add(acc3);
    }

    private void displayUser() {
        if (pending.isEmpty()) {
            displayEmptyPage();
        } else {
            currUser = pending.remove();
            ImageView imgView = (ImageView) findViewById(R.id.user_img);
            imgView.setImageResource(currUser.imgURL);

            TextView txtView = (TextView) findViewById(R.id.invite_text);
            txtView.setText("Invite " + currUser.fName + "?");
        }
    }

    private void displayEmptyPage() {
        TextView txtView = (TextView) findViewById(R.id.invite_text);
        txtView.setText("No pending guests.");
    }

}
