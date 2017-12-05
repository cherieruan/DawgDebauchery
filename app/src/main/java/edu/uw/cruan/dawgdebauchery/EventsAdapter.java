package edu.uw.cruan.dawgdebauchery;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by cherieruan on 12/4/17.
 */

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.MyViewHolder> {

    private List<Event> eventsList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, date, time;

        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.event_list_name);
            date = (TextView) view.findViewById(R.id.event_list_date);
            time = (TextView) view.findViewById(R.id.event_list_time);
        }
    }

    public EventsAdapter(List<Event> eventsList) {
        this.eventsList = eventsList;
    }


    @Override
    public EventsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.event_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(EventsAdapter.MyViewHolder holder, int position) {
        Event event = eventsList.get(position);
        holder.name.setText(event.name);
        holder.date.setText(event.date);
        holder.time.setText(event.time);
    }

    @Override
    public int getItemCount() {
        return eventsList.size();
    }
}
