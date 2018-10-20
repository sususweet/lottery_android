package com.sususweet.lottery;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatSeekBar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.sususweet.lottery.common.FragmentBackHandler;
import com.sususweet.lottery.utils.AppUtils;
import com.sususweet.lottery.utils.BackHandlerHelper;
import com.sususweet.lottery.utils.NumAnim;
import com.sususweet.lottery.utils.RandomCounter;
import com.sususweet.lottery.utils.SharedPrefsUtils;
import com.sususweet.lottery.utils.ToastUtils;

import java.util.ArrayList;

import static com.sususweet.lottery.info.SharedPrefsContract.*;
import static java.lang.System.exit;

/**
 * A placeholder fragment containing a simple view.
 */
public class MultiRandomFragment extends Fragment implements FragmentBackHandler {
    private TextView lotteryResult;
    private TextView lotteryTotal;
    private TextView versionText;
    private ImageView lotteryControl;
    private ImageView lotterySetting;
    private TableLayout lotteryResultTable;
    private int minCount;
    private int maxCount;
    private int countValue;
    final int MSG_SUCCESS = 0;
    private float showNoneRandomLevel;
    private Thread mThread = null;
    private boolean isStarted = false;

    public MultiRandomFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_multi_random, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        lotteryResult = (TextView) view.findViewById(R.id.lottery_result);
        lotteryTotal = (TextView) view.findViewById(R.id.lottery_total);
        lotteryControl = (ImageView) view.findViewById(R.id.lottery_control);
        lotterySetting = (ImageView) view.findViewById(R.id.lottery_setting);
        versionText = (TextView) view.findViewById(R.id.version);
        lotteryResultTable = (TableLayout) view.findViewById(R.id.multi_random_table);
        init();
    }

    private void init() {
        minCount = SharedPrefsUtils.getInt(KEY_SAMPLE_MIN_CAPACITY, DEFAULT_SAMPLE_MIN_CAPACITY, getContext());
        maxCount = SharedPrefsUtils.getInt(KEY_SAMPLE_CAPACITY, DEFAULT_SAMPLE_CAPACITY, getContext());
        countValue = SharedPrefsUtils.getInt(KEY_SAMPLE_ARRAY_COUNT, DEFAULT_SAMPLE_ARRAY_COUNT, getContext());

        lotteryTotal.setText(getString(R.string.lottery_sample_capacity_count, minCount, maxCount, countValue));
        lotteryControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                minCount = SharedPrefsUtils.getInt(KEY_SAMPLE_MIN_CAPACITY, DEFAULT_SAMPLE_MIN_CAPACITY, getContext());
                maxCount = SharedPrefsUtils.getInt(KEY_SAMPLE_CAPACITY, DEFAULT_SAMPLE_CAPACITY, getContext());
                countValue = SharedPrefsUtils.getInt(KEY_SAMPLE_ARRAY_COUNT, DEFAULT_SAMPLE_ARRAY_COUNT, getContext());

                if (maxCount <= 0) {
                    ToastUtils.showAfterCancel(getString(R.string.lottery_sample_capacity_not_set), Toast.LENGTH_SHORT, getContext());
                } else if (countValue <= 0) {
                    ToastUtils.showAfterCancel(getString(R.string.dialog_array_count_setting_no_empty), Toast.LENGTH_SHORT, getContext());
                } else {
                    if (!isStarted) {
                        startLottery();
                    }else{
                        isStarted = false;
                    }
                }

            }
        });

        lotterySetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = LayoutInflater.from(getActivity());
                final LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.dialog_lottery_multi_setting, null);
                final AppCompatEditText minEditText = (AppCompatEditText) layout.findViewById(R.id.total_setting_min_text);
                final AppCompatEditText maxEditText = (AppCompatEditText) layout.findViewById(R.id.total_setting_max_text);
                final AppCompatEditText randomCount = (AppCompatEditText) layout.findViewById(R.id.total_setting_count_text);
                final AppCompatSeekBar randomLevel = (AppCompatSeekBar) layout.findViewById(R.id.lottery_random_level);

                int maxValue = SharedPrefsUtils.getInt(KEY_SAMPLE_CAPACITY, DEFAULT_SAMPLE_CAPACITY, getContext());
                int minValue = SharedPrefsUtils.getInt(KEY_SAMPLE_MIN_CAPACITY, DEFAULT_SAMPLE_MIN_CAPACITY, getContext());
                int countValue = SharedPrefsUtils.getInt(KEY_SAMPLE_ARRAY_COUNT, DEFAULT_SAMPLE_ARRAY_COUNT, getContext());
                float randomLevelValue = SharedPrefsUtils.getFloat(KEY_RANDOM_LEVEL, DEFAULT_RANDOM_LEVEL, getContext());

                lotteryTotal.setText(getString(R.string.lottery_sample_capacity_count, minCount, maxCount, countValue));
                /*初始化开始*/
                randomLevel.setProgress((int) (randomLevelValue * 100));
                randomLevel.setMax(100);
                randomCount.setText(String.valueOf(countValue));
                maxEditText.setText(String.valueOf(maxValue));
                minEditText.setText(String.valueOf(minValue));

                new AlertDialog.Builder(getActivity()).setTitle(R.string.dialog_capacity_setting_title)
                        .setView(layout)
                        .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String maxInput = maxEditText.getText().toString();
                                String minInput = minEditText.getText().toString();
                                String randomCountInput = randomCount.getText().toString();

                                if (maxInput.equals("") || minInput.equals("")) {
                                    ToastUtils.showAfterCancel(getString(R.string.dialog_capacity_setting_no_empty),
                                            Toast.LENGTH_LONG, getContext());
                                } else if (randomCountInput.equals("")) {
                                    ToastUtils.showAfterCancel(getString(R.string.dialog_array_count_setting_no_empty),
                                            Toast.LENGTH_LONG, getContext());
                                } else {
                                    int maxCount, minCount, totalCount;
                                    try {
                                        maxCount = Integer.parseInt(maxInput);
                                        minCount = Integer.parseInt(minInput);
                                        totalCount = Integer.parseInt(randomCountInput);
                                    } catch (NumberFormatException e) {
                                        maxCount = 0;
                                        minCount = 0;
                                        totalCount = 0;
                                    }
                                    if (maxCount == 0) {
                                        ToastUtils.showAfterCancel(getString(R.string.dialog_capacity_setting_invalid), Toast.LENGTH_LONG, getContext());
                                    } else if (maxCount <= minCount) {
                                        ToastUtils.showAfterCancel(getString(R.string.dialog_capacity_setting_max_min_invalid), Toast.LENGTH_LONG, getContext());
                                    } else if (totalCount == 0) {
                                        ToastUtils.showAfterCancel(getString(R.string.dialog_array_count_setting_no_empty),
                                                Toast.LENGTH_LONG, getContext());
                                    } else {
                                        ArrayList<Integer> numSelected = new ArrayList<Integer>();
                                        numSelected.clear();
                                        SharedPrefsUtils.putArray(KEY_NUMBER_SELECTED, numSelected, getContext());
                                        SharedPrefsUtils.putInt(KEY_SAMPLE_MIN_CAPACITY, minCount, getContext());
                                        SharedPrefsUtils.putInt(KEY_SAMPLE_CAPACITY, maxCount, getContext());
                                        SharedPrefsUtils.putInt(KEY_SAMPLE_ARRAY_COUNT, totalCount, getContext());
                                        SharedPrefsUtils.putFloat(KEY_RANDOM_LEVEL, ((float) randomLevel.getProgress()) / 100, getContext());
                                        lotteryTotal.setText(getString(R.string.lottery_sample_capacity_count, minCount, maxCount, totalCount));
                                    }
                                }
                            }
                        })
                        .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
            }
        });

        String versionName = AppUtils.getPackageInfo(getContext()).versionName;
        String channel = BuildConfig.BUILD_TYPE;
        String version;
        if (TextUtils.isEmpty(channel) || channel.equals("release")) {
            version = getString(R.string.about_version, versionName);
        } else {
            version = getString(R.string.about_version_channel, versionName, channel);
        }
        versionText.setText(version);
    }

    private void startLottery() {
        ToastUtils.showAfterCancel(getString(R.string.dialog_array_random_start),
                Toast.LENGTH_LONG, getContext());

        lotteryResultTable.removeAllViews();
        showNoneRandomLevel = SharedPrefsUtils.getFloat(KEY_RANDOM_LEVEL, DEFAULT_RANDOM_LEVEL, getContext());
        if (maxCount - minCount + 1 < countValue) {
            showNoneRandomLevel = 1;
            ToastUtils.showAfterCancel(getString(R.string.lottery_random_level_reset),
                    Toast.LENGTH_LONG, getContext());
        }
        isStarted = true;
        mThread = new Thread(multiRandomRunner);
        mThread.start();

       /* MultiRandomRunner runner=new MultiRandomRunner();
        //r.run();//这是方法调用，而不是开启一个线程
        Thread t=new Thread(runner);//调用了Thread(Runnable target)方法。且父类对象变量指向子类对象。
        t.start();*/
    }

    private Handler mHandler = new Handler() {
        public void handleMessage (Message msg) {//此方法在ui线程运行
            switch(msg.what) {
                case MSG_SUCCESS:
                    lotteryResultTable.addView((View) msg.obj);
                    break;
            }
        }
    };

    Runnable multiRandomRunner=new Runnable(){
        @Override
        public void run() {
            int currentCount = 0;
            TableRow random_array_row = new TableRow(getContext());
            RandomCounter randomCounter = new RandomCounter(lotteryResult, minCount, maxCount, 10, showNoneRandomLevel, getContext());
            while (currentCount < countValue && isStarted) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                int result = randomCounter.generateSingleRandom();
                currentCount++;
                LinearLayout mItemLayout = (LinearLayout) LayoutInflater
                        .from(random_array_row.getContext()).inflate(R.layout.multi_random_table_item,
                                random_array_row, false);
                TextView tableItemValue = (TextView) mItemLayout.findViewById(R.id.multi_random_table_item_value);
                tableItemValue.setText(String.valueOf(result));
                random_array_row.addView(mItemLayout);

                if (currentCount % 4 == 0) {
                    mHandler.obtainMessage(MSG_SUCCESS, random_array_row).sendToTarget();
                    random_array_row = new TableRow(getContext());
                }
            }
            mHandler.obtainMessage(MSG_SUCCESS, random_array_row).sendToTarget();
            isStarted = false;
        }

    };
    /*private class MultiRandomRunner implements Runnable{
        @Override
        public void run(){

        }

    }*/

    @Override
    public boolean onBackPressed() {
        // 当确认没有子Fragmnt时可以直接return false
        if (isStarted) {
            isStarted = false;
            return true;
        } else return BackHandlerHelper.handleBackPress(this);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            //相当于Fragment的onResume
        } else {
            isStarted = false;
        }
    }
}
