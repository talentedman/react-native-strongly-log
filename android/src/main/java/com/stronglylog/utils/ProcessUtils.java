package com.stronglylog.utils;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.Build;

import java.util.List;

public class ProcessUtils {
    public static boolean isMainProcess(Context context) {
        if (context == null) {
            return false;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            String currentProcessName = Application.getProcessName();
            String packageName = context.getPackageName();
            return packageName.equals(currentProcessName);
        } else {
            // 对于 API 28 以下的设备，使用 ActivityManager (见下文)
            String currentProcessName = getCurrentProcessNameByActivityManager(context);
            if (currentProcessName != null) {
                return currentProcessName.equals(context.getPackageName());
            }
            return false; // 或者根据具体情况决定返回值
        }
    }

    // 辅助方法，用于 API 28 以下
    private static String getCurrentProcessNameByActivityManager(Context context) {
        if (context == null) {
            return null;
        }
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (am == null) {
            return null;
        }
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = am.getRunningAppProcesses();
        if (runningAppProcesses == null || runningAppProcesses.isEmpty()) {
            return null;
        }
        int myPid = android.os.Process.myPid();
        for (ActivityManager.RunningAppProcessInfo processInfo : runningAppProcesses) {
            if (processInfo.pid == myPid) {
                return processInfo.processName;
            }
        }
        return null;
    }
}
