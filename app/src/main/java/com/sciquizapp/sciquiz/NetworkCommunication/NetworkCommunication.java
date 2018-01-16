package com.sciquizapp.sciquiz.NetworkCommunication;

import java.util.ArrayList;

import android.app.Application;
import android.content.Context;
import android.widget.TextView;

import com.sciquizapp.sciquiz.database_management.DbHelper;
import com.sciquizapp.sciquiz.LTApplication;

public class NetworkCommunication {
	private Context mContextNetCom;
	private Application mApplication;
	private ArrayList<ArrayList<String>> mNetwork_addresses;
	private WifiCommunication mWifiCom;
	private TextView mTextOut;
	public Boolean connectedThroughBT = false;
	private int network_solution = 0; //0: all devices connected to same wifi router



	public NetworkCommunication(Context arg_context, Application application) {
		mNetwork_addresses = new ArrayList<ArrayList<String>>();
		mContextNetCom = arg_context;
		mWifiCom = new WifiCommunication(arg_context, application);
		mApplication = application;
	}

	public NetworkCommunication(Context arg_context, Application application, TextView textOut) {
		mNetwork_addresses = new ArrayList<ArrayList<String>>();
		mContextNetCom = arg_context;
		mApplication = application;
		mTextOut = textOut;
		mWifiCom = new WifiCommunication(arg_context, application);
		//((LTApplication) mApplication).setAppWifi(mWifiCom);
		((LTApplication) mApplication).setAppNetwork(this);
	}
	/**
	 * method to launch the network of smartphones and 1 laptop communicating using wifi direct and bluetooth
	 */
	public void ConnectToMaster() {
		if (network_solution == 0) {
			String MacAddress = android.provider.Settings.Secure.getString(mContextNetCom.getContentResolver(), "bluetooth_address");
			DbHelper db_for_name = new DbHelper(mContextNetCom);
			String name = db_for_name.getName();

			final String connection = "CONN" + "///" + MacAddress + "///" + name;
			new Thread(new Runnable() {
				public void run() {
					mWifiCom.connectToServer(connection);
				}
			}).start();
		}
	}

	public void sendAnswerToServer(String answer, String question, int id) {
		String MacAddress = android.provider.Settings.Secure.getString(mContextNetCom.getContentResolver(), "bluetooth_address");
		DbHelper db_for_name = new DbHelper(mContextNetCom);
		String name = db_for_name.getName();

		answer = "ANSW0" + "///" + MacAddress + "///" + name + "///" + answer + "///" + question + "///" + String.valueOf(id);
		if (network_solution == 0) {
			mWifiCom.sendAnswerToServer(answer);
		}
	}

}
