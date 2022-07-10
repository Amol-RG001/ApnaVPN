package com.apnagroup.apnavpn.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.apnagroup.apnavpn.R;

import java.util.ArrayList;

public class BoostResult extends AppCompatActivity {

    private TextView mTvBoostDone;
    private ImageView mImgBooster;
    private ImageView mImgBoostDone;
    private Button mBtnDone;
    private ArrayList<TaskInfo> mTaskInfos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boost_result);
//        mTaskInfos = (ArrayList<TaskInfo>) getIntent().getSerializableExtra("mylist");
//        mImgBooster = (ImageView) findViewById(R.id.imgBooster);
//        mImgBoostDone = (ImageView) findViewById(R.id.imgBoostDone);
//        mTvBoostDone = (TextView) findViewById(R.id.tvBoostDone);
        mBtnDone = (Button) findViewById(R.id.btnDone);
//        mImgBooster.setBackgroundResource(R.drawable.loader_boost);

//        mImgBoostDone.setVisibility(View.GONE);
//        mBtnDone.setVisibility(View.INVISIBLE);
//        mTvBoostDone.setVisibility(View.INVISIBLE);
//        AnimationDrawable rocketAnimation = (AnimationDrawable) mImgBooster.getBackground();
//        rocketAnimation.start();
//        slideToTopSlow(mImgBooster);
        mBtnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                onBackPressed();
            }
        });
    }

//    public void slideToTopSlow(View view) {
//        TranslateAnimation animate = new TranslateAnimation(0, 50 / 2, 0, -50);
//        animate.setDuration(2000);
//        animate.setFillAfter(true);
//        animate.setAnimationListener(new Animation.AnimationListener() {
//            @Override
//            public void onAnimationStart(Animation animation) {
//                // no op
//            }
//
//            @Override
//            public void onAnimationEnd(Animation animation) {
//                new TaskBoost(BoostResult.this, mTaskInfos,
//                        new TaskBoost.OnTaskBoostListener() {
//                            @Override
//                            public void OnResult() {
//                                slideToTop(mImgBooster);
//                            }
//                        }).execute();
//            }
//
//            @Override
//            public void onAnimationRepeat(Animation animation) {
//                // no op
//            }
//        });
//        view.startAnimation(animate);
//    }
//
//    // To animate view slide out from bottom to top
//    public void slideToTop(View view) {
//        TranslateAnimation animate = new TranslateAnimation(0, 100 / 2, 0, -100);
//        animate.setDuration(1000);
//        animate.setFillAfter(true);
//        animate.setAnimationListener(new Animation.AnimationListener() {
//            @Override
//            public void onAnimationStart(Animation animation) {
//                // no op
//            }
//
//            @Override
//            public void onAnimationEnd(Animation animation) {
//                mImgBoostDone.setVisibility(View.VISIBLE);
//                mBtnDone.setVisibility(View.VISIBLE);
//                mTvBoostDone.setVisibility(View.VISIBLE);
//            }
//
//            @Override
//            public void onAnimationRepeat(Animation animation) {
//                // no op
//            }
//        });
//        view.startAnimation(animate);
//        view.setVisibility(View.GONE);
//    }

}