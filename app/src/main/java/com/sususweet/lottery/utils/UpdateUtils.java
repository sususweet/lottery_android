package com.sususweet.lottery.utils;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import com.xiaomi.market.sdk.UpdateResponse;
import com.xiaomi.market.sdk.UpdateStatus;
import com.xiaomi.market.sdk.XiaomiUpdateAgent;
import com.xiaomi.market.sdk.XiaomiUpdateListener;

import java.lang.ref.WeakReference;

public class UpdateUtils {
    public static void checkUpdate(boolean triggered, final FragmentActivity activity) {
        final Context applicationContext = activity.getApplicationContext();
        final WeakReference<FragmentActivity> activityWeakReference = new WeakReference<>(activity);
        //XiaomiUpdateAgent.setUpdateAutoPopup(!triggered);
        if (triggered) {
            XiaomiUpdateAgent.setUpdateListener(new XiaomiUpdateListener() {
                @Override
                public void onUpdateReturned(int updateStatus, UpdateResponse updateInfo) {
                    switch (updateStatus) {
                        case UpdateStatus.STATUS_UPDATE:
                            // 有更新， UpdateResponse为本次更新的详细信息
                            // 其中包含更新信息，下载地址，MD5校验信息等，可自行处理下载安装
                            // 如果希望 SDK继续接管下载安装事宜，可调用
                            //  XiaomiUpdateAgent.arrange()

                            // use reflection to get the class of auto popup

                            break;
                        case UpdateStatus.STATUS_NO_UPDATE:

                            // 无更新， UpdateResponse为null
                            break;
                        case UpdateStatus.STATUS_NO_WIFI:
                            // 设置了只在WiFi下更新，且WiFi不可用时， UpdateResponse为null
                            break;
                        case UpdateStatus.STATUS_NO_NET:
                            // 没有网络， UpdateResponse为null

                            break;
                        case UpdateStatus.STATUS_FAILED:
                            // 检查更新与服务器通讯失败，可稍后再试， UpdateResponse为null

                            break;
                        case UpdateStatus.STATUS_LOCAL_APP_FAILED:
                            // 检查更新获取本地安装应用信息失败， UpdateResponse为null
                            break;
                        default:
                            break;
                    }
                }
            });
        }
        XiaomiUpdateAgent.update(applicationContext);
    }

}
