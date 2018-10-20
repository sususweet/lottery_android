package com.sususweet.lottery.utils;

import android.content.Context;
import android.widget.TextView;

import com.sususweet.lottery.R;

import java.util.ArrayList;

import static com.sususweet.lottery.info.SharedPrefsContract.DEFAULT_RANDOM_LEVEL;
import static com.sususweet.lottery.info.SharedPrefsContract.KEY_NUMBER_SELECTED;
import static com.sususweet.lottery.info.SharedPrefsContract.KEY_RANDOM_LEVEL;

/*
 * Created by tangyq on 2017/4/17.
 */

public class RandomCounter implements Runnable {

    private final TextView view;
    private long perTime;
    private int min;
    private int max;
    private Context context;
    private ArrayList<Integer> numSelected;
    private ArrayList<Integer> numArraySelected = new ArrayList<Integer>();
    private float showNoneRandomLevel;

    public RandomCounter(TextView view, int min, int max, long time, float randomLevel, Context context) {
        this.view = view;
        this.min = min;
        this.max = max;
        this.perTime = time;
        this.showNoneRandomLevel = randomLevel;
        this.context = context.getApplicationContext();
        numSelected = SharedPrefsUtils.getArray(KEY_NUMBER_SELECTED, context.getApplicationContext());
    }

    @Override
    public void run() {
        int result = (int) (Math.random() * (max - min + 1) + min);
        double showNoneRandom = Math.random();
        /*showNoneRandomLevel为随机数水平，0为完全不随机，即不重复抽样*/
        if (showNoneRandom > showNoneRandomLevel) {
            for (int num : numSelected) {
                if (result == num) {
                    view.postDelayed(RandomCounter.this, perTime);
                    return;
                }
            }
        }
        view.setText(String.format(context.getString(R.string.lottery_result_format), result));
        view.postDelayed(RandomCounter.this, perTime);
    }

    public int generateSingleRandom(){
        int result = (int) (Math.random() * (max - min + 1) + min);
        double showNoneRandom = Math.random();
        /*showNoneRandomLevel为随机数水平，0为完全不随机，即不重复抽样*/
        if (showNoneRandom > showNoneRandomLevel) {
            while(true) {
                boolean reRandom = false;
                for (int num : numArraySelected) {
                    if (result == num) {
                        reRandom = true;
                        break;
                    }
                }
                if (reRandom) {
                    result = (int) (Math.random() * (max - min + 1) + min);
                }else break;
            }
        }
        numArraySelected.add(result);
        return result;
    }
}