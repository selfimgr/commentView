package com.chaowen.commentlibrary.emoji;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Rect;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import com.chaowen.commentlibrary.R;
import com.chaowen.commentlibrary.recoder.AudioMediaRecorder;
import com.chaowen.commentlibrary.recoder.MediaRecorderOnErrorListener;
import com.chaowen.commentlibrary.recoder.MediaRecorderOnProgressListener;
import com.chaowen.commentlibrary.recoder.RecordUtils;
import com.chaowen.commentlibrary.util.LogUtil;

import java.io.File;


public class RecordButton extends Button {
    private static final double MAX_RECORD_AUDIO_TIME = 120 * 1000;
    private static final String TAG = "RecordButton";
    private static final int STATE_INIT = 0;
    private static final int STATE_NORMAL = 1;
    private static final int STATE_UP_CANCEL = 2;
    private static final int STATE_TO_SHROT = 3;
    private static int[] ANIM_RESOURCES = {
            R.drawable.ic_chat_recorder_v1,
            R.drawable.ic_chat_recorder_v2,
            R.drawable.ic_chat_recorder_v3,
            R.drawable.ic_chat_recorder_v4,
            R.drawable.ic_chat_recorder_v5,
            R.drawable.ic_chat_recorder_v6,
            R.drawable.ic_chat_recorder_v7
    };
    private long mStartTime;
    private Dialog mTipDialog;
    private AudioMediaRecorder mMediaRecorder;
    private String mRecordFilePath;
    private ObtainDecibelThread mAnimThread;
    private ShowVolumeHandler mAnimHandler;
    private OnFinishedRecordListener mCallback;
    private View mLyNormal, mLyUpToCancel, mLyTooShort;
    private int mState = STATE_INIT;

    private MediaRecorderOnProgressListener onRecordProgressListener = new MediaRecorderOnProgressListener() {

        @Override
        public void onProgress(long progress) {

        }
    };

    private MediaRecorderOnErrorListener onRecordErrorListener = new MediaRecorderOnErrorListener() {

        @Override
        public void onError(MediaRecorder arg0, int arg1, int arg2) {
            handleError();
        }

        @Override
        public void onError(int i, String s) {
            handleError();
        }

        private void handleError() {

        }
    };


    public RecordButton(Context context) {
        this(context, null);
    }

    public RecordButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public RecordButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public RecordButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_recorder, null);
        mLyNormal = view.findViewById(R.id.ly_normal);
        mLyUpToCancel = view.findViewById(R.id.ly_up_to_cancel);
        mLyTooShort = view.findViewById(R.id.ly_too_short);
        mTipDialog = new Dialog(context, R.style.chat_recorder_style);
        mTipDialog.setContentView(view);
        WindowManager.LayoutParams lp = mTipDialog.getWindow().getAttributes();
        lp.gravity = Gravity.CENTER;

        ImageView mAnimView = (ImageView) view.findViewById(R.id.iv_anim);
        mAnimHandler = new ShowVolumeHandler(mAnimView);

        mMediaRecorder = new AudioMediaRecorder();
        mMediaRecorder.setMaxRecordTime(MAX_RECORD_AUDIO_TIME);
        mMediaRecorder.setOnProgressListener(onRecordProgressListener);
        mMediaRecorder.setOnErrorListener(onRecordErrorListener);


    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mState = STATE_NORMAL;
                mStartTime = System.currentTimeMillis();
                // start record
                mTipDialog.show();
                mRecordFilePath = RecordUtils.getNewRecordFile();
                File file = new File(mRecordFilePath);
                File parent = file.getParentFile();
                parent.mkdirs();
                mMediaRecorder.start(mRecordFilePath);

                mAnimThread = new ObtainDecibelThread();
                mAnimThread.start();

                updateDialog();
                setBackgroundResource(R.drawable.btn_chat_voice_pressed);
                return true;
            case MotionEvent.ACTION_UP:
                //finish record
                mAnimThread.exit();
                mMediaRecorder.stop();
                int len = (int) ((System.currentTimeMillis() - mStartTime) / 1000);
                if (len < 3) {
                    mState = STATE_TO_SHROT;
                    updateDialog();
                } else {
                    mTipDialog.dismiss();
                    if (mCallback != null && mState != STATE_UP_CANCEL) {
                        mCallback.onFinishedRecord(mRecordFilePath, len);
                    }
                }
                setText(getResources().getText(R.string.presstalk));
                setBackgroundResource(R.drawable.btn_chat_voice_normal);
                mState = STATE_INIT;
                break;
            case MotionEvent.ACTION_CANCEL:// 当手指移动到view外面，会cancel
                LogUtil.v(TAG, "cancel");
                //cancelRecord();
                mAnimThread.exit();
                mMediaRecorder.stop();
                mTipDialog.dismiss();

                setText(getResources().getText(R.string.presstalk));
                setBackgroundResource(R.drawable.btn_chat_voice_normal);
                mState = STATE_INIT;
                break;
            case MotionEvent.ACTION_MOVE:
                Rect out = new Rect();
                getDrawingRect(out);
                int y = (int) event.getY();
                int x = (int) event.getX();
                int initState = mState;
                if (out.contains(x, y)) {
                    mState = STATE_NORMAL;
                } else {
                    mState = STATE_UP_CANCEL;
                }
                if (initState != mState) {
                    updateDialog();
                }
                return true;
        }
        return super.onTouchEvent(event);
    }

    private void updateDialog() {
        switch (mState) {
            case STATE_TO_SHROT:
                mLyNormal.setVisibility(View.INVISIBLE);
                mLyUpToCancel.setVisibility(View.INVISIBLE);
                mLyTooShort.setVisibility(View.VISIBLE);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mTipDialog.dismiss();
                    }
                }, 500);
                break;
            case STATE_NORMAL:
                mLyNormal.setVisibility(View.VISIBLE);
                mLyUpToCancel.setVisibility(View.INVISIBLE);
                mLyTooShort.setVisibility(View.INVISIBLE);
                setText(getResources().getText(R.string.letoff));
                break;
            case STATE_UP_CANCEL:
                mLyNormal.setVisibility(View.INVISIBLE);
                mLyUpToCancel.setVisibility(View.VISIBLE);
                mLyTooShort.setVisibility(View.INVISIBLE);
                setText(getResources().getText(R.string.cancelsend));
                break;
        }
    }

    public void setRecorderCallback(OnFinishedRecordListener callback) {
        mCallback = callback;
    }

    public interface OnFinishedRecordListener {
        public void onFinishedRecord(String audioPath, int length);
    }

    static class ShowVolumeHandler extends Handler {
        private ImageView animView;

        ShowVolumeHandler(ImageView animView) {
            this.animView = animView;
        }

        @Override
        public void handleMessage(Message msg) {
            animView.setImageResource(ANIM_RESOURCES[msg.what]);
        }
    }

    private class ObtainDecibelThread extends Thread {

        private volatile boolean running = true;

        public void exit() {
            running = false;
        }

        @Override
        public void run() {
            while (running) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (mMediaRecorder == null || !running) {
                    break;
                }
                int amplitude = mMediaRecorder.getMaxAmplitude();
                if (amplitude != 0) {
                    // int f = (int) (10 * Math.log(amplitude) / Math.log(10));
                    double f = 0.8f + ((2.0 * Math.log10(amplitude / 20)) / 10.0) * 9.0f;
                    //TLog.log(TAG, "f:" + f);
                    if (f < 3.5)
                        mAnimHandler.sendEmptyMessage(0);
                    else if (f < 4)
                        mAnimHandler.sendEmptyMessage(1);
                    else if (f < 4.5)
                        mAnimHandler.sendEmptyMessage(2);
                    else if (f < 5.0)
                        mAnimHandler.sendEmptyMessage(3);
                    else if (f < 5.5)
                        mAnimHandler.sendEmptyMessage(4);
                    else if (f < 6)
                        mAnimHandler.sendEmptyMessage(5);
                    else
                        mAnimHandler.sendEmptyMessage(6);
                }
            }
        }
    }
}
