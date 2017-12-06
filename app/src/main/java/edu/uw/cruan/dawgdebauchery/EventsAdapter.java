package edu.uw.cruan.dawgdebauchery;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.Map;

/**
 * Created by cherieruan on 12/4/17.
 */

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.MyViewHolder> {

    public static final String TAG = "EventsAdapter";
    private List<Event> eventsList;
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    private Context mCtx;

    public EventsAdapter(Context ctx, List<Event> eventsList) {
        this.mCtx = ctx;
        this.eventsList = eventsList;
    }

    @Override
    public EventsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.event_list_row, parent, false);

        final MyViewHolder eventView = new MyViewHolder(itemView);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mDatabase.child("events").addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                            final Event event = (Event) postSnapshot.getValue(Event.class);
                            if (event.name.equals(String.valueOf(eventView.name.getText()))) {
                                final String eventKey = postSnapshot.getKey();
                                final String uID = FirebaseAuth.getInstance().getCurrentUser().getUid();

                                // Check if user is already attending this event

                                mDatabase.child("Users").child(uID).child("saved_events").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.getValue() == null || !(((Map<String, String>) dataSnapshot.getValue()).values().contains(eventKey))) {
                                            if (!event.private_party) {  // Add user to event's attending list
                                                mDatabase.child("events").child(eventKey).child("attendees").push().setValue(uID);
                                                Toast.makeText(mCtx, "Added to guest list.",
                                                        Toast.LENGTH_SHORT).show();
                                                // Add event to user's interested events list
                                                mDatabase.child("Users").child(uID).child("saved_events").push().setValue(eventKey);
                                            } else {  // event is private, check if invitation is pending
                                                mDatabase.child("events").child(eventKey).child("pending").addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                        if (dataSnapshot.getValue() == null || !(((Map<String, String>) dataSnapshot.getValue()).values().contains(uID))) {  // Not pending, user added to pending list
                                                            mDatabase.child("events").child(eventKey).child("pending").push().setValue(uID);
                                                            Toast.makeText(mCtx, "Invitation requested.",
                                                                    Toast.LENGTH_SHORT).show();
                                                        } else {
                                                            Toast.makeText(mCtx, "Invitation already pending.",
                                                                    Toast.LENGTH_SHORT).show();
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {
                                                    }
                                                });
                                            }
                                        } else {
                                            Toast.makeText(mCtx, "Already attending this event.",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });
            }
        });

        return eventView;
    }

    @Override
    public void onBindViewHolder(EventsAdapter.MyViewHolder holder, int position) {
        Event event = eventsList.get(position);
        holder.name.setText(event.name);
        holder.date.setText(event.date);
        holder.time.setText(event.time);
        holder.description.setText(event.description);
        holder.priv.setText(String.valueOf(event.private_party));
        if (!event.private_party) {  // public party
            holder.icon.setImageResource(R.drawable.ic_add_black_24dp);
        } else {
            holder.icon.setImageResource(R.drawable.ic_email_black_24dp);
        }
    }


    @Override
    public int getItemCount() {
        return eventsList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, date, time, description, priv;
        public ImageView icon;

        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.event_list_name);
            date = (TextView) view.findViewById(R.id.event_list_date);
            time = (TextView) view.findViewById(R.id.event_list_time);
            description = (TextView) view.findViewById(R.id.event_list_description);
            priv = (TextView) view.findViewById(R.id.event_list_private);
            icon = (ImageView) view.findViewById(R.id.event_list_icon);
        }

    }
}
