package com.sususweet.lottery.info;

/*
 * Created by tangyq on 2017/4/17.
 */

public interface SharedPrefsContract {

    // NOTE: Default value handled in MobileVersionHelper.

    public static final String KEY_SAMPLE_CAPACITY = "pref_key_sample_capacity";
    public static final int DEFAULT_SAMPLE_CAPACITY = 0;

    public static final String KEY_SAMPLE_MIN_CAPACITY = "pref_key_sample_min_capacity";
    public static final int DEFAULT_SAMPLE_MIN_CAPACITY = 0;

    public static final String KEY_SAMPLE_ARRAY_COUNT = "pref_sample_array_count";
    public static final int DEFAULT_SAMPLE_ARRAY_COUNT = 0;

    public static final String KEY_NUMBER_SELECTED = "pref_key_number_selected";
    public static final boolean DEFAULT_NUMBER_SELECTED = false;

    public static final String KEY_RANDOM_LEVEL = "pref_key_random_level";
    public static final float DEFAULT_RANDOM_LEVEL = (float) 0.3;

}
