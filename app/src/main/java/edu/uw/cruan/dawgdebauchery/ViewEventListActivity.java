package edu.uw.cruan.dawgdebauchery;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ViewEventListActivity extends AppCompatActivity {

    public static final String TAG = "ViewEventList";
    private Map<String, Map<String, Object>> eventsMap;
    private List<Event> eventsList = new ArrayList<Event>();
    private RecyclerView recyclerView;
    private EventsAdapter mAdapter;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_event_list);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        mAdapter = new EventsAdapter(this, eventsList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        prepareData();

        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(
                recyclerView.getContext(),
                DividerItemDecoration.VERTICAL
        );
        recyclerView.addItemDecoration(mDividerItemDecoration);
    }

    protected void prepareData() {
        mDatabase.child("events").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                eventsMap = (Map<String, Map<String, Object>>) dataSnapshot.getValue();
                Log.v(TAG, eventsMap.toString());
                toEventList(eventsMap, eventsList, mAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    protected static void toEventList(Map<String, Map<String, Object>> eventsMap,
                               List<Event> eventsList,
                               EventsAdapter adapter) {
        for (String eventKey : eventsMap.keySet()) {
            Map<String, Object> event = eventsMap.get(eventKey);
            if (event != null) {
                Event e = new Event((String) event.get("name"), (String) event.get("address"), (String) event.get("date"),
                        (String) event.get("time"), (String) event.get("description"),
                        eventKey,
                        (Boolean) event.get("private_party"));
                eventsList.add(e);
                adapter.notifyDataSetChanged();
            }
        }
    }



}
