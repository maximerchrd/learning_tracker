package com.sciquizapp.LearningTracker.Activities;

import com.sciquizapp.LearningTracker.NetworkCommunication.NetworkCommunication;
import com.sciquizapp.LearningTracker.R;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class InteractiveModeActivity extends Activity {
	NetworkCommunication mNetCom;
	public TextView intmod_out;
	TextView intmod_wait_for_question;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//initialize view
		setContentView(R.layout.activity_interactivemode);
		intmod_wait_for_question = (TextView) findViewById(R.id.intmod_wait_for_question);
		intmod_out = (TextView) findViewById(R.id.intmod_out);
		intmod_wait_for_question.append(getString(R.string.waiting_for_question));
		
		//mNetCom = new NetworkCommunication(this, getApplication());
		mNetCom = new NetworkCommunication(this, getApplication(), intmod_out);
		mNetCom.ConnectToMaster();
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
    }

	public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
		if ((keyCode == android.view.KeyEvent.KEYCODE_BACK)) {
			Log.v(this.getClass().getName(), "back button pressed");
		}
		return super.onKeyDown(keyCode, event);
	}
}
