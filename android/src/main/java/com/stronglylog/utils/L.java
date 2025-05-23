package com.stronglylog.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.stronglylog.BuildConfig;

import java.io.File;
import java.io.FileWriter;
import java.util.Calendar;
import java.util.Locale;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.schedulers.Schedulers;

/**
 * Logcat统一管理类
 */
public class L {

    public static boolean IS_LOG = BuildConfig.DEBUG;
    private static boolean IS_CONSOLE = BuildConfig.DEBUG;

    private static String sm_logFolderPath;
    private static File sm_logFile;
    private static FileWriter file;

    private static final String TAG = "strongly_L";

    public static final String TAG_EXCEPTION = "EXCEPTION";

    private static volatile Thread sm_logThread;
    private static Runnable sm_logRunnable;
    private static StringBuffer sm_curLogBuffer;

    /**
     * 日志初始化操作 必调
     * MainApplication onCreate调用
     * L.setup(this);
     */
    public static void setup(Context ctx) {
        if (!ProcessUtils.isMainProcess(ctx)) {
            return;
        }

        String logFileFolder = getLogFileFolder(ctx).getAbsolutePath();
        boolean bLog = true;
        boolean bConsole = true;

        IS_LOG = bLog;
        IS_CONSOLE = bConsole;
        initLogFile(logFileFolder);

        //清除4天前旧日志
        clearOldLogsInIOThread(ctx);
    }

    private static Runnable createLogRunnable() {
        if (null != sm_logRunnable) {
            return sm_logRunnable;
        }

        sm_logRunnable = new Runnable() {
            @Override
            public void run() {
                int iFreeLoop = 0;
                for (; ; ) {
                    ++iFreeLoop;
                    if (iFreeLoop > 10) {
                        //空跑超过10次， 10 * 50 = 500ms，那么先结束线程
                        synchronized (L.class) {
                            sm_logThread = null;
                            return;
                        }
                    }
                    try {
                        Thread.sleep(50);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    StringBuffer buffer = sm_curLogBuffer;
                    if (null == buffer) continue;

                    sm_curLogBuffer = null;
                    iFreeLoop = 0;


                    if (null == file) {
                        if (!doCreateLogFile()) continue;
                    }

                    if (null == file) {
                        continue;
                    }

                    try {
                        file.append(buffer.toString());
                        file.flush();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }


            }
        };

        return sm_logRunnable;
    }

    private static void startLogTimer() {
        if (null != sm_logThread) {
            return;
        }

        try {
            synchronized (L.class) {
                if (null != sm_logThread) {
                    return;
                }

                sm_logThread = new Thread(createLogRunnable(), "strongly_log");
            }

            sm_logThread.start();
        } catch (OutOfMemoryError e) {
            sm_logThread = null;
        } catch (Exception e) {
            sm_logThread = null;
        }
    }

    private static synchronized void logFile(String level, String tag, String msg) {
        StringBuffer buffer = sm_curLogBuffer;
        if (null == buffer) {
            buffer = new StringBuffer();
            sm_curLogBuffer = buffer;
        }

        try {
            buffer.append(getNowTime())
                    .append(" ")
                    .append(level)
                    .append(" ")
                    .append(tag)
                    .append(" ")
                    .append(msg)
                    .append('\n');
        } catch (Exception e) {
            e.printStackTrace();
        }

        startLogTimer();

    }

    private static void closeLogFile() {
        if (null == file) return;

        try {
            file.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        file = null;
        sm_logFile = null;
    }

    private static String getNowTime() {
        Calendar c = Calendar.getInstance();
        return String.format(
                Locale.CHINA,
                "%d-%d-%d-%d.%d.%d.%d",
                c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH),
                c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), c.get(Calendar.SECOND), c.get(Calendar.MILLISECOND)
        );
    }

    private static void initLogFile(String logFileFolder) {
        closeLogFile();
        if (TextUtils.isEmpty(logFileFolder)) {
            sm_logFolderPath = null;
            return;
        }

        sm_logFolderPath = logFileFolder;

        doCreateLogFile();
    }

    private static boolean doCreateLogFile() {
        try {
            //noinspection ResultOfMethodCallIgnored
            new File(sm_logFolderPath).mkdirs();

            sm_logFile = new File(sm_logFolderPath, getNowTime() + ".log");
            file = new FileWriter(sm_logFile, true);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static File getLogFileFolder(Context ctx) {
        return new File(FileUtils.getExtStorageDir(ctx), "rnsl_logs");
    }

    public static File getLogZipFolder(Context ctx) {
        return new File(FileUtils.getExtStorageDir(ctx), "rnsl_logs_zip");
    }

    public static File getLogFile() {
        return sm_logFile;
    }

    /**
     * 清理旧日志
     */
    public static void clearOldLogs(Context ctx) {
        final File logFileFolder = getLogFileFolder(ctx);

        if (!logFileFolder.exists()) {
            return;
        }


        long now = System.currentTimeMillis();

        File[] files = logFileFolder.listFiles();
        if (null == files) {
            return;
        }
        for (File file : files) {
            if (file.isDirectory()) continue;

            int days = (int) ((now - file.lastModified()) / (1000 * 3600 * 24));
            if (days < 4) continue;

            FileUtils.deleteFile(file);
        }
    }

    public static void clearOldLogsInIOThread(Context ctx) {
        Observable.create((ObservableOnSubscribe<String>) emitter -> {
            clearOldLogs(ctx);

            emitter.onComplete();
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    public static void v(String msg) {
        if (!IS_LOG) return;
        v(TAG, msg);
    }

    public static void v(String tag, String msg) {
        if (!IS_LOG) return;

        if (IS_CONSOLE) {
            Log.v(tag, msg);
        }

        if (null != sm_logFolderPath) {
            logFile("v", TAG, msg);
        }
    }

    public static void v(String tag, String format, Object... args) {
        if (!IS_LOG) return;
        v(tag, String.format(Locale.CHINA, format, args));
    }

    public static void d(String msg) {
        if (!IS_LOG) return;
        d(TAG, msg);
    }

    public static void d(String tag, String msg) {
        if (!IS_LOG) return;

        if (IS_CONSOLE) {
            if (msg.length() > 3000) {
                for (int i = 0; i < msg.length(); i += 3000) {
                    if (i + 3000 < msg.length())
                        Log.d(tag + i, msg.substring(i, i + 3000));
                    else
                        Log.d(tag + i, msg.substring(i));
                }
            } else
                Log.d(tag, msg);
        }

        if (null != sm_logFolderPath) {
            logFile("D", tag, msg);
        }
    }

    public static void d(String tag, String format, Object... args) {
        if (!IS_LOG) return;
        d(tag, String.format(Locale.CHINA, format, args));
    }

    public static void i(String msg) {
        if (!IS_LOG) return;
        i(TAG, msg);
    }

    public static void i(String tag, String msg) {
        if (!IS_LOG) return;

        if (IS_CONSOLE) {
            if (msg.length() > 3000) {
                for (int i = 0; i < msg.length(); i += 3000) {
                    if (i + 3000 < msg.length())
                        Log.i(tag + i, msg.substring(i, i + 3000));
                    else
                        Log.i(tag + i, msg.substring(i));
                }
            } else
                Log.i(tag, msg);
        }

        if (null != sm_logFolderPath) {
            logFile("I", tag, msg);
        }
    }

    public static void i(String tag, String format, Object... args) {
        if (!IS_LOG) return;
        i(tag, String.format(Locale.CHINA, format, args));
    }

    public static void w(String msg) {
        if (!IS_LOG) return;
        w(TAG, msg);
    }

    public static void w(String tag, String msg) {
        if (!IS_LOG) return;

        if (IS_CONSOLE)
            Log.w(tag, msg);

        if (null != sm_logFolderPath) {
            logFile("W", tag, msg);
        }
    }

    public static void w(String tag, String format, Object... args) {
        if (!IS_LOG) return;
        w(tag, String.format(Locale.CHINA, format, args));
    }

    public static void e(String msg) {
        if (!IS_LOG) return;
        e(TAG, msg);
    }

    public static void e(Throwable throwable) {
        if (!IS_LOG) return;
        e(TAG_EXCEPTION, Log.getStackTraceString(throwable));
    }

    public static void e(String tag, Throwable throwable) {
        if (!IS_LOG) return;
        e(tag, Log.getStackTraceString(throwable));
    }

    public static void e(String tag, String msg) {
        if (!IS_LOG) return;

        if (IS_CONSOLE) {
            if (msg.length() > 3000) {
                for (int i = 0; i < msg.length(); i += 3000) {
                    if (i + 3000 < msg.length())
                        Log.e(tag + i, StringUtils.decode(msg.substring(i, i + 3000)));
                    else
                        Log.e(tag + i, StringUtils.decode(msg.substring(i)));
                }
            } else {
                Log.e(tag, StringUtils.decode(msg));
            }
        }

        if (null != sm_logFolderPath) {
            logFile("E", tag, msg);
        }
    }

    public static void e(String format, Object... args) {
        if (!IS_LOG) return;
        e(TAG, String.format(Locale.CHINA, format, args));
    }

    public static void e(String tag, String format, Object... args) {
        if (!IS_LOG) return;
        e(tag, String.format(Locale.CHINA, format, args));
    }

}