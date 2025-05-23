package com.stronglylog;

import android.text.TextUtils;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.common.ReactConstants;
import com.facebook.react.module.annotations.ReactModule;
import com.stronglylog.utils.FileUtils;
import com.stronglylog.utils.L;
import com.stronglylog.utils.ZipUtil;

import java.io.File;
import java.util.Map;

import javax.annotation.Nonnull;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.schedulers.Schedulers;

@ReactModule(name = StronglyLogModule.NAME)
public class StronglyLogModule  extends ReactContextBaseJavaModule {

    static final String NAME = "StronglyLogModule";

    StronglyLogModule(@Nonnull ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Nonnull
    @Override
    public String getName() {
        return NAME;
    }

    @ReactMethod
    public void debug(String s) {
        L.d(ReactConstants.TAG, s);
    }

    @ReactMethod
    public void info(String s) {
        L.i(ReactConstants.TAG, s);
    }

    @ReactMethod
    public void warn(String s) {
        L.w(ReactConstants.TAG, s);
    }

    @ReactMethod
    public void error(String s) {
        L.e(ReactConstants.TAG, s);
    }

    @ReactMethod
    public void clearOldLogs(final Promise promise) {

        //在io线程上面跑
        Observable.create((ObservableOnSubscribe<String>) emitter -> {
            L.clearOldLogs(getReactApplicationContext());

            promise.resolve(1);

            emitter.onComplete();
        }).subscribeOn(Schedulers.io()).subscribe();

    }

    @ReactMethod
    public void zipLogFiles(final String zipFileName, final Promise promise) {
        //在io线程上面跑
        Observable.create((ObservableOnSubscribe<String>) emitter -> {
            promise.resolve(doZipLogFiles());

            emitter.onComplete();
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    private String doZipLogFiles(String zipFileName) {
        final File logFileFolder = L.getLogFileFolder(getReactApplicationContext());
        if (!logFileFolder.exists()) {
            return null;
        }
        final File zipFile = new File(L.getLogZipFolder(getReactApplicationContext()), zipFileName);

        if (ZipUtil.zip(logFileFolder.getAbsolutePath(), zipFile.getAbsolutePath())) {
            return zipFile.getAbsolutePath();
        }

        return null;
    }

    @ReactMethod
    public void clearAllLogs(final Promise promise) {
        //在io线程上面跑
        Observable.create((ObservableOnSubscribe<String>) emitter -> {
            doClearAllLogs();

            promise.resolve(1);

            emitter.onComplete();
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    private void doClearAllLogs() {
        final File logFileFolder = L.getLogFileFolder(getReactApplicationContext());
        if (!logFileFolder.exists()) {
            return;
        }
        final File nowLogFile = L.getLogFile();
        final String strNowLogFile = null != nowLogFile ? nowLogFile.getAbsolutePath() : null;


        File[] files = logFileFolder.listFiles();
        if (null == files) {
            return;
        }
        for (File file : files) {
            if (file.isDirectory()) continue;

            //不删除当前使用的日志
            if (TextUtils.equals(strNowLogFile, file.getAbsolutePath())) continue;

            FileUtils.deleteFile(file);
        }
    }

    @ReactMethod
    public void flush(final Promise promise) {
        // android 这边写日志内部会flush, 所有这里不需要做任何事情, 只是为了同步ios方法
        promise.resolve(1);
    }
}
