package com.sususweet.lottery.utils;

/*
 * Created by tangyq on 2017/4/17.
 */


import android.content.Context;
import android.widget.Toast;

public class ToastUtils {

    private static Toast mToast;

    public static void showAfterCancel(CharSequence text, int duration, Context context){
        if(mToast == null) {
            mToast = Toast.makeText(context.getApplicationContext(), text, duration);
        } else {
            mToast.cancel();
            mToast = Toast.makeText(context.getApplicationContext(), text, duration);
        }
        mToast.show();
    }

    public static void cancelToast() {
        if (mToast != null) mToast.cancel();
    }

    public static void show(CharSequence text, int duration, Context context) {
        Toast.makeText(context, text, duration).show();
    }
    public static void show(int resId, int duration, Context context) {
        show(context.getText(resId), duration, context);
    }

    public static void show(CharSequence text, Context context) {
        show(text, Toast.LENGTH_SHORT, context);
    }

    public static void show(int resId, Context context) {
        show(context.getText(resId), context);
    }

}

