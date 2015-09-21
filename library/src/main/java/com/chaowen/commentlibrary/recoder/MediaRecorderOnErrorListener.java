package com.chaowen.commentlibrary.recoder;


public interface MediaRecorderOnErrorListener extends android.media.MediaRecorder.OnErrorListener {

    public abstract void onError(int i, String s);
}
