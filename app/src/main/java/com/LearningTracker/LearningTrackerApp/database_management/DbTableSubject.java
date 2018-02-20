package com.LearningTracker.LearningTrackerApp.database_management;

import android.database.Cursor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Vector;

/**
 * Created by maximerichard on 20.02.18.
 */
public class DbTableSubject {
    static public void createTableSubject() {
        try {
            String sql = "CREATE TABLE IF NOT EXISTS subjects " +
                    "(ID_SUBJECT       INTEGER PRIMARY KEY AUTOINCREMENT," +
                    " ID_SUBJECT_GLOBAL      INT     NOT NULL, " +
                    " SUBJECT           TEXT    NOT NULL, UNIQUE (SUBJECT)); ";
            DbHelper.dbase.execSQL(sql);
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
    }
    static public void addSubject(String subject) {
        try {
            String sql = 	"INSERT OR IGNORE INTO subjects (ID_SUBJECT_GLOBAL,SUBJECT) " +
                    "VALUES ('" +
                    2000000 + "','" +
                    subject +"');";
            DbHelper.dbase.execSQL(sql);
            sql = "UPDATE subjects SET ID_SUBJECT_GLOBAL = 2000000 + ID_SUBJECT WHERE ID_SUBJECT = (SELECT MAX(ID_SUBJECT) FROM subjects);";
            DbHelper.dbase.execSQL(sql);
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
    }
    static public Vector<String> getSubjectsForQuestionID(int questionID) {
        Vector<String> subjects = new Vector<>();
        try {
            String query = "SELECT SUBJECT FROM subjects " +
                    "INNER JOIN question_subject_relation ON subjects.ID_SUBJECT_GLOBAL = question_subject_relation.ID_SUBJECT_GLOBAL " +
                    "INNER JOIN generic_questions ON generic_questions.ID_GLOBAL = question_subject_relation.ID_GLOBAL " +
                    "WHERE generic_questions.ID_GLOBAL = '" + questionID + "';";
            Cursor cursor = DbHelper.dbase.rawQuery(query, null);
            while (cursor.moveToNext()) {
                subjects.add(cursor.getString(0));
            }
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }

        return subjects;
    }
    static public Vector<String> getAllSubjects() {
        Vector<String> subjects = new Vector<>();
        try {
            String query = "SELECT SUBJECT FROM subjects;";
            Cursor cursor = DbHelper.dbase.rawQuery(query, null);
            while (cursor.moveToNext()) {
                subjects.add(cursor.getString(0));
            }
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }

        return subjects;
    }
    static public Vector<String> getSubjectsWithParent(String parentSubject) {
        Vector<String> subjects = new Vector<>();
        String query = "";
        if (parentSubject.contentEquals("")) {
            query = "SELECT SUBJECT FROM subjects " +
                    "WHERE ID_SUBJECT_GLOBAL NOT IN (SELECT ID_SUBJECT_GLOBAL_CHILD FROM subject_subject_relation);";
        } else {
            query = "SELECT SUBJECT FROM subjects " +
                    "INNER JOIN subject_subject_relation ON subjects.ID_SUBJECT_GLOBAL = subject_subject_relation.ID_SUBJECT_GLOBAL_CHILD " +
                    "WHERE subject_subject_relation.ID_SUBJECT_GLOBAL_PARENT = (select ID_SUBJECT_GLOBAL from subjects where SUBJECT='" + parentSubject + "');";
        }
        try {

            Cursor cursor = DbHelper.dbase.rawQuery(query, null);
            while (cursor.moveToNext()) {
                subjects.add(cursor.getString(0));
            }
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }

        return subjects;
    }

    static public Vector<String> getSubjectsWithChild(String childSubject) {
        Vector<String> subjects = new Vector<>();
        String query = "SELECT SUBJECT FROM subjects " +
                "INNER JOIN subject_subject_relation ON subjects.ID_SUBJECT_GLOBAL = subject_subject_relation.ID_SUBJECT_GLOBAL_PARENT " +
                "WHERE subject_subject_relation.ID_SUBJECT_GLOBAL_CHILD = (select ID_SUBJECT_GLOBAL from subjects where SUBJECT='" + childSubject + "');";

        try {
            Cursor cursor = DbHelper.dbase.rawQuery(query, null);
            while (cursor.moveToNext()) {
                subjects.add(cursor.getString(0));
            }
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }

        return subjects;
    }
}
