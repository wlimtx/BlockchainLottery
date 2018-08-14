package com.sunmi.blockchainlottery.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.WriterException;
import com.sunmi.blockchainlottery.R;
import com.sunmi.blockchainlottery.bean.Account;
import com.uuzuche.lib_zxing.encoding.EncodingHandler;

import java.util.List;

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
        if (accounts.get(position).getName().equals(getSelectAccount().getName())) {
            viewHolder.account_flag.setVisibility(View.VISIBLE);
            accountUseFlag = viewHolder.account_flag;
        } else {
            viewHolder.account_flag.setVisibility(View.GONE);
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
            }
        });

    }

    @Override
    public int getItemCount() {
        return accounts.size();
    }
}
