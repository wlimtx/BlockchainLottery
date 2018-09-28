package com.sunmi.blockchainlottery.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sunmi.blockchainlottery.MainActivity;
import com.sunmi.blockchainlottery.MyApplication;
import com.sunmi.blockchainlottery.R;
import com.sunmi.blockchainlottery.adapter.LotteryAdapter;
import com.sunmi.blockchainlottery.bean.Account;
import com.sunmi.blockchainlottery.bean.CCUser;
import com.sunmi.blockchainlottery.bean.Message;
import com.sunmi.blockchainlottery.component.TransferDialog;
import com.sunmi.blockchainlottery.item.Guess;
import com.sunmi.blockchainlottery.util.Constant;
import com.sunmi.blockchainlottery.util.DialogUtil;
import com.sunmi.blockchainlottery.util.ECKeyIO;
import com.sunmi.blockchainlottery.util.NetUtil;
import com.sunmi.blockchainlottery.util.Worker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class LotteryFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private LotteryAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;

    private List<Guess> guesses;

    private boolean loading = false;

    ProgressBar upPb;

    BlockingQueue<CCUser> seq = new LinkedBlockingQueue<>();
//    private CCUser ccUser = new CCUser();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private Account account;

    public LotteryFragment() {
        // Required empty public constructor
        System.out.println("new fragment");
        Worker.setJob(() -> {
            System.out.println("update" + mLayoutManager.getChildCount() + ", " + guesses.size());
            if (mLayoutManager.getChildCount() > 1 && guesses.size() > 0) {
                long diff = (60 * 1000 - (System.currentTimeMillis() -
                        guesses.get(0).getStartTime().getTime())) / 1000;
                updateNew(diff);
            }
        }, () -> {
            if (account != null) {
                NetUtil.query(account.getAddress(), new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                        onButtonPressed(() -> Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show());
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        ResponseBody body = response.body();
                        response.close();
                        if (body != null) {
                            String json = body.string();
                            Message<List<String>> listMessage = new ObjectMapper()
                                    .readValue(json
                                            , new TypeReference<Message<List<String>>>() {
                                            });
                            if (listMessage.getCode() == 200) {
                                List<String> data = listMessage.getData();
                                if (data.size() == 0) {
                                    onButtonPressed(() -> Toast.makeText(getContext(), "blockchain network exception", Toast.LENGTH_SHORT).show());
                                    return;
                                }
                                while (!seq.isEmpty()) {
                                    seq.poll();
                                }
                                seq.add(new ObjectMapper().readValue(data.get(0), CCUser.class));
                                account.setAsset(seq.peek().getAsset());
                                onButtonPressed(() -> asset.setText(String.format("%.2f", Double.valueOf(seq.peek().getAsset()))));
                            } else {
                                onButtonPressed(() -> Toast.makeText(getContext(), listMessage.getMessage() + listMessage.getData(), Toast.LENGTH_SHORT).show());
                            }
                        }
                    }
                });
            }
        });
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BlankFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LotteryFragment newInstance(String param1, String param2) {
        LotteryFragment fragment = new LotteryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        System.out.println("onCreate");
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    private View contentView;
    TextView tv;
    TextView asset;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        System.out.println("onCreateView");
        if (contentView == null) {
            System.out.println("create new view");
            // Inflate the layout for this fragment

            contentView = inflater.inflate(R.layout.fragment_lottery, container, false);
            mRecyclerView = contentView.findViewById(R.id.my_recycler_view);


            View.OnClickListener onClickListener = v -> {
                Dialog[] dialogs = {null};
                dialogs[0] = DialogUtil.showRechargeDialog(account.getAddress(), new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                        onButtonPressed(() -> Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show());
                        dialogs[0].dismiss();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {

                        ResponseBody body = response.body();
                        response.close();
                        if (body != null) {
                            Message<String> message = new Gson().fromJson(body.string(),
                                    new TypeToken<Message<String>>() {
                                    }.getType());
                            if (message.getCode() == 200) {
                                onButtonPressed(() -> Toast.makeText(getContext(),
                                        "充值成功等待确认:" + message.getData(), Toast.LENGTH_SHORT).show());
                            } else {
                                onButtonPressed(() -> Toast.makeText(getContext(),
                                        "充值失败:" + message.getMessage() + message.getData(), Toast.LENGTH_SHORT).show());
                            }
                        }

                        dialogs[0].dismiss();
                    }
                }, (Activity) mListener);
                dialogs[0].show();
            };
            contentView.findViewById(R.id.recharge).setOnClickListener(onClickListener);
            contentView.findViewById(R.id.recharge_tv).setOnClickListener(onClickListener);

            View.OnClickListener onClickListener2 = v2 -> {

                TransferDialog dialogs = new TransferDialog();
                dialogs.setWorker(account, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                        onButtonPressed(() -> Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show());
                        dialogs.dismiss();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {

                        ResponseBody body = response.body();
                        response.close();
                        if (body != null) {
                            Message<String> message = new Gson().fromJson(body.string(),
                                    new TypeToken<Message<String>>() {
                                    }.getType());
                            if (message.getCode() == 200) {
                                onButtonPressed(() -> Toast.makeText(getContext(),
                                        "转账成功等待确认:" + message.getData(), Toast.LENGTH_SHORT).show());
                            } else {
                                onButtonPressed(() -> Toast.makeText(getContext(),
                                        "转账失败:" + message.getMessage() + message.getData(), Toast.LENGTH_SHORT).show());
                            }
                        }
                        dialogs.dismiss();
                    }
                });
                if (mListener!=null)
                    dialogs.show(((MainActivity) mListener).getFragmentManager(), "TransferDialog");

            };
            contentView.findViewById(R.id.transfer).setOnClickListener(onClickListener2);
            contentView.findViewById(R.id.transfer_tv).setOnClickListener(onClickListener2);
            tv = contentView.findViewById(R.id.nick_name);
            asset = contentView.findViewById(R.id.asset);

            upPb = contentView.findViewById(R.id.upPb);
            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
//        mRecyclerView.setHasFixedSize(true);

            // use a linear layout manager
            mLayoutManager = new LinearLayoutManager(getContext()) {
                @Override
                public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
                    try {
                        super.onLayoutChildren(recycler, state);
                    } catch (IndexOutOfBoundsException e) {
                        e.printStackTrace();
                    }
                }
            };
            mRecyclerView.setLayoutManager(mLayoutManager);


            loading = false;
            guesses = new ArrayList<>();
            initData(2);


            // specify an adapter (see also next example)
            mAdapter = new LotteryAdapter(guesses, account, this);
            mRecyclerView.setAdapter(mAdapter);

            mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                long lastInvokeTime = 0;
                int lastVisibleItem = -1;

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
//                lastVisibleItem = mLayoutManager.findLastVisibleItemPosition();
                }

                //          https://www.jianshu.com/p/4d038beb01a1
                //这个办法应该更好，下次尝试下
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);

                    System.out.println("scroll" + loading + ", " + ", " + lastInvokeTime);

                    //当不可再往底部滑动 且 手指离开屏幕时 确定为需要加载分页

                    //loading 必须等待前一次查询结束
                    //必须是飞快的自动滑动
                    //并且不可在往底部滑动
                    //并且2s内只会被调用一次
                    if (!loading && newState == AbsListView.OnScrollListener.SCROLL_STATE_FLING && !mRecyclerView.canScrollVertically(1)
                            && System.currentTimeMillis() - lastInvokeTime >= 2000) {
                        lastInvokeTime = System.currentTimeMillis();
                        if (guesses.size() == 0) initData(2);
                        else loadMoreDate(1);

                        System.out.println("scrolling");

                    }
                }
            });
        } else {
            loading = false;
            int size = guesses.size();
            guesses.clear();
            mAdapter.notifyItemRangeRemoved(0, size);
            initData(2);
        }

        if (account != null) {
            tv.setText(account.getName());
            asset.setText(String.format("%.2f", Double.valueOf(account.getAsset())));
            mAdapter.setAccount(account, this);

        }

        return contentView;
    }

    public void onButtonPressed(Runnable runnable) {
        if (mListener != null) {
            mListener.runOnMainThread(runnable);
        }
    }

    @Override
    public void onAttach(Context context) {
        System.out.println("onAttach");
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        System.out.println("onDetach");
        super.onDetach();
        mListener = null;
        guesses.clear();
    }

    public List<Guess> getGuesses() {
        return guesses;
    }

    public void setAccount(Account account) {

        this.account = account;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void runOnMainThread(Runnable runnable);
    }


    private void updateNew(long diff) {
        NetUtil.last(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                //getActivity().runOnUiThread(() -> Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show());
                onButtonPressed(() -> Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show());
                loadUpEnd();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                ResponseBody body = response.body();
                response.close();
                if (body != null) {

                    Message<String> message = new Gson().fromJson(body.string(),
                            new TypeToken<Message<String>>() {
                            }.getType());
                    Guess guess = Guess.fromJson(message.getData());
                    final long count;
                    try {
                        count = guess.getId() - guesses.get(0).getId();
                    } catch (Exception e) {
                        e.printStackTrace();
                        return;
                    }

                    if (count <= 0) {
                        mRecyclerView.post(() -> {
                            try {
                                View v = mLayoutManager.getChildAt(0);
                                View view = v.findViewById(R.id.left_time_tv);
                                if (view != null) {


                                    ((TextView) view)
                                            .setText((diff < 0 ? "0" : String.valueOf(diff)) + "秒后结束投注");

                                }
                                if (guess.getSumBet() != guesses.get(0).getSumBet()) {
                                    view = v.findViewById(R.id.sum_bet_tv);
                                    if (view != null) ((TextView) view)
                                            .setText(String.format("%.2f", guess.getSumBet()));
                                }
                                TextView bet_action = v.findViewById(R.id.bet_action);

                                String status = seq.peek().getStatus();
                                System.out.println("status: " + "0".equals(status));

                                judgeStatus(bet_action, seq.peek());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        });

                        return;
                    }
                    if (loading) return;
                    loadUpStart();
                    NetUtil.pageOf((int) (guess.getId() + 1), (int) count + 1, new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            e.printStackTrace();
                            //getActivity().runOnUiThread(() -> Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show());
                            onButtonPressed(() -> Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show());
                            loadUpEnd();
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            ResponseBody body1 = response.body();
                            response.close();
                            if (body1 != null) {
                                Message<String> message = new Gson().fromJson(body1.string(),
                                        new TypeToken<Message<String>>() {
                                        }.getType());
                                List<Guess> list = Guess.fromJsonToList(message.getData());
                                if (list.size() > 0) {
                                    System.out.println("listsize" + list.size() + ", myguess:" + guess);

                                    guesses.get(0).cloneFrom(list.get(0));

                                    for (int i = 1; i < list.size(); i++)
                                        guesses.add(0, list.get(i));

                                    mRecyclerView.post(() -> {
                                        mAdapter.notifyItemRangeInserted(0, list.size());

                                        mAdapter.notifyItemChanged(list.size());

                                        mRecyclerView.scrollToPosition(0);
                                        loadUpEnd();
                                    });
                                } else {
                                    //getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "数据中断....", Toast.LENGTH_SHORT).show());
                                    onButtonPressed(() -> Toast.makeText(getContext(), "数据中断....", Toast.LENGTH_SHORT).show());
                                    loadUpEnd();
                                }
                            }

                        }
                    });
                }
            }
        });
    }
    private void judgeStatus(TextView bet_action, CCUser ccUser) {
        if (!"0".equals(ccUser.getStatus())) {
            bet_action.setTextColor(Color.rgb(153, 157, 195));
            bet_action.setBackground(MyApplication.getInstance().getResources().getDrawable(R.drawable.corner_white_all));
            bet_action.setText("已投注");
            bet_action.setClickable(false);
        } else {
            bet_action.setTextColor(Color.rgb(255, 247, 247));
            bet_action.setBackground(MyApplication.getInstance().getResources().getDrawable(R.drawable.corner_color_all));
            bet_action.setText("立即投注");
            bet_action.setClickable(true);
        }
    }



    private void initData(int count) {
        if (loading) return;
        loadStart();
        NetUtil.last(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                //getActivity().runOnUiThread(() -> Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show());
                onButtonPressed(() -> Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show());
                loadEnd();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ResponseBody body = response.body();
                response.close();
                if (body != null) {

                    Message<String> message = new Gson().fromJson(body.string(),
                            new TypeToken<Message<String>>() {
                            }.getType());
                    Guess guess = Guess.fromJson(message.getData());
                    //如果服务器没有数据，那么guess就是null
                    if (guess != null) {
                        guesses.add(guess);
                        View v = mLayoutManager.getChildAt(0);
                        TextView bet_action = v.findViewById(R.id.bet_action);
                        mRecyclerView.post(() -> mAdapter.notifyItemInserted(0));
                        judgeStatus(bet_action, seq.peek());
                        try {
                            Thread.sleep(600);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        loadEnd();
                        loadMoreDate(count);
                    } else {
                        //getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "没有更多的数据...", Toast.LENGTH_SHORT).show());
                        onButtonPressed(() -> Toast.makeText(getContext(), "没有更多的数据...", Toast.LENGTH_SHORT).show());
                        loadEnd();
                    }
                }
            }
        });
    }

    private void loadMoreDate(int count) {
        if (loading) return;
        loadStart();
        try {
            Long min = guesses.get(guesses.size() - 1).getId();
            NetUtil.pageOf(min.intValue(), count, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                    //getActivity().runOnUiThread(() -> Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show());
                    onButtonPressed(() -> Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show());
                    loadEnd();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    ResponseBody body = response.body();
                    response.close();
                    if (body != null) {
                        Message<String> message = new Gson().fromJson(body.string(),
                                new TypeToken<Message<String>>() {
                                }.getType());
                        List<Guess> list = Guess.fromJsonToList(message.getData());
                        System.out.println("GuessList: " + list);
                        if (list.size() > 0) {
                            int oldSize = guesses.size();
                            for (int i = list.size() - 1; i >= 0; i--) guesses.add(list.get(i));
                            mRecyclerView.post(() -> {
//                            if (notifyAll) {
//                                notifyAll = false;
//                                mAdapter.notifyDataSetChanged();
//                            } else {
                                mAdapter.notifyItemRangeInserted(oldSize, list.size());
//                            }
                                loadEnd();
                            });
                        } else {
                            //getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "没有更多的数据....", Toast.LENGTH_SHORT).show());
                            onButtonPressed(() -> Toast.makeText(getContext(), "没有更多的数据....", Toast.LENGTH_SHORT).show());
                            loadEnd();
                        }
                    }
                }
            });
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            loadEnd();
        }
    }


    public void loadStart() {
        loading = true;
        //initData loadMoreDate 加载完数据后自动设置loading=false
        mRecyclerView.post(() -> mAdapter.changeMoreStatus(LotteryAdapter.LOADING_MORE));
    }

    public void loadEnd() {
        loading = false;
        mRecyclerView.post(() -> mAdapter.changeMoreStatus(LotteryAdapter.PULL_UP_LOAD_MORE));
    }

    private void loadUpStart() {
        loading = true;
        //getActivity().runOnUiThread(() -> upPb.setVisibility(View.VISIBLE));
        onButtonPressed(() -> upPb.setVisibility(View.VISIBLE));
    }

    private void loadUpEnd() {
        loading = false;
        //getActivity().runOnUiThread(() -> upPb.setVisibility(View.GONE));
        onButtonPressed(() -> upPb.setVisibility(View.GONE));
    }

    @Override
    public void onResume() {
        super.onResume();
        Worker.resume(2, 1, 2, 6);
        System.out.println("onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        Worker.pause();
        System.out.println("onPause");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        loading = true;
        System.out.println("onDestroyView");
    }

}
