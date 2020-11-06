package com.hanabi.todoapp.models;

import android.app.Activity;

import androidx.work.BackoffPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.hanabi.todoapp.works.RemindWork;

import java.util.concurrent.TimeUnit;

public class Remind {

    private RemindWork remindWork;

    public static void remindVeryDay(Activity activity) {
        WorkManager workManager = WorkManager.getInstance(activity);
        PeriodicWorkRequest periodicWorkRemind =
                new PeriodicWorkRequest.Builder(RemindWork.class, 1, TimeUnit.MINUTES, 15, TimeUnit.MINUTES)
                        .setBackoffCriteria(
                                BackoffPolicy.LINEAR,
                                OneTimeWorkRequest.MIN_BACKOFF_MILLIS,
                                TimeUnit.MILLISECONDS)
                        .build();

        workManager.enqueue(periodicWorkRemind);
    }
}
