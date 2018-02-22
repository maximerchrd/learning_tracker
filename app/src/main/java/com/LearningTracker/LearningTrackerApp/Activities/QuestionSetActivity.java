package com.LearningTracker.LearningTrackerApp.Activities;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;

import com.LearningTracker.LearningTrackerApp.Activities.SwipingTools.ExerciseCollectionPagerAdapter;
import com.LearningTracker.LearningTrackerApp.R;

import java.util.ArrayList;

/**
 * Created by maximerichard on 21.02.18.
 */
public class QuestionSetActivity extends FragmentActivity {
    // When requested, this adapter returns a ExerciseObjectFragment,
    // representing an object in the collection.
    ExerciseCollectionPagerAdapter mDemoCollectionPagerAdapter;
    ViewPager mViewPager;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questionset);

        //get question ids from the bundle
        Bundle bundle = getIntent().getExtras();
        ArrayList<Integer> questionIDs = bundle.getIntegerArrayList("IDsArray");
        for (int i = 0; i < questionIDs.size(); i++) {
            Log.v("ID:", String.valueOf(questionIDs.get(i)));
        }

        // ViewPager and its adapters use support library
        // fragments, so use getSupportFragmentManager.
        mDemoCollectionPagerAdapter =
                new ExerciseCollectionPagerAdapter(getSupportFragmentManager());
        mDemoCollectionPagerAdapter.setmQuestionIDs(questionIDs);
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mDemoCollectionPagerAdapter);
    }
}
