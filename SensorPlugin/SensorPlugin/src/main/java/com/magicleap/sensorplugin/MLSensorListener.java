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
import com.unity3d.player.UnityPlayer;

//To extend UnityPlayerActivity, you have to copy the java class from Unity/Hub/Editor/{UnityVersion}/Editor/Data/PlaybackEngines/AndroidPlayer/Source/com/unity3d/player
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
    //callbackGameObjectName: in order to receive callbacks for sensor updates, you must give a gameObjectName that will be receiving the callback
    //callbackMethodName: the name of the method/function that will be used as your callback in Unity
    public void startSensorListening(String sensorName, String callbackGameObjectName, String callbackFunctionName)
    {
        MLSensor mlSensor;
        try
        {
            mlSensor = MLSensor.forName(sensorName);
        }
        catch(IllegalArgumentException e)
        {
            Log.e(getClass().getName(), "No sensor by " + sensorName + "was found");
            return;
        }

        if(sensorManager == null)
        {
            sensorManager = (SensorManager) unityActivity.getSystemService(Context.SENSOR_SERVICE);
        }

        Sensor requestedSensor = findSensor(mlSensor);

        if(requestedSensor != null)
        {
            if(!allEventListeners.containsKey(sensorName))
            {
                UnitySensorEventListener sensorEventListener = new UnitySensorEventListener(requestedSensor, new UnityCallback(callbackGameObjectName, callbackFunctionName));
                allEventListeners.put(sensorName, sensorEventListener);
                sensorEventListener.startListening(sensorManager);
            }

        }
    }

    public void stopSensorListening(String sensorName)
    {
       MLSensor mlSensor;
       try
       {
           mlSensor = MLSensor.forName(sensorName);
       }
       catch(IllegalArgumentException e)
       {
           Log.e(getClass().getName(), "No sensor by " + sensorName + " was found");
           return;
       }

       if(allEventListeners.containsKey(mlSensor.sensorName))
       {
           UnitySensorEventListener listener = allEventListeners.get(mlSensor.sensorName);
           if (listener != null) {
               listener.stopListening(sensorManager);
           }
           allEventListeners.remove(mlSensor.sensorName);
       }

    }

    private Sensor findSensor(MLSensor mlSensor)
    {
        List<Sensor> allSensorsOfType = sensorManager.getSensorList(Sensor.TYPE_ALL);
        for (Sensor sensor: allSensorsOfType)
        {
            if(sensor.getName().contains(mlSensor.sensorName))
            {
                Log.i(getClass().getName(), "Sensor found by name: " + mlSensor.sensorName);
                return sensor;
            }
        }
        Log.e(getClass().getName(), "No sensor found by name: " + mlSensor.sensorName);
        return null;
    }


    private enum MLSensor
    {
        lightSensor("Ambient Light Sensor"),
        headsetPressureSensor("Headset Pressure Sensor"),
        computePackPressureSensor("Compute Pack Pressure Sensor"),
        computePackGyroscopeSensor("Compute Pack Gyroscope Sensor"),
        headsetLeftGyroscopeSensor("Headset Left Gyroscope Sensor"),
        headsetRightGyroscopeSensor("Headset Right Gyroscope Sensor"),
        computePackAccelerometerSensor("Compute Pack Accelerometer Sensor"),
        headsetLeftAccelerometerSensor("Headset Left Accelerometer Sensor"),
        headsetRightAccelerometerSensor("Headset Right Accelerometer Sensor"),
        headsetRightUncalibratedMagnetometerSensor("Headset Right Uncalibrated Magnetometer Sensor"),
        headsetLeftUncalibratedMagnetometerSensor("Headset Left Uncalibrated Magnetometer Sensor"),
        headsetRightCalibratedMagnetometerSensor("Headset Right Calibrated Magnetometer Sensor"),
        headsetLeftCalibratedMagnetometerSensor("Headset Left Calibrated Magnetometer Sensor")
        ;

        final String sensorName;

        MLSensor(String sensorName)
        {
            this.sensorName = sensorName;
        }

        private static final MLSensor[] copyOfValues = values();

        public static MLSensor forName(String name) {
            for (MLSensor value : copyOfValues) {
                if (value.sensorName.equals(name)) {
                    return value;
                }
            }
            return null;
        }
    }

    private static class UnityCallback {
        private final String gameObjectName;
        private final String methodName;

        UnityCallback(String gameObjectName, String methodName) {
            this.gameObjectName = gameObjectName;
            this.methodName = methodName;
            Log.i(getClass().getName(), "Callback created for " + gameObjectName + " " + methodName);
        }

        public String getGameObjectName() {
            return gameObjectName;
        }

        public String getMethodName() {
            return methodName;
        }
    }

    private static class UnitySensorEventListener implements SensorEventListener
    {
        Sensor sensor;

        private UnityCallback unityCallback = null;

        public UnitySensorEventListener(Sensor requestedSensor, UnityCallback callback)
        {
            Log.i(getClass().getName(), "Event listener created for " + requestedSensor.getName());
            sensor = requestedSensor;
            unityCallback = callback;
        }

        @Override
        public void onSensorChanged(SensorEvent event)
        {
            if(unityCallback != null)
            {
                UnityPlayer.UnitySendMessage(unityCallback.getGameObjectName(), unityCallback.getMethodName(), Arrays.toString(event.values));
            }
            else
            {
                Log.e(getClass().getName(), "Unity callback was null");
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy)
        {

        }

        public void startListening(SensorManager sensorManager)
        {
            Log.i(getClass().getName(), "registering sensor: " + sensor.getName());
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        }

        public void stopListening(SensorManager sensorManager)
        {
            sensorManager.unregisterListener(this, sensor);
        }
    }

}
