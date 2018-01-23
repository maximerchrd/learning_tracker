package com.sciquizapp.LearningTracker;

import android.app.Application;

import com.sciquizapp.LearningTracker.NetworkCommunication.NetworkCommunication;
import com.sciquizapp.LearningTracker.NetworkCommunication.WifiCommunication;

/**
 * Created by maximerichard on 17/02/17.
 */
public class LTApplication extends Application {
    private WifiCommunication appWifi;
    private NetworkCommunication appNetwork;

    public WifiCommunication getAppWifi() {return appWifi;}
    public NetworkCommunication getAppNetwork() {
        return appNetwork;
    }


    public void setAppWifi(WifiCommunication appWifi) {
        this.appWifi = appWifi;
    }

    public void setAppNetwork(NetworkCommunication appNetwork) {
        this.appNetwork = appNetwork;
    }


}
