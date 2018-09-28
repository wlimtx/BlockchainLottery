package com.sunmi.blockchainlottery.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.zxing.WriterException;
import com.sunmi.blockchainlottery.MyApplication;
import com.sunmi.blockchainlottery.R;
import com.sunmi.blockchainlottery.bean.Account;
import com.sunmi.blockchainlottery.bean.CCUser;
import com.sunmi.blockchainlottery.bean.Message;
import com.sunmi.blockchainlottery.fragment.AccountFragment;
import com.sunmi.blockchainlottery.util.DialogUtil;
import com.sunmi.blockchainlottery.util.ECKeyIO;
import com.sunmi.blockchainlottery.util.NetUtil;
import com.sunmi.blockchainlottery.util.SystemUtil;
import com.uuzuche.lib_zxing.encoding.EncodingHandler;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AccountAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public AccountAdapter(List<Account> accounts, AccountSelectListener accountSelectListener) {
        this.accounts = accounts;
        this.accountSelectListener = accountSelectListener;
    }

    public interface AccountSelectListener {
        void onSelect(Account account);
    }

    private AccountSelectListener accountSelectListener;

    private Account selectAccount;

    public Account getSelectAccount() {
        if (selectAccount == null)
            selectAccount = ECKeyIO.loadAccount(((AccountFragment) accountSelectListener).getContext());
        return selectAccount;
    }

    public void setSelectAccount(Account selectAccount) {
        this.selectAccount = selectAccount;
    }

    private List<Account> accounts;

    public static class MyViewHolder extends RecyclerView.ViewHolder {


        TextView name;
        TextView asset;
        TextView address;
        TextView account_flag;
        ImageView small_qr_code;


        public MyViewHolder(View view, TextView name, TextView asset, TextView address, ImageView small_qr_code, TextView account_flag) {
            super(view);
            this.name = name;
            this.asset = asset;
            this.address = address;
            this.small_qr_code = small_qr_code;
            this.account_flag = account_flag;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.account_item, parent, false);
        TextView name = view.findViewById(R.id.name);
        TextView asset = view.findViewById(R.id.asset);
        TextView address = view.findViewById(R.id.address);
        ImageView small_qr_code = view.findViewById(R.id.small_qr_code);
        TextView account_flag = view.findViewById(R.id.account_flag);


        return new MyViewHolder(view, name, asset, address, small_qr_code, account_flag);
    }

    private View accountUseFlag;
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MyViewHolder viewHolder = (MyViewHolder) holder;
        String name = getSelectAccount().getName();
        String name2 = accounts.get(position).getName();
        System.out.println(name + ", " + name2);
        if (name2.equals(name)) {
            viewHolder.account_flag.setVisibility(View.VISIBLE);
            accountUseFlag = viewHolder.account_flag;
            System.out.println("setActionListener");

        } else {
            viewHolder.account_flag.setVisibility(View.GONE);
            viewHolder.itemView.setOnClickListener(null);
        }

        viewHolder.address.setText(accounts.get(position).getAddress()
                .replaceFirst("(.{24}).*+", "$1...>"));
        viewHolder.name.setText(accounts.get(position).getName());
        viewHolder.asset.setText(accounts.get(position).getAsset());
        try {
            viewHolder.small_qr_code.setImageBitmap(EncodingHandler.createQRCode(accounts.get(position)
                    .getAddress(), 40));
        } catch (WriterException e) {
            e.printStackTrace();
        }


        viewHolder.itemView.setOnClickListener(v -> {
            if (accountUseFlag != viewHolder.account_flag) {
                accountUseFlag.setVisibility(View.GONE);
                viewHolder.account_flag.setVisibility(View.VISIBLE);
                accountUseFlag = viewHolder.account_flag;
                setSelectAccount(accounts.get(position));
                accountSelectListener.onSelect(getSelectAccount());
            } else {

                try {
                    System.out.println("show dialog");
                    Context context = ((AccountFragment) accountSelectListener).getContext();
                    if (context != null)
                        DialogUtil.showQrCode(accounts.get(position).getAddress(), context);
                } catch (WriterException e) {
                    e.printStackTrace();
                }


            }
        });
        query(accounts.get(position).getAddress(), viewHolder.asset);
    }

    @Override
    public int getItemCount() {
        return accounts.size();
    }

    public void query(String address, TextView view) {
        NetUtil.query(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                ((AccountFragment) accountSelectListener).onButtonPressed(() -> Toast
                        .makeText(((AccountFragment) accountSelectListener)
                                .getContext(), e.getMessage(), Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.body() != null) {
                    String json = response.body().string();



                    Message<List<String>> listMessage = new ObjectMapper()
                            .readValue(json
                                    , new TypeReference<Message<List<String>>>() {
                                    });
                    if (listMessage.getCode() == 200) {
                        List<String> data = listMessage.getData();
                        if (data.size() == 0) {
                            ((AccountFragment) accountSelectListener).onButtonPressed(() -> Toast.makeText( ((AccountFragment) accountSelectListener).getContext(), "blockchain network exception", Toast.LENGTH_SHORT).show());
                            return;
                        }
                        CCUser ccUser = new ObjectMapper().readValue(data.get(0), CCUser.class);

                        ((AccountFragment) accountSelectListener).onButtonPressed(() -> view.setText(String.format("%.2f", Double.valueOf(ccUser.getAsset()))));
                    } else {
                        ((AccountFragment) accountSelectListener).onButtonPressed(() -> Toast.makeText( ((AccountFragment) accountSelectListener)
                                .getContext(), listMessage.getMessage() + listMessage.getData(), Toast.LENGTH_SHORT).show());


                    }

                }
                response.close();
            }
        });
    }
}
