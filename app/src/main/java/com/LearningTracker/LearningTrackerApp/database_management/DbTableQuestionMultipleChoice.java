package com.LearningTracker.LearningTrackerApp.database_management;

import android.database.Cursor;
import android.util.Log;

import com.LearningTracker.LearningTrackerApp.Questions.QuestionMultipleChoice;

/**
 * Created by maximerichard on 03.01.18.
 */
public class DbTableQuestionMultipleChoice {
    static public void createTableQuestionMultipleChoice() {
        try {
            String sql = "CREATE TABLE IF NOT EXISTS multiple_choice_questions " +
                    "(ID_QUESTION       INTEGER PRIMARY KEY AUTOINCREMENT," +
                    " LEVEL      INT     NOT NULL, " +
                    " QUESTION           TEXT    NOT NULL, " +
                    " OPTION0           TEXT    NOT NULL, " +
                    " OPTION1           TEXT    NOT NULL, " +
                    " OPTION2           TEXT    NOT NULL, " +
                    " OPTION3           TEXT    NOT NULL, " +
                    " OPTION4           TEXT    NOT NULL, " +
                    " OPTION5           TEXT    NOT NULL, " +
                    " OPTION6           TEXT    NOT NULL, " +
                    " OPTION7           TEXT    NOT NULL, " +
                    " OPTION8           TEXT    NOT NULL, " +
                    " OPTION9           TEXT    NOT NULL, " +
                    " TRIAL0           TEXT    NOT NULL, " +
                    " TRIAL1           TEXT    NOT NULL, " +
                    " TRIAL2           TEXT    NOT NULL, " +
                    " TRIAL3           TEXT    NOT NULL, " +
                    " TRIAL4           TEXT    NOT NULL, " +
                    " TRIAL5           TEXT    NOT NULL, " +
                    " TRIAL6           TEXT    NOT NULL, " +
                    " TRIAL7           TEXT    NOT NULL, " +
                    " TRIAL8           TEXT    NOT NULL, " +
                    " TRIAL9           TEXT    NOT NULL, " +
                    " NB_CORRECT_ANS        INT     NOT NULL, " +
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
    static public void addMultipleChoiceQuestion(QuestionMultipleChoice quest) throws Exception {
        try {
            String sql = 	"INSERT OR IGNORE INTO multiple_choice_questions (LEVEL,QUESTION,OPTION0," +
                    "OPTION1,OPTION2,OPTION3,OPTION4,OPTION5,OPTION6,OPTION7,OPTION8,OPTION9,TRIAL0,TRIAL1,TRIAL2,TRIAL3,TRIAL4,TRIAL5,TRIAL6,TRIAL7," +
                    "TRIAL8,TRIAL9,NB_CORRECT_ANS,IMAGE_PATH,ID_GLOBAL) " +
                    "VALUES ('" +
                    quest.getLEVEL() + "','" +
                    quest.getQUESTION().replace("'","''") + "','" +
                    quest.getOPT0().replace("'","''") + "','" +
                    quest.getOPT1().replace("'","''") + "','" +
                    quest.getOPT2().replace("'","''") + "','" +
                    quest.getOPT3().replace("'","''") + "','" +
                    quest.getOPT4().replace("'","''") + "','" +
                    quest.getOPT5().replace("'","''") + "','" +
                    quest.getOPT6().replace("'","''") + "','" +
                    quest.getOPT7().replace("'","''") + "','" +
                    quest.getOPT8().replace("'","''") + "','" +
                    quest.getOPT9().replace("'","''") + "','" +
                    quest.getTRIAL0() + "','" +
                    quest.getTRIAL1() + "','" +
                    quest.getTRIAL2() + "','" +
                    quest.getTRIAL3() + "','" +
                    quest.getTRIAL4() + "','" +
                    quest.getTRIAL5() + "','" +
                    quest.getTRIAL6() + "','" +
                    quest.getTRIAL7() + "','" +
                    quest.getTRIAL8() + "','" +
                    quest.getTRIAL9() + "','" +
                    quest.getNB_CORRECT_ANS() + "','" +
                    quest.getIMAGE().replace("'","''") + "','" +
                    quest.getID() +"');";
            DbHelper.dbase.execSQL(sql);
            Log.v("insert multQuest, ID: ", String.valueOf(quest.getID()));
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
    }
    static public QuestionMultipleChoice getQuestionWithId(int globalID) {
        QuestionMultipleChoice questionMultipleChoice = new QuestionMultipleChoice();
        try {
            String selectQuery = "SELECT  LEVEL,QUESTION,OPTION0,OPTION1,OPTION2,OPTION3,OPTION4,OPTION5,OPTION6,OPTION7,OPTION8,OPTION9,NB_CORRECT_ANS," +
                    "IMAGE_PATH FROM multiple_choice_questions WHERE ID_GLOBAL=" + globalID + ";";
            //DbHelper.dbase = DbHelper.getReadableDatabase();
            Cursor cursor = DbHelper.dbase.rawQuery(selectQuery, null);
            // looping through all rows and adding to list
            if (cursor.moveToPosition(0)) {
                questionMultipleChoice.setLEVEL(cursor.getString(0));
                questionMultipleChoice.setQUESTION(cursor.getString(1));
                questionMultipleChoice.setOPT0(cursor.getString(2));
                questionMultipleChoice.setOPT1(cursor.getString(3));
                questionMultipleChoice.setOPT2(cursor.getString(4));
                questionMultipleChoice.setOPT3(cursor.getString(5));
                questionMultipleChoice.setOPT4(cursor.getString(6));
                questionMultipleChoice.setOPT5(cursor.getString(7));
                questionMultipleChoice.setOPT6(cursor.getString(8));
                questionMultipleChoice.setOPT7(cursor.getString(9));
                questionMultipleChoice.setOPT8(cursor.getString(10));
                questionMultipleChoice.setOPT9(cursor.getString(11));
                questionMultipleChoice.setNB_CORRECT_ANS(Integer.valueOf(cursor.getString(12)));
                questionMultipleChoice.setIMAGE(cursor.getString(13));
            }
            questionMultipleChoice.setID(globalID);
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
        return questionMultipleChoice;
    }
}
