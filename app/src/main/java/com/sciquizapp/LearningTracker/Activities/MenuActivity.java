package com.sciquizapp.LearningTracker.Activities;


import java.io.File;

import javax.mail.AuthenticationFailedException;
import javax.mail.MessagingException;

import com.sciquizapp.LearningTracker.BuildConfig;
import com.sciquizapp.LearningTracker.database_management.DbHelper;
import com.sciquizapp.LearningTracker.Mail;
import com.sciquizapp.LearningTracker.R;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.provider.Settings.Secure;
import android.view.ViewGroup;

public class MenuActivity extends Activity {
	Button  scoresButton, buttonChangeSettings, interactiveModeButton;
	TextView consignes;
	private Boolean firstTime = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu);
		scoresButton = (Button)findViewById(R.id.scoresbutton);
		interactiveModeButton = (Button)findViewById(R.id.interactivemodebutton);
		buttonChangeSettings = (Button)findViewById(R.id.buttonchangesettings);
		consignes = (TextView) findViewById(R.id.textViewmenu);
		final DbHelper db = new DbHelper(this);

		String subjectsRaw = db.getSubjects();
		final String[] subjects = subjectsRaw.split("/");

		//set  text for consignes
		consignes.setText(getString(R.string.hello) + " " + db.getName() +".\n");

		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,      
				LinearLayout.LayoutParams.WRAP_CONTENT
				);
		int height = getApplicationContext().getResources().getDisplayMetrics().heightPixels;
		int width = getApplicationContext().getResources().getDisplayMetrics().widthPixels;
		params.setMargins(width / 40, height / 200, width / 40, height / 200);  //left, top, right, bottom
		//startButton.setLayoutParams(params);
		scoresButton.setLayoutParams(params);
		interactiveModeButton.setLayoutParams(params);
		buttonChangeSettings.setLayoutParams(params);

		// Define a new Adapter:  First parameter - Context; Second parameter - Layout for the row,
		//Third parameter - ID of the TextView to which the data is written; Fourth - the Array of data
		//where subjects are fed to the layout
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, android.R.id.text1, subjects){
			@Override
			public View getView(int position, View convertView,
					ViewGroup parent) {
				View view =super.getView(position, convertView, parent);

				TextView textView=(TextView) view.findViewById(android.R.id.text1);

				/*YOUR CHOICE OF COLOR*/
				textView.setTextColor(Color.BLACK);

				return view;
			}
		};

		//go to scores button
		scoresButton.setOnClickListener(new View.OnClickListener() {		
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MenuActivity.this, EvaluationResultsActivity.class);
				startActivity(intent);
			}
		});

		
		//start interactive questions session
		interactiveModeButton.setOnClickListener(new View.OnClickListener() {		
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MenuActivity.this, InteractiveModeActivity.class);
				startActivity(intent);
			}
		});

		//open change settings activity
		buttonChangeSettings.setOnClickListener(new View.OnClickListener() {		
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MenuActivity.this, SettingsActivity.class);
				startActivity(intent);
			}
		});
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_menu, menu);
		return true;
	}
}
