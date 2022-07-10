package com.apnagroup.apnavpn.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;

import com.apnagroup.apnavpn.R;
import com.apnagroup.apnavpn.utils.AppData;

import java.util.Date;

public class SplashScreen extends AppCompatActivity implements AppData{

    SharedPreferences prefs;
    Date expiryDate;
    Date currentDate;
    Boolean dateLifetime = false;
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        prefs = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        expiryDate = new Date(prefs.getLong(DATE_END, 0));
        dateLifetime = prefs.getBoolean(DATE_LIFETIME, false);
        currentDate = new Date();
        if (!dateLifetime){
            // jika tidak lifetime
            // compare between two date
            if (expiryDate.before(currentDate)) {
                // expied
                prefs.edit().putBoolean(IS_VIP, false).apply();
//                Log.d(" expiry: ", "change VIP to false");
            }
        }
        handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent=new Intent(SplashScreen.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        },3000);
    }

}
