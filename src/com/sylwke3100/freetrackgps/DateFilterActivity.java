package com.sylwke3100.freetrackgps;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;

import java.util.Calendar;


public class DateFilterActivity extends Activity {
    private Button okButton, cancelButton;
    private DatePicker localPickier;
    private SharedPreferences sharePrefs;
    @Override public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_filter);
        sharePrefs = getSharedPreferences("Pref", Activity.MODE_PRIVATE);
        okButton = (Button) findViewById(R.id.okButton);
        cancelButton = (Button) findViewById(R.id.cancelButton);
        localPickier = (DatePicker) findViewById(R.id.datePicker);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                saveFilter();
                finish();
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                cancelFilter();
                finish();
            }
        });
        setLocalPickier();
    }

    @Override public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void setLocalPickier(){
        long time = sharePrefs.getLong("filterOneTime", -1);
        Calendar calendar = Calendar.getInstance();
        if (time!= -1)
            calendar.setTimeInMillis(time);
        else
            calendar.setTimeInMillis(System.currentTimeMillis());
        localPickier.updateDate(calendar.get(calendar.YEAR), calendar.get(calendar.MONTH), calendar.get(calendar.DAY_OF_MONTH));
    }

    private void saveFilter(){
        Calendar localCalendar = Calendar.getInstance();
        localCalendar.set(localPickier.getYear(), localPickier.getMonth(), localPickier.getDayOfMonth());
        SharedPreferences.Editor preferencesEditor = sharePrefs.edit();
        preferencesEditor.putLong("filterOneTime", localCalendar.getTimeInMillis());
        preferencesEditor.commit();
    }

    private void cancelFilter(){
        SharedPreferences.Editor preferencesEditor = sharePrefs.edit();
        preferencesEditor.putLong("filterOneTime", -1);
        preferencesEditor.commit();
    }
}
