package com.sciquizapp.sciquiz;

import android.app.Application;

import com.sciquizapp.sciquiz.NetworkCommunication.BluetoothCommunication;
import com.sciquizapp.sciquiz.NetworkCommunication.WifiCommunication;

/**
 * Created by maximerichard on 17/02/17.
 */
public class LTApplication extends Application {
    private BluetoothCommunication appBluetooth;
    private WifiCommunication appWifi;
    public WifiCommunication getAppWifi() {return appWifi;}

    public BluetoothCommunication getAppBluetooth() {
        return appBluetooth;
    }

    public void setAppBluetooth(BluetoothCommunication appBluetooth) {
        this.appBluetooth = appBluetooth;
    }

    public void setAppWifi(WifiCommunication appWifi) {
        this.appWifi = appWifi;
    }


}
