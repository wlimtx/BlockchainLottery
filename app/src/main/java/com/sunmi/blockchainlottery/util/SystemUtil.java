package com.sunmi.blockchainlottery.util;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;

public class SystemUtil {

    public static void putTextIntoClip(Context context,String copy){
        ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        //创建ClipData对象
        ClipData clipData = ClipData.newPlainText("simple text copy", copy);
        if (clipboardManager == null) {
            return;
        }
        //添加ClipData对象到剪切板中
        clipboardManager.setPrimaryClip(clipData);
    }

    public static String getTextFromClip(Context context) {
        ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboardManager == null) {
            return "";
        }
        //判断剪切版时候有内容
        if (!clipboardManager.hasPrimaryClip())
            return "";
        ClipData clipData = clipboardManager.getPrimaryClip();
        //获取 ClipDescription
        ClipDescription clipDescription = clipboardManager.getPrimaryClipDescription();
        //获取 lable
        String lable = clipDescription.getLabel().toString();
        //获取 text
        String s = clipData.getItemAt(0).getText().toString();
        return s;
    }



}
