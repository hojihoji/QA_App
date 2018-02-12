package jp.techacademy.yuuya.mito.qa_app;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Yuya on 2018/02/11.
 */

public class Question implements Serializable{
    private String mTitle;
    private String mBody;
    private String mName;
    private String mUid;
    private String mQuestionUid;
    private int mGenre;
    private int mFavorite;
    private byte[] mBitmapArray;
    private ArrayList<Answer> mAnswerArrayList;

    public String getTitle(){
        return mTitle;
    }

    public String getBody(){
        return mBody;
    }

    public String getName(){
        return mName;
    }

    public String getUid(){
        return mUid;
    }

    public String getQuestionUid(){
        return mQuestionUid;
    }

    public int getGenre(){
        return mGenre;
    }

    public int getFavorite(){
        return mFavorite;
    }

    public byte[] getImageByte(){
        return mBitmapArray;
    }

    public ArrayList<Answer> getAnswers(){
        return mAnswerArrayList;
    }

    public Question(String title, String body, String name, String uid, String questionUid, int genre, int favorite, byte[]bytes, ArrayList<Answer> answers){
        mTitle = title;
        mBody = body;
        mName = name;
        mUid = uid;
        mQuestionUid = questionUid;
        mGenre = genre;
        mFavorite = favorite; //追記
        mBitmapArray = bytes.clone();
        mAnswerArrayList = answers;

    }
}
