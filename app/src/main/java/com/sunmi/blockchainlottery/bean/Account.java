package com.sunmi.blockchainlottery.bean;

import com.sunmi.blockchainlottery.util.PemUtil;

import org.bouncycastle.util.encoders.Hex;

import java.security.SecureRandom;

public class Account {


    private String name;
    private String asset;
    private String address;
    private String pk;
    private String sk;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAsset() {
        return asset;
    }

    public void setAsset(String asset) {
        this.asset = asset;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPk() {
        return pk;
    }

    public void setPk(String pk) {
        this.pk = pk;
    }

    public String getSk() {
        return sk;
    }

    public void setSk(String sk) {
        this.sk = sk;
    }

    public Account(String name, String pk, String sk) {
        this.name = name;
        this.pk = pk;
        this.sk = sk;
        address = Hex.toHexString(PemUtil.sha256(Hex.decode(pk)));
        asset = "0.0";
    }

    public static Account of() {
        byte[] pk = new byte[32];
        byte[] sk = new byte[32];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(pk);
        secureRandom.nextBytes(sk);
        return new Account("Player" + secureRandom.nextInt(20),
                Hex.toHexString(pk),
                Hex.toHexString(sk)
        );
    }

}
