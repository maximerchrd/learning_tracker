package com.sciquizapp.LearningTracker.Activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sciquizapp.LearningTracker.AndroidClient;
import com.sciquizapp.LearningTracker.database_management.DbHelper;
import com.sciquizapp.LearningTracker.Questions.Question;
import com.sciquizapp.LearningTracker.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class QuestionActivity extends Activity {
	//List<Question> quesList;
	List<Question> quesList;
	int score=0;
	int qid=0;
	int level=1;
	int nbQuestionsLevel1 = 8;
	int nbQuestionsLevel2 = 6;
	int nbQuestionsLevel3 = 5;
	int trialCounter = 1;
	int questionId = 1;
	Question currentQ;
	TextView txtQuestion;
	Button answerButton1, answerButton2, answerButton3, answerButton4, submitButton;
	CheckBox checkbox1, checkBox2, checkBox3, checkBox4, checkBox5, checkBox6, checkBox7;
	ArrayList<CheckBox> checkBoxesArray;
	ArrayList<String> arrayOfOptions;
	ImageView picture;
	boolean isImageFitToScreen;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.activity_question);
		final DbHelper db = new DbHelper(this);
		Bundle bun = getIntent().getExtras();
		questionId = bun.getInt("questionID");
		//final String subjectQuiz = bun.getString("subject");
		//quesList = db.getQuestionsFromSubject(subjectQuiz);
		quesList = db.getAllQuestions();
		checkBoxesArray = new ArrayList<>();


		txtQuestion=(TextView)findViewById(R.id.textViewQuest1);
		submitButton = (Button)findViewById(R.id.submitButton);
		picture = (ImageView)findViewById(R.id.pictureQuest);

		currentQ=quesList.get(questionId);

		if (currentQ.getIMAGE().length() > 0) {
			picture.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (isImageFitToScreen) {
						isImageFitToScreen = false;
						picture.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0.19f));
						picture.setAdjustViewBounds(true);
					} else {
						isImageFitToScreen = true;
						picture.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
					}
				}
			});
		}
		setQuestionView();
		final CheckBox tempCheckBox = new CheckBox(getApplicationContext());
		tempCheckBox.setText(currentQ.getOPTA());
		checkBoxesArray.add(tempCheckBox);
		tempCheckBox.setText(currentQ.getOPTB());
		checkBoxesArray.add(tempCheckBox);
		tempCheckBox.setText(currentQ.getOPTC());
		checkBoxesArray.add(tempCheckBox);
		tempCheckBox.setText(currentQ.getOPTD());
		checkBoxesArray.add(tempCheckBox);

		submitButton.setOnClickListener(new View.OnClickListener() {
			@SuppressLint("SimpleDateFormat") @Override
			public void onClick(View v) {
				Intent intent = new Intent(QuestionActivity.this, AndroidClient.class);
				Bundle b = new Bundle();
				String answer = "";
				for (int i = 0; i < 4; i++) {
					if (checkBoxesArray.get(i).isChecked()) {
						answer += checkBoxesArray.get(i).getText();
					}
				}

				b.putString("answer", answer);
				intent.putExtras(b);
				startActivity(intent);
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
		answerButton1.setBackgroundColor(Color.parseColor("#00CCCB"));
		answerButton2.setBackgroundColor(Color.parseColor("#00CCCB"));
		answerButton3.setBackgroundColor(Color.parseColor("#00CCCB"));
		answerButton4.setBackgroundColor(Color.parseColor("#00CCCB"));
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT
		);
		int height = getApplicationContext().getResources().getDisplayMetrics().heightPixels;
		int width = getApplicationContext().getResources().getDisplayMetrics().widthPixels;
		params.setMargins(width / 40, height / 200, width / 40, height / 200);  //left, top, right, bottom
		answerButton1.setLayoutParams(params);
		answerButton2.setLayoutParams(params);
		answerButton3.setLayoutParams(params);
		answerButton4.setLayoutParams(params);

		int imageResource = getResources().getIdentifier(currentQ.getIMAGE(), null, getPackageName());
		picture.setImageResource(imageResource);


		String[] answerOptions;
		answerOptions = new String[4];
		answerOptions[0] = currentQ.getOPTA();
		answerOptions[1] = currentQ.getOPTB();
		answerOptions[2] = currentQ.getOPTC();
		answerOptions[3] = currentQ.getOPTD();

		//implementing Fisher-Yates shuffle
		Random rnd = new Random();
		for (int i = answerOptions.length - 1; i > 0; i--)
		{
			int index = rnd.nextInt(i + 1);
			// Simple swap
			String a = answerOptions[index];
			answerOptions[index] = answerOptions[i];
			answerOptions[i] = a;
		}


		answerButton1.setText(answerOptions[0]);
		answerButton2.setText(answerOptions[1]);
		answerButton3.setText(answerOptions[2]);
		answerButton4.setText(answerOptions[3]);
		qid++;
	}
}