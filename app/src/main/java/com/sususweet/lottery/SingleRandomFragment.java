package com.sususweet.lottery;

import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatSeekBar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sususweet.lottery.common.FragmentBackHandler;
import com.sususweet.lottery.utils.AppUtils;
import com.sususweet.lottery.utils.BackHandlerHelper;
import com.sususweet.lottery.utils.CardExpandAnimationUtils;
import com.sususweet.lottery.utils.NumAnim;
import com.sususweet.lottery.utils.RandomCounter;
import com.sususweet.lottery.utils.SharedPrefsUtils;
import com.sususweet.lottery.utils.ToastUtils;

import java.util.ArrayList;
import static com.sususweet.lottery.info.SharedPrefsContract.*;

/**
 * A placeholder fragment containing a simple view.
 */
public class SingleRandomFragment extends Fragment implements FragmentBackHandler {
    private RandomCounter counter = null;
    private TextView lotteryResult;
    private TextView lotteryTotal;
    private TextView versionText;
    private ImageView lotteryControl;
    private ImageView lotterySetting;
    private int minCount;
    private int maxCount;

    public SingleRandomFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_single_random, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        lotteryResult = (TextView) view.findViewById(R.id.lottery_result);
        lotteryTotal =  (TextView) view.findViewById(R.id.lottery_total);
        lotteryControl = (ImageView) view.findViewById(R.id.lottery_control);
        lotterySetting = (ImageView) view.findViewById(R.id.lottery_setting);
        versionText = (TextView) view.findViewById(R.id.version);
        init();
    }

    private void init(){
        minCount = SharedPrefsUtils.getInt(KEY_SAMPLE_MIN_CAPACITY, 0, getContext());
        maxCount = SharedPrefsUtils.getInt(KEY_SAMPLE_CAPACITY, 0, getContext());
        lotteryTotal.setText(getString(R.string.lottery_sample_capacity, minCount, maxCount));

        lotteryControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                minCount = SharedPrefsUtils.getInt(KEY_SAMPLE_MIN_CAPACITY, 0, getContext());
                maxCount = SharedPrefsUtils.getInt(KEY_SAMPLE_CAPACITY, 0, getContext());
                if (maxCount <= 0) {
                    ToastUtils.showAfterCancel(getString(R.string.lottery_sample_capacity_not_set), Toast.LENGTH_SHORT, getContext());
                } else{
                    if (NumAnim.getStarted()) {
                        endLottery();
                    }
                    else {
                        startLottery();
                    }
                }

            }
        });

        lotterySetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = LayoutInflater.from(getActivity());
                final LinearLayout layout = (LinearLayout)inflater.inflate(R.layout.dialog_lottery_single_setting, null);
                final AppCompatEditText minEditText = (AppCompatEditText) layout.findViewById(R.id.total_setting_min_text);
                final AppCompatEditText maxEditText = (AppCompatEditText) layout.findViewById(R.id.total_setting_max_text);
                final AppCompatSeekBar randomLevel = (AppCompatSeekBar) layout.findViewById(R.id.lottery_random_level);

                float randomLevelValue = SharedPrefsUtils.getFloat(KEY_RANDOM_LEVEL, DEFAULT_RANDOM_LEVEL, getContext());
                int maxValue = SharedPrefsUtils.getInt(KEY_SAMPLE_CAPACITY, DEFAULT_SAMPLE_CAPACITY, getContext());
                int minValue = SharedPrefsUtils.getInt(KEY_SAMPLE_MIN_CAPACITY, DEFAULT_SAMPLE_MIN_CAPACITY, getContext());

                lotteryTotal.setText(getString(R.string.lottery_sample_capacity, minCount, maxCount));
                /*初始化开始*/
                randomLevel.setProgress((int) (randomLevelValue *100));
                randomLevel.setMax(100);
                maxEditText.setText(String.valueOf(maxValue));
                minEditText.setText(String.valueOf(minValue));

                new AlertDialog.Builder(getActivity()).setTitle(R.string.dialog_capacity_setting_title)
                        .setView(layout)
                        .setPositiveButton(R.string.dialog_ok,  new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String maxInput = maxEditText.getText().toString();
                                String minInput = minEditText.getText().toString();

                                if (maxInput.equals("") || minInput.equals("")) {
                                    ToastUtils.showAfterCancel(getString(R.string.dialog_capacity_setting_no_empty),
                                            Toast.LENGTH_LONG, getContext());
                                }
                                else {
                                    int maxCount,minCount;
                                    try {
                                        maxCount = Integer.parseInt(maxInput);
                                        minCount = Integer.parseInt(minInput);
                                    } catch (NumberFormatException e) {
                                        maxCount = 0;
                                        minCount = 0;
                                    }
                                    if (maxCount == 0) {
                                        ToastUtils.showAfterCancel(getString(R.string.dialog_capacity_setting_invalid), Toast.LENGTH_LONG,getContext());
                                    } else if (maxCount <= minCount){
                                        ToastUtils.showAfterCancel(getString(R.string.dialog_capacity_setting_max_min_invalid), Toast.LENGTH_LONG,getContext());
                                    } else{
                                        ArrayList<Integer> numSelected =new ArrayList<Integer>();
                                        numSelected.clear();
                                        SharedPrefsUtils.putArray(KEY_NUMBER_SELECTED, numSelected, getContext());
                                        SharedPrefsUtils.putInt(KEY_SAMPLE_MIN_CAPACITY, minCount, getContext());
                                        SharedPrefsUtils.putInt(KEY_SAMPLE_CAPACITY, maxCount, getContext());
                                        SharedPrefsUtils.putFloat(KEY_RANDOM_LEVEL,((float) randomLevel.getProgress())/100, getContext());
                                        lotteryTotal.setText(getString(R.string.lottery_sample_capacity,minCount,maxCount));
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

    private void startLottery(){
        CardExpandAnimationUtils.animationIvOpen(lotteryControl);
        counter = NumAnim.startRandom(lotteryResult, minCount, maxCount, 10, getContext());
    }

    private void endLottery(){
        CardExpandAnimationUtils.animationIvClose(lotteryControl);
        NumAnim.endRandom(lotteryResult, counter);
        ArrayList<Integer> numSelected =
                SharedPrefsUtils.getArray(KEY_NUMBER_SELECTED, getContext());
        String totalCountString = lotteryResult.getText().toString();
        try {
            int totalCount = Integer.parseInt(totalCountString);
            numSelected.add(totalCount);
            if (numSelected.size()>= (maxCount - minCount + 1)){
                numSelected.clear();
            }
            SharedPrefsUtils.putArray(KEY_NUMBER_SELECTED, numSelected, getContext());
        } catch (NumberFormatException ignored) {}
    }

    @Override
    public boolean onBackPressed() {
        // 当确认没有子Fragmnt时可以直接return false
        if (NumAnim.getStarted()) {
            endLottery();
            return true;
        }
        else return BackHandlerHelper.handleBackPress(this);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            //相当于Fragment的onResume
        } else {
            if (NumAnim.getStarted()) {
                endLottery();
            }
        }
    }
}
