package com.LearningTracker.LearningTrackerApp.database_management;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DbHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "learning_tracker.db";  //added the ".db" extension because onCreate() wasn't called anymore
    // tasks table name
    private static final String TABLE_QUEST = "quest";
    // tasks Table Columns names for the question table
    private static final String KEY_ID = "id";
    private static final String KEY_SUBJECT = "subject";
    private static final String KEY_LEVEL = "level";
    private static final String KEY_QUES = "question";
    private static final String KEY_ANSWER = "answer"; //correct option
    private static final String KEY_OPTA= "opta"; //option a
    private static final String KEY_OPTB= "optb"; //option b
    private static final String KEY_OPTC= "optc"; //option c
    private static final String KEY_OPTD= "optd"; //option d
    private static final String KEY_IMAGE = "image";
    private static final String KEY_TRIAL1 = "questscore1"; //good answer on first trial
    private static final String KEY_TRIAL2 = "questscore2";
    private static final String KEY_TRIAL3 = "questscore3";
    private static final String KEY_TRIAL4 = "questscore4";

    // tasks table name
    private static final String TABLE_SCORES = "scores";
    // tasks Table Columns names for the score table
    private static final String KEY_IDscores = "idscores";
    private static final String KEY_TIME = "time";
    private static final String KEY_SUBJECTscores = "subjectscores";
    private static final String KEY_SCORE = "score";
    private static final String KEY_LEVELscore = "levelscore";

    // tasks table settings
    private static final String TABLE_SETTINGS = "settings";
    // tasks Table Columns names for the settings table
    private static final String KEY_IDsettings = "idsettings";
    private static final String KEY_NAME = "name";
    private static final String KEY_MASTER = "master";


    public static SQLiteDatabase dbase;
    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        dbase=db;
        //add table of questions
        String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_QUEST + " ( "
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + KEY_SUBJECT + " TEXT, "+ KEY_LEVEL + " TEXT, " +
                KEY_QUES + " TEXT, " + KEY_ANSWER+ " TEXT, "+KEY_OPTA +" TEXT, "
                +KEY_OPTB +" TEXT, "+KEY_OPTC+" TEXT, "+KEY_OPTD +" TEXT, "+KEY_TRIAL1 +" TEXT, "
                +KEY_TRIAL2 +" TEXT, "+KEY_TRIAL3+" TEXT, "+KEY_TRIAL4 +" TEXT,"+KEY_IMAGE+" TEXT)";
        db.execSQL(sql);
        //addQuestions();

        //add table for scores
        String sql2 = "CREATE TABLE IF NOT EXISTS " + TABLE_SCORES + " ( "
                + KEY_IDscores + " INTEGER PRIMARY KEY AUTOINCREMENT, " + KEY_TIME + " TEXT, "+ KEY_SUBJECTscores + " TEXT, " +
                KEY_SCORE + " TEXT, " +	KEY_LEVELscore +" TEXT)";
        db.execSQL(sql2);

        //add table for settings
        String sql3 = "CREATE TABLE IF NOT EXISTS " + TABLE_SETTINGS + " ( "
                + KEY_IDsettings + " INTEGER PRIMARY KEY AUTOINCREMENT, " + KEY_NAME +" TEXT," + KEY_MASTER + " TEXT)";
        db.execSQL(sql3);
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, "Anonyme");
        values.put(KEY_MASTER, "192.168.1.100");
        // Inserting of Replacing Row
        dbase.insert(TABLE_SETTINGS, null, values);

        //Create other tables if it doesn't exist
        DbTableQuestionMultipleChoice.createTableQuestionMultipleChoice();
        DbTableLearningObjective.createTableLearningObjectives();
        DbTableRelationQuestionObjective.createTableRelationQuestionObjectives();
        DbTableIndividualQuestionForResult.createTableIndividualQuestionForResult();
        DbTableQuestionShortAnswer.createTableQuestionShortAnswer();
        Log.v("database: ", "finished creating tables");
        //db.close();
    }
    //add new name
    public void addName(String newname)
    {
        //SQLiteDatabase db = this.getWritableDatabase();
        dbase=this.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, newname);
        // Replacing Row
        dbase.update(TABLE_SETTINGS, values, null, null);
    }
    //get name from db
    public String getName() {
        // Select All Query
        String name = "";
        String selectQuery = "SELECT  * FROM " + TABLE_SETTINGS;
        dbase=this.getReadableDatabase();
        Cursor cursor = dbase.rawQuery(selectQuery, null);
        if (cursor.moveToPosition(0)) {
            name = cursor.getString(1);
        }
        // return string name
        return name;
    }
    //add new name
    public void addMaster(String newname)
    {
        //SQLiteDatabase db = this.getWritableDatabase();
        dbase=this.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_MASTER, newname);
        // Replacing Row
        dbase.update(TABLE_SETTINGS, values, null, null);
    }
    //get name from db
    public String getMaster() {
        // Select All Query
        String master = "";
        String selectQuery = "SELECT  * FROM " + TABLE_SETTINGS;
        dbase=this.getReadableDatabase();
        Cursor cursor = dbase.rawQuery(selectQuery, null);
        if (cursor.moveToPosition(0)) {
            master = cursor.getString(2);
        }
        // return string name
        return master;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_QUEST);
        // Create tables again
        onCreate(db);
    }
}
