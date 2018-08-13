package com.sunmi.blockchainlottery.util;



import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class NetUtil {

    public static final String baseHttpUrl
            = "http://172.16.2.198:8888/";

    public static void last(Callback callback) {
        get(baseHttpUrl + "last", callback);
    }

    public static void pageOf(int min, Callback callback) {
        pageOf(min, 1, callback);
    }

    public static void pageOf(int min, int count, Callback callback) {
        get(baseHttpUrl + "pageOf?min=" + min + "&count=" + count, callback);
    }

    public static void get(String url, Callback callback) {
        try {
            OkHttpClient.Builder mBuilder = new OkHttpClient.Builder();
            OkHttpClient client = mBuilder.build();
            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .build();
//            System.out.println(client.newCall(request).execute().body().string());
            client.newCall(request).enqueue(callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
