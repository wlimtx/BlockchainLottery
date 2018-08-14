package com.sunmi.blockchainlottery.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.sunmi.blockchainlottery.MainActivity;
import com.sunmi.blockchainlottery.R;
import com.sunmi.blockchainlottery.bean.Account;
import com.sunmi.blockchainlottery.dialog.MyDialog;
import com.sunmi.blockchainlottery.fragment.AccountFragment;

import java.security.KeyPair;

public class DialogUtil {
    public static void showExitDialog(Context context, String message) {
        AlertDialog dialog = new AlertDialog.Builder(context).setTitle("Exception when read secret key").setMessage(message).setPositiveButton("退出", (dialogInterface, i) -> {
            System.exit(1);
        }).create();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
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
            } else {
                callBack.newAccount(
                        new Account(name, keys[0], keys[1], keys[2]));
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public static void showRechargeDialog(CallBack callBack, Activity activity) {
        View view = ((LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.dialog_new_account, null);

        EditText asset = view.findViewById(R.id.asset);
        Button cancel = view.findViewById(R.id.cancel);
        Button confirm = view.findViewById(R.id.confirm);
        MyDialog dialog = new MyDialog(activity, view, R.style.dialog);


        cancel.setOnClickListener(v -> dialog.dismiss());
        confirm.setOnClickListener(v -> {
            String money = asset.getText().toString();
            try {
                double m = Double.parseDouble(money);
                dialog.dismiss();
            } catch (NumberFormatException e) {
                activity.runOnUiThread(() -> Toast.makeText(activity, "请输入正确的金额", Toast.LENGTH_SHORT).show());
            }
        });
        dialog.show();
    }
}
