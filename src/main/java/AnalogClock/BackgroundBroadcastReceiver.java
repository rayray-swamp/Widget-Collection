package AnalogClock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BackgroundBroadcastReceiver extends BroadcastReceiver {

    BackgroundBroadcastReceiverListener backgroundBroadcastReceiverListener;

    public void setBackgroundBroadcastReceiverListener(BackgroundBroadcastReceiverListener backgroundBroadcastReceiverListener){
        this.backgroundBroadcastReceiverListener=backgroundBroadcastReceiverListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("recive","get!");
        if (backgroundBroadcastReceiverListener==null)
            return;
        String action= intent.getAction();
        if(action.equals(Intent.ACTION_SCREEN_ON)){
            //画面ON時の処理
            backgroundBroadcastReceiverListener.onScreenOn();
        }else if(action.equals(Intent.ACTION_SCREEN_OFF)){
            //画面OFF時の処理
            backgroundBroadcastReceiverListener.onScreenOff();
        }else if(action.equals(Intent.ACTION_USER_PRESENT)){
            //スクリーンロック解除時の処理
            backgroundBroadcastReceiverListener.onUserPresent();
        }
    }
    public interface BackgroundBroadcastReceiverListener {
        void onScreenOn();
        void onScreenOff();
        void onUserPresent();
    }
}
