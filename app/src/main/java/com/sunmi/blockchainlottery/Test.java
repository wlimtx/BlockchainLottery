package com.sunmi.blockchainlottery;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sunmi.blockchainlottery.bean.Message;
import com.sunmi.blockchainlottery.item.Guess;
import com.sunmi.blockchainlottery.util.NetUtil;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class Test {

    private int anInt = 3;
    private int b = 7;
    private String c = "gsh";

    public int getAnInt() {
        return anInt;
    }

    public void setAnInt(int anInt) {
        this.anInt = anInt;
    }

    public int getB() {
        return b;
    }

    public void setB(int b) {
        this.b = b;
    }

    public String getC() {
        return c;
    }

    public void setC(String c) {
        this.c = c;
    }

    public static void main(String[] args) {
        NetUtil.pageOf(13, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.body() != null) {
                    Message<String> message = new Gson().fromJson(response.body().string(),
                            new TypeToken<Message<String>>() {
                            }.getType());
                    System.out.println(message.getData());
                    List<Guess> list = Guess.fromJsonToList(message.getData());
                    for (Guess guess : list) {
                        System.out.println(guess);

                    }
                }


            }
        });

    }

    @Override
    public String toString() {
        return "Test{" +
                "anInt=" + anInt +
                ", b=" + b +
                ", c='" + c + '\'' +
                '}';
    }
}
