package com.LearningTracker.LearningTrackerApp.Activities;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.LearningTracker.LearningTrackerApp.R;
import com.LearningTracker.LearningTrackerApp.database_management.DbTableSubject;

import java.util.Vector;

/**
 * Created by maximerichard on 20.02.18.
 */
public class ExerciceActivity extends Activity {
    private Button homeWorkButton, freePracticeButton;
    private Spinner teachersSpinner, subjectsSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercice);

        //couple code with UI
        homeWorkButton = (Button) findViewById(R.id.homework_button);
        freePracticeButton = (Button) findViewById(R.id.freepractice_button);
        teachersSpinner = (Spinner) findViewById(R.id.teachers_spinner);
        subjectsSpinner = (Spinner) findViewById(R.id.subjects_spinner);

        //puts the subjects for which there are poorly evaluated questions into the spinner
        Vector<String> subjectsVector = DbTableSubject.getSubjectsAndQuestionsNeedingPractice().get(1);
        subjectsVector.insertElementAt(getString(R.string.all_subjects),0);
        String[] arraySpinner = subjectsVector.toArray(new String[subjectsVector.size()]);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.spinner_item, arraySpinner);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);


        subjectsSpinner.setAdapter(adapter);
    }
}
