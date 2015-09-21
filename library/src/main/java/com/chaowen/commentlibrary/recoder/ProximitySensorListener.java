package com.chaowen.commentlibrary.recoder;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Message;

public class ProximitySensorListener implements SensorEventListener {

    private Handler handler;
    private Context context;
    private SensorManager sensorManager;
    private float maxRange;
    private boolean hasChange; //
    private Sensor sensor; //

    public ProximitySensorListener(Context context, Handler handler) {
        this.context = context;
        this.handler = handler;
        if (context == null || handler == null) {
            throw new IllegalArgumentException();
        } else {
            init();
            return;
        }
    }

    private void init() {//
        try {
            sensorManager = (SensorManager) context
                    .getSystemService(Context.SENSOR_SERVICE);
            sensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
            maxRange = sensor.getMaximumRange();
            if (maxRange > 10.0F) {
                maxRange = 10.0F;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Error e) {
            e.printStackTrace();
        }
    }

    private void sendMessage() {
        Message message = Message.obtain();
        message.what = 2449;
        handler.dispatchMessage(message);
    }

    public void registerListener() {//
        try {
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME);
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Error e) {
            e.printStackTrace();
        }
    }

    public void unregisterListener() {//
        try {
            sensorManager.unregisterListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Error e) {
            e.printStackTrace();
        }
    }

    public boolean hasChange() {
        return hasChange;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        boolean flag = true;
        if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
            float value = event.values[0];
            boolean v = false;
            if (value < maxRange) {
                v = flag;
            }
            if (hasChange != v) {
                if (value >= maxRange) {
                    flag = false;
                }
                hasChange = flag;
                sendMessage();
            }
        }
    }
}
