package com.LearningTracker.LearningTrackerApp.database_management;

import android.database.Cursor;
import android.util.Log;

import com.LearningTracker.LearningTrackerApp.Questions.QuestionMultipleChoice;
import com.LearningTracker.LearningTrackerApp.Questions.QuestionShortAnswer;

import java.util.ArrayList;

/**
 * Created by maximerichard on 03.01.18.
 */
public class DbTableQuestionShortAnswer {
    static public void createTableQuestionShortAnswer() {
        try {
            String sql = "CREATE TABLE IF NOT EXISTS short_answer_questions " +
                    "(ID_QUESTION       INTEGER PRIMARY KEY AUTOINCREMENT," +
                    " LEVEL      INT     NOT NULL, " +
                    " QUESTION           TEXT    NOT NULL, " +
                    " IMAGE_PATH           TEXT    NOT NULL, " +
                    " ID_GLOBAL           INT    NOT NULL, " +
                    " UNIQUE(ID_GLOBAL)) ";
            DbHelper.dbase.execSQL(sql);
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
    }

    /**
     * method for inserting new question into table multiple_choice_question
     * @param quest
     * @throws Exception
     */
    static public void addShortAnswerQuestion(QuestionShortAnswer quest) throws Exception {
        try {
            String sql = 	"INSERT OR IGNORE INTO short_answer_questions (LEVEL,QUESTION,IMAGE_PATH,ID_GLOBAL) " +
                    "VALUES ('" +
                    quest.getLEVEL() + "','" +
                    quest.getQUESTION().replace("'","''") + "','" +
                    quest.getIMAGE().replace("'","''") + "','" +
                    quest.getID() +"');";
            DbHelper.dbase.execSQL(sql);
            for (int i = 0; i < quest.getAnswers().size(); i++) {
                DbTableAnswerOptions.addAnswerOption(String.valueOf(quest.getID()),quest.getAnswers().get(i));
            }
            Log.v("insert shrtaQuest, ID: ", String.valueOf(quest.getID()));
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
    }
    static public QuestionShortAnswer getShortAnswerQuestionWithId(int globalID) {
        QuestionShortAnswer questionShortAnswer = new QuestionShortAnswer();
        try {
            String selectQuery = "SELECT  LEVEL,QUESTION,IMAGE_PATH FROM short_answer_questions WHERE ID_GLOBAL=" + globalID + ";";
            //DbHelper.dbase = DbHelper.getReadableDatabase();
            Cursor cursor = DbHelper.dbase.rawQuery(selectQuery, null);
            // looping through all rows and adding to list
            if (cursor.moveToPosition(0)) {
                questionShortAnswer.setLEVEL(cursor.getString(0));
                questionShortAnswer.setQUESTION(cursor.getString(1));
                questionShortAnswer.setIMAGE(cursor.getString(2));
            }

            //get answers
            ArrayList<String> answers = new ArrayList<>();
            selectQuery = "SELECT OPTION FROM answer_options " +
                    "INNER JOIN question_answeroption_relation ON answer_options.ID_ANSWEROPTION_GLOBAL = question_answeroption_relation.ID_ANSWEROPTION_GLOBAL " +
                    "INNER JOIN short_answer_questions ON question_answeroption_relation.ID_GLOBAL = short_answer_questions.ID_GLOBAL " +
                    "WHERE short_answer_questions.ID_GLOBAL = '" + globalID +"';";
            Cursor cursor2 = DbHelper.dbase.rawQuery(selectQuery, null);
            while ( cursor2.moveToNext() ) {
                answers.add(cursor2.getString(0));
            }
            questionShortAnswer.setAnswers(answers);
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
        return questionShortAnswer;
    }
}
