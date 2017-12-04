package edu.uw.cruan.dawgdebauchery;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by cherieruan on 12/3/17.
 */

public class ListAdapter extends ArrayAdapter<UserAccount> {

    public static final String TAG = "ListAdapter";
    List<UserAccount> data;


    public ListAdapter(Context context, List<UserAccount> data) {
        super(context, 0, data);
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.guest_item, null);
        }

        ImageView pic = (ImageView) v.findViewById(R.id.guest_pic);
        TextView name = (TextView) v.findViewById(R.id.guest_name);

        if (pic != null) {
            pic.setImageResource(data.get(position).imgURL);
        }

        if (name != null) {
            name.setText(data.get(position).fName + " " + data.get(position).lName);
        }


        return v;
    }

}