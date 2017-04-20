package com.sciquizapp.sciquiz.NetworkCommunication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.sciquizapp.sciquiz.DataConversion;
import com.sciquizapp.sciquiz.LTApplication;
import com.sciquizapp.sciquiz.Question;
import com.sciquizapp.sciquiz.R;
import com.sciquizapp.sciquiz.SingleQuestionActivity;
import com.sciquizapp.sciquiz.WifiAccessManager;

import android.app.Activity;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class WifiCommunication {
	private WifiManager mWifi;
	private Context mContextWifCom;
	private Application mApplication;
	private OutputStream mOutputStream = null;
	private InputStream mInputStream = null;
	private Vector<OutputStream> outputStreamVector = null;
	private Vector<InputStream> inputStreamVector = null;
	private int current = 0;
	private int bytes_read = 0;
	List<android.net.wifi.ScanResult> mScanResults = new ArrayList<android.net.wifi.ScanResult>();
	BroadcastReceiver scanningreceiver;

	public WifiCommunication(Context arg_context, Application arg_application) {
		mContextWifCom = arg_context;
		mWifi = (WifiManager) mContextWifCom.getSystemService(Context.WIFI_SERVICE);
		mApplication = arg_application;
	}

	private void startServerSocket() {
		try {
			Log.v("startServerSocket: ","beginning");
			Boolean end = false;
			ServerSocket ss = new ServerSocket(8080);
			outputStreamVector = new Vector<OutputStream>();
			inputStreamVector = new Vector<InputStream>();
			while(!end){
				//Server is waiting for client here, if needed
				Socket s = ss.accept();
				outputStreamVector.add(s.getOutputStream());
				inputStreamVector.add(s.getInputStream());
				BufferedReader input = new BufferedReader(new InputStreamReader(s.getInputStream()));
				PrintWriter output = new PrintWriter(s.getOutputStream(),true); //Autoflush
				String st = input.readLine();
				Log.d("Tcp Example", "From client: "+st);
				output.println("Good bye and thanks for all the fish :)");
				s.close();
				if (false){ end = true; }
			}
			ss.close();


		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void connectToServer() {
		try {
			Log.v("connectToServer", "beginning");
			Socket s = new Socket("192.168.43.92",8080);

			//outgoing stream redirect to socket
			mOutputStream = s.getOutputStream();
			mInputStream = s.getInputStream();
			//Close connection
			//s.close();


		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * method that scans for wifi network and check if one of them is named wifiName
	 * @param wifiName
	 * @return true if the wifi name was detected, false if not
	 */
	public Boolean scanForWifiName(String wifiName) {
		//start wifi if not yet done
		if (mWifi.isWifiEnabled() == false) {
			mWifi.setWifiEnabled(true);
		}

		//scan for wifi networks and get the list
		mContextWifCom.registerReceiver(scanningreceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context c, Intent intent) 
			{
				mScanResults = mWifi.getScanResults();
			}
		}, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

		//start the scan and wait so that mScanResults isnt empty
		mWifi.startScan();
		try {
			Thread.sleep(6000);
			mContextWifCom.unregisterReceiver(scanningreceiver);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//get the names of the wifi networks
		List<String> ScanResultNames = new ArrayList<String>();
		for (int i = 0; i < mScanResults.size(); i++) {
			ScanResultNames.add(mScanResults.get(i).SSID);
		}

		if (ScanResultNames.contains(wifiName)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * method to start an adhoc wifi
	 * @param wifiName
	 * @param wifiPassword
     */
	public void startAdhocWifi(String wifiName, String wifiPassword) {
		//turn off the wifi if it is on
		new Thread(new Runnable() {
			public void run() {
				if (mWifi.isWifiEnabled()) {
					mWifi.setWifiEnabled(false);
				}
			}
		}).start();

		//wait for the wifi to be turned off, otherwise hotspot cannot be turned on. 200 ms work on huawei Y560/L01
		try {
			Thread.sleep(300);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//setup the ad hoc wifi configuration
		WifiConfiguration wifi_configuration = new WifiConfiguration();
		wifi_configuration.SSID = wifiName;
		wifi_configuration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
		wifi_configuration.preSharedKey = wifiPassword;
//		wifi_configuration.hiddenSSID = true;
//		wifi_configuration.status = WifiConfiguration.Status.DISABLED;     
//		wifi_configuration.priority = 40;
//		wifi_configuration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
//		wifi_configuration.allowedProtocols.set(WifiConfiguration.Protocol.RSN); 
//		wifi_configuration.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
//		wifi_configuration.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
//		wifi_configuration.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
//		wifi_configuration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
//		wifi_configuration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
//		wifi_configuration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
//		wifi_configuration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
//
//		wifi_configuration.wepKeys[0] = "\"aaabbb1234\""; //This is the WEP Password
//		wifi_configuration.wepTxKeyIndex = 0;
		mWifi.addNetwork(wifi_configuration);
		try {
			mWifi.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class).invoke(mWifi, wifi_configuration, true);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		startServerSocket();
	}

	/**
	 * method used to connect to an open wifi network
	 * @param wifiName
	 * 
	 */
	public void connectToWifiWithName (String wifiName, String wifiPassword) {
		//setup the wifi configuration
		WifiConfiguration wifi_configuration = new WifiConfiguration();
		wifi_configuration.SSID = "\"" +  wifiName + "\"";
//		wifi_configuration.hiddenSSID = true;
//		wifi_configuration.status = WifiConfiguration.Status.DISABLED;     
//		wifi_configuration.priority = 40;
//		wifi_configuration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_EAP);
		wifi_configuration.preSharedKey = "\""+ "1p4W159#" +"\"";
//		wifi_configuration.allowedProtocols.set(WifiConfiguration.Protocol.RSN); 
//		wifi_configuration.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
//		wifi_configuration.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
//		wifi_configuration.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
//		wifi_configuration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
//		wifi_configuration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
//		wifi_configuration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
//		wifi_configuration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
//
//		wifi_configuration.wepKeys[0] = "\"aaabbb1234\""; //This is the WEP Password
//		wifi_configuration.wepTxKeyIndex = 0;

		int networkID = mWifi.addNetwork(wifi_configuration);
		mWifi.disconnect();
		mWifi.enableNetwork(networkID, true);
		mWifi.saveConfiguration();
		mWifi.reconnect();               

	}

	public void sendAnswerToServer(String answer) {
		byte[] ansBuffer = answer.getBytes();
		try {
			mOutputStream.write(ansBuffer, 0, ansBuffer.length);
			Log.d("answer buffer length: ", String.valueOf(ansBuffer.length));
			mOutputStream.flush();
		} catch (IOException e) {
			String msg = "In sendAnswerToServer() and an exception occurred during write: " + e.getMessage();
			Log.e("Fatal Error", msg);
		}
		answer = "";
	}

	public void forwardQuestionToClient (byte[] whole_question_buffer) {
		if ( outputStreamVector != null) {
			for (int i = 0; i < outputStreamVector.size(); i++) {
				try {
					outputStreamVector.elementAt(i).write(whole_question_buffer);
				} catch (IOException e) {
					outputStreamVector.remove(i);
					e.printStackTrace();
				}
			}
		}
	}

	public void forwardDataToServer () {

	}

	public void listenForQuestions() {
		new Thread(new Runnable() {
			public void run() {
//                    Boolean able_to_read = true;
//                    while (able_to_read) {
//                        try {
//                            bytes_read = inputStream.read(buffer,0,buffer.length);
//                            //read_offset += bytes_read;
//                            vector_of_buffers.add(buffer);
//                            if (buffer[1023] == 0) {
//                                DataConversion convert_question = new DataConversion();
//                                launchQuestionActivity(convert_question.bytearrayvectorToQuestion(vector_of_buffers));
//                            }
////                            if(bytes_read < 0) {
////                                read_offset = 0;
////                            }
//                        } catch (IOException e) {
//                            able_to_read = false;
//                            e.printStackTrace();
//                        }
//                    }
				Boolean able_to_read = true;
				while (able_to_read && mInputStream != null) {
					current = 0;
					byte[] prefix_buffer = new byte[20];
					String sizes = "";
					String byteread = "";
					try {
						Log.v("read input stream", "first");
						bytes_read = mInputStream.read(prefix_buffer, 0, 20);
						sizes = new String(prefix_buffer, "UTF-8");
					} catch (IOException e) {
						e.printStackTrace();
						able_to_read = false;
					}
					if (sizes.split(":")[0].contains("QUEST")) {
						int size_of_image = Integer.parseInt(sizes.split(":")[1]);
						int size_of_text = Integer.parseInt(sizes.split(":")[2].replaceAll("\\D+", ""));
						byte[] whole_question_buffer = new byte[20 + size_of_image + size_of_text];
						for (int i = 0; i < 20; i++) {
							whole_question_buffer[i] = prefix_buffer[i];
						}
						current = 20;
						do {
							try {
								//Log.v("read input stream", "second");

								bytes_read = mInputStream.read(whole_question_buffer, current, (20 + size_of_image + size_of_text - current));
								Log.v("number of bytes read:", Integer.toString(bytes_read));
//                                    for (int k = 0; k < 20 && current > 20; k++) {
//                                        byteread += whole_question_buffer[current -21 + k];
//                                    }
//                                    Log.v("last bytes read: ", byteread);
//                                    byteread = "";
							} catch (IOException e) {
								e.printStackTrace();
								able_to_read = false;
							}
							if (bytes_read >= 0) {
								current += bytes_read;
								if (able_to_read == false) {
									bytes_read = -1;
									able_to_read = true;
								}
							}
						}
						while (bytes_read > 0);    //shall be sizeRead > -1, because .read returns -1 when finished reading, but outstream not closed on server side
						bytes_read = 1;
						DataConversion convert_question = new DataConversion(mContextWifCom);
						launchQuestionActivity(convert_question.bytearrayvectorToQuestion(whole_question_buffer));
					} else {

					}
				}
			}
		}).start();
	}

	private void launchQuestionActivity(Question question_to_display) {
		((LTApplication) mApplication).setAppWifi(this);
		Intent mIntent = new Intent(mContextWifCom, SingleQuestionActivity.class);
		Bundle bun = new Bundle();
		bun.putString("question", question_to_display.getQUESTION());
		bun.putString("optA", question_to_display.getOPTA());
		bun.putString("optB", question_to_display.getOPTB());
		bun.putString("optC", question_to_display.getOPTC());
		bun.putString("optD", question_to_display.getOPTD());
		bun.putString("image_name", question_to_display.getIMAGE());
//        if (question_text_string.length() > 0) {
//            bun.putString("question", question_text_string.split("///")[0]);
//            bun.putString("optA", question_text_string.split("///")[1]);
//            bun.putString("optB", question_text_string.split("///")[2]);
//            bun.putString("optC", question_text_string.split("///")[3]);
//            bun.putString("optD", question_text_string.split("///")[4]);
//            bun.putString("image_name", question_text_string.split("///")[5]);
//        } else {
//            bun.putString("question", "the question couldn't be read");
//            bun.putString("optA", "");
//            bun.putString("optB", "");
//            bun.putString("optC", "");
//            bun.putString("optD", "");
//            bun.putString("image_name", "");
//        }
		//		bun.putParcelable("bluetoothSocket", btSocket);
		//		bun.putParcelable("bluetoothObject", this);
		mIntent.putExtras(bun);
		mContextWifCom.startActivity(mIntent);
	}
}