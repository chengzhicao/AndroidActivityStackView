package com.cheng.plugins;

public class FilterNode {
    /***************Task***************/
    private static final String TASK_1 = "userId=";
    private static final String TASK_2 = "autoRemoveRecents=";
    private static final String TASK_3 = "rootWasReset=";
    private static final String TASK_4 = "askedCompatMode=";
    private static final String TASK_5 = "lastThumbnail=";
    private static final String TASK_6 = "hasBeenVisible=";


    /***************Hist***************/
    private static final String HIST_1 = "launchedFromUid=";
    private static final String HIST_2 = "baseDir=";
    private static final String HIST_3 = "dataDir=";
    private static final String HIST_4 = "stateNotNeeded=";
    private static final String HIST_5 = "compat=";
    private static final String HIST_6 = "config=";
    private static final String HIST_7 = "stackConfigOverride=";
    private static final String HIST_8 = "taskDescription:";
    private static final String HIST_9 = "launchFailed=";
    private static final String HIST_10 = "haveState=";
    private static final String HIST_11 = "state=";
    private static final String HIST_12 = "keysPaused=";
    private static final String HIST_13 = "fullscreen=";
    private static final String HIST_14 = "frozenBeforeDestroy=";
    private static final String HIST_15 = "waitingVisible=";

    /***************Display***************/
    private static final String DISPLAY_1 = "mSleepTimeout=";
    private static final String DISPLAY_2 = "mUserStackInFront=";
    private static final String DISPLAY_3 = "mActivityContainers=";
    private static final String DISPLAY_4 = "mLockTaskModeState=";
    private static final String DISPLAY_5 = "mLockTaskModeTasks";

    public static final String[] ALL = {DISPLAY_1, DISPLAY_2, DISPLAY_3, DISPLAY_4, DISPLAY_5,
            TASK_1, TASK_2, TASK_3, TASK_4, TASK_5, TASK_6,
            HIST_1, HIST_2, HIST_3, HIST_4, HIST_5,
            HIST_6, HIST_7, HIST_8, HIST_9, HIST_10,
            HIST_11, HIST_12, HIST_13, HIST_14, HIST_15};

}
