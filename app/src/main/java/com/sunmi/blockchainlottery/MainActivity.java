package com.sunmi.blockchainlottery;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.sunmi.blockchainlottery.bean.Account;
import com.sunmi.blockchainlottery.fragment.AccountFragment;
import com.sunmi.blockchainlottery.fragment.LotteryFragment;
import com.sunmi.blockchainlottery.util.Constant;
import com.sunmi.blockchainlottery.util.DialogUtil;
import com.sunmi.blockchainlottery.util.ECKeyIO;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements
        LotteryFragment.OnFragmentInteractionListener
        , AccountFragment.OnFragmentInteractionListener {

    LotteryFragment lotteryFragment;
    AccountFragment accountFragment;
    private Account account = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        lotteryFragment = new LotteryFragment();

        accountFragment = new AccountFragment();
        TextView lottery_tv;
        lottery_tv = findViewById(R.id.lottery_tv);
        TextView account_tv;
        account_tv = findViewById(R.id.account_tv);

        try {
            account = loadAccount();
            lotteryFragment.setAccount(account);
            setFragment(lotteryFragment);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "密钥读取失败", Toast.LENGTH_SHORT).show();
            DialogUtil.showExitDialog(this, Arrays.toString(e.getStackTrace()));
        }

        lottery_tv.setOnClickListener(view -> {
            lottery_tv.setTextColor(Color.rgb(255, 255, 255));
            lottery_tv.setBackground(getResources().getDrawable(R.drawable.left_corner_color));
            account_tv.setTextColor(Color.rgb(51, 51, 51));
            account_tv.setBackground(getResources().getDrawable(R.drawable.right_corner_white));
            lotteryFragment.setAccount(account = accountFragment.getAccount());
            setFragment(lotteryFragment);
        });
        account_tv.setOnClickListener(view -> {
            account_tv.setTextColor(Color.rgb(255, 255, 255));
            account_tv.setBackground(getResources().getDrawable(R.drawable.right_corner_color));
            lottery_tv.setTextColor(Color.rgb(51, 51, 51));
            lottery_tv.setBackground(getResources().getDrawable(R.drawable.left_corner_white));
            accountFragment.setAccount(account);
            setFragment(accountFragment);
        });



    }

    private void setFragment(Fragment lotteryFragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, lotteryFragment).commit();
    }

    @Override
    public void runOnMainThread(Runnable runnable) {
        runOnUiThread(runnable);
    }

    private Account loadAccount() throws Exception {
        return ECKeyIO.readByName(this, Constant.getLastSelectAccountName(this));
    }


}
