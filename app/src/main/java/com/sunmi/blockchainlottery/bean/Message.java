package com.sunmi.blockchainlottery.bean;

import com.google.gson.Gson;

import java.io.Serializable;
import java.lang.reflect.Type;

public class Message<T> implements Serializable {
    private int code;
    private String message;
    private T data;

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }

    public Message() {
    }

    public static <T> Message fromJson(String json, Type typeOfT) {
        return new Gson().fromJson(json, typeOfT);
    }
}
