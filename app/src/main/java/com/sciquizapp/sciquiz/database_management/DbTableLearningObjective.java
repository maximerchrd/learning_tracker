package com.sciquizapp.sciquiz.database_management;

import android.database.Cursor;
import android.util.Log;

import com.sciquizapp.sciquiz.Questions.QuestionMultipleChoice;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Collections;
import java.util.Vector;

/**
 * Created by maximerichard on 03.01.18.
 */
public class DbTableLearningObjective {
    static public void createTableLearningObjectives() {
        try {
            String sql = "CREATE TABLE IF NOT EXISTS learning_objectives " +
                    "(ID_OBJECTIVE       INTEGER PRIMARY KEY AUTOINCREMENT," +
                    " ID_OBJECTIVE_GLOBAL      INT     NOT NULL, " +
                    " OBJECTIVE      TEXT     NOT NULL, " +
                    " LEVEL_COGNITIVE_ABILITY      INT     NOT NULL, " +
                    " UNIQUE (OBJECTIVE) ); ";
            DbHelper.dbase.execSQL(sql);
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
    }

    /**
     * method for inserting new objective into table learningObjectives
     * @param
     * @throws Exception
     */
    static public void addLearningObjective(String objective, int level_cognitive_ability) throws Exception {
        try {
            String sql = 	"INSERT OR IGNORE INTO learning_objectives (ID_OBJECTIVE_GLOBAL,OBJECTIVE,LEVEL_COGNITIVE_ABILITY) " +
                    "VALUES ('" +
                    2000000 + "','" +
                    objective + "','" +
                    level_cognitive_ability +"');";
            DbHelper.dbase.execSQL(sql);
            sql = "UPDATE learning_objectives SET ID_OBJECTIVE_GLOBAL = 2000000 + ID_OBJECTIVE WHERE ID_OBJECTIVE = (SELECT MAX(ID_OBJECTIVE) FROM learning_objectives)";
            DbHelper.dbase.execSQL(sql);
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
    }
    static public Vector<String> getObjectivesForQuestionID(int questionID) {
        Vector<String> objectives = new Vector<>();
        try {
            String query = "SELECT OBJECTIVE FROM learning_objectives " +
                    "INNER JOIN question_objective_relation ON learning_objectives.OBJECTIVE = question_objective_relation.OBJECTIVE " +
                    "INNER JOIN multiple_choice_questions ON multiple_choice_questions.ID_GLOBAL = question_objective_relation.ID_GLOBAL " +
                    "WHERE multiple_choice_questions.ID_GLOBAL = '" + questionID + "';";
            Cursor cursor = DbHelper.dbase.rawQuery(query, null);
            while (cursor.moveToNext()) {
                objectives.add(cursor.getString(0));
            }
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }

        return objectives;
    }
    static public Vector<Vector<String>> getResultsPerObjective() {
        Vector<String> objectives = new Vector<>();
        Vector<String> results = new Vector<>();
        try {
            String query = "SELECT ID_GLOBAL,QUANTITATIVE_EVAL FROM individual_question_for_result;";
            Cursor cursor = DbHelper.dbase.rawQuery(query, null);            Vector<String> id_questions = new Vector<>();
            Vector<String> evaluations_for_each_question = new Vector<>();
            while (cursor.moveToNext()) {
                id_questions.add(cursor.getString(0));
                evaluations_for_each_question.add(cursor.getString(1));
            }
            Vector<String> objectives_for_question = new Vector<>();
            for (int i = 0; i < id_questions.size(); i++) {
                query = "SELECT OBJECTIVE FROM question_objective_relation " +
                        "WHERE ID_GLOBAL = '" + id_questions.get(i) + "';";
                Cursor cursor2 = DbHelper.dbase.rawQuery(query, null);
                while (cursor2.moveToNext()) {
                    objectives_for_question.add(cursor2.getString(0));
                    //multiplies each evaluation for a specific question by the number of objectives attributed to the question
                    evaluations_for_each_question.insertElementAt(evaluations_for_each_question.get(objectives_for_question.size() - 1), objectives_for_question.size());
                }
                evaluations_for_each_question.remove(objectives_for_question.size());
            }
            for (int i = 0; i < objectives_for_question.size(); i++) {
                if (!objectives.contains(objectives_for_question.get(i))) {
                    objectives.add(objectives_for_question.get(i));
                    results.add(evaluations_for_each_question.get(i));
                } else {
                    int old_result_index = objectives.indexOf(objectives_for_question.get(i));
                    double old_result = Double.parseDouble(results.get(old_result_index));
                    old_result += Double.parseDouble(evaluations_for_each_question.get(i));
                    results.set(old_result_index,String.valueOf(old_result));
                }
            }
            for (int i = 0; i < results.size(); i++) {
                double result_for_averaging = Double.parseDouble(results.get(i));
                int number_occurences = Collections.frequency(objectives_for_question,objectives.get(i));
                results.set(i,String.valueOf(result_for_averaging/number_occurences));
            }
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
        Vector<Vector<String>> vectors = new Vector<Vector<String>>();
        vectors.add(objectives);
        vectors.add(results);
        return vectors;
    }
}
