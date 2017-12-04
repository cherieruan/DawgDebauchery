package edu.uw.cruan.dawgdebauchery;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.app.Fragment;
import android.text.format.DateFormat;
import android.widget.DatePicker;
import android.widget.TextView;
import android.app.DialogFragment;
import android.app.Dialog;
import java.util.Calendar;


/**
 * A simple {@link Fragment} subclass.
 */
public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener{

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        //Use the current time as the default values for the time picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        //Create and return a new instance of TimePickerDialog
        return new DatePickerDialog(getActivity(),this, year, month, day);
    }

    //onTimeSet() callback method
    public void onDateSet(DatePicker view, int year, int month, int day){
        //Do something with the user chosen time
        //Get reference of host activity (XML Layout File) TextView widget
        TextView tv = (TextView) getActivity().findViewById(R.id.create_event_set_date_text_view);

        //Display the user changed time on TextView
        tv.setText(String.valueOf(month) + "/" + String.valueOf(day) + "/" + String.valueOf(year));
    }
}