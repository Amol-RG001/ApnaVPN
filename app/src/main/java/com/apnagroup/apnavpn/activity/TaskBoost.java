package com.apnagroup.apnavpn.activity;

import android.app.ActivityManager;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.util.List;

public class TaskBoost extends AsyncTask<Void, Void, Void> {

    private static final String TAG = TaskBoost.class.getName();
    private List<TaskInfo> mTaskInfos;
    private ActivityManager mActivityManager;
    private OnTaskBoostListener mOnTaskBoostListener;
    private int mPercent;

    public TaskBoost(Context context,List<TaskInfo> taskInfos, OnTaskBoostListener onTaskBoostListener) {
        mTaskInfos = taskInfos;
        mOnTaskBoostListener = onTaskBoostListener;
        mActivityManager = (ActivityManager) context.getSystemService(
                Context.ACTIVITY_SERVICE);
        mPercent = 100 / mTaskInfos.size();
    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected Void doInBackground(Void... params) {
        for (TaskInfo taskInfo : mTaskInfos) {
            if (taskInfo.isChceked()) {
                Log.d(TAG, "==killBackgroundProcesses--=>>" + taskInfo.getAppinfo().packageName);
                mActivityManager.killBackgroundProcesses(taskInfo.getAppinfo().packageName);
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            publishProgress();
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);

    }

    @Override
    protected void onPostExecute(Void result) {

        if (null != mOnTaskBoostListener) {
            mOnTaskBoostListener.OnResult();
        }
    }

    public interface OnTaskBoostListener {
        void OnResult();
    }
}