package com.sunmi.blockchainlottery.fragment;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.sunmi.blockchainlottery.MainActivity;
import com.sunmi.blockchainlottery.R;
import com.sunmi.blockchainlottery.adapter.AccountAdapter;
import com.sunmi.blockchainlottery.bean.Account;
import com.sunmi.blockchainlottery.util.Constant;
import com.sunmi.blockchainlottery.util.DialogUtil;
import com.sunmi.blockchainlottery.util.ECKeyIO;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AccountFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AccountFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AccountFragment extends Fragment implements AccountAdapter.AccountSelectListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private Account account;
    public AccountFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AccountFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AccountFragment newInstance(String param1, String param2) {
        AccountFragment fragment = new AccountFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    private View contentView;
    private RecyclerView recyclerView;
    private AccountAdapter adapter;
    private List<Account> accounts;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (contentView == null) {
            // Inflate the layout for this fragment
            contentView = inflater.inflate(R.layout.fragment_account, container, false);

            recyclerView = contentView.findViewById(R.id.my_recycler_view);

            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()) {
                @Override
                public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
                    try {
                        super.onLayoutChildren(recycler, state);
                    } catch (IndexOutOfBoundsException e) {
                        e.printStackTrace();
                    }
                }
            });


            accounts = new ArrayList<>();

            try {
                accounts.addAll(ECKeyIO.readAll((Context) mListener));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText((Context) mListener, "密钥读取失败，" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }


            adapter = new AccountAdapter(accounts, this);
            recyclerView.setAdapter(adapter);



            View new_account = contentView.findViewById(R.id.new_account);
            new_account.setOnClickListener(view -> {
                try {
                    DialogUtil.showNewDialog(account -> {
                        try {
                            ECKeyIO.save(account, (Context) mListener);
                            accounts.add(account);
                            adapter.notifyItemInserted(accounts.size() - 1);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText((Context) mListener, "密钥存储失败，" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }, (Activity) mListener);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText((Context) mListener, "密钥生成失败，" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        adapter.setSelectAccount(account);
        return contentView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Runnable runnable) {
        if (mListener != null) mListener.runOnMainThread(runnable);
    }

    @Override
    public void onAttach(Context context) {
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
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onSelect(Account account) {
        this.account = account;

        ((Context) mListener).getSharedPreferences(Constant.DefaultDatabase, 0)
                .edit()
                .putString("name", account.getName()).apply();
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

    public Account getAccount() {
        return account;
    }

}
