package com.apnagroup.apnavpn.browser.utils;

import androidx.preference.PreferenceManager;

import com.apnagroup.apnavpn.MainApplication;
import com.apnagroup.apnavpn.R;

public class ThemeUtils {

    public static int getCurrentTheme() {
        if (PreferenceManager.getDefaultSharedPreferences(MainApplication.getInstance().getApplicationContext()).getBoolean("night", false)) {
            return R.style.AppThemeDark;
        } else {
            return R.style.AppTheme;
        }
    }

    public static int getCurrentBottomTheme() {
        if (PreferenceManager.getDefaultSharedPreferences(MainApplication.getInstance().getApplicationContext()).getBoolean("night", false)) {
            return R.style.BottomSheetDark;
        } else {
            return R.style.BottomSheetLight;
        }
    }
}