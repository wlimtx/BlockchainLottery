package com.sunmi.blockchainlottery.util;



import android.content.Context;

import java.io.File;

public class Constant {

    public static String getLastSelectAccountName(Context context) {
        return context.getSharedPreferences(Constant.DefaultDatabase, 0).getString("name", null);
    }
    public static final String BaseSkPrefix = "3041020100301306072a8648ce3d020106082a8648ce3d030107042730250201010420";

    public static final String DefaultDatabase = "last_account_name";

    public static final String BasePkPrefix = "3059301306072a8648ce3d020106082a8648ce3d03010703420004";
    public static final File Path = new File("/Users/liumingxing/IdeaProjects/FabricFinal/fabric_key");
    public static final String KeyRoot = "fabric_key";
//    String keyDir = getApplicationContext().getFilesDir().getAbsolutePath() + "/" + Constant.KeyRoot;




}
