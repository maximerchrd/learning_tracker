package com.sciquizapp.sciquiz;

import java.util.Vector;

/**
 * Created by maximerichard on 10/02/17.
 * Class to transform data from one format to another
 * e.g.: byte array to MultipleChoiceQuestion
 */
public class DataConversion {

    /**
     * Method that converts a vector of byte arrays into a Question object
     */
    public Question bytearrayvectorToQuestion(Vector<byte[]> vector_of_buffers) {
        Question question_to_return = null;
        byte [] buffer_for_whole_question = new byte[vector_of_buffers.size()*1024];
        for (int i = 0; i < vector_of_buffers.size(); i++) {
            for (int j = 0; j < 1024; j++) {
                buffer_for_whole_question[j*(i+1)] = vector_of_buffers.elementAt(i)[j];
            }
        }
        return question_to_return;
    }
}
