package com.stronglylog;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Promise;

public class StronglyLogModule extends ReactContextBaseJavaModule {

    private final String TAG = "StronglyLogModule";

    @NonNull
    @Override
    public String getName() {
        return "StronglyLogModule";
    }

    public StronglyLogModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

}
