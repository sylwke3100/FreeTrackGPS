package com.example.freetrackgps;

import android.app.Activity;
import android.os.Bundle;
import android.content.pm.PackageInfo;
import android.widget.TextView;
import android.content.pm.PackageManager.NameNotFoundException;

public class About extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        try {
            PackageInfo manager= this.getPackageManager().getPackageInfo(getPackageName(), 0);
            ((TextView) this.findViewById(R.id.textViewVersion)).setText(manager.versionName);
        } catch (NameNotFoundException e) {
        }
    }
}