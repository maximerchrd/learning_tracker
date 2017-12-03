package com.sciquizapp.sciquiz.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import android.widget.Button;
import android.widget.EditText;
import android.view.View;

import com.sciquizapp.sciquiz.DbHelper;
import com.sciquizapp.sciquiz.R;


public class SettingsActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);

		final EditText editName;
		final Button buttonSaveAndBack;
		final DbHelper db = new DbHelper(this);
		final EditText editMaster;

		editName = (EditText) findViewById(R.id.edittextnom);
		buttonSaveAndBack = (Button) findViewById(R.id.buttonsaveandback);
		editName.setText(db.getName(), null);

		editMaster = (EditText) findViewById(R.id.edittextmaster);
		editName.setText(db.getMaster(), null);

		buttonSaveAndBack.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				String name = editName.getText().toString();
				String master = editMaster.getText().toString();
				db.addName(name);
				db.addMaster(master);
				Intent intent = new Intent(SettingsActivity.this, MenuActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.putExtra("flag", "modify");
				startActivity(intent);
				finish();
			}
		});
	}
}