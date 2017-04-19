package com.sciquizapp.sciquiz.NetworkCommunication;

import java.util.ArrayList;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.widget.TextView;

import com.sciquizapp.sciquiz.DbHelper;

public class NetworkCommunication {
	private Context mContextNetCom;
	private Application mApplication;
	private ArrayList<ArrayList<String>> mNetwork_addresses;
	private WifiCommunication mWifiCom;
	private TextView mTextOut;
	private Boolean connectedThroughBT;
	public String mWifiName = "LT_AdHoc";
	public String mWifiPassword = "L3J28#loL";
	private BluetoothCommunication BTCommunication;



	public NetworkCommunication(Context arg_context, Application application) {
		mNetwork_addresses = new ArrayList<ArrayList<String>>();
		mContextNetCom = arg_context;
		mWifiCom = new WifiCommunication(arg_context, application);
		mApplication = application;
	}

	public NetworkCommunication(Context arg_context, Application application, TextView textOut) {
		mNetwork_addresses = new ArrayList<ArrayList<String>>();
		mContextNetCom = arg_context;
		mWifiCom = new WifiCommunication(arg_context, application);
		mApplication = application;
		mTextOut = textOut;
	}
	/**
	 * method to launch the network of smartphones and 1 laptop communicating using wifi direct and bluetooth
	 */
	public void ConnectToMaster() {
		BTCommunication = new BluetoothCommunication(mContextNetCom, mApplication, mTextOut);
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

	public void sendAnswerToServer(String answer) {
		String MacAddress = android.provider.Settings.Secure.getString(mContextNetCom.getContentResolver(), "bluetooth_address");
		DbHelper db_for_name = new DbHelper(mContextNetCom);
		String name = db_for_name.getName();

		answer = "ANSW0" + "///" + MacAddress + "///" + name + "///" + answer;
		if (connectedThroughBT) {
			BTCommunication.sendAnswerToServer(answer);
		} else {
			mWifiCom.sendAnswerToServer(answer);
		}
	}

	}
