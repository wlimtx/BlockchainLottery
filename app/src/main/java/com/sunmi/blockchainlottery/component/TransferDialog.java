package com.sunmi.blockchainlottery.component;
 
import android.Manifest;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.sunmi.blockchainlottery.R;
import com.sunmi.blockchainlottery.bean.Account;
import com.sunmi.blockchainlottery.util.DialogUtil;
import com.sunmi.blockchainlottery.util.NetUtil;
import com.uuzuche.lib_zxing.activity.CaptureActivity;
import com.uuzuche.lib_zxing.activity.CodeUtils;

import okhttp3.Callback;


public class TransferDialog extends DialogFragment {

    Callback callBack;
    private Account account;
    public void setWorker(Account account, Callback callBack) {
        this.account = account;
        this.callBack = callBack;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.dialog_transfer, container);
        Activity activity = getActivity();
        view.findViewById(R.id.scan).setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA}, 1);
            } else {
                Intent intent = new Intent(activity, CaptureActivity.class);
                startActivityForResult(intent, Activity.RESULT_FIRST_USER);

            }
        });

        amount = view.findViewById(R.id.asset);

        address = view.findViewById(R.id.address);
        cancel = view.findViewById(R.id.cancel);
        transfer = view.findViewById(R.id.confirm);

        cancel.setOnClickListener(v -> {
            Toast.makeText(activity, "您取消了", Toast.LENGTH_SHORT).show();
            dismiss();
        });
        transfer.setOnClickListener(v -> {
            String money = amount.getText().toString();
            try {
                double m = Double.parseDouble(money);
                if (m > Double.parseDouble(account.getAsset())) {
                    activity.runOnUiThread(() -> Toast.makeText(activity, "余额不足", Toast.LENGTH_SHORT).show());
                    return;
                }
                String hexReceiverAddress = address.getText().toString();
                if (!hexReceiverAddress.matches("[\\da-f]++")) {
                    activity.runOnUiThread(() -> Toast.makeText(activity, "无效的账户ID", Toast.LENGTH_SHORT).show());
                    return;
                }
                NetUtil.transfer(account, m, hexReceiverAddress, callBack);

                transfer.setClickable(false);
            } catch (NumberFormatException e) {
                activity.runOnUiThread(() -> Toast.makeText(activity, "请输入正确的金额", Toast.LENGTH_SHORT).show());
            } catch (Exception e) {
                e.printStackTrace();
                activity.runOnUiThread(() -> Toast.makeText(activity, "签名失败" + e.getMessage(), Toast.LENGTH_SHORT).show());
            }


        });
        return view;
    }

    EditText amount;
    EditText address;
    TextView cancel;
    TextView transfer;


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Activity.RESULT_FIRST_USER:
                if (null != data) {
                    Bundle bundle = data.getExtras();
                    if (bundle == null) return;
                    if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS)
                        address.setText(bundle.getString(CodeUtils.RESULT_STRING));
                    else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED)
                        Toast.makeText(getActivity()
                                , "解析二维码失败", Toast.LENGTH_LONG).show();
                }
                break;
            default:
                break;
        }
    }
}