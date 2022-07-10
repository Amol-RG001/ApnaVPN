package com.apnagroup.apnavpn.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.apnagroup.apnavpn.R;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BoostActivity extends AppCompatActivity {

    private TextView mTvTotalBoost;
    private TextView mTvType;
    private TextView mTvSuggesterBoost;
    private TextView mTvBoosterPercent;
    private FrameLayout mFrameLayout;
    private RecyclerView mRecyclerViewBoost;
    private LinearLayout mViewEmpty;
    private Button mBtnBoost;
    private Button mBtnDone;
    private BoostAdapter mAdapter;
    private List<TaskInfo> mTaskInfos = new ArrayList<>();
    private long mTotalSelect;
    private long mTotalMemory;
    private long mAvailMem;
    private Handler mTimerHandler = null;
    private boolean mIsFragmentPause;
    ProgressBar mProgressBarLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boost);
        mTvTotalBoost = (TextView) findViewById(R.id.tvTotalBoost);
        mTvType = (TextView) findViewById(R.id.tvType);
        mTvSuggesterBoost = (TextView) findViewById(R.id.tvSuggesterBoost);
        mFrameLayout = (FrameLayout) findViewById(R.id.recyclerViewBoost);
        mRecyclerViewBoost = new RecyclerView(BoostActivity.this);
        mFrameLayout.addView(mRecyclerViewBoost);
        mProgressBarLoading = (ProgressBar) findViewById(R.id.progressBarLoading);
        mTvBoosterPercent = (TextView) findViewById(R.id.tvBoosterPercent);
        mBtnBoost = (Button) findViewById(R.id.btnBoost);
        mViewEmpty = (LinearLayout) findViewById(R.id.viewEmpty);
        mBtnBoost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PreferenceUtil.saveLongTimeBoost(BoostActivity.this, System.currentTimeMillis());
                Toast.makeText(BoostActivity.this, "Boosting in progress", Toast.LENGTH_SHORT).show();
                new TaskBoost(BoostActivity.this, mTaskInfos,
                        new TaskBoost.OnTaskBoostListener() {
                            @Override
                            public void OnResult() {
                                Toast.makeText(BoostActivity.this, "Boosting Done", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(BoostActivity.this,BoostResult.class));
                            }
                        }).execute();
            }
        });
        mBtnDone = (Button) findViewById(R.id.btnDone);
        mBtnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mTotalMemory = getTotalMemory();// get total device memory
        updateMemoryStatus();// update device memory status
        mTimerHandler = new Handler();// initialize timer handler

        //set show/hide view
        mBtnDone.setVisibility(View.GONE);
        mBtnBoost.setVisibility(View.GONE);

        mAdapter = new BoostAdapter(BoostActivity.this, mTaskInfos, new BoostAdapter.OnHandleItemBoostClickListener() {
            @Override
            public void onSelectedItem(int position) {
                boolean ischeck = mTaskInfos.get(position).isChceked();
                if (ischeck) {
                    mTotalSelect = mTotalSelect - mTaskInfos.get(position).getMem();
                } else {
                    mTotalSelect = mTotalSelect + mTaskInfos.get(position).getMem();
                }
                Utils.setTextFromSize(mTotalSelect, mTvTotalBoost, mTvType);
                mTaskInfos.get(position).setChceked(!ischeck);
                mAdapter.notifyDataSetChanged();
            }
        });
        mRecyclerViewBoost.setLayoutManager(new LinearLayoutManager(BoostActivity.this));
        mRecyclerViewBoost.setAdapter(mAdapter);

        new TaskList(BoostActivity.this, mProgressBarLoading, new TaskList.OnTaskListListener() {
            @Override
            public void OnResult(ArrayList<TaskInfo> taskInfos, long total) {
                if (mIsFragmentPause) {
                    return;
                }
                long useRam = mTotalMemory - mAvailMem;
                int percentMem = (int) (((double) useRam / (double) mTotalMemory) * 100);

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    mTvTotalBoost.setText(String.valueOf(percentMem));
                    mTvType.setText(getString(R.string.percent_none));
                    mTvSuggesterBoost.setText("Used");
                    if (taskInfos.size() != 0) {

                        if((PreferenceUtil.getLongTimeBoost(BoostActivity.this)+120*1000)< System.currentTimeMillis()){
                            mViewEmpty.setVisibility(View.GONE);
                            mFrameLayout.setVisibility(View.VISIBLE);
                            mBtnDone.setVisibility(View.GONE);
                            mBtnBoost.setVisibility(View.VISIBLE);
                            mTvSuggesterBoost.setVisibility(View.VISIBLE);

                            mTaskInfos.addAll(taskInfos);
                            mAdapter.notifyDataSetChanged();
//                            mTotalSelect = total;
                        }else{
                            mViewEmpty.setVisibility(View.VISIBLE);
                            mFrameLayout.setVisibility(View.GONE);
                            mBtnDone.setVisibility(View.VISIBLE);
                            mBtnBoost.setVisibility(View.GONE);
                        }
                    }else{
                        mViewEmpty.setVisibility(View.VISIBLE);
                        mFrameLayout.setVisibility(View.GONE);
                        mBtnDone.setVisibility(View.VISIBLE);
                        mBtnBoost.setVisibility(View.GONE);

                        mTvTotalBoost.setText(String.valueOf(percentMem));
                        mTvType.setText(getString(R.string.percent_none));
                    }



                }else{
                    if (taskInfos.size() != 0) {
                        Log.e("CHECKTIME","TIME "+(PreferenceUtil.getLongTimeBoost(BoostActivity.this)+120*1000));
                        Log.e("CHECKTIME","TIME "+ System.currentTimeMillis());
                        if(PreferenceUtil.getLongTimeBoost(BoostActivity.this)+120*1000< System.currentTimeMillis()){
                            mViewEmpty.setVisibility(View.GONE);
                            mFrameLayout.setVisibility(View.VISIBLE);
                            mBtnDone.setVisibility(View.GONE);
                            mBtnBoost.setVisibility(View.VISIBLE);
                            mTvSuggesterBoost.setVisibility(View.VISIBLE);

                            mTaskInfos.addAll(taskInfos);
                            mAdapter.notifyDataSetChanged();
                            mTotalSelect = total;
                            Utils.setTextFromSize(total, mTvTotalBoost, mTvType);
                        }else{
                            mViewEmpty.setVisibility(View.VISIBLE);
                            mFrameLayout.setVisibility(View.GONE);
                            mBtnDone.setVisibility(View.VISIBLE);
                            mBtnBoost.setVisibility(View.GONE);

                            mTvTotalBoost.setText(String.valueOf(percentMem));
                            mTvType.setText(getString(R.string.percent_none));
                            mTvSuggesterBoost.setText("Used");
                        }

                    } else {
                        mViewEmpty.setVisibility(View.VISIBLE);
                        mFrameLayout.setVisibility(View.GONE);
                        mBtnDone.setVisibility(View.VISIBLE);
                        mBtnBoost.setVisibility(View.GONE);

                        mTvTotalBoost.setText(String.valueOf(percentMem));
                        mTvType.setText(getString(R.string.percent_none));
                        mTvSuggesterBoost.setText("Used");
                    }
                }
            }
        }).execute();

    }

    Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {
            updateMemoryStatus();
            mTimerHandler.postDelayed(timerRunnable, 5000);
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        mTimerHandler.postDelayed(timerRunnable, 0);
        mIsFragmentPause = false;
    }

    @Override
    public void onPause() {
        super.onPause();
        mIsFragmentPause = true;
        mTimerHandler.removeCallbacks(timerRunnable);// remove timer runnable interface
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mTimerHandler.removeCallbacks(timerRunnable);// remove timer runnable interface
    }

    private void updateMemoryStatus() {
        ActivityManager.MemoryInfo memInfo = new ActivityManager.MemoryInfo();
        ((ActivityManager) getSystemService(
                Context.ACTIVITY_SERVICE)).getMemoryInfo(memInfo);

        mAvailMem = memInfo.availMem;
        float f1 = mTotalMemory;
        int i = (int) (((float) mAvailMem / f1) * 100F);
        if (i != 0) {
            mTvBoosterPercent.setText(String.format(getString(R.string.index_storage),
                    Utils.formatSize(mTotalMemory - mAvailMem), Utils.formatSize(mTotalMemory)));
        }
    }

    private long getTotalMemory() {
        String str1 = "/proc/meminfo";
        String str2 = "tag";
        String[] arrayOfString;
        long initial_memory = 0;
        try {
            FileReader localFileReader = new FileReader(str1);
            BufferedReader localBufferedReader = new BufferedReader(
                    localFileReader, 8192);
            for (int i = 0; i < 2; i++) {
                str2 = str2 + " " + localBufferedReader.readLine();
            }
            arrayOfString = str2.split("\\s+");
            // total Memory
            initial_memory = Integer.valueOf(arrayOfString[2]);
            localBufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return (initial_memory * 1024L);
    }
}