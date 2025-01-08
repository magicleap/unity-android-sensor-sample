using System.Collections.Generic;
using System.Linq;
using Newtonsoft.Json;
using TMPro;
using UnityEngine;

public class SensorPluginManager : MonoBehaviour
{
    private AndroidJavaClass unityClass;
    private AndroidJavaObject unityActivity;
    private AndroidJavaObject pluginInstance;

    [SerializeField] private TMP_Text pressureSensorLabel;
    [SerializeField] private TMP_Text accelerometerSensorLabel;


    private SensorDataCallbackProxy pressureSensorCallback;
    private SensorDataCallbackProxy accelerometerSensorCallback;

    // Start is called before the first frame update
    void Start()
    {
        InititUnityPlugin("com.magicleap.sensorplugin.MLSensorListener");
    }

    void InititUnityPlugin(string pluginName)
    {
        //logic required for initializing an android plugin
        //you can find more information here
        //https://docs.unity3d.com/Manual/android-plugins-java-code-from-c-sharp.html
        unityClass = new AndroidJavaClass("com.unity3d.player.UnityPlayer");
        unityActivity = unityClass.GetStatic<AndroidJavaObject>("currentActivity");
        pluginInstance = new AndroidJavaObject(pluginName);
        if (pluginInstance != null)
        {
            Debug.Log("Plugin Instance Loaded!");
        }

        //set the unity activity
        pluginInstance.CallStatic("receiveUnityActivity", unityActivity);
        //initialize callbacks
        pressureSensorCallback = new SensorDataCallbackProxy(pressureSensorLabel, "Pressure Value in Hectopascals:");
        accelerometerSensorCallback = new SensorDataCallbackProxy(accelerometerSensorLabel, "Accelerometer values:");
        
        //Get list of all sensors
        string allSensorJson = pluginInstance.Call<string>("getAvailableSensors");
        List<Sensor> sensors = JsonConvert.DeserializeObject<List<Sensor>>(allSensorJson);
        
        //Here you can easily see all available sensors
        foreach (Sensor sensor in sensors)
        {
            Debug.Log(sensor.name);
        }
        
        //start listening to all the sensors you want
        pluginInstance.Call("startSensorListening", sensors.First(x=> x.name.Contains("Compute Pack Pressure Sensor")).name, pressureSensorCallback);
        pluginInstance.Call("startSensorListening", sensors.First(x=> x.name.Contains("Headset Right Accelerometer Sensor")).name, accelerometerSensorCallback);
    }
}

/// <summary>
/// Below are classes used to easily deserialize json data from the android plugin
/// Sensor contains:
/// - name
/// - type
/// - vendor
/// - version
///
/// Sensor List is just an array of the above sensors
/// </summary>
[System.Serializable]
public class Sensor
{
    public string name;
    public int type;
    public string vendor;
    public int version;
}

[System.Serializable]
public class SensorList
{
    public Sensor[] sensors;
}
