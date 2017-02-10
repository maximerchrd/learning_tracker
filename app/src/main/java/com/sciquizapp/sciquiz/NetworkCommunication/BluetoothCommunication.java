package com.sciquizapp.sciquiz.NetworkCommunication;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.app.*;

import java.lang.reflect.InvocationTargetException;
import java.util.*;


import com.sciquizapp.sciquiz.OldBluetoothCommunication;
import com.sciquizapp.sciquiz.SingleQuestionActivity;

import java.io.*;

public class BluetoothCommunication {
    private Context mContext;
    private static final int REQUEST_ENABLE_BT = 1;
    private BluetoothAdapter mBluetoothAdapter = null;
    private static BluetoothSocket btSocket = null;
    private OutputStream outStream = null;
    private InputStream inputStream = null;
    private int current = 0; //tells where is the "cursor" when reading a file
    private String question_text_string = "";
    private BroadcastReceiver mActivityReceiver;
    private BluetoothDevice mDevice;
    byte[] buffer = new byte[1024];
    private Vector<byte[]> vector_of_buffers = new Vector<byte[]>();
    private int read_offset = 0;
    private int bytes_read = 1;

    // Well known SPP UUID
    //private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static final UUID MY_UUID = UUID.fromString("d0c722b0-7e15-11e1-b0c4-0800200c9a66");


    /**
     * method that tries to connect to the PC through bluetooth
     * @return true if connection succeeded, false if not
     */
    public Boolean BTConnectToMaster() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        CheckBTState();
        searchForPairedDevices();

        // Two things are needed to make a connection:
        //   A MAC address, which we got above.
        //   A Service ID or UUID.  In this case we are using the
        //     UUID for SPP.
        try {
            btSocket = mDevice.createRfcommSocketToServiceRecord(MY_UUID);
            //btSocket = device.createInsecureRfcommSocketToServiceRecord(MY_UUID);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Discovery is resource intensive.  Make sure it isn't going on
        // when you attempt to connect and pass your message.
        mBluetoothAdapter.cancelDiscovery();

        // Establish the connection.  This will block until it connects.
        try {
            btSocket.connect();
            listenForQuestions();
            Log.v("BT connection", "...Connection established and data link opened...");
            return true;
        } catch (IOException e) {
            Log.v("BT connection", "...failed ...");
            e.printStackTrace();
            try {
                btSocket.close();
            } catch (IOException e2) {
                Log.w("Fatal Error", "unable to close socket during connection failure" + e2.getMessage() + ".");
            }
        }

        //send string to pc
        if (btSocket.isConnected()) {
//            try {
//                outStream = btSocket.getOutputStream();
//            } catch (IOException e) {
//                Log.w("Fatal Error", "In onResume() and output stream creation failed:" + e.getMessage() + ".");
//            }
//            new Thread(new Runnable() {
//                public void run() {
//                    while (true) {
//
//                        String message = "bla\n";
//                        byte[] msgBuffer = message.getBytes();
//                        try {
//                            outStream.write(msgBuffer);
//                        } catch (IOException e) {
//                            String msg = "In onResume() and an exception occurred during write: " + e.getMessage();
//                            msg = msg + ".\n\nCheck that the SPP UUID: " + MY_UUID.toString() + " exists on server.\nor server not started.\n";
//
//                            Log.w("Fatal Error", msg);
//                        }
//                        try {
//                            Thread.sleep(2000);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//            }).start();
        }

//        try {
//            if (outStream != null) {
//                outStream.close();
//                outStream = null;
//            }
//            if (btSocket != null) {
//                btSocket.close();
//                btSocket = null;
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        return false;
    }

    /**
     * method that enables the smartphone to listen for the questions from the PC
     */
    public void listenForQuestions () {
        if (btSocket.isConnected()) {
            try {
                inputStream = btSocket.getInputStream();
            } catch (IOException e) {
                Log.w("Fatal Error", "input stream creation failed:" + e.getMessage() + ".");
            }
            new Thread(new Runnable() {
                public void run() {
                    Boolean able_to_read = true;
                    while (able_to_read) {
                        try {
                            bytes_read = inputStream.read(buffer,0,buffer.length);
                            //read_offset += bytes_read;
                            vector_of_buffers.add(buffer);
//                            if(bytes_read < 0) {
//                                read_offset = 0;
//                            }
                        } catch (IOException e) {
                            able_to_read = false;
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
        }
    }

    /**
     * method that sends the answer to the PC through bluetooth
     */
    public void sendAnswer(String answer) {
        //send string to pc
        if (btSocket != null && btSocket.isConnected()) {
            try {
                outStream = btSocket.getOutputStream();
            } catch (IOException e) {
                Log.w("Fatal Error", "In sendAnswer() and output stream creation failed:" + e.getMessage() + ".");
            }

            String message = answer;
            byte[] msgBuffer = message.getBytes();
            try {
                outStream.write(msgBuffer);
            } catch (IOException e) {
                String msg = "In sendAnswer() and an exception occurred during write: " + e.getMessage();
                msg = msg + ".\n\nCheck that the SPP UUID: " + MY_UUID.toString() + " exists on server.\nor server not started.\n";

                Log.w("Fatal Error", msg);
            }
        }
    }

    /**
     * method that informs pc that the student possibly left the application. It is
     * intended to be called when the home button or the back key are pressed
     */
    public void studentLeftApp() {
        //send string to pc
        if (btSocket != null && btSocket.isConnected()) {
            try {
                outStream = btSocket.getOutputStream();
            } catch (IOException e) {
                Log.w("Fatal Error", "In onResume() and output stream creation failed:" + e.getMessage() + ".");
            }

            String message = "student possibly left app";
            byte[] msgBuffer = message.getBytes();
            try {
                outStream.write(msgBuffer);
            } catch (IOException e) {
                String msg = "In onResume() and an exception occurred during write: " + e.getMessage();
                msg = msg + ".\n\nCheck that the SPP UUID: " + MY_UUID.toString() + " exists on server.\nor server not started.\n";

                Log.w("Fatal Error", msg);
            }
        }


        try {
            if (outStream != null) {
                outStream.close();
                outStream = null;
            }
            if (btSocket != null) {
                btSocket.close();
                btSocket = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void CheckBTState() {
        // Check for Bluetooth support and then check to make sure it is turned on

        // Emulator doesn't support Bluetooth and will return null
        if (mBluetoothAdapter == null) {
            Log.w("Fatal Error", "Bluetooth Not supported. Aborting.");
        } else {
            if (mBluetoothAdapter.isEnabled()) {
                Log.v("BT state", "...Bluetooth is enabled...");
            } else {
                Log.v("BT state", "Turn on BT");
                //Prompt user to turn on Bluetooth
//                Intent enableBtIntent = new Intent(btAdapter.ACTION_REQUEST_ENABLE);
//                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }
    }

    private void searchForPairedDevices() {
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        // If there are paired devices
        if (pairedDevices.size() > 0) {
            // Loop through paired devices
            for (BluetoothDevice device : pairedDevices) {
                // Add the name and address to an array adapter to show in a ListView
                Log.v("paired device", device.getName());
                mDevice = device;
            }
        }
    }

    /**
     *
     */
    private void launchQuestionActivity() {
        Intent mIntent = new Intent(mContext, SingleQuestionActivity.class);
        Bundle bun = new Bundle();
        if (question_text_string.length() > 0) {
            bun.putString("question", question_text_string.split("///")[0]);
            bun.putString("optA", question_text_string.split("///")[1]);
            bun.putString("optB", question_text_string.split("///")[2]);
            bun.putString("optC", question_text_string.split("///")[3]);
            bun.putString("optD", question_text_string.split("///")[4]);
            bun.putString("image_name", question_text_string.split("///")[5]);
        } else {
            bun.putString("question", "the question couldn't be read");
            bun.putString("optA", "");
            bun.putString("optB", "");
            bun.putString("optC", "");
            bun.putString("optD", "");
            bun.putString("image_name", "");
        }
        //		bun.putParcelable("bluetoothSocket", btSocket);
        //		bun.putParcelable("bluetoothObject", this);
        mIntent.putExtras(bun);
        mContext.startActivity(mIntent);
    }

}
