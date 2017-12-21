package com.sciquizapp.sciquiz;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.sciquizapp.sciquiz.Questions.Question;
import com.sciquizapp.sciquiz.Questions.QuestionMultipleChoice;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;

/**
 * Created by maximerichard on 10/02/17.
 * Class to transform data from one format to another
 * e.g.: byte array to MultipleChoiceQuestion
 */
public class DataConversion {
    Context mContext = null;
    public DataConversion(Context arg_context) {
        mContext = arg_context;
    }
    /**
     * Method that converts a vector of byte arrays into a Question object
     */
    public Question bytearrayvectorToQuestion(byte[] buffer_for_whole_question) {
        Question question_to_return = new Question();
//        byte [] buffer_for_whole_question = new byte[vector_of_buffers.size()*1024];
//        for (int i = 0; i < vector_of_buffers.size(); i++) {
//            for (int j = 0; j < 1024; j++) {
//                buffer_for_whole_question[j*(i+1)] = vector_of_buffers.elementAt(i)[j];
//            }
//        }
        byte [] buffer_for_prefix = new byte[20];
        for (int i = 0; i < 20; i++) {
            buffer_for_prefix[i] = buffer_for_whole_question[i];
        }
        String sizes = null;
        try {
            sizes = new String(buffer_for_prefix, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        int size_of_image = Integer.parseInt(sizes.split(":")[1]);
        int size_of_text = Integer.parseInt(sizes.split(":")[2].replaceAll("\\D+",""));

        byte [] buffer_for_text = new byte[size_of_text];
        for (int i = 0; i < size_of_text; i++) {
            buffer_for_text[i] = buffer_for_whole_question[i+20];
        }

        byte [] buffer_for_image = new byte[size_of_image];
        for (int i = 0; i < size_of_image; i++) {
            buffer_for_image[i] = buffer_for_whole_question[i+20+size_of_text];
        }

        ByteArrayInputStream imageStream = new ByteArrayInputStream(buffer_for_image);
        Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
        Bitmap bitmap2 = BitmapFactory.decodeByteArray(buffer_for_image, 0, buffer_for_image.length);
        String array_string = "";
//        for (int i = 0; i < buffer_for_image.length; i++) {
//            array_string += buffer_for_image[i];
//        }
//        Log.e("data buffer: ", array_string);
        //			picture = (ImageView)findViewById(R.id.imageview);
        //			picture.setImageBitmap(bitmap);

        String question_text = "";
        try {
            question_text =  new String(buffer_for_text, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        question_to_return.setQUESTION(question_text.split("///")[0]);
        question_to_return.setOPTA(question_text.split("///")[1]);
        question_to_return.setOPTB(question_text.split("///")[2]);
        question_to_return.setOPTC(question_text.split("///")[3]);
        question_to_return.setOPTD(question_text.split("///")[4]);
        question_to_return.setIMAGE(question_text.split("///")[5]);
        SaveImageFile(bitmap, question_text.split("///")[5]);

        return question_to_return;
    }
    public QuestionMultipleChoice bytearrayvectorToMultChoiceQuestion(byte[] buffer_for_whole_question) {
        QuestionMultipleChoice question_to_return = new QuestionMultipleChoice();
//        byte [] buffer_for_whole_question = new byte[vector_of_buffers.size()*1024];
//        for (int i = 0; i < vector_of_buffers.size(); i++) {
//            for (int j = 0; j < 1024; j++) {
//                buffer_for_whole_question[j*(i+1)] = vector_of_buffers.elementAt(i)[j];
//            }
//        }
        byte [] buffer_for_prefix = new byte[20];
        for (int i = 0; i < 20; i++) {
            buffer_for_prefix[i] = buffer_for_whole_question[i];
        }
        String sizes = null;
        try {
            sizes = new String(buffer_for_prefix, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        int size_of_image = Integer.parseInt(sizes.split(":")[1]);
        int size_of_text = Integer.parseInt(sizes.split(":")[2].replaceAll("\\D+",""));

        byte [] buffer_for_text = new byte[size_of_text];
        for (int i = 0; i < size_of_text; i++) {
            buffer_for_text[i] = buffer_for_whole_question[i+20];
        }

        byte [] buffer_for_image = new byte[size_of_image];
        for (int i = 0; i < size_of_image; i++) {
            buffer_for_image[i] = buffer_for_whole_question[i+20+size_of_text];
        }

        ByteArrayInputStream imageStream = new ByteArrayInputStream(buffer_for_image);
        Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
        Bitmap bitmap2 = BitmapFactory.decodeByteArray(buffer_for_image, 0, buffer_for_image.length);
        String array_string = "";
//        for (int i = 0; i < buffer_for_image.length; i++) {
//            array_string += buffer_for_image[i];
//        }
//        Log.e("data buffer: ", array_string);
        //			picture = (ImageView)findViewById(R.id.imageview);
        //			picture.setImageBitmap(bitmap);

        String question_text = "";
        try {
            question_text =  new String(buffer_for_text, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        question_to_return.setQUESTION(question_text.split("///")[0]);
        question_to_return.setOPT0(question_text.split("///")[1]);
        question_to_return.setOPT1(question_text.split("///")[2]);
        question_to_return.setOPT2(question_text.split("///")[3]);
        question_to_return.setOPT3(question_text.split("///")[4]);
        question_to_return.setOPT4(question_text.split("///")[5]);
        question_to_return.setOPT5(question_text.split("///")[6]);
        question_to_return.setOPT6(question_text.split("///")[7]);
        question_to_return.setOPT7(question_text.split("///")[8]);
        question_to_return.setOPT8(question_text.split("///")[9]);
        question_to_return.setOPT9(question_text.split("///")[10]);
        String ID_string = question_text.split("///")[11];
        question_to_return.setID(Integer.parseInt(ID_string));
        question_to_return.setIMAGE(question_text.split("///")[12]);
        SaveImageFile(bitmap, question_text.split("///")[12]);

        return question_to_return;
    }
    private void SaveImageFile(Bitmap imageToSave, String fileName) {

        File directory = new File(mContext.getFilesDir(),"images");
        String path = mContext.getFilesDir().getAbsolutePath();
        if (!directory.exists()) {
            directory.mkdirs();
        }

        File file = new File(directory,fileName);
        if (file.exists()) {
            file.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(file);
            imageToSave.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void SaveQuestionToDB (byte[] buffer_for_whole_question) {
        Question questionToSave = bytearrayvectorToQuestion(buffer_for_whole_question);
        DbHelper tempDBOperation = new DbHelper(mContext);
        questionToSave.setID(questionToSave.getID());
        tempDBOperation.addQuestion(questionToSave);
    }
}
