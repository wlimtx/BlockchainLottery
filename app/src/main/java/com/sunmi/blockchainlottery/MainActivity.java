package com.sunmi.blockchainlottery;

import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.sunmi.blockchainlottery.fragment.AccountFragment;
import com.sunmi.blockchainlottery.fragment.LotteryFragment;

public class MainActivity extends AppCompatActivity implements
        LotteryFragment.OnFragmentInteractionListener
        , AccountFragment.OnFragmentInteractionListener {

    LotteryFragment lotteryFragment;
    AccountFragment accountFragment;

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
        lottery_tv.setOnClickListener(view -> {
            setFragment(lotteryFragment);
            System.out.println(lotteryFragment.getGuesses().size());
        });
        account_tv.setOnClickListener(view -> setFragment(accountFragment));
        setFragment(lotteryFragment);
    }

    private void setFragment(Fragment lotteryFragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, lotteryFragment).commit();
    }

    @Override
    public void runOnMainThread(Runnable runnable) {
        runOnUiThread(runnable);
    }
}
