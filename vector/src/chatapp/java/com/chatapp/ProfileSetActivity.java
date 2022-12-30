package com.chatapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;

import android.os.Bundle;
import android.os.Handler;

import im.vector.R;
import im.vector.util.PreferencesManager;

public class ProfileSetActivity extends AppCompatActivity implements ISyncListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_set);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Profile");
        getSupportActionBar().setTitle("Profile");
        getSupportFragmentManager().beginTransaction().replace(R.id.container, new ProfileUpdateFragment()).commit();
        if (!PreferenceManager.getDefaultSharedPreferences(this).getBoolean(PreferencesManager.IS_SYNC_DIALOG_SHOWN, false))
            showSyncDialog();
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        super.onBackPressed();
    }
    private void showSyncDialog() {
        ISyncListener listener = this;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                SyncFragment f = new SyncFragment();
                f.isyncListener = listener;
                f.show(getSupportFragmentManager());
            }
        }, 200);

    }

    @Override
    public void onSyncEnableClick() {

    }
}
