package com.sunmi.blockchainlottery.util;



import com.sunmi.blockchainlottery.bean.Account;
import com.sunmi.sdk.chaincode.LotteryUtil;

import org.bouncycastle.util.encoders.Hex;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

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

    public static void transfer(Account account, double money, String receiveAddress, Callback callback) throws Exception {
        String[] transferArgs = LotteryUtil
                .transfer(String.valueOf(money), receiveAddress,
                        account.getPk(), account.getSk());

        String args = "{\"args\":" + Arrays.toString(transferArgs).replaceAll("([\\w.]++)", "\\\"$1\\\"") + ",\"token\":\""
                + ValidToken + "\"}";
        System.out.println(args);
        String httpsURL = baseHttpUrl + "api/blockchain/fabric/v1/invoke";
        System.out.println(httpsURL);
        post(httpsURL, args, callback);
    }
    public static void bet(Account account, double wager, Callback callback) throws Exception {
        String[] transferArgs = LotteryUtil
                .bet(String.valueOf(wager), Hex.toHexString(PemUtil.randomBytes()),
                        account.getPk(), account.getSk());

        String args = "{\"args\":" + Arrays.toString(transferArgs).replaceAll("([\\w.]++)", "\\\"$1\\\"") + ",\"token\":\""
                + ValidToken + "\"}";
        System.out.println(args);
        String httpsURL = baseHttpUrl + "api/blockchain/fabric/v1/invoke";
        System.out.println(httpsURL);
        post(httpsURL, args, callback);
    }

    public static void recharge(String address, double money, Callback callback) {
        get(baseHttpUrl + "recharge?address=" + address + "&money=" + money, callback);
    }
}
