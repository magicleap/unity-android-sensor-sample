package com.magicleap.sensorplugin;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

public class MLSensorListener
{
    private static Activity unityActivity;
    private SensorManager sensorManager = null;
    private Map<String, UnitySensorEventListener> allEventListeners = new HashMap<>();

    public static void receiveUnityActivity(Activity _activity)
    {
        unityActivity = _activity;
    }

    //sensorName = name of the sensor you wish to listen to (Must be a sensor name valid for ML2 Device)
    //SensorDataCallback = utilizing Unity AndroidJavaProxy, please create and pass in a sensordatacallback instance
    public void startSensorListening(String requestedSensorName, SensorDataCallback sensorCallback)
    {
        Sensor requestedSensor = findSensorByName(requestedSensorName);

        if(requestedSensor != null)
        {
            if(!allEventListeners.containsKey(requestedSensorName))
            {
                UnitySensorEventListener sensorEventListener = new UnitySensorEventListener(requestedSensor, sensorCallback);
                allEventListeners.put(requestedSensorName, sensorEventListener);
                sensorEventListener.startListening(sensorManager);
            }

        }
    }

    //Get all sensors in JSON format for ease of parsing
    public String getAvailableSensors()
    {
        if(sensorManager == null)
        {
            sensorManager = (SensorManager) unityActivity.getSystemService(Context.SENSOR_SERVICE);
        }

        //get all available sensors
        List<Sensor> allSensors = sensorManager.getSensorList(Sensor.TYPE_ALL);

        //create json entry for each sensor
        JSONArray jsonArray = new JSONArray();
        for (Sensor sensor : allSensors) {
            try {
                JSONObject sensorJson = new JSONObject();
                sensorJson.put("name", sensor.getName());
                sensorJson.put("type", sensor.getType());
                sensorJson.put("vendor", sensor.getVendor());
                sensorJson.put("version", sensor.getVersion());
                jsonArray.put(sensorJson);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //return json as string for parsing in Unity
        return jsonArray.toString();
    }

    //function to stop listening to a sensor immediately
    //requestedSensorName = name of the sensor you wish to stop listening to
    public void stopSensorListening(String requestedSensorName)
    {
        Sensor requestedSensor = findSensorByName(requestedSensorName);

        if(requestedSensor != null && allEventListeners.containsKey(requestedSensorName))
        {
            UnitySensorEventListener listener = allEventListeners.get(requestedSensorName);
            if (listener != null) {
                listener.stopListening(sensorManager);
            }
            allEventListeners.remove(requestedSensorName);
        }

    }

    //helper function to find a sensor by its name
    //returns the sensor if found
    //returns null if no sensor by the passed name is found
    private Sensor findSensorByName(String sensorName)
    {
        if(sensorManager == null)
        {
            sensorManager = (SensorManager) unityActivity.getSystemService(Context.SENSOR_SERVICE);
        }

        List<Sensor> allSensorsOfType = sensorManager.getSensorList(Sensor.TYPE_ALL);
        for (Sensor sensor: allSensorsOfType)
        {
            if(sensor.getName().contains(sensorName))
            {
                Log.i(getClass().getName(), "Sensor found by name: " + sensorName);
                return sensor;
            }
        }
        Log.e(getClass().getName(), "No sensor found by name: " + sensorName);
        return null;
    }

    //class that contains all logic relevant to a sensor receiving listener events
    private static class UnitySensorEventListener implements SensorEventListener
    {
        Sensor sensor;

        private SensorDataCallback sensorCallback = null;

        //constructor for UnitySensorEventListener
        //requestedSensor = the sensor that will be listened to
        //callback = the SensorDataCallback to send when a sensor receives update events
        public UnitySensorEventListener(Sensor requestedSensor, SensorDataCallback callback)
        {
            Log.i(getClass().getName(), "Event listener created for " + requestedSensor.getName());
            sensor = requestedSensor;
            sensorCallback = callback;
        }

        //callback for when a sensor is updated
        @Override
        public void onSensorChanged(SensorEvent event)
        {
            if(sensorCallback != null)
            {
                //get the timestamp of the update
                long timestamp = event.timestamp;
                //send callback
                sensorCallback.onSensorChanged(Arrays.toString(event.values), timestamp);
            }
            else
            {
                Log.e(getClass().getName(), "Unity callback was null");
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy)
        {
            if(sensorCallback != null)
            {
                //format sensor data with name of sensor and new accuracy value
                String sensorData = sensor.getName() + " " + accuracy;
                //send callback
                sensorCallback.onAccuracyChanged(sensorData);
            }
            else
            {
                Log.e(getClass().getName(), "Unity callback was null");
            }
        }

        //method to register a sensor listener
        public void startListening(SensorManager sensorManager)
        {
            Log.i(getClass().getName(), "registering sensor: " + sensor.getName());
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        }

        //method to unregister a sensor listener
        public void stopListening(SensorManager sensorManager)
        {
            sensorManager.unregisterListener(this, sensor);
        }
    }

}
