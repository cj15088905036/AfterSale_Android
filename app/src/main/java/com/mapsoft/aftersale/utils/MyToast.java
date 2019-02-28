package com.mapsoft.aftersale.utils;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.mapsoft.aftersale.R;

/**
 * Created by Administrator on 2017/11/25.
 */

public class MyToast {
    private static Toast toast;

    public static void popLongToast(Context context, String text) {    //封装全局自定义Toast
        // LayoutInflater inflater = context.getLayoutInflater();
        View view = LayoutInflater.from(context).inflate(R.layout.app_toast, null);
        TextView textView = (TextView) view.findViewById(R.id.toast_message);
        textView.setText(text);
        if (toast == null) {
            toast = new Toast(context);//单例
        }
        toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, 0, 200);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(view);
        toast.show();
    }
    public static void popToast(Context context, String text) {    //封装全局自定义Toast
        // LayoutInflater inflater = context.getLayoutInflater();
        View view = LayoutInflater.from(context).inflate(R.layout.app_toast, null);
        TextView textView = (TextView) view.findViewById(R.id.toast_message);
        textView.setText(text);
        if (toast == null) {
            toast = new Toast(context);//单例
        }
        toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, 0, 200);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(view);
        toast.show();
    }
}
