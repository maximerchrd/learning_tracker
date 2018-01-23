package com.sciquizapp.sciquiz.Activities;


import java.io.File;

import javax.mail.AuthenticationFailedException;
import javax.mail.MessagingException;

import com.sciquizapp.sciquiz.BuildConfig;
import com.sciquizapp.sciquiz.database_management.DbHelper;
import com.sciquizapp.sciquiz.Mail;
import com.sciquizapp.sciquiz.R;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.provider.Settings.Secure;
import android.view.ViewGroup;

public class MenuActivity extends Activity {
	Button startButton, scoresButton, sendButton, buttonChangeSettings, interactiveModeButton;
	TextView consignes;
	ListView listSubjects;
	Boolean resultsSent = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu);
		//startButton = (Button)findViewById(R.id.startbutton);
		scoresButton = (Button)findViewById(R.id.scoresbutton);
		//sendButton = (Button)findViewById(R.id.sendbutton);
		interactiveModeButton = (Button)findViewById(R.id.interactivemodebutton);
		buttonChangeSettings = (Button)findViewById(R.id.buttonchangesettings);
		consignes = (TextView) findViewById(R.id.textViewmenu);
		//listSubjects = (ListView) findViewById(R.id.listView1);
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
		// Assign adapter to ListView
		/*listSubjects.setAdapter(adapter);
		listSubjects.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

		// ListView Item Click Listener
		listSubjects.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				final String subject = (String)parent.getAdapter().getItem(position);
				// Set the item as checked to be highlighted
				for (int i = 0; i < listSubjects.getCount(); i++) {
					listSubjects.getChildAt(i).setBackgroundColor(Color.WHITE);
				}
				//listSubjects.setItemChecked(0, true);
				view.setBackgroundColor(Color.parseColor("#79F8F8"));

				startButton.setOnClickListener(new View.OnClickListener() {		
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(MenuActivity.this, QuizActivity.class);
						Bundle bun = new Bundle();
						bun.putString("subject", subject);
						intent.putExtras(bun);
						startActivity(intent);
						resultsSent = false;
					}
				});
			}
		});*/

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

	final DbHelper db = new DbHelper(this);

	//class for sending mail
	class SendEmailAsyncTask extends AsyncTask <Void, Void, Boolean> {
		Mail m = new Mail("sciquiz.sender@gmail.com", "sciqkiss");

		public SendEmailAsyncTask() {
			if (BuildConfig.DEBUG) Log.v(SendEmailAsyncTask.class.getName(), "SendEmailAsyncTask()");
			String[] toArr = { "sciquiz.receiver@gmail.com"};
			m.setTo(toArr);
			m.setFrom("sciquiz.sender@gmail.com");
			String username = db.getName();
			final String android_id = Secure.getString(getBaseContext().getContentResolver(),
					Secure.ANDROID_ID);
			m.setSubject(username+"   "+android_id);
			m.setBody("body.");
			try {
				File results = new File(Environment.getExternalStorageDirectory(), "SciQuiz/resultats.txt");
				m.addAttachment(results.getAbsolutePath());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			if (BuildConfig.DEBUG) Log.v(SendEmailAsyncTask.class.getName(), "doInBackground()");
			try {
				m.send();
				return true;
			} catch (AuthenticationFailedException e) {
				Log.e(SendEmailAsyncTask.class.getName(), "Bad account details");
				e.printStackTrace();
				return false;
			} catch (MessagingException e) {
				Log.e(SendEmailAsyncTask.class.getName(), m.getTO() + "failed");
				e.printStackTrace();
				return false;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
	}
}
