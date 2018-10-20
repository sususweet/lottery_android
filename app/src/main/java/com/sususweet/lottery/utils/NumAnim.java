package com.sususweet.lottery.utils;

/*
 * Created by tangyq on 2017/4/17.
 */

import java.util.LinkedList;
import java.util.Random;

import android.content.Context;
import android.widget.TableLayout;
import android.widget.TextView;
import com.sususweet.lottery.utils.NumUtil;

import static com.sususweet.lottery.info.SharedPrefsContract.DEFAULT_RANDOM_LEVEL;
import static com.sususweet.lottery.info.SharedPrefsContract.KEY_RANDOM_LEVEL;

public class NumAnim {

    //每秒刷新多少次
    private static final int COUNTPERS = 100;
    private static boolean isStart = false;

    public static void startAnim(TextView textV, float num) {
        startAnim(textV, num, 500);
    }

    public static RandomCounter startRandom(TextView textView, int min, int max, long time, Context context) {
        float showNoneRandomLevel = SharedPrefsUtils.getFloat(KEY_RANDOM_LEVEL, DEFAULT_RANDOM_LEVEL, context.getApplicationContext());
        RandomCounter counter = new RandomCounter(textView, min, max, time,showNoneRandomLevel, context.getApplicationContext());
        textView.removeCallbacks(counter);
        textView.post(counter);
        isStart = true;
        return counter;
    }

    public static void endRandom(TextView textView, RandomCounter counter) {
        isStart = false;
        if (counter == null) {
            return;
        }
        textView.removeCallbacks(counter);
    }

    public static boolean getStarted(){
        return isStart;
    }

    public static void startAnim(TextView textV, float num, long time) {
        if (num == 0) {
            textV.setText(NumUtil.NumberFormat(num,2));
            return;
        }

        Float[] nums = splitnum(num, (int)((time/1000f)*COUNTPERS));

        Counter counter = new Counter(textV, nums, time);

        textV.removeCallbacks(counter);
        textV.post(counter);
    }

    private static Float[] splitnum(float num, int count) {
        Random random = new Random();
        float numtemp = num;
        float sum = 0;
        LinkedList<Float> nums = new LinkedList<Float>();
        nums.add(0f);
        while (true) {
            float nextFloat = NumUtil.NumberFormatFloat(
                    (random.nextFloat()*num*2f)/(float)count,
                    2);
            System.out.println("next:" + nextFloat);
            if (numtemp - nextFloat >= 0) {
                sum = NumUtil.NumberFormatFloat(sum + nextFloat, 2);
                nums.add(sum);
                numtemp -= nextFloat;
            } else {
                nums.add(num);
                return nums.toArray(new Float[0]);
            }
        }
    }

    private static class Counter implements Runnable {

        private final TextView view;
        private Float[] nums;
        private long pertime;

        private int i = 0;

        Counter(TextView view,Float[] nums,long time) {
            this.view = view;
            this.nums = nums;
            this.pertime = time/nums.length;
        }

        @Override
        public void run() {
            if (i>nums.length-1) {
                view.removeCallbacks(Counter.this);
                return;
            }
            view.setText(NumUtil.NumberFormat(nums[i++],2));
            view.removeCallbacks(Counter.this);
            view.postDelayed(Counter.this, pertime);
        }
    }
}