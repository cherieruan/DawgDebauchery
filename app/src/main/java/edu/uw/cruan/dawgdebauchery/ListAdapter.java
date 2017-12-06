package edu.uw.cruan.dawgdebauchery;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

import java.util.List;

/**
 * Created by cherieruan on 12/3/17.
 */

public class ListAdapter extends ArrayAdapter<UserAccount> {

    public static final String TAG = "ListAdapter";
    private List<UserAccount> data;


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

        NetworkImageView pic = (NetworkImageView) v.findViewById(R.id.guest_pic);
        TextView name = (TextView) v.findViewById(R.id.guest_name);

        if (pic != null) {
            Log.v(TAG, ""+data.get(position).imgURL);
            pic.setImageUrl(Uri.parse(data.get(position).imgURL).toString(), RequestSingleton.getInstance(this.getContext().getApplicationContext()).imageLoader);
        }

        if (name != null) {
            name.setText(data.get(position).fName + " " + data.get(position).lName);
        }

        return v;
    }

}