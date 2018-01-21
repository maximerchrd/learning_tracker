package com.sciquizapp.sciquiz.Activities;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.app.Activity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.sciquizapp.sciquiz.R;
import com.sciquizapp.sciquiz.database_management.DbTableLearningObjective;

import java.util.ArrayList;
import java.util.Vector;

public class EvaluationResultsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evaluation_results);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        Vector<Vector<String>> evalForObjectives = DbTableLearningObjective.getResultsPerObjective();
        Vector<String> objectives = evalForObjectives.get(0);
        Vector<String> evaluations = evalForObjectives.get(1);
        HorizontalBarChart chart = (HorizontalBarChart) findViewById(R.id.chart);

        ArrayList<BarEntry> entries = new ArrayList<>();
        for (int i = 0; i < evaluations.size(); i++) {
            entries.add(new BarEntry(i, Float.valueOf(evaluations.get(i))));
        }

        BarDataSet dataset = new BarDataSet(entries, "Evaluation for each learning objective");

        //Defining the X-Axis Labels
        final ArrayList<String> labels = new ArrayList<String>();

        for (int i = 0; i < objectives.size(); i++) {
            /*if (objectives.get(i).length() > 30) {
                String splitstring = objectives.get(i);
                splitstring = splitstring.substring(0,10) + "\n" + splitstring.substring(10,splitstring.length());
                splitstring = splitstring.substring(0,25) + "\\n" + splitstring.substring(25,splitstring.length());
                objectives.set(i,splitstring);
            }*/
            labels.add(objectives.get(i));
        }

        //creating the chart
        //BarChart chart = new BarChart(getApplicationContext());
        //setContentView(chart);

        XAxis xAxis = chart.getXAxis();
        xAxis.setGranularity(1.0f);
        xAxis.setPosition(XAxis.XAxisPosition.TOP_INSIDE);
        xAxis.setDrawGridLines(false);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return labels.get((int) value);
            }
        });

        YAxis yAxis = chart.getAxisLeft();
        yAxis.setAxisMaximum(100);
        yAxis.setAxisMinimum(0);
        YAxis yAxis2 = chart.getAxisRight();
        yAxis2.setAxisMaximum(100);
        yAxis2.setAxisMinimum(0);

        dataset.setDrawValues(false);

        BarData data = new BarData(dataset);
        chart.setData(data);

    }

}
