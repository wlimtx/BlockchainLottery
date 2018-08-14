package com.sunmi.blockchainlottery.util;



import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NetUtil {
    public static final String ValidToken = "12a8181e05ff132db462448a1785c09bdd8a7456ac795f70646a24847ab06d46";

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
            client.newCall(request).enqueue(callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void post(String url, String json, Callback callback) {
        try {
            OkHttpClient.Builder mBuilder = new OkHttpClient.Builder();
            OkHttpClient client = mBuilder.build();
            RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();
            client.newCall(request).enqueue(callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void query(String address, Callback callback) {
        StringBuilder json = new StringBuilder();
        json.append('{');
        if (address != null) json.append("\"address\":\"").append(address).append("\",");
        json.append("\"token\":\"")
                .append(ValidToken)
                .append("\"}");
        String httpsURL = baseHttpUrl + "api/blockchain/fabric/v1/query";
        post(httpsURL, json.toString(), callback);

    }
}
