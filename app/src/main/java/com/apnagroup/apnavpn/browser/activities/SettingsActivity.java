package com.apnagroup.apnavpn.browser.activities;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.apnagroup.apnavpn.R;
import com.apnagroup.apnavpn.browser.fragments.SettingsFragment;
import com.apnagroup.apnavpn.browser.utils.ThemeUtils;
import com.apnagroup.apnavpn.browser.view.CenteredToolbar;

public class SettingsActivity extends AppCompatActivity {

    private CenteredToolbar mToolbar;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(ThemeUtils.getCurrentTheme());
        super.onCreate(savedInstanceState);
        if (getResources().getBoolean(R.bool.portrait_only)){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        setContentView(R.layout.activity_settingsbrowser);
        mToolbar = findViewById(R.id.toolbar);
        mToolbar.setTitle(R.string.settings);
        mToolbar.setNavigationIcon(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("night", false)
                ? R.drawable.ic_arrow_back_white_24dp : R.drawable.ic_arrow_back_black_24dp);
        mToolbar.setNavigationOnClickListener(v -> {
            finish();
        });
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, SettingsFragment.newInstance())
                .commit();
    }
    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
