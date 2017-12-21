package com.sciquizapp.sciquiz.Activities;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.sciquizapp.sciquiz.AndroidClient;
import com.sciquizapp.sciquiz.DbHelper;
import com.sciquizapp.sciquiz.LTApplication;
import com.sciquizapp.sciquiz.NetworkCommunication.NetworkCommunication;
import com.sciquizapp.sciquiz.Questions.Question;
import com.sciquizapp.sciquiz.Questions.QuestionMultipleChoice;
import com.sciquizapp.sciquiz.R;

public class MultChoiceQuestionActivity extends Activity {
	//List<Question> quesList;
	List<Question> quesList;
	int score=0;
	int qid=0;
	int level=1;
	int number_of_possible_answers = 0;
	int nbQuestionsLevel1 = 8;
	int nbQuestionsLevel2 = 6;
	int nbQuestionsLevel3 = 5;
	int trialCounter = 1;
	int questionId = 1;
	QuestionMultipleChoice currentQ;
	TextView txtQuestion;
	Button answerButton1, answerButton2, answerButton3, answerButton4, submitButton;
	CheckBox checkbox1, checkBox2, checkBox3, checkBox4, checkBox5, checkBox6, checkBox7;
	ArrayList<CheckBox> checkBoxesArray;
	ArrayList<String> arrayOfOptions;
	ImageView picture;
	boolean isImageFitToScreen = true;
	LinearLayout linearLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.activity_singlequestion);


		linearLayout = (LinearLayout) findViewById(R.id.linearLayout);
		txtQuestion = (TextView)findViewById(R.id.textViewQuest1);
		picture = new ImageView(getApplicationContext());
		submitButton = new Button(getApplicationContext());
		checkBoxesArray = new ArrayList<>();


		//get bluetooth client object
//		final BluetoothClientActivity bluetooth = (BluetoothClientActivity)getIntent().getParcelableExtra("bluetoothObject");

		//get question from the bundle
		Bundle bun = getIntent().getExtras();
		final String question = bun.getString("question");
		String opt0 = bun.getString("opt0");		//should also be the answer
		String opt1 = bun.getString("opt1");
		String opt2 = bun.getString("opt2");
		String opt3 = bun.getString("opt3");
		String opt4 = bun.getString("opt4");
		String opt5 = bun.getString("opt5");
		String opt6 = bun.getString("opt6");
		String opt7 = bun.getString("opt7");
		String opt8 = bun.getString("opt8");
		String opt9 = bun.getString("opt9");
		int id = bun.getInt("id");
		String image_path = bun.getString("image_name");
//		final BluetoothClientActivity bluetooth = bun.getParcelable("bluetoothObject");
		//final OldBluetoothCommunication bluetooth = new OldBluetoothCommunication(getApplicationContext());
		currentQ = new QuestionMultipleChoice("chimie","1",question,opt0,opt1,opt2,opt3,opt4,opt5,opt6,opt7,opt8,opt9,image_path);
		currentQ.setID(id);
		if (currentQ.getIMAGE().length() > 0) {
			picture.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if(isImageFitToScreen) {
						isImageFitToScreen=false;
						picture.setAdjustViewBounds(true);
						picture.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
						picture.setAdjustViewBounds(true);
					}else{
						isImageFitToScreen=true;
						picture.setAdjustViewBounds(true);
						picture.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 200));
					}
				}
			});
		}
		setQuestionView();

		//check if no error has occured
		if (question == "the question couldn't be read") {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			finish();
		}

		submitButton.setOnClickListener(new View.OnClickListener() {
			@SuppressLint("SimpleDateFormat") @Override
			public void onClick(View v) {
				String answer = "";
				for (int i = 0; i < number_of_possible_answers; i++) {
					if (checkBoxesArray.get(i).isChecked()) {
						answer += checkBoxesArray.get(i).getText() + "|||";
					}
				}

				NetworkCommunication networkCommunication = ((LTApplication) getApplication()).getAppNetwork();
				networkCommunication.sendAnswerToServer(String.valueOf(answer), question, currentQ.getID());
				finish();
				invalidateOptionsMenu();
			}
		});
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_quiz, menu);
		MenuItem menuLevel = menu.findItem(R.id.menu_level);
		String stringlevel = String.valueOf(level);
		menuLevel.setTitle("NIVEAU:\n"+stringlevel);
		MenuItem menuScore = menu.findItem(R.id.menu_score);
		String stringscore = String.valueOf(score);
		menuScore.setTitle("SCORE:\n"+stringscore);
		return true;
	}
	private void setQuestionView()
	{
		txtQuestion.setText(currentQ.getQUESTION());

		File imgFile = new  File(getFilesDir()+"/images/" + currentQ.getIMAGE());
		if(imgFile.exists()){
			String path = imgFile.getAbsolutePath();
			Bitmap myBitmap = BitmapFactory.decodeFile(path);
			picture.setImageBitmap(myBitmap);
		}
		picture.setAdjustViewBounds(true);
		picture.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 200));
		linearLayout.addView(picture);

//		int imageResource = getResources().getIdentifier(currentQ.getIMAGE(), null, getPackageName());
//		picture.setImageResource(imageResource);


		String[] answerOptions;
		answerOptions = new String[10];
		answerOptions[0] = currentQ.getOPT0();
		answerOptions[1] = currentQ.getOPT1();
		answerOptions[2] = currentQ.getOPT2();
		answerOptions[3] = currentQ.getOPT3();
		answerOptions[4] = currentQ.getOPT4();
		answerOptions[5] = currentQ.getOPT5();
		answerOptions[6] = currentQ.getOPT6();
		answerOptions[7] = currentQ.getOPT7();
		answerOptions[8] = currentQ.getOPT8();
		answerOptions[9] = currentQ.getOPT9();

		for (int i = 0; i < 10; i++) {
			if (!answerOptions[i].equals(" ")) {
				number_of_possible_answers++;
			}
		}

		//implementing Fisher-Yates shuffle
		Random rnd = new Random();
		for (int i = number_of_possible_answers - 1; i > 0; i--)
		{
			int index = rnd.nextInt(i + 1);
			// Simple swap
			String a = answerOptions[index];
			answerOptions[index] = answerOptions[i];
			answerOptions[i] = a;
		}

		CheckBox tempCheckBox = null;

		for (int i = 0; i < number_of_possible_answers; i++) {
			tempCheckBox = new CheckBox(getApplicationContext());
			tempCheckBox.setText(answerOptions[i]);
			tempCheckBox.setTextColor(Color.BLACK);
			checkBoxesArray.add(tempCheckBox);
			if(checkBoxesArray.get(i).getParent()!=null)
				((ViewGroup)checkBoxesArray.get(i).getParent()).removeView(checkBoxesArray.get(i));

			checkBoxesArray.get(i).setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT, 10f));

			linearLayout.addView(checkBoxesArray.get(i));
		}
		submitButton.setText("soumettre la réponse");
		submitButton.setBackgroundColor(Color.parseColor("#00CCCB"));
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT
		);
		int height = getApplicationContext().getResources().getDisplayMetrics().heightPixels;
		int width = getApplicationContext().getResources().getDisplayMetrics().widthPixels;
		params.setMargins(width / 40, height / 200, width / 40, height / 200);  //left, top, right, bottom
		submitButton.setLayoutParams(params);
		submitButton.setTextColor(Color.WHITE);
		linearLayout.addView(submitButton);
		qid++;
	}
}