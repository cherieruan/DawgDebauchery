package edu.uw.cruan.dawgdebauchery;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.LinkedList;
import java.util.Queue;

public class HostTinderActivity extends AppCompatActivity {

    public static final String TAG = "Tinder Activity";
    public Queue<UserAccount> attendees = new LinkedList<UserAccount>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_tinder);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String jsonQueue = extras.getString("attendees");
            attendees = new Gson().fromJson(jsonQueue, Queue.class);

            Button noButton = (Button) findViewById(R.id.no_button);
            noButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.v(TAG, "No button clicked");
                    displayUser();
                }
            });

            Button yesButton = (Button) findViewById(R.id.yes_button);
            yesButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.v(TAG, "Yes button clicked");
                    displayUser();
                }
            });
            displayUser();
        }


    }

    public void populateAttendees() {
        UserAccount acc1 = new UserAccount("Taylor", "Swift", R.drawable.ic_audiotrack_black_24dp);
        UserAccount acc2 = new UserAccount("Taylor", "Swift2", R.drawable.ic_brightness_low_black_24dp);
        UserAccount acc3 = new UserAccount("Taylor", "Swift3", R.drawable.ic_favorite_black_24dp);

        attendees.add(acc1);
        attendees.add(acc2);
        attendees.add(acc3);
    }

    public void displayUser() {
        UserAccount user = attendees.remove();
        ImageView imgView = (ImageView) findViewById(R.id.user_img);
        imgView.setImageResource(user.imgURL);

        TextView txtView = (TextView) findViewById(R.id.invite_text);
        txtView.setText("Invite " + user.fName + "?");
    }

}
