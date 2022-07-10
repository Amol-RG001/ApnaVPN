package com.apnagroup.apnavpn.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.widget.LinearLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.apnagroup.apnavpn.Fragments.FragmentFree;
import com.apnagroup.apnavpn.Fragments.FragmentVip;
import com.apnagroup.apnavpn.adapter.TabAdapter;
import com.apnagroup.apnavpn.R;
import com.apnagroup.apnavpn.utils.AppData;

import butterknife.ButterKnife;

public class Servers extends AppCompatActivity implements AppData {

    private TabAdapter adapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;


    SharedPreferences prefs;
    Boolean isVip = false;

    LinearLayout ad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_servers);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarold);
        toolbar.setTitle("Servers");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);



        viewPager = (ViewPager) findViewById(R.id.viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        adapter = new TabAdapter(getSupportFragmentManager());
        adapter.addFragment(new FragmentVip(), "Vip Server");
        adapter.addFragment(new FragmentFree(), "Free Server");
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        prefs = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        isVip = prefs.getBoolean(IS_VIP, false);

        ad = (LinearLayout) findViewById(R.id.ads);
        if (isVip) {
            ad.setVisibility(LinearLayout.GONE);
        } else {
            if (!isVip) {
                    AdView mAdView = (AdView) findViewById(R.id.adView);
                    AdRequest adRequest1 = new AdRequest.Builder().build();
                    mAdView.loadAd(adRequest1);

            }
        }

    }
}
