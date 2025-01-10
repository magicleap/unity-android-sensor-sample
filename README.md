# Android Sensor Plugin for Unity

## Overview
This repository demonstrates the integration of an Android plugin with Unity, focusing on accessing device sensors. The Android plugin provides sensor updates while the Unity project receives these updates and displays the data in the UI to the user.

## Projects
### Android Plugin Project
#### Requirements:
- Android Studio Ladybug or higher

This project demonstrates:

- How to create an Android plugin for Unity.
- How to access device sensors and receive updates when sensor values change.
- How to enable a plugin to support AndroidJavaProxy callbacks in Unity

### Unity Example Project
#### Requirements:
- Unity 2022.3.22 or higher
- Android build support 

This project illustrates:

- How to integrate and use an Android plugin.
- How sensor data from the Android plugin can be consumed in Unity.
- How to utilize AndroidJavaProxy as a callback for sensor updates.

## Getting Started
### Cloning the Repository
```bash
git clone https://github.com/magicleap/AndroidSensorPlugin.git 
```

### Setting Up the Android Plugin Project
- Open the SensorPlugin folder in Android Studio.
- Optional: build the plugin and generate the .aar file via Build > Make Module 'android-sensor-plugin'
- You can find the generated .aar file at path `SensorPlugin/build/outputs/aar`
### Setting Up the Unity Project
- Open the `Unity Project` folder in Unity.
- Follow the example scene and scripts to see the plugin in action.

## Script Overview

### Android Plugin Project
#### MLSensorListener
Contains most of the logic that makes up the Android Plugin.
- `public static void receiveUnityActivity(Activity _activity)` is called from Unity to pass the current activity to the plugin. This is needed for communication from the plugin to Unity.
- `public void startSensorListening(String requestedSensorName, SensorDataCallback sensorCallback)` is called from Unity to register and begin listening to a specific sensor.
- `public String getAvailableSensors()` is used to get a list of all currently available sensors from the device
- `public void stopSensorListening(String requestedSensorName)` is called at will from Unity to stop listening to a sensor.

#### SensorDataCallback
An interface that defines methods that can be used as callbacks for sensor value updates.
This enables the use of AndroidJavaProxy in the Unity project as a callback.
- `void onSensorChanged(String sensorData, long timestamp)` is called when sensor values change
- `void onAccuracyChanged(String sensorData)` is called when the accuracy of a sensor changes.

### Example Unity Project
#### Sensor Plugin Manager
Class that contains the core logic of initializing and using the android plugin.
- `void InititUnityPlugin(string pluginName)` contains all necessary logic to initialize and interact with the android plugin. 

#### Sensor Data Callback Proxy
Class that is used as a callback for the Android Plugin.
The class is made up of a constructor and 2 functions that share the same signature as the functions from `SensorDataCallback` in the Android Plugin Project

## License 
Put license info here