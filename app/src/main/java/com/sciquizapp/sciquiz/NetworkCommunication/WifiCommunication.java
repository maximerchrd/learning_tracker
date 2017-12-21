package com.sciquizapp.sciquiz.NetworkCommunication;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import com.sciquizapp.sciquiz.Activities.MultChoiceQuestionActivity;
import com.sciquizapp.sciquiz.Activities.QuestionActivity;
import com.sciquizapp.sciquiz.Activities.SingleQuestionActivity;
import com.sciquizapp.sciquiz.DataConversion;
import com.sciquizapp.sciquiz.DbHelper;
import com.sciquizapp.sciquiz.LTApplication;
import com.sciquizapp.sciquiz.Questions.Question;
import com.sciquizapp.sciquiz.Questions.QuestionMultipleChoice;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;

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
	private String ip_address = "192.168.1.100";
	List<android.net.wifi.ScanResult> mScanResults = new ArrayList<android.net.wifi.ScanResult>();
	BroadcastReceiver scanningreceiver;

	public WifiCommunication(Context arg_context, Application arg_application) {
		mContextWifCom = arg_context;
		mWifi = (WifiManager) mContextWifCom.getSystemService(Context.WIFI_SERVICE);
		mApplication = arg_application;
		final DbHelper db = new DbHelper(arg_context);
		ip_address = db.getMaster();
	}


	public void connectToServer(String connectionString) {
		try {
			Log.v("connectToServer", "beginning");
			Socket s = new Socket(ip_address,9090);
			//Socket s = new Socket("192.168.88.252",9090);
			Log.v("server name",s.getInetAddress().getCanonicalHostName());
			Log.v("server name",s.getInetAddress().getHostName());
			//outgoing stream redirect to socket
			mOutputStream = s.getOutputStream();
			mInputStream = s.getInputStream();

			byte[] conBuffer = connectionString.getBytes();
			try {
				mOutputStream.write(conBuffer, 0, conBuffer.length);
				mOutputStream.flush();
			} catch (IOException e) {
				String msg = "In connectToServer() and an exception occurred during write: " + e.getMessage();
				Log.e("Fatal Error", msg);
			}

			listenForQuestions();
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
						if (bytes_read < 0) {
							able_to_read = false;
						}
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
					} else if (sizes.split(":")[0].contains("MULTQ")) {
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
						launchMultChoiceQuestionActivity(convert_question.bytearrayvectorToMultChoiceQuestion(whole_question_buffer));
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
		mIntent.putExtras(bun);
		mContextWifCom.startActivity(mIntent);
	}
	private void launchMultChoiceQuestionActivity(QuestionMultipleChoice question_to_display) {
		((LTApplication) mApplication).setAppWifi(this);
		Intent mIntent = new Intent(mContextWifCom, MultChoiceQuestionActivity.class);
		Bundle bun = new Bundle();
		bun.putString("question", question_to_display.getQUESTION());
		bun.putString("opt0", question_to_display.getOPT0());
		bun.putString("opt1", question_to_display.getOPT1());
		bun.putString("opt2", question_to_display.getOPT2());
		bun.putString("opt3", question_to_display.getOPT3());
		bun.putString("opt4", question_to_display.getOPT4());
		bun.putString("opt5", question_to_display.getOPT5());
		bun.putString("opt6", question_to_display.getOPT6());
		bun.putString("opt7", question_to_display.getOPT7());
		bun.putString("opt8", question_to_display.getOPT8());
		bun.putString("opt9", question_to_display.getOPT9());
		bun.putInt("id", question_to_display.getID());
		bun.putString("image_name", question_to_display.getIMAGE());
		mIntent.putExtras(bun);
		mContextWifCom.startActivity(mIntent);
	}

	/**
	 * WORK IN PROGRESS
	 * method used to scan the wifi network to which the smartphone is connected
	 * and try to connect to the "interesting" ip addresses (e.g. 192.168.x.xx, 127.0.x.x)
	 */
	public void scanAndConnectToIP() {
		try {
			Enumeration nis = NetworkInterface.getNetworkInterfaces();
			while(nis.hasMoreElements())
			{
				NetworkInterface ni = (NetworkInterface) nis.nextElement();
				Enumeration ias = ni.getInetAddresses();
				while (ias.hasMoreElements())
				{
					InetAddress ia = (InetAddress) ias.nextElement();
					Log.d("address found:",ia.getHostAddress());
				}

			}
		} catch (SocketException ex) {
		}
	}
}