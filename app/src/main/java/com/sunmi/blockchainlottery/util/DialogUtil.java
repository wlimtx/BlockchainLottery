package com.sunmi.blockchainlottery.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Scroller;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.WriterException;
import com.sunmi.blockchainlottery.R;
import com.sunmi.blockchainlottery.bean.Account;
import com.sunmi.blockchainlottery.dialog.MyDialog;
import com.sunmi.blockchainlottery.fragment.AccountFragment;
import com.uuzuche.lib_zxing.encoding.EncodingHandler;

import java.security.KeyPair;
import java.util.Objects;
import java.util.logging.Handler;

import okhttp3.Callback;

public class DialogUtil {
    public static void showExitDialog(Context context, String message) {
        AlertDialog dialog = new AlertDialog.Builder(context).setTitle("Exception when read secret key").setMessage(message).setPositiveButton("退出", (dialogInterface, i) -> {
            System.exit(1);
        }).create();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    public static void showQrCode(String address, Context context) throws WriterException {
        View view = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.dialog_qrcode, null);
        TextView address_tv = view.findViewById(R.id.address);
        ImageView big_qr_code = view.findViewById(R.id.big_qr_code);


        address_tv.setOnClickListener(v->{
            if (address != null) {
                SystemUtil.putTextIntoClip(context, address);
                Toast.makeText(context, "已复制:" + address, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "复制失败" ,Toast.LENGTH_SHORT).show();
            }

        });

        address_tv.setText(address);
        big_qr_code.setImageBitmap(EncodingHandler.createQRCode(address, 200));
        MyDialog dialog = new MyDialog(context, view, R.style.dialog);
        dialog.show();
    }

    public interface CallBack {
        void newAccount(Account account);
    }

    public static void showNewDialog(CallBack callBack, Activity activity) throws Exception {
        View view = ((LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.dialog_new_account, null);
        TextView address = view.findViewById(R.id.address);
        EditText nick_name = view.findViewById(R.id.nick_name);
//        nick_name.setScroller(new Scroller(activity));
//        nick_name.setMaxLines(1);
//        nick_name.setVerticalScrollBarEnabled(false);
//        nick_name.setHorizontalScrollBarEnabled(true);
//        nick_name.setMovementMethod(new ScrollingMovementMethod());
        Button cancel = view.findViewById(R.id.cancel);
        Button confirm = view.findViewById(R.id.confirm);
        KeyPair keyPair = ECDSAUtil.generateKey();
        String[] keys = ECKeyIO.split(keyPair);
        address.setText(keys[0]);
        MyDialog dialog = new MyDialog(activity, view, R.style.dialog);


        cancel.setOnClickListener(v -> dialog.dismiss());
        confirm.setOnClickListener(v -> {
            String name = nick_name.getText().toString();
            if (name.trim().length() == 0) {
                activity.runOnUiThread(() -> Toast.makeText(activity, "用户名不能为空", Toast.LENGTH_SHORT).show());
            } else if (name.trim().length() > 4) {
                activity.runOnUiThread(() -> Toast.makeText(activity, "用户名长度过长", Toast.LENGTH_SHORT).show());
            } else {

                callBack.newAccount(
                        new Account(name, keys[0], keys[1], keys[2]));
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public static Dialog showRechargeDialog(String address, Callback callBack, Activity activity) {
        View view = ((LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.dialog_recharge, null);

        EditText asset = view.findViewById(R.id.asset);
        Button cancel = view.findViewById(R.id.cancel);
        Button confirm = view.findViewById(R.id.confirm);
        MyDialog dialog = new MyDialog(activity, view, R.style.dialog);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        cancel.setOnClickListener(v -> dialog.dismiss());
        confirm.setOnClickListener(v -> {
            String money = asset.getText().toString();
            try {
                double m = Double.parseDouble(money);
                if (m > 100000) {
                    activity.runOnUiThread(() -> Toast.makeText(activity, "单笔充值不可超过10w", Toast.LENGTH_SHORT).show());
                    return;
                }
                NetUtil.recharge(address, m, callBack);
                dialog.dismiss();
            } catch (NumberFormatException e) {
                activity.runOnUiThread(() -> Toast.makeText(activity, "请输入正确的金额", Toast.LENGTH_SHORT).show());
            }
        });
        return dialog;
    }

    public static Dialog showBetDialog(Account account, Callback callBack, Activity activity) {
        View view = ((LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.dialog_betting, null);

        EditText asset = view.findViewById(R.id.asset);
        Button cancel = view.findViewById(R.id.cancel);
        Button confirm = view.findViewById(R.id.confirm);
        MyDialog dialog = new MyDialog(activity, view, R.style.dialog);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        cancel.setOnClickListener(v -> dialog.dismiss());
        confirm.setOnClickListener(v -> {
            String money = asset.getText().toString();
            try {
                double m = Double.parseDouble(money);

                NetUtil.bet(account, m, callBack);
                dialog.dismiss();
            } catch (NumberFormatException e) {
                activity.runOnUiThread(() -> Toast.makeText(activity, "请输入正确的金额", Toast.LENGTH_SHORT).show());
            } catch (Exception e) {
                e.printStackTrace();
                activity.runOnUiThread(() -> Toast.makeText(activity, "签名失败" + e.getMessage(), Toast.LENGTH_SHORT).show());
                dialog.dismiss();
            }
        });
        return dialog;
    }

    @Deprecated
    public static Dialog showTransferDialog(Account account, Callback callBack, Activity activity) {
        View view = ((LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.dialog_transfer, null);

        EditText asset = view.findViewById(R.id.asset);
        EditText address = view.findViewById(R.id.address);
        Button cancel = view.findViewById(R.id.cancel);
        Button confirm = view.findViewById(R.id.confirm);
        MyDialog dialog = new MyDialog(activity, view, R.style.dialog);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        cancel.setOnClickListener(v -> dialog.dismiss());
        confirm.setOnClickListener(v -> {
            String money = asset.getText().toString();
            try {
                double m = Double.parseDouble(money);
                String addr = address.getText().toString();
                if (!addr.matches("[\\da-f]++")) {
                    activity.runOnUiThread(() -> Toast.makeText(activity, "无效的账户ID", Toast.LENGTH_SHORT).show());
                    return;
                }
                NetUtil.transfer(account, m, addr, callBack);
                dialog.dismiss();
            } catch (NumberFormatException e) {
                activity.runOnUiThread(() -> Toast.makeText(activity, "请输入正确的金额", Toast.LENGTH_SHORT).show());
            } catch (Exception e) {
                e.printStackTrace();
                activity.runOnUiThread(() -> Toast.makeText(activity, "签名失败" + e.getMessage(), Toast.LENGTH_SHORT).show());
                dialog.dismiss();
            }
        });
        return dialog;
    }
}
