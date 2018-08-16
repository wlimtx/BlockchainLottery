package com.sunmi.blockchainlottery.item;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;

public class Guess {
    private static final long serialVersionUID = 2L;

    private Long id;
    private Date startTime;
    private String contractAddress;
    private String winner;
    private double sumBet;
    private double award;
    private double fee;
    private boolean over;

    public Guess() {
    }

    public static Guess of() {
        return new Guess(new Date(), "0000", "0000", 0.0, 0.0, 0.0, false);
    }

    public Guess(Date startTime, String contractAddress, String winner, double sumBet, double award, double fee, boolean over) {
        this.startTime = startTime;
        this.contractAddress = contractAddress;
        this.winner = winner;
        this.sumBet = sumBet;
        this.award = award;
        this.fee = fee;
        this.over = over;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public String getContractAddress() {
        return contractAddress;
    }

    public void setContractAddress(String contractAddress) {
        this.contractAddress = contractAddress;
    }

    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    public double getSumBet() {
        return sumBet;
    }

    public void setSumBet(double sumBet) {
        this.sumBet = sumBet;
    }

    public double getAward() {
        return award;
    }

    public void setAward(double award) {
        this.award = award;
    }

    public double getFee() {
        return fee;
    }

    public void setFee(double fee) {
        this.fee = fee;
    }

    public boolean isOver() {
        return over;
    }

    public void setOver(boolean over) {
        this.over = over;
    }


    public static Guess fromJson(String json) {

        // Register an adapter to manage the date types as long values
        return new GsonBuilder().registerTypeAdapter(Date.class, (JsonDeserializer<Date>) (json1, typeOfT, context)
                -> new Date(json1.getAsJsonPrimitive().getAsLong()))
                .create().fromJson(json, Guess.class);

    }

    public static List<Guess> fromJsonToList(String json) {
        // Register an adapter to manage the date types as long values
        return new GsonBuilder().registerTypeAdapter(Date.class, (JsonDeserializer<Date>) (json1, typeOfT, context)
                -> new Date(json1.getAsJsonPrimitive().getAsLong()))
                .create().fromJson(json, new TypeToken<List<Guess>>() {
                }.getType());
    }



    @Override
    public String toString() {
        return "Guess{" +
                "id=" + id +
                ", startTime=" + startTime +
                ", contractAddress='" + contractAddress + '\'' +
                ", winner='" + winner + '\'' +
                ", sumBet=" + sumBet +
                ", award=" + award +
                ", fee=" + fee +
                ", isOver=" + over +
                '}';
    }

    public void cloneFrom(Guess guess) {
        setAward(guess.award);
        setFee(guess.fee);
        setOver(guess.over);
        setSumBet(guess.sumBet);
        setWinner(guess.winner);
    }
}
