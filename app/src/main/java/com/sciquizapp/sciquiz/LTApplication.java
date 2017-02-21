package com.sciquizapp.sciquiz;

import android.app.Application;

import com.sciquizapp.sciquiz.NetworkCommunication.BluetoothCommunication;

/**
 * Created by maximerichard on 17/02/17.
 */
public class LTApplication extends Application {
    private BluetoothCommunication appBluetooth;

    public BluetoothCommunication getAppBluetooth() {
        return appBluetooth;
    }

    public void setAppBluetooth(BluetoothCommunication appBluetooth) {
        this.appBluetooth = appBluetooth;
    }


}
