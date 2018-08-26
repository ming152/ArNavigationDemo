package com.example.arnavigationdemo.manager;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * Created by ming on 2018/8/24.
 * 通过传感器获取手机旋转角度
 */

public class OrientationManager implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor magneticSensor, accelerometerSensor;
    private float[] values, r, gravity, geomagnetic;
    private OrientationChangeListener listener;

    public OrientationManager(Context context, OrientationChangeListener listener){
        this.listener = listener;
        /**
         * 初始化传感器
         * */
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        //获取Sensor
        magneticSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        //初始化数组
        values = new float[3];//用来保存最终的结果
        gravity = new float[3];//用来保存加速度传感器的值
        r = new float[9];//
        geomagnetic = new float[3];//用来保存地磁传感器的值
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            geomagnetic = event.values;
        }
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            gravity = event.values;
            getOritation();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    /**
     * 获取手机旋转角度
     */
    public void getOritation() {
        // r从这里返回
        SensorManager.getRotationMatrix(r, null, gravity, geomagnetic);
        //values从这里返回
        SensorManager.getOrientation(r, values);
        //提取数据
        double degreeZ = Math.toDegrees(values[0]);
        double degreeX = Math.toDegrees(values[1]);
        double degreeY = Math.toDegrees(values[2]);
        if(listener != null){
            listener.onOrientationChange(degreeX, degreeY, degreeZ);
        }
    }

    /**
     * 注册传感器监听
     */
    public void start(){
        sensorManager.registerListener(this, magneticSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    /**
     * 取消注册传感器监听
     */
    public void stop(){
        sensorManager.unregisterListener(this);
    }

    public interface OrientationChangeListener{
        void onOrientationChange(double degreeX, double degreeY, double degreeZ);
    }
}
