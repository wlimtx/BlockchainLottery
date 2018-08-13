package com.sunmi.blockchainlottery.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sunmi.blockchainlottery.R;
import com.sunmi.blockchainlottery.adapter.LotteryAdapter;
import com.sunmi.blockchainlottery.bean.Message;
import com.sunmi.blockchainlottery.item.Guess;
import com.sunmi.blockchainlottery.util.NetUtil;
import com.sunmi.blockchainlottery.util.Worker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class LotteryFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private LotteryAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;

    private List<Guess> guesses;

    private boolean loading = false;

    ProgressBar upPb;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public LotteryFragment() {
        // Required empty public constructor
        System.out.println("new fragment");
        Worker.setJob(() -> {
            System.out.println("update" + mLayoutManager.getChildCount() + ", " + guesses.size());
            if (mLayoutManager.getChildCount() > 1 && guesses.size() > 0) {
                long diff = (60 * 1000 - (System.currentTimeMillis() -
                        guesses.get(0).getStartTime().getTime())) / 1000
                        + 8 * 3600;
                if (diff < 0) {
                    //拉取最新发生的数据
                    updateNew();
                } else {
                    mRecyclerView.post(() -> {
                        View view = mLayoutManager.getChildAt(0).findViewById(R.id.left_time_tv);
                        if (view != null) ((TextView) view)
                                .setText(String.valueOf(diff) + "秒后结束投注");
                    });
                }
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
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        System.out.println("onCreateView");
        if (contentView == null) {
            System.out.println("create new view");
            // Inflate the layout for this fragment

            contentView = inflater.inflate(R.layout.fragment_lottery, container, false);
            mRecyclerView = contentView.findViewById(R.id.my_recycler_view);

            upPb = contentView.findViewById(R.id.upPb);
            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
//        mRecyclerView.setHasFixedSize(true);

            // use a linear layout manager
            mLayoutManager = new LinearLayoutManager(getContext()){
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
            mAdapter = new LotteryAdapter(guesses);
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
    }

    public List<Guess> getGuesses() {
        return guesses;
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


    private void updateNew() {
        if (loading) return;

        loadUpStart();
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
                if (response.body() != null) {

                    Message<String> message = new Gson().fromJson(response.body().string(),
                            new TypeToken<Message<String>>() {
                            }.getType());
                    Guess guess = Guess.fromJson(message.getData());
                    final long count = guess.getId() - guesses.get(0).getId();
                    if (count <= 0) {

//                        runOnUiThread(() -> Toast.makeText(MainActivity.this, "等待开奖....", Toast.LENGTH_SHORT).show());
                        loadUpEnd();
                        return;
                    }
                    NetUtil.pageOf((int) (guess.getId() + 1), (int) count, new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            e.printStackTrace();
                            //getActivity().runOnUiThread(() -> Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show());
                            onButtonPressed(() -> Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show());
                            loadUpEnd();
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            if (response.body() != null) {
                                Message<String> message = new Gson().fromJson(response.body().string(),
                                        new TypeToken<Message<String>>() {
                                        }.getType());
                                List<Guess> list = Guess.fromJsonToList(message.getData());
                                if (list.size() > 0) {
                                    for (int i = 0; i < list.size(); i++)
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
                if (response.body() != null) {

                    Message<String> message = new Gson().fromJson(response.body().string(),
                            new TypeToken<Message<String>>() {
                            }.getType());
                    Guess guess = Guess.fromJson(message.getData());
                    //如果服务器没有数据，那么guess就是null
                    if (guess != null) {
                        guesses.add(guess);
                        mRecyclerView.post(() -> mAdapter.notifyItemInserted(0));

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
                    if (response.body() != null) {
                        Message<String> message = new Gson().fromJson(response.body().string(),
                                new TypeToken<Message<String>>() {
                                }.getType());
                        List<Guess> list = Guess.fromJsonToList(message.getData());
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
        Worker.resume(1, 1);
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