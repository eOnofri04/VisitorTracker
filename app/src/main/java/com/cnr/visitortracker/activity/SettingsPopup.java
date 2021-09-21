package com.cnr.visitortracker.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.TextView;

import com.cnr.visitortracker.BuildConfig;
import com.cnr.visitortracker.R;


public class SettingsPopup extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popupinfo);

        TextView version_number = (TextView) findViewById(R.id.setting_version);
        version_number.setText(getString(R.string.version) + BuildConfig.VERSION_NAME);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width*.8), (int)(height*.8));


    }
}
