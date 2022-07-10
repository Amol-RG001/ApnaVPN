package com.apnagroup.apnavpn.activity;

import android.app.Activity;
import android.app.AppOpsManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.anchorfree.hydrasdk.HydraSdk;
import com.anchorfree.hydrasdk.api.response.RemainingTraffic;
import com.anchorfree.hydrasdk.callbacks.Callback;
import com.anchorfree.hydrasdk.exceptions.HydraException;
import com.anchorfree.hydrasdk.vpnservice.VPNState;
import com.bumptech.glide.Glide;
import com.apnagroup.apnavpn.utils.AppData;
import com.apnagroup.apnavpn.utils.Converter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.apnagroup.apnavpn.R;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.apnagroup.apnavpn.activity.MainActivity.selectedCountry;
import static jonathanfinerty.once.Once.beenDone;

public abstract class UIActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, AppData {

    protected static final String TAG = MainActivity.class.getSimpleName();

    private InterstitialAd mInterstitialAd;
    private int adCount = 0;
    VPNState state;
    int progressBarValue = 0;
    Handler handler = new Handler();
    private Handler customHandler = new Handler();
    private long startTime = 0L;
    long timeInMilliseconds = 0L;
    long timeSwapBuff = 0L;
    long updatedTime = 0L;
    public static Menu menuItem;

    @BindView(R.id.toolbar)
    protected Toolbar toolbar;

//    @BindView(R.id.tv_connect_message)
//    TextView connectedMessage;

    @BindView(R.id.tv_timer)
    TextView timerTextView;

    @BindView(R.id.layout_speed)
    LinearLayout layoutSpeed;

    @BindView(R.id.connect_btn)
    TextView connectBtnTextView;

    @BindView(R.id.image_gif)
    ImageView imgGif;

    @BindView(R.id.connection_state)
    TextView connectionStateTextView;

    @BindView(R.id.speed_up)
    RelativeLayout speedup;
    @BindView(R.id.speed_up1)
    RelativeLayout speed_up1;


//    @BindView(R.id.connection_progress)
//    ProgressBar connectionProgressBar;

    @BindView(R.id.serverUp)
    TextView serverUp;

    @BindView(R.id.serverDown)
    TextView serverDown;

    SharedPreferences prefs;
    Boolean isVip = false;

    LinearLayout ad;
    Date expiryDate;
    Date currentDate;
    Boolean dateLifetime = false;
    Boolean isSubs;
    private RewardedAd mRewardedAd;
    private com.facebook.ads.InterstitialAd interstitialAd,interstitialAd1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {}
        });
        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(this,getString(R.string.Admob_intertesial_id), adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                // The mInterstitialAd reference will be null until
                // an ad is loaded.
                mInterstitialAd = interstitialAd;
                mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback(){
                    @Override
                    public void onAdDismissedFullScreenContent() {
                        // Called when fullscreen content is dismissed.
                        Log.d("TAG", "The ad was dismissed.");
                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                        // Called when fullscreen content failed to show.
                        Log.d("TAG", "The ad failed to show.");
                    }

                    @Override
                    public void onAdShowedFullScreenContent() {
                        // Called when fullscreen content is shown.
                        // Make sure to set your reference to null so you don't
                        // show it a second time.
                        mInterstitialAd = null;
                        Log.d("TAG", "The ad was shown.");
                    }
                });
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                // Handle the error
                mInterstitialAd = null;
            }
        });


        AdRequest adRequest2 = new AdRequest.Builder().build();
        RewardedAd.load(this, getString(R.string.rewardvideo),
                adRequest2, new RewardedAdLoadCallback(){
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error.
                        Log.d("TAG", loadAdError.getMessage());
                        mRewardedAd = null;
                    }

                    @Override
                    public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                        mRewardedAd = rewardedAd;
                        Log.d("TAG", "Ad was loaded.");
                        mRewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                            @Override
                            public void onAdShowedFullScreenContent() {
                                // Called when ad is shown.
                                Log.d("TAG", "Ad was shown.");
                                mRewardedAd = null;
                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(AdError adError) {
                                // Called when ad fails to show.
                                Log.d("TAG", "Ad failed to show.");
                            }

                            @Override
                            public void onAdDismissedFullScreenContent() {
                                // Called when ad is dismissed.
                                // Don't forget to set the ad reference to null so you
                                // don't show the ad a second time.
                                Log.d("TAG", "Ad was dismissed.");
                            }
                        });
                    }
                });

        prefs = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        isVip = prefs.getBoolean(IS_VIP, false);
        Log.d("is VIP: ", isVip.toString());

        expiryDate = new Date(prefs.getLong(DATE_END, 0));
        dateLifetime = prefs.getBoolean(DATE_LIFETIME, false);
        isSubs = prefs.getBoolean(IS_SUBS, false);
        // get date now
        currentDate = new Date();

        ad = (LinearLayout) findViewById(R.id.ads);
        RelativeLayout unlockvideo=findViewById(R.id.unlockvideo);
        LinearLayout unlockvideo1=findViewById(R.id.unlockvideo1);
        SharedPreferences sharedpreferences = getSharedPreferences("MyPREFERENCES", Context.MODE_PRIVATE);
        if (sharedpreferences.getLong("ExpiredDate", -1) > System.currentTimeMillis()) {
            unlockvideo.setVisibility(View.GONE);
        } else {
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.clear();
            editor.apply();
            isVip = false;
            prefs.edit().putBoolean(IS_VIP, false).apply();

        }


        unlockvideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AdRequest adRequest = new AdRequest.Builder().build();
                RewardedAd.load(UIActivity.this, "ca-app-pub-3940256099942544/5224354917",
                        adRequest, new RewardedAdLoadCallback() {
                            @Override
                            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                                // Handle the error.
                                Log.d(TAG, loadAdError.getMessage());
                                mRewardedAd = null;
                            }

                            @Override
                            public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                                mRewardedAd = rewardedAd;
                                Log.d(TAG, "Ad was loaded.");
                            }
                        });
                mRewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdShowedFullScreenContent() {
                        // Called when ad is shown.
                        Log.d(TAG, "Ad was shown.");
                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                        // Called when ad fails to show.
                        Log.d(TAG, "Ad failed to show.");
                    }

                    @Override
                    public void onAdDismissedFullScreenContent() {
                        // Called when ad is dismissed.
                        // Set the ad reference to null so you don't show the ad a second time.
                        Log.d(TAG, "Ad was dismissed.");
                        mRewardedAd = null;
                    }
                });
                    if (mRewardedAd != null) {
                        Activity activityContext = UIActivity.this;
                        mRewardedAd.show(activityContext, new OnUserEarnedRewardListener() {
                            @Override
                            public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                                SharedPreferences sharedpreferences = getSharedPreferences("MyPREFERENCES", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedpreferences.edit();
                                editor.putLong("ExpiredDate", System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(720));
                                editor.apply();
                                prefs.edit().putBoolean(IAP_MONTHLY, true).apply();
                                prefs.edit().putBoolean(IS_VIP, true).apply();
                                prefs.edit().putBoolean(IS_SUBS, true).apply();
                                startActivity(new Intent(UIActivity.this,MainActivity.class));

                                finish();
                            }
                        });
                    } else {
                        Toast.makeText(UIActivity.this, "loading videos", Toast.LENGTH_SHORT).show();
                    }

            }
        });
        if (isVip) {
            ad.setVisibility(LinearLayout.GONE);
            unlockvideo.setVisibility(View.GONE);
        } else {
            if (!isVip) {
                    AdView mAdView = (AdView) findViewById(R.id.adView);
                    AdRequest adRequest1 = new AdRequest.Builder().build();
                    mAdView.loadAd(adRequest1);

            }

        }
        unlockvideo1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    if (mRewardedAd != null) {
                        Activity activityContext = UIActivity.this;
                        mRewardedAd.show(activityContext, new OnUserEarnedRewardListener() {
                            @Override
                            public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                                SharedPreferences sharedpreferences = getSharedPreferences("MyPREFERENCES", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedpreferences.edit();
                                editor.putLong("ExpiredDate", System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(720));
                                editor.apply();
                                prefs.edit().putBoolean(IAP_MONTHLY, true).apply();
                                prefs.edit().putBoolean(IS_VIP, true).apply();
                                prefs.edit().putBoolean(IS_SUBS, true).apply();
                                startActivity(new Intent(UIActivity.this,MainActivity.class));

                                finish();
                            }
                        });
                    } else {
                        Toast.makeText(UIActivity.this, "loading videos", Toast.LENGTH_SHORT).show();
                    }
            }
        });

        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        speedup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isVip) {
                        if (mInterstitialAd != null) {
                            mInterstitialAd.show(UIActivity.this);
                        }
                }
                startActivity(new Intent(UIActivity.this, com.apnagroup.apnavpn.browser.activities.MainActivity.class));
            }
        });

        speed_up1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isVip) {
                        if (mInterstitialAd != null) {
                            mInterstitialAd.show(UIActivity.this);
                        }
                }
                startActivity(new Intent(UIActivity.this, Servers.class));
            }
        });

    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.main, menu);
        // menu.getItem(0).setIcon(getResources().getIdentifier("drawable/"+"abc",null,getPackageName()));
        menuItem = menu;
        if (selectedCountry != null)
            if (!selectedCountry.equalsIgnoreCase(""))
                //menuItem.findItem(R.id.action_glob).setIcon(R.drawable.de);
                menuItem.findItem(R.id.action_glob).setIcon(this.getResources().getIdentifier(selectedCountry.toLowerCase(), "drawable", this.getPackageName()));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_glob) {
            startActivity(new Intent(this, Servers.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public final boolean isUsageAccessAllowed() {
        boolean granted = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            try {
                AppOpsManager appOps = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
                int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), getPackageName());
                if (mode == AppOpsManager.MODE_DEFAULT) {
                    String permissionUsage = "android.permission.PACKAGE_USAGE_STATS";
                    granted = (checkCallingOrSelfPermission(permissionUsage) == PackageManager.PERMISSION_GRANTED);
                } else {
                    granted = (mode == AppOpsManager.MODE_ALLOWED);
                }
            } catch (Throwable e) {
            }
        } else {
            return true;
        }
        return granted;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_upgrade) {
            if (!isVip) {
                    if (mInterstitialAd != null) {
                        mInterstitialAd.show(UIActivity.this);
                    }
            }
            startActivity(new Intent(this, Servers.class));
        } else if (id == R.id.browser) {
            if (!isVip) {
                    if (mInterstitialAd != null) {
                        mInterstitialAd.show(UIActivity.this);
                    }

            }
            startActivity(new Intent(this, com.apnagroup.apnavpn.browser.activities.MainActivity.class));
        } else if (id == R.id.bootact) {
            if (!isVip) {
                    if (mInterstitialAd != null) {
                        mInterstitialAd.show(UIActivity.this);
                    }
            }
            if (!isUsageAccessAllowed()) {
                startActivity(new Intent(android.provider.Settings.ACTION_USAGE_ACCESS_SETTINGS));
                Toast.makeText(this, "Give Usage access to boost ", Toast.LENGTH_SHORT).show();
            }else{
                startActivity(new Intent(this, BoostActivity.class));
            }
        } else if (id == R.id.speedtest) {
            if (!isVip) {
                    if (mInterstitialAd != null) {
                        mInterstitialAd.show(UIActivity.this);
                    }

            }
            startActivity(new Intent(this, SpeedTest.class));
        } else if (id == R.id.nav_premium) {
            if (!isVip) {
                    if (mInterstitialAd != null) {
                        mInterstitialAd.show(UIActivity.this);
                    }
            }
            startActivity(new Intent(this, SubsActivity.class));
        } else if (id == R.id.nav_helpus) {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(getString(R.string.privacy_policy)));
            startActivity(i);
        } else if (id == R.id.nav_rate) {
            rateUs();
        } else if (id == R.id.nav_share) {
            try {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "share app");
                shareIntent.putExtra(Intent.EXTRA_TEXT, "Bee VPN is world best vpn app its Super Faster Free VPN Unlimited Proxy Server lifetime free access https://play.google.com/store/apps/details?id="+getPackageName());
                startActivity(Intent.createChooser(shareIntent, "choose one"));
            } catch (Exception e) {
                //e.toString();
            }
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private Handler mUIHandler = new Handler(Looper.getMainLooper());
    final Runnable mUIUpdateRunnable = new Runnable() {
        @Override
        public void run() {
            updateUI();
            checkRemainingTraffic();
            mUIHandler.postDelayed(mUIUpdateRunnable, 10000);
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        isConnected(new Callback<Boolean>() {
            @Override
            public void success(@NonNull Boolean aBoolean) {
                if (aBoolean) {
                    startUIUpdateTask();
                }
            }

            @Override
            public void failure(@NonNull HydraException e) {

            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopUIUpdateTask();
    }

    protected abstract void loginToVpn();


    @OnClick(R.id.image_gif)
    public void onConnectBtnClick(View v) {
        isConnected(new Callback<Boolean>() {
            @Override
            public void success(@NonNull Boolean aBoolean) {
                if (aBoolean) {
                    disconnectAlert();
                    //disconnectFromVnp();
                } else {
                    updateUI();
                    connectToVpn();
                }
            }

            @Override
            public void failure(@NonNull HydraException e) {
                Toast.makeText(getApplicationContext(), "" + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    protected abstract void isConnected(Callback<Boolean> callback);

    protected abstract void connectToVpn();

    protected abstract void disconnectFromVnp();

    protected abstract void chooseServer();

    protected abstract void getCurrentServer(Callback<String> callback);

    protected void startUIUpdateTask() {
        stopUIUpdateTask();
        mUIHandler.post(mUIUpdateRunnable);
    }

    protected void stopUIUpdateTask() {
        mUIHandler.removeCallbacks(mUIUpdateRunnable);
        updateUI();
    }

    protected abstract void checkRemainingTraffic();

    protected void updateUI() {
        HydraSdk.getVpnState(new Callback<VPNState>() {
            @Override
            public void success(@NonNull VPNState vpnState) {
                state = vpnState;
                switch (vpnState) {
                    case IDLE: {
                        loadIcon();
//                        connectBtnTextView.setEnabled(true);
                        connectBtnTextView.setText(R.string.txt_disconnected);
                        connectionStateTextView.setText(R.string.disconnected);
                        timerTextView.setVisibility(View.GONE);
//                        layoutSpeed.setVisibility(View.GONE);
                        layoutSpeed.setVisibility(View.VISIBLE);
                        hideConnectProgress();
                        break;
                    }
                    case CONNECTED: {
                        loadIcon();
//                        connectBtnTextView.setEnabled(true);
                        connectBtnTextView.setText(R.string.txt_connected);
                        connectionStateTextView.setText(R.string.connected);
                        timer();
                        timerTextView.setVisibility(View.VISIBLE);
                        layoutSpeed.setVisibility(View.VISIBLE);
                        hideConnectProgress();
                        loadAd();
                        break;
                    }
                    case CONNECTING_VPN:
                    case CONNECTING_CREDENTIALS:
                    case CONNECTING_PERMISSIONS: {
                        loadIcon();
//                        connectionStateTextView.setText(R.string.connecting);
//                        connectionStateTextView.setVisibility(View.INVISIBLE);
                        connectBtnTextView.setVisibility(View.INVISIBLE);
//                        connectBtnTextView.setEnabled(false);
                        timerTextView.setVisibility(View.GONE);
//                        layoutSpeed.setVisibility(View.GONE);
                        layoutSpeed.setVisibility(View.VISIBLE);
                        showConnectProgress();
                        break;
                    }
                    case PAUSED: {
//                        connectBtnTextView.setBackgroundResource(R.drawable.icon_connect);
//                        connectionStateTextView.setText(R.string.paused);
                        break;
                    }
                }
            }

            @Override
            public void failure(@NonNull HydraException e) {

            }
        });

        getCurrentServer(new Callback<String>() {
            @Override
            public void success(@NonNull final String currentServer) {


                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                    }
                });
            }

            @Override
            public void failure(@NonNull HydraException e) {
               /* currentServerBtn.setText(R.string.optimal_server);
                selectedServerTextView.setText("UNKNOWN");*/
            }
        });
    }

    protected void updateTrafficStats(long outBytes, long inBytes) {
        String outString = Converter.humanReadableByteCountOld(outBytes, false);
        String inString = Converter.humanReadableByteCountOld(inBytes, false);

//        serverSpeed.setText(getResources().getString(R.string.traffic_stats, outString, inString));
        serverUp.setText(getResources().getString(R.string.traffic_stats_up, outString));
        serverDown.setText(getResources().getString(R.string.traffic_stats_down, inString));
    }

    protected void updateRemainingTraffic(RemainingTraffic remainingTrafficResponse) {
        if (remainingTrafficResponse.isUnlimited()) {
            //trafficLimitTextView.setText("UNLIMITED available");
        } else {
            String trafficUsed = Converter.megabyteCount(remainingTrafficResponse.getTrafficUsed()) + "Mb";
            String trafficLimit = Converter.megabyteCount(remainingTrafficResponse.getTrafficLimit()) + "Mb";

            //trafficLimitTextView.setText(getResources().getString(R.string.traffic_limit, trafficUsed, trafficLimit));
        }
    }

    protected void showConnectProgress() {
        // connectionProgressBar.setProgress(10);
//        connectionProgressBar.setVisibility(View.VISIBLE);
        //connectionStateTextView.setVisibility(View.GONE);
        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub

                while (state == VPNState.CONNECTING_VPN || state == VPNState.CONNECTING_CREDENTIALS) {
                    progressBarValue++;

                    handler.post(new Runnable() {

                        @Override
                        public void run() {

//                            connectionProgressBar.setProgress(progressBarValue);
                            //  ShowText.setText(progressBarValue +"/"+Progressbar.getMax());

                        }
                    });
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    protected void hideConnectProgress() {
//        connectionProgressBar.setVisibility(View.GONE);
        connectionStateTextView.setVisibility(View.VISIBLE);
    }

    protected void showMessage(String msg) {
        Toast.makeText(UIActivity.this, msg, Toast.LENGTH_SHORT).show();
    }

    protected void rateUs() {
        Uri uri = Uri.parse("market://details?id=" + this.getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        // To count with Play market backstack, After pressing back button,
        // to taken back to our application, we need to add following flag to intent.
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=" + this.getPackageName())));
        }
    }

    protected void timer() {
        if (adCount == 0) {
            startTime = SystemClock.uptimeMillis();
            customHandler.postDelayed(updateTimerThread, 0);
        }
    }

    private Runnable updateTimerThread = new Runnable() {

        public void run() {

            timeInMilliseconds = SystemClock.uptimeMillis() - startTime;

            updatedTime = timeSwapBuff + timeInMilliseconds;

            int secs = (int) (updatedTime / 1000);
            int mins = secs / 60;
            int hrs = mins / 60;
            secs = secs % 60;
            int milliseconds = (int) (updatedTime % 1000);
            timerTextView.setText(String.format("%02d", hrs) + ":"
                    + String.format("%02d", mins) + ":"
                    + String.format("%02d", secs));
            customHandler.postDelayed(this, 0);
        }

    };

    protected void loadAd() {

        if (adCount == 0) {
            if (!isVip) {
                if (mInterstitialAd != null) {
                    mInterstitialAd.show(UIActivity.this);
                }
            }

        }
        adCount++;
    }

    protected void loadIcon() {
        if (state == VPNState.IDLE) {
//            Glide.with(this).load(R.drawable.icon_connect).into(connectBtnTextView);
            Glide.with(this).load(R.drawable.start).into(imgGif);
//            connectedMessage.setVisibility(View.INVISIBLE);
        } else if (state == VPNState.CONNECTING_VPN || state == VPNState.CONNECTING_CREDENTIALS) {
            imgGif.setVisibility(View.VISIBLE);
            Glide.with(this).asGif().load(R.drawable.rabbit).into(imgGif);
            connectBtnTextView.setVisibility(View.INVISIBLE);
//            connectedMessage.setVisibility(View.INVISIBLE);
        } else if (state == VPNState.CONNECTED) {
//            Glide.with(this).load(R.drawable.icon_disconnect).into(connectBtnTextView);
            Glide.with(this).load(R.drawable.stop).into(imgGif);
            connectBtnTextView.setVisibility(View.VISIBLE);
//            connectedMessage.setVisibility(View.VISIBLE);

        }
    }

    protected void disconnectAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Do you want to disconnet?");
        builder.setPositiveButton("Disconnect",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        adCount = 0;
                        disconnectFromVnp();
                    }
                });
        builder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        builder.show();
    }


}
