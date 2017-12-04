package edu.uw.cruan.dawgdebauchery;

import android.app.TimePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Fragment;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.widget.TextView;
import android.app.DialogFragment;
import android.app.Dialog;
import java.util.Calendar;
import android.widget.TimePicker;


/**
 * A simple {@link Fragment} subclass.
 */
public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener{

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        //Use the current time as the default values for the time picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        //Create and return a new instance of TimePickerDialog
        TimePickerDialog tpd = new TimePickerDialog(getActivity(),this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));

        /*.........Set a custom title for picker........*/
//        TextView tvTitle = new TextView(getActivity());
//        tvTitle.setText("Pick A Party Time");
//        tvTitle.setBackgroundColor(Color.parseColor("#FFFFFF"));
//        tvTitle.setPadding(5, 3, 5, 3);
//        tvTitle.setGravity(Gravity.CENTER_HORIZONTAL);
//        tpd.setCustomTitle(tvTitle);

        return tpd;
    }

    //onTimeSet() callback method
    public void onTimeSet(TimePicker view, int hourOfDay, int minute){
        //Do something with the user chosen time
        //Get reference of host activity (XML Layout File) TextView widget
        TextView tv = (TextView) getActivity().findViewById(R.id.create_event_set_time_text_view);

        //Get the AM or PM for current time
        String aMpM = "AM";
        if(hourOfDay >11)
        {
            aMpM = "PM";
        }

        //Make the 24 hour time format to 12 hour time format
        int currentHour;
        if(hourOfDay>11)
        {
            currentHour = hourOfDay - 12;
        }
        else
        {
            currentHour = hourOfDay;
        }

        String currentMinute = "";
        if (minute < 10) {
            currentMinute = "0"+String.valueOf(minute);
        } else {
            currentMinute = String.valueOf(minute);
        }

        //Display the user changed time on TextView
        tv.setText(String.valueOf(currentHour) + ":" + currentMinute + " " + aMpM);
    }
}