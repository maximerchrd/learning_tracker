package com.sciquizapp.sciquiz.NetworkCommunication;

import java.util.ArrayList;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.widget.TextView;

public class NetworkCommunication {
	private Context mContextNetCom;
	private Application mApplication;
	private ArrayList<ArrayList<String>> mNetwork_addresses;
	private WifiCommunication mWifiCom;
	private TextView mTextOut;
	public String mWifiName = "LT_AdHoc";
	public String mWifiPassword = "L3J28#loL";


	public NetworkCommunication(Context arg_context, Application application) {
		mNetwork_addresses = new ArrayList<ArrayList<String>>();
		mContextNetCom = arg_context;
		mWifiCom = new WifiCommunication(arg_context);
		mApplication = application;
	}

	public NetworkCommunication(Context arg_context, Application application, TextView textOut) {
		mNetwork_addresses = new ArrayList<ArrayList<String>>();
		mContextNetCom = arg_context;
		mWifiCom = new WifiCommunication(arg_context);
		mApplication = application;
		mTextOut = textOut;
	}
	/**
	 * method to launch the network of smartphones and 1 laptop communicating using wifi direct and bluetooth
	 */
	public void ConnectToMaster() {
		BluetoothCommunication BTCommunication = new BluetoothCommunication(mContextNetCom, mApplication, mTextOut);
		BTCommunication.BTConnectToMaster();
//		new Thread(new Runnable() {
//			public void run() {
//				if (mWifiCom.scanForWifiName(mWifiName)) {
//					mWifiCom.connectToWifiWithName(mWifiName, mWifiPassword);
//				} else {
//					Log.d("startNetwork", "starting new hotspot");
//					mWifiCom.startAdhocWifi(mWifiName, mWifiPassword);
//				}
//			}
//		}).start();
	}


}
