package com.LearningTracker.LearningTrackerApp.Questions;

import java.util.Vector;

/**
 * Created by maximerichard on 26.10.17.
 */
public class QuestionShortAnswer {
    private int ID;
    private String SUBJECT;
    private String LEVEL;
    private String QUESTION;
    private String IMAGE;

    private Vector<String> subjects;
    private Vector<String> objectives;
    public QuestionShortAnswer()	{
        ID=0;
        SUBJECT="";
        LEVEL="";
        QUESTION="";
        IMAGE="none";
    }
    public QuestionShortAnswer(String lEVEL, String qUESTION, String iMAGE) {

        LEVEL = lEVEL;
        QUESTION = qUESTION;
        if (iMAGE.length() == 0) {
            IMAGE = "none";
        } else {
            IMAGE = iMAGE;
        }
    }
    public int getID()
    {
        return ID;
    }
    public String getSUBJECT() {
        return SUBJECT;
    }
    public String getLEVEL() {
        return LEVEL;
    }
    public String getQUESTION() {
        return QUESTION;
    }
    public String getIMAGE() {
        return IMAGE;
    }
    public Vector<String> getSubjects() {
        return subjects;
    }
    public Vector<String> getObjectives() {
        return objectives;
    }
    public void setID(int id)
    {
        ID=id;
    }
    public void setSUBJECT(String sUBJECT) {
        SUBJECT = sUBJECT;
    }
    public void setLEVEL(String lEVEL) {
        LEVEL = lEVEL;
    }
    public void setQUESTION(String qUESTION) {
        QUESTION = qUESTION;
    }
    public void setIMAGE(String iMAGE) {
        if (iMAGE.length() == 0) {
            IMAGE = "none";
        } else {
            IMAGE = iMAGE;
        }
    }
    public void setSubjects(Vector<String> subjects) {
        this.subjects = subjects;
    }
    public void setObjectives(Vector<String> objectives) {
        this.objectives = objectives;
    }
}
