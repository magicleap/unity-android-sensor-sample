package com.magicleap.sensorplugin;
//Interface that defines the methods for a sensor receiving update events
public interface SensorDataCallback {
    //sensor data update callback
    void onSensorChanged(String sensorData, long timestamp);
    //sensor accuracy update callback
    void onAccuracyChanged(String sensorData);
}
