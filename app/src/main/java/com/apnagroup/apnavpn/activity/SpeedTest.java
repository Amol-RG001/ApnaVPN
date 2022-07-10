package com.apnagroup.apnavpn.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.apnagroup.apnavpn.R;

public class SpeedTest extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView tvSubTitle;
    private ViewPager viewPager;
    private ViewPagerAdapter adapter;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speed_test1);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarold);
        toolbar.setTitle("Speed Test");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        find_views_by_id ();
        init_variables ();
    }

    private void find_views_by_id () {
        toolbar = findViewById (R.id.toolbar);
        tvSubTitle = toolbar.findViewById (R.id.toolbar_subtitle);
        viewPager = findViewById (R.id.viewpager);
        bottomNavigationView = findViewById (R.id.bottom_navigation);
    }

    private void init_variables () {
        FirebaseAnalytics.getInstance (this);

        Intent intentBC = new Intent ();
        intentBC.setAction ("com.d3vdr0id.speedtest");
        sendBroadcast (intentBC);
        adapter = new ViewPagerAdapter (getSupportFragmentManager ());
        if (viewPager != null)
            viewPager.setAdapter (adapter);
        if (viewPager != null)
            viewPager.setCurrentItem (1);
        tvSubTitle.setText ("Speed Test");
        bottomNavigationView.setSelectedItemId (R.id.action_speed);
        bottomNavigationView.setOnNavigationItemSelectedListener (item -> {
            switch (item.getItemId ()) {
                case R.id.action_speed:
                    viewPager.setCurrentItem (1);
                    tvSubTitle.setText ("Speed Test");
                    break;
                default:
                    break;
            }
            return true;
        });
        if (viewPager != null)
            viewPager.addOnPageChangeListener (new ViewPager.OnPageChangeListener () {
                @Override
                public void onPageScrolled (int position, float positionOffset, int positionOffsetPixels) {
                }

                @Override
                public void onPageSelected (int position) {
                    switch (position) {
                        case 0:
                            bottomNavigationView.getMenu ().findItem (R.id.action_speed).setChecked (true);
                            tvSubTitle.setText ("Speed Test");
                            break;
                        default:
                            break;
                    }
                }

                @Override
                public void onPageScrollStateChanged (int state) {
                }
            });
    }

    @Override
    protected void onResume () {
        super.onResume ();
    }

    @Override
    protected void onPause () {
        super.onPause ();
    }

    @Override
    protected void onDestroy () {
        super.onDestroy ();
    }

    private class ViewPagerAdapter extends FragmentStatePagerAdapter {
        /**
         * The Page count.
         */
        final int PAGE_COUNT = 1;
        private final String[] mTabsTitle = {"Speed Test"};

        /**
         * Instantiates a new View pager adapter.
         *
         * @param fm the fm
         */
        ViewPagerAdapter (FragmentManager fm) {
            super (fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }

        /**
         * Gets tab view.
         *
         * @param position the position
         * @return the tab view
         */
        public View getTabView (int position) {
            View view = LayoutInflater.from (getApplicationContext ()).inflate (R.layout.custom_tabspeed, null);
            TextView title = view.findViewById (R.id.title);
            title.setText (mTabsTitle[ position ]);
            return view;
        }

        @NonNull
        @Override
        public Fragment getItem (int pos) {
            switch (pos) {
                case 0:
                    return new SpeedTestFragment ();
                default:
                    break;
            }
            return null;
        }

        @Override
        public void destroyItem (ViewGroup viewPager, int position, @NonNull Object object) {
        }

        @Override
        public int getCount () {
            return PAGE_COUNT;
        }

        @Override
        public CharSequence getPageTitle (int position) {
            return null;
        }
    }
}
