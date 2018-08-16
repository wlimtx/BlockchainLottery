package com.sunmi.blockchainlottery.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sunmi.blockchainlottery.MyApplication;
import com.sunmi.blockchainlottery.R;
import com.sunmi.blockchainlottery.bean.Account;
import com.sunmi.blockchainlottery.bean.Message;
import com.sunmi.blockchainlottery.fragment.LotteryFragment;
import com.sunmi.blockchainlottery.item.Guess;
import com.sunmi.blockchainlottery.util.DialogUtil;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class LotteryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Guess> guesses;
    private LotteryFragment lotteryFragment;

    public void setAccount(Account account, LotteryFragment lotteryFragment) {

        this.account = account;
        this.lotteryFragment = lotteryFragment;

    }



    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView date_seq_tv;
        TextView status_tv;
        TextView sum_bet_tv;
        TextView left_time_tv;
        TextView bet_action;
        LinearLayout award_layout;
        TextView award;
        public MyViewHolder(View view, TextView date_seq_tv, TextView status_tv, TextView sum_bet_tv, TextView left_time_tv, TextView bet_action,
                            LinearLayout award_layout, TextView award) {

            super(view);
            this.date_seq_tv = date_seq_tv;
            this.status_tv = status_tv;
            this.sum_bet_tv = sum_bet_tv;
            this.left_time_tv = left_time_tv;
            this.bet_action = bet_action;
            this.award_layout = award_layout;
            this.award = award;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public LotteryAdapter(List<Guess> guessList, Account account, LotteryFragment lotteryFragment) {

        guesses = guessList;
        this.account = account;
        this.lotteryFragment = lotteryFragment;
    }


    private Account account;
    // Create new views (invoked by the layout manager)
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == TYPE_ITEM) {
            // create a new view
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.lottery_item, parent, false);
            TextView date_seq_tv = view.findViewById(R.id.date_seq_tv);
            TextView status_tv = view.findViewById(R.id.status_tv);
            TextView sum_bet_tv = view.findViewById(R.id.sum_bet_tv);
            TextView left_time_tv = view.findViewById(R.id.left_time_tv);
            TextView bet_action = view.findViewById(R.id.bet_action);
            LinearLayout award_layout = view.findViewById(R.id.award_layout);
            TextView award = view.findViewById(R.id.award);
            return new MyViewHolder(view, date_seq_tv, status_tv, sum_bet_tv, left_time_tv, bet_action, award_layout, award);
        } else if (viewType == TYPE_FOOTER) {
            View foot_view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_foot_view, parent, false);
            //这边可以做一些属性设置，甚至事件监听绑定
            //view.setBackgroundColor(Color.RED);
            return new FootViewHolder(foot_view);
        }
        throw new RuntimeException("not null");
    }

    private View.OnClickListener onClickListener;

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MyViewHolder) {
            MyViewHolder vh = (MyViewHolder) holder;
            Guess guess = guesses.get(position);
            vh.date_seq_tv.setText(
                    (guess.getStartTime().getMonth() + 1) + "月"
                            + guess.getStartTime().getDate() + "日第"
                            + guess.getId() + "轮竞猜");
            vh.status_tv.setText(guess.isOver() ? "竞猜结束" : "竞猜中");
            System.out.println("sumBet: " + guess.getSumBet());

            vh.sum_bet_tv.setText(String.format("%.2f", guess.getSumBet()));



            if (position == 0) {
                vh.award_layout.setVisibility(View.GONE);
                vh.left_time_tv.setVisibility(View.VISIBLE);
                vh.bet_action.setVisibility(View.VISIBLE);
                setUnClickable(vh.bet_action);
                long diff = (60 * 1000 - System.currentTimeMillis() + guess.getStartTime().getTime()) / 1000 + 8;
                if (diff < 0) diff = 0;
                vh.left_time_tv.setText(String.valueOf(diff) + "秒后结束投注");
                vh.bet_action.setOnClickListener( v -> {
                    Dialog[] dialogs = {null};
                    dialogs[0] = DialogUtil.showBetDialog(account, new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            e.printStackTrace();
                            lotteryFragment.onButtonPressed(() -> Toast.makeText(lotteryFragment.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show());
                            dialogs[0].dismiss();
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            if (response.body() != null) {
                                Message<String> message = new Gson().fromJson(response.body().string(),
                                        new TypeToken<Message<String>>() {
                                        }.getType());
                                if (message.getCode() == 200) {
                                    lotteryFragment.onButtonPressed(() -> {
                                        setUnClickable(vh.bet_action);
                                        Toast.makeText(lotteryFragment.getContext(),
                                                "投注成功等待确认:" + message.getData(), Toast.LENGTH_SHORT).show();
                                    });

                                } else {

                                    lotteryFragment.onButtonPressed(() -> Toast.makeText(lotteryFragment.getContext(),
                                            "投注失败:" + message.getMessage() + message.getData(), Toast.LENGTH_SHORT).show());
                                    System.out.println(message.getMessage() + ", " + message.getData());
                                }
                            }
                            response.close();
                            dialogs[0].dismiss();
                        }
                    }, (Activity) lotteryFragment.getContext());

                    dialogs[0].show();
                });

            } else {
                vh.left_time_tv.setVisibility(View.GONE);
                vh.bet_action.setVisibility(View.GONE);
                vh.bet_action.setOnClickListener(null);
                vh.award_layout.setVisibility(View.VISIBLE);
                System.out.println("account.getAddress"+account.getAddress());
                System.out.println("winner"+guess.getWinner());
                if (account.getAddress() != null && account.getAddress().equals(guess.getWinner())) {
                    vh.award.setText(String.valueOf(guess.getAward()));
                } else {
                    vh.award.setText("0.00");
                }

            }



        } else if (holder instanceof FootViewHolder) {

            FootViewHolder vh = (FootViewHolder) holder;
            switch (load_more_status) {
                case PULL_UP_LOAD_MORE:
                    vh.progress_bar.setVisibility(View.GONE);
                    vh.tips_tv.setVisibility(View.VISIBLE);
                    vh.tips_tv.setText("上拉加载更多...");
                    break;
                case LOADING_MORE:
                    vh.tips_tv.setVisibility(View.GONE);
                    vh.progress_bar.setVisibility(View.VISIBLE);
                    break;
            }
        }
    }
    @Override
    public int getItemCount() {
        return guesses.size() + 1;
    }

    private static final int TYPE_ITEM =0;  //普通Item View

    private static final int TYPE_FOOTER = 1;  //底部FootView

    public int getItemViewType(int position) {
        return position + 1 == getItemCount() ? TYPE_FOOTER : TYPE_ITEM;
    }

    /**
     * 底部FootView布局
     */
    public static class FootViewHolder extends  RecyclerView.ViewHolder{
        private TextView tips_tv;
        private ProgressBar progress_bar;
        public FootViewHolder(View view) {
            super(view);
            tips_tv = view.findViewById(R.id.tips_tv);
            progress_bar = view.findViewById(R.id.progress_bar);
        }
    }

    private int load_more_status = 0;
    public static final int PULL_UP_LOAD_MORE = 0;
    public static final int LOADING_MORE = 1;
    public static final int NO_MORE_DATA = 2;

    /**
     * //上拉加载更多
     * PULLUP_LOAD_MORE=0;
     * //正在加载中
     * LOADING_MORE=1;
     * //加载完成已经没有更多数据了
     * NO_MORE_DATA=2;
     *
     * @param status
     */
    public void changeMoreStatus(int status) {
        load_more_status = status;
        notifyItemChanged(getItemCount() - 1);
    }

    private void setUnClickable(TextView bet_action) {
        bet_action.setTextColor(Color.rgb(153, 157, 195));
        bet_action.setBackground(MyApplication.getInstance().getResources().getDrawable(R.drawable.corner_white_all));
        bet_action.setText("已投注");
        bet_action.setClickable(false);
    }
}