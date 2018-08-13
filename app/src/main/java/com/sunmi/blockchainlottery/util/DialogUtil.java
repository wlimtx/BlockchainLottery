package com.sunmi.blockchainlottery.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.sunmi.blockchainlottery.MainActivity;
import com.sunmi.blockchainlottery.R;
import com.sunmi.blockchainlottery.fragment.AccountFragment;

public class DialogUtil {
    public interface CallBack {
        void newAccount(String[] account);
    }

    public static void showNewDialog(CallBack callBack, Activity activity) {
        View view = ((LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.dialog_new_account, null);
        TextView address = view.findViewById(R.id.address);
        EditText nick_name = view.findViewById(R.id.nick_name);
        Button cancel = view.findViewById(R.id.cancel);
        Button confirm = view.findViewById(R.id.confirm);
        AlertDialog dialog = new AlertDialog.Builder(activity)
                .setView(view).show();
        cancel.setOnClickListener(v -> dialog.dismiss());
        confirm.setOnClickListener(v -> {
            String name = nick_name.getText().toString();
            if (name.trim().length() == 0) {
                activity.runOnUiThread(() -> Toast.makeText(activity, "用户名不能为空", Toast.LENGTH_SHORT).show());
            } else {
                dialog.dismiss();
            }
        });

    }
}
