package com.sciquizapp.sciquiz;

import com.sciquizapp.sciquiz.AndroidClient.SendAsyncTask;
import com.sciquizapp.sciquiz.NetworkCommunication.BluetoothCommunication;
import com.sciquizapp.sciquiz.NetworkCommunication.NetworkCommunication;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class InteractiveModeActivity extends Activity {
	NetworkCommunication mNetCom;
	public TextView intmod_out;
	TextView intmod_wait_for_question;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//prompts the "settings menu" to add the write_settings permission
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
			if (android.provider.Settings.System.canWrite(getApplicationContext())) {
				// Do stuff here
			}
			else {
				Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS);
				intent.setData(android.net.Uri.parse("package:" + getPackageName()));
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
			}
		}

		//initialize view
		setContentView(R.layout.activity_interactivemode);
		intmod_wait_for_question = (TextView) findViewById(R.id.intmod_wait_for_question);
		intmod_out = (TextView) findViewById(R.id.intmod_out);
		intmod_wait_for_question.append("En attente de la question suivante");
		
		//mNetCom = new NetworkCommunication(this, getApplication());
		mNetCom = new NetworkCommunication(this, getApplication(), intmod_out);
		mNetCom.ConnectToMaster();
//        android.app.Application.ActivityLifecycleCallbacks callback = null;
//        getApplication().registerActivityLifecycleCallbacks(callback);
	}
	
	public void onStart() {
		super.onStart();
	}
	public void onPause() {
		super.onPause();
	}

	public void onStop() {
		super.onStop();
	}

	
	/** Called when system is low on resources or finish() called on activity*/
	public void onDestroy() {
		super.onDestroy();
	}
    @Override
    protected void onUserLeaveHint()
    {
        Log.v("onUserLeaveHint","Home button pressed");
        super.onUserLeaveHint();
		BluetoothCommunication bluetooth = ((LTApplication) getApplication()).getAppBluetooth();
		if (bluetooth != null) bluetooth.studentLeftApp();
    }

	public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
		if ((keyCode == android.view.KeyEvent.KEYCODE_BACK)) {
			Log.v(this.getClass().getName(), "back button pressed");
			BluetoothCommunication bluetooth = ((LTApplication) getApplication()).getAppBluetooth();
			if (bluetooth != null) bluetooth.studentLeftApp();
		}
		return super.onKeyDown(keyCode, event);
	}
}
