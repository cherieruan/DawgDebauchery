package edu.uw.cruan.dawgdebauchery;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class CancelEventFragment extends DialogFragment {

    private DatabaseReference mDatabase;
    private String eventKey;
    private String userID;
    private Context ctx;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        mDatabase = FirebaseDatabase.getInstance().getReference();
        eventKey = getArguments().getString("eventKey");
        userID = getArguments().getString("uid");

            return new AlertDialog.Builder(getActivity())
                // Set Dialog Title
                .setTitle("Cancel Event")
                // Set Dialog Message
                .setMessage("Are you sure you want to cancel your event?")

                // Positive button
                .setPositiveButton("Cancel Event", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        deleteFromUsers();
                        mDatabase.child("events").child(eventKey).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                // Remove user from database list
                                dataSnapshot.getRef().removeValue();
                                Toast.makeText(getContext(), "Event Successfully Canceled.",
                                        Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {}
                        });
                    }
                })

                // Negative Button
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Do something else
                    }
                }).create();
    }

    private void deleteFromUsers() {
        // Delete from other users
        mDatabase.child("events").child(eventKey).child("attendees").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Remove user from database list
                Map<String, String> userIDs = (Map<String, String>) dataSnapshot.getValue();
                for (String id : userIDs.keySet()) {
                    String eventK = userIDs.get(id);
                    if (eventK.equals(eventKey)) {
                        mDatabase.child("Users").child(id).child("saved_events").child(eventK).removeValue();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        // Delete from this user
        mDatabase.child("Users").child(userID).child("eventKey").removeValue();
    }
}

