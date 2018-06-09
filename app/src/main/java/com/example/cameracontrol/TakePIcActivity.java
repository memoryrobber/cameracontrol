package com.example.cameracontrol;

import android.app.Activity;
import android.os.Bundle;


/**
 * Activity displaying a fragment that implements RAW photo captures.
 */
public class TakePIcActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        if (null == savedInstanceState) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.container, Camera2RawFragment.newInstance())
                    .commit();
        }
    }
}