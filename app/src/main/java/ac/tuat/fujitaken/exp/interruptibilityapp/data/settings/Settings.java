package ac.tuat.fujitaken.exp.interruptibilityapp.data.settings;

import android.app.Application;
import android.content.Context;
import android.util.Log;

/**
 *
 * Created by hi on 2017/02/28.
 */

public class Settings extends Application {

    private static Settings instance;
    private AppSettings appSettings;
    private DeviceSettings deviceSettings;
    private EventCounter eventCounter;

    @Override
    public void onCreate() {
        Log.d("Info", "Settings.onCreate_1");  //s 追加
        super.onCreate();
        Log.d("Info", "Settings.onCreate_2");  //s 追加

        instance = this;
        instance.settings();
        Log.d("Info", "Settings.onCreate_3e");  //s 追加
    }

    private void settings(){
        appSettings = new AppSettings(getApplicationContext());
        deviceSettings = new DeviceSettings(getApplicationContext());
        eventCounter = new EventCounter(getApplicationContext());
    }

    public static AppSettings getAppSettings() {
        return instance.appSettings;
    }

    public static Context getContext() {
        return instance.getApplicationContext();
    }

    public static DeviceSettings getDeviceSettings() {
        return instance.deviceSettings;
    }

    public static EventCounter getEventCounter() {
        return instance.eventCounter;
    }
}
