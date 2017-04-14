package com.fyp.n3015509.bookbuzzerapp.tasks;

import com.fyp.n3015509.bookbuzzerapp.WatchBooksService;
import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.PeriodicTask;

/**
 * Created by tomha on 13-Apr-17.
 */

public class TaskManagement {
    private GcmNetworkManager mGcmNetworkManager;
    public static final String TASK_TAG_PERIODIC = "periodic_task";

    public TaskManagement(GcmNetworkManager man)
    {
        this.mGcmNetworkManager = man;
    }

    public void startBookWatchTask() {
        // [START start_periodic_task]
        PeriodicTask task = new PeriodicTask.Builder()
                .setService(WatchBooksService.class)
                .setTag(TASK_TAG_PERIODIC)
                .setPeriod(30L)
                .build();

        mGcmNetworkManager.schedule(task);
        // [END start_periodic_task]
    }
}
