using TMPro;
using UnityEngine;

/// <summary>
/// Class used as a callback for all sensors via AndroidJavaProxy
/// You can find more information about AndroidJavaProxy and Unity here
/// https://docs.unity3d.com/6000.0/Documentation/ScriptReference/AndroidJavaProxy.html
/// </summary>
public class SensorDataCallbackProxy : AndroidJavaProxy
{
    //each sensor will update label text, so here we cache the label
    private TMP_Text sensorLabel;
    //beginning of each message for the sensor
    private string messageBeginning;
    
    //constructor
    public SensorDataCallbackProxy(TMP_Text label, string message) : base("com.magicleap.sensorplugin.SensorDataCallback")
    {
        sensorLabel = label;
        messageBeginning = message;
    }

    //callback received when sensor values update
    public void onSensorChanged(string sensorData, long timestamp)
    {
        sensorLabel.text = $"{messageBeginning}  {sensorData} at time {timestamp}";
    }

    //callback received when the accuracy of a sensor updates
    public void onAccuracyChanged(string sensorData)
    {
        Debug.Log("Accuracy changed: " + sensorData);
    }
}
