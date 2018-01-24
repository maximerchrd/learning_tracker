package com.LearningTracker.LearningTrackerApp;

import android.app.Application;

import com.LearningTracker.LearningTrackerApp.NetworkCommunication.NetworkCommunication;
import com.LearningTracker.LearningTrackerApp.NetworkCommunication.WifiCommunication;

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
