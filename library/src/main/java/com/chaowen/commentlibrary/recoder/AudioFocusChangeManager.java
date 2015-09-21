package com.chaowen.commentlibrary.recoder;

import android.content.Context;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.os.Handler;
import android.os.Message;
import android.util.SparseIntArray;
import android.widget.Toast;

import com.chaowen.commentlibrary.BaseContext;
import com.chaowen.commentlibrary.R;


public class AudioFocusChangeManager {//

    public static ProximitySensorListener sensorListener;
    private static AudioManager audioManger;
    private static Object c;
    private static int d = AudioManager.AUDIOFOCUS_LOSS_TRANSIENT;
    private static SparseIntArray volumeArray = new SparseIntArray(); //
    private static Handler handler = new Handler() {

        public void handleMessage(Message msg) {
            byte mode = 0;
            boolean flag = hasChange();
            if (flag)
                AudioMediaPlayer.getInstance().seekTo(0);
            if (flag)
                mode = 2;
            updateMode(BaseContext.getInstance(), mode);
        }

    };

    static {
        audioManger = (AudioManager) BaseContext.getInstance().getSystemService(
                Context.AUDIO_SERVICE);
    }

    public static void checkVolume() {//
        if (audioManger.getStreamVolume(AudioManager.STREAM_MUSIC) == 0) {
            Toast.makeText(BaseContext.getInstance(), R.string.volumeTips,Toast.LENGTH_SHORT).show();
        }
    }

    public static void updateMode(Context context, int mode) {//
        try {
            if (audioManger.getMode() == 0) {
                volumeArray.put(0,
                        audioManger.getStreamVolume(AudioManager.STREAM_MUSIC));
            }
            audioManger.setMode(mode);
            switch (mode) {
                case AudioManager.MODE_NORMAL:
                    if (volumeArray.indexOfKey(mode) >= 0) {
                        int volume = volumeArray.get(mode);
                        audioManger.setStreamVolume(AudioManager.STREAM_MUSIC,
                                volume, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
                        volumeArray.delete(0);
                    }
                    break;
                case AudioManager.MODE_RINGTONE:
                case AudioManager.MODE_IN_CALL:
                    audioManger.setStreamVolume(AudioManager.STREAM_MUSIC,
                            audioManger
                                    .getStreamMaxVolume(AudioManager.STREAM_MUSIC),
                            AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateToNormalMode() {//
        updateMode(BaseContext.getInstance(), AudioManager.MODE_NORMAL);
    }

    public static int requestAudioFocus() {//
        try {
            c = new OnAudioFocusChangeListener() {

                @Override
                public void onAudioFocusChange(int focusChange) {
                    if (focusChange != d
                            && focusChange != AudioManager.AUDIOFOCUS_GAIN
                            && focusChange == AudioManager.AUDIOFOCUS_LOSS)
                        audioManger
                                .abandonAudioFocus((OnAudioFocusChangeListener) c);
                }
            };// b();
            return audioManger.requestAudioFocus(
                    (OnAudioFocusChangeListener) c, 3, 1);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static void abandonAudioFocus() {
        try {
            if (audioManger != null) {
                audioManger.abandonAudioFocus((OnAudioFocusChangeListener) c);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static boolean registerListener() {
        if (sensorListener == null)
            sensorListener = new ProximitySensorListener(BaseContext.getInstance(),
                    handler);
        if (sensorListener == null) {
            return false;
        } else {
            sensorListener.registerListener();
            return true;
        }
    }

    public static boolean unregisterListener() {//
        if (sensorListener == null) {
            return false;
        } else {
            sensorListener.unregisterListener();
            return true;
        }
    }

    public static boolean hasChange() {
        if (sensorListener == null)
            return false;
        else
            return sensorListener.hasChange();
    }
}
