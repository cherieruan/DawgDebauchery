package edu.uw.cruan.dawgdebauchery;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private boolean hasEvent = true;
    private String userUID;
    private DatabaseReference mDatabase;
    private String fullName = "";
    private boolean hasName = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        Button main_view_map = (Button) this.findViewById(R.id.main_view_map);
        Button main_view_events_list = (Button) this.findViewById(R.id.main_view_events_list);
        Button main_edit_profile = (Button) this.findViewById(R.id.main_edit_profile);
        Button main_create_event = (Button) this.findViewById(R.id.main_create_event);

        // Set current user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        userUID = user.getUid();

        // Grab name from database
        mDatabase.child("Users").child(userUID).child("firstName").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (hasName) {
                    fullName = "Welcome, " + ((String) dataSnapshot.getValue()) + " " + fullName + "!";
                    TextView profileName = (TextView) findViewById(R.id.profile_name);
                    profileName.setText(fullName);
                } else {
                    fullName += ((String) dataSnapshot.getValue()) + " ";
                    hasName = !hasName;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        mDatabase.child("Users").child(userUID).child("lastName").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                fullName = fullName + (String) dataSnapshot.getValue();
                if (hasName) {
                    TextView profileName = (TextView) findViewById(R.id.profile_name);
                    profileName.setText(fullName);
                } else {
                    hasName = !hasName;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        main_view_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                startActivity(intent);
            }
        });

        // TODO Create activity for event list and properly bind this button
        main_view_events_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CreateEventActivity.class);
                startActivity(intent);
            }
        });

        main_edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, RegistrationActivity.class);
                startActivity(intent);
            }
        });

        if (hasEvent) {
            main_create_event.setText("MANAGE YOUR EVENT");
            main_create_event.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MainActivity.this, HostPartyActivity.class);
                    startActivity(intent);
                }
            });
        } else {
            main_create_event.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MainActivity.this, CreateEventActivity.class);
                    startActivity(intent);
                }
            });
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
