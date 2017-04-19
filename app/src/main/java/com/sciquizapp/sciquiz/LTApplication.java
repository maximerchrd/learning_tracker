package com.sciquizapp.sciquiz;

import android.app.Application;

import com.sciquizapp.sciquiz.NetworkCommunication.BluetoothCommunication;
import com.sciquizapp.sciquiz.NetworkCommunication.NetworkCommunication;
import com.sciquizapp.sciquiz.NetworkCommunication.WifiCommunication;

/**
 * Created by maximerichard on 17/02/17.
 */
public class LTApplication extends Application {
    private BluetoothCommunication appBluetooth;
    private WifiCommunication appWifi;
    private NetworkCommunication appNetwork;

    public WifiCommunication getAppWifi() {return appWifi;}
    public BluetoothCommunication getAppBluetooth() {
        return appBluetooth;
    }
    public NetworkCommunication getAppNetwork() {
        return appNetwork;
    }

    public void setAppBluetooth(BluetoothCommunication appBluetooth) {
        this.appBluetooth = appBluetooth;
    }

    public void setAppWifi(WifiCommunication appWifi) {
        this.appWifi = appWifi;
    }

    public void setAppNetwork(NetworkCommunication appNetwork) {
        this.appNetwork = appNetwork;
    }


}
