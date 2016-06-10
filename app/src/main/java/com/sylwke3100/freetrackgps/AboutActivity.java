package com.sylwke3100.freetrackgps;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.widget.TextView;

public class AboutActivity extends Activity {
    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        try {
            PackageInfo manager = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            ((TextView) this.findViewById(R.id.textWorkoutStatusLabel))
                .setText(manager.versionName);
        } catch (NameNotFoundException e) {
        }
    }
}
