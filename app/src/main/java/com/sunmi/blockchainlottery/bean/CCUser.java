package com.sunmi.blockchainlottery.bean;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;

public class CCUser {
    private String asset;
    private String bet_asset;
    private String cipher;
    private String luck_bytes;
    private String status;


    public String getAsset() {
        return asset;
    }

    public void setAsset(String asset) {
        this.asset = asset;
    }

    public String getBet_asset() {
        return bet_asset;
    }

    public void setBet_asset(String bet_asset) {
        this.bet_asset = bet_asset;
    }

    public String getCipher() {
        return cipher;
    }

    public void setCipher(String cipher) {
        this.cipher = cipher;
    }

    public String getLuck_bytes() {
        return luck_bytes;
    }

    public void setLuck_bytes(String luck_bytes) {
        this.luck_bytes = luck_bytes;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "CCUser{" +
                "asset='" + asset + '\'' +
                ", bet_asset='" + bet_asset + '\'' +
                ", cipher='" + cipher + '\'' +
                ", luck_bytes='" + luck_bytes + '\'' +
                ", status='" + status + '\'' +
                '}';
    }

    public static void main(String[] args) throws IOException {
        String str = "{\"code\":200,\"message\":\"success\",\"data\":\"{\\\"id\\\":52,\\\"startTime\\\":1534190953113,\\\"contractAddress\\\":\\\"0000000000000000000000000000000000000000000000000000000000000000\\\",\\\"winner\\\":null,\\\"sumBet\\\":0.0,\\\"award\\\":0.0,\\\"fee\\\":0.0,\\\"over\\\":true}\"}";

        str = "{\"code\":200,\"message\":\"success query one\",\"data\":[\"{\\\"asset\\\":1000002,\\\"bet_asset\\\":0,\\\"cipher\\\":null,\\\"luck_bytes\\\":null,\\\"status\\\":0}\"]}";

        List<String> data = ((Message<List<String>>) new ObjectMapper()
                .readValue(str
                        , new TypeReference<Message<List<String>>>() {
                        })).getData();
        for (String datum : data) {
            System.out.println(new ObjectMapper().readValue(datum, CCUser.class));
        }
        List<CCUser> users = ((Message<List<CCUser>>) new ObjectMapper()
                .readValue(str
                        , new TypeReference<Message<List<CCUser>>>() {
                        })).getData();


        System.out.println(users);
    }

}
