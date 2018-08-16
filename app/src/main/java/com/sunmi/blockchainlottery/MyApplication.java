package com.sunmi.blockchainlottery;

import android.content.Context;

import com.uuzuche.lib_zxing.ZApplication;

public class MyApplication extends ZApplication {
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
    }

    public static Context getInstance() {
        return mContext;
    }

}
