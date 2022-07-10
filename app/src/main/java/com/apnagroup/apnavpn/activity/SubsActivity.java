package com.apnagroup.apnavpn.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.apnagroup.apnavpn.R;
import com.apnagroup.apnavpn.utils.AppData;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class SubsActivity extends AppCompatActivity implements AppData, BillingProcessor.IBillingHandler {

    LinearLayout subs1month;
    LinearLayout subs1year;
    LinearLayout subslifetime;

    BillingProcessor bp;
    private boolean readyToPurchase = false;
    private static final String LOG_TAG = "iabv3";
    SharedPreferences prefs;
    Date currentDate;
    Date expiryDate;
    Date expiryDateCheck;
    Calendar c;
    SimpleDateFormat sdf;

    Boolean dateLifetime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subs);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarold);
        toolbar.setTitle("Upgrade Plan");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        subs1month = (LinearLayout) findViewById(R.id.subs1month);
        subs1year = (LinearLayout) findViewById(R.id.subs1year);
        subslifetime = (LinearLayout) findViewById(R.id.subslifetime);

        prefs = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        expiryDateCheck = new Date(prefs.getLong(DATE_END, 0));
        dateLifetime = prefs.getBoolean(DATE_LIFETIME, false);

        sdf = new SimpleDateFormat("yyyy-MM-dd");
        c = Calendar.getInstance();

        // get current date
        currentDate = new Date();


        boolean isAvailable = BillingProcessor.isIabServiceAvailable(this);
        if (!isAvailable) {
            // continue
            Toast.makeText(this, "Sorry, Purchase is not ready on your device", Toast.LENGTH_LONG).show();
        }

        bp = new BillingProcessor(this, GOOGLE_BILLING_KEY, this);
        bp.initialize();

    }


    @Override
    public void onProductPurchased(@NonNull String productId, @Nullable TransactionDetails details) {

        if (productId.equals(SUBS_1_MONTH)) {
            prefs.edit().putBoolean(IAP_MONTHLY, true).apply();
            prefs.edit().putBoolean(IS_VIP, true).apply();
            prefs.edit().putBoolean(IS_SUBS, true).apply();

            //tambah point 30 day
            prefs.edit().putLong(DATE_PURCHASE, currentDate.getTime()).apply();

            // convert date to calendar
            c.setTime(currentDate);
            // manipulate date
            c.add(Calendar.MONTH, 1);
            // convert calendar to date
            expiryDate = c.getTime();

            prefs.edit().putLong(DATE_END, expiryDate.getTime()).apply();
        }

        if (productId.equals(SUBS_12_MONTH)) {
            prefs.edit().putBoolean(IAP_YEARLY, true).apply();
            prefs.edit().putBoolean(IS_VIP, true).apply();
            prefs.edit().putBoolean(IS_SUBS, true).apply();

            //tambah point 365 day
            prefs.edit().putLong(DATE_PURCHASE, currentDate.getTime()).apply();

            // convert date to calendar
            c.setTime(currentDate);
            // manipulate date
            c.add(Calendar.YEAR, 1);
            // convert calendar to date
            expiryDate = c.getTime();

            prefs.edit().putLong(DATE_END, expiryDate.getTime()).apply();
        }

        if (productId.equals(SUBS_LIFETIME)) {
            prefs.edit().putBoolean(IAP_LIFETIME, true).apply();
            prefs.edit().putBoolean(IS_VIP, true).apply();
            prefs.edit().putBoolean(DATE_LIFETIME, true).apply();
            prefs.edit().putBoolean(IS_SUBS, true).apply();

            prefs.edit().putLong(DATE_PURCHASE, currentDate.getTime()).apply();
        }
    }

    @Override
    public void onPurchaseHistoryRestored() {
        for (String sku : bp.listOwnedProducts())
            System.out.println("Owned Managed Product: " + sku);
        for(String sku : bp.listOwnedSubscriptions())
            System.out.println("Owned Subscription: " + sku);

    }

    @Override
    public void onBillingError(int errorCode, @Nullable Throwable error) {
        Toast.makeText(this, "Purchase error", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBillingInitialized() {
        readyToPurchase = true;
    }

    //    Handle on click purchase
    public void subs1month(View view) {

        if (dateLifetime) {
            // jika true
            Toast.makeText(this, "Your subscription is active forever", Toast.LENGTH_LONG).show();
        } else {
            // cek subscribe
            if (expiryDateCheck.before(currentDate)) {
                // expied
                // purchase
                boolean isSubsUpdateSupported = bp.isSubscriptionUpdateSupported();
                if(isSubsUpdateSupported) {
                    // launch payment flow
                    bp.subscribe(this, SUBS_1_MONTH);
                } else {
                    Toast.makeText(this, "This device not supported to purchase", Toast.LENGTH_LONG).show();
                }
            } else {
                // subscribe
                Toast.makeText(this, "Your subscription has not ended", Toast.LENGTH_LONG).show();
            }
        }

    }

    public void subs1year(View view) {

        if (dateLifetime) {
            // jika true
            Toast.makeText(this, "Your subscription is active forever", Toast.LENGTH_LONG).show();
        } else {
            // cek subscribe
            if (expiryDateCheck.before(currentDate)) {
                // expied
                // purchase
                boolean isSubsUpdateSupported = bp.isSubscriptionUpdateSupported();
                if(isSubsUpdateSupported) {
                    // launch payment flow
                    bp.subscribe(this, SUBS_12_MONTH);
                } else {
                    Toast.makeText(this, "This device not supported to purchase", Toast.LENGTH_LONG).show();
                }

            } else {
                // subscribe
                Toast.makeText(this, "Your subscription has not ended", Toast.LENGTH_LONG).show();
            }
        }

    }

    public void subslifetime(View view) {
        if (dateLifetime) {
            // jika true
            Toast.makeText(this, "Your subscription is active forever", Toast.LENGTH_LONG).show();
        } else {
            // cek subscribe
            if (expiryDateCheck.before(currentDate)) {
                // expied
                // purchase
                boolean isOneTimePurchaseSupported = bp.isOneTimePurchaseSupported();
                if (isOneTimePurchaseSupported) {
                    // launch payment flow
                    bp.purchase(this, SUBS_LIFETIME);

                } else {
                    Toast.makeText(this, "This device not supported to purchase", Toast.LENGTH_LONG).show();
                }
            } else {
                // subscribe
                Toast.makeText(this, "Your subscription has not ended", Toast.LENGTH_LONG).show();
            }
        }

    }

    @Override
    public void onDestroy() {
        if (bp != null)
            bp.release();
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!bp.handleActivityResult(requestCode, resultCode, data))
            super.onActivityResult(requestCode, resultCode, data);
    }

}
