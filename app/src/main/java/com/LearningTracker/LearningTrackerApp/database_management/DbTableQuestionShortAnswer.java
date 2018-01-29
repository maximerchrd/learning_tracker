package com.LearningTracker.LearningTrackerApp.database_management;

import android.database.Cursor;
import android.util.Log;

import com.LearningTracker.LearningTrackerApp.Questions.QuestionMultipleChoice;
import com.LearningTracker.LearningTrackerApp.Questions.QuestionShortAnswer;

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
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
        return questionShortAnswer;
    }
}
