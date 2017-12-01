package edu.uw.cruan.dawgdebauchery;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;

import java.util.Calendar;

public class CreateEventTimeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event_time);

        final DatePicker create_event_date = (DatePicker) findViewById(R.id.create_event_date);
        final TimePicker create_event_time = (TimePicker) findViewById(R.id.create_event_time);

        Button create_event_next = (Button) findViewById(R.id.create_event_next);
        create_event_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int year = create_event_date.getYear();
                int month = create_event_date.getMonth();
                int date = create_event_date.getDayOfMonth();
                Calendar formattedDate = Calendar.getInstance();
                formattedDate.set(year, month, date);

                String formattedTime = "";
                int hour = create_event_time.getCurrentHour();
                String sHour = "00";
                if(hour < 10){
                    sHour = "0"+hour;
                } else {
                    sHour = String.valueOf(hour);
                }

                int minute = create_event_time.getCurrentMinute();
                String sMinute = "00";
                if(minute < 10){
                    sMinute = "0"+minute;
                } else {
                    sMinute = String.valueOf(minute);
                }

                formattedTime = sHour+":"+sMinute;
            }
        });
    }
}
