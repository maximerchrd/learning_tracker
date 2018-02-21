package com.LearningTracker.LearningTrackerApp.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.LearningTracker.LearningTrackerApp.R;
import com.LearningTracker.LearningTrackerApp.database_management.DbTableSubject;

import java.util.ArrayList;
import java.util.Vector;

/**
 * Created by maximerichard on 20.02.18.
 */
public class ExerciseActivity extends Activity {
    private Button homeWorkButton, freePracticeButton;
    private Spinner teachersSpinner, subjectsSpinner;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercice);

        mContext = getApplicationContext();

        //couple code with UI
        homeWorkButton = (Button) findViewById(R.id.homework_button);
        freePracticeButton = (Button) findViewById(R.id.freepractice_button);
        teachersSpinner = (Spinner) findViewById(R.id.teachers_spinner);
        subjectsSpinner = (Spinner) findViewById(R.id.subjects_spinner);

        //puts the subjects for which there are poorly evaluated questions into the spinner
        Vector<Vector<String>> questionIdAndSubjectsVector = DbTableSubject.getSubjectsAndQuestionsNeedingPractice();
        Vector<String> subjectsVector = questionIdAndSubjectsVector.get(1);
        subjectsVector.insertElementAt(getString(R.string.all_subjects),0);
        String[] arraySpinner = subjectsVector.toArray(new String[subjectsVector.size()]);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.spinner_item, arraySpinner);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        subjectsSpinner.setAdapter(adapter);

        //implements the practice button
        Vector<String> questionIDsVector =  questionIdAndSubjectsVector.get(0);
        final ArrayList<Integer> questionIDsArray = new ArrayList<>();
        for (int i = 0; i < questionIDsVector.size(); i++) {
            questionIDsArray.add(Integer.valueOf(questionIDsVector.get(i)));
        }
        freePracticeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putIntegerArrayList("IDsArray",questionIDsArray);
                Intent myIntent = new Intent(mContext, QuestionSetActivity.class);
                myIntent.putExtras(bundle);
                mContext.startActivity(myIntent);
            }
        });
    }
}
