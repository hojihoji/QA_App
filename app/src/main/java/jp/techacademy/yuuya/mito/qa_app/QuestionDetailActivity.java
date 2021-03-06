package jp.techacademy.yuuya.mito.qa_app;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Yuya on 2018/02/11.
 */

public class QuestionDetailActivity extends AppCompatActivity {

    //private int favoriteStatus ;//課題追記
    private FloatingActionButton favoriteFab;//課題追記

    private ListView mListView;
    private Question mQuestion;
    private QuestionDetailListAdapter mAdapter;

    private DatabaseReference mAnswerRef;
    //private DatabaseReference mDatabaseReference;//課題追記

    boolean mFavoriteFlag = false;

    private ChildEventListener mFavoriteListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            mFavoriteFlag = true;
            favoriteFab.setImageResource(R.drawable.favorite);
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    private ChildEventListener mEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            HashMap map = (HashMap) dataSnapshot.getValue();

            String answerUid = dataSnapshot.getKey();

            for(Answer answer : mQuestion.getAnswers()){
                //同じAnswerUidのものが存在しているときは何もしない
                if(answerUid.equals(answer.getAnswerUid())){
                    return;
                }
            }

            String body = (String) map.get("body");
            String name = (String) map.get("name");
            String uid = (String) map.get("uid");



            Answer answer = new Answer(body, name, uid, answerUid);
            mQuestion.getAnswers().add(answer);
            mAdapter.notifyDataSetChanged();

        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {


        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    @Override
    protected void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_question_detail);

        //渡ってきたQuestionのオブジェクトを保持する
        Bundle extras = getIntent().getExtras();
        mQuestion = (Question) extras.get("question");

        setTitle(mQuestion.getTitle());

        //ListViewの準備
        mListView = (ListView) findViewById(R.id.listView);
        mAdapter = new QuestionDetailListAdapter(this, mQuestion);
        mListView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                //ログイン済みのユーザーを取得する
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                if(user == null){
                    //ログインしていなければログイン画面に遷移させる
                    Intent intent = new Intent(getApplicationContext(),  LoginActivity.class);
                    startActivity(intent);

                    //ログインに成功したら、favoriteFabを表示する　←追記
                    OnCompleteListener<AuthResult> mLoginListener = new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(Task<AuthResult> task){
                            //アカウント
                            if(task.isSuccessful()){
                                favoriteFab.setVisibility(View.VISIBLE);
                            }else{
                                //アカウント作成に失敗したらなにもしない
                            }
                        }
                    };

                }else{
                    //Questionを渡して回答作成画面を起動する
                    Intent intent = new Intent(getApplicationContext(), AnswerSendActivity.class);
                    intent.putExtra("question", mQuestion);
                    startActivity(intent);
                }

            }
        });

        //課題追記
        favoriteFab = (FloatingActionButton) findViewById(R.id.favoriteFab);
        //ログインしていなければfavoriteFabを表示しない
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null) {
            favoriteFab.setVisibility(View.INVISIBLE);
        }else{
            favoriteFab.setVisibility(View.VISIBLE);
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
            DatabaseReference userRef = databaseReference.child(Const.favoritePATH).child(user.getUid()).child(mQuestion.getQuestionUid());
            userRef.addChildEventListener(mFavoriteListener);
        }

        //課題追記お気に入りボタンの初期設定
        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference();
        DatabaseReference favoriteRef = databaseReference1.child(Const.favoritePATH).child(user.getUid()).child(String.valueOf(mQuestion.getQuestionUid()));
        String favoriteUid = mQuestion.getQuestionUid().toString();
        DatabaseReference questionRef = databaseReference1.child(Const.ContentsPATH).child(String.valueOf(mQuestion.getGenre())).child(mQuestion.getQuestionUid());
        String questionUid = mQuestion.getQuestionUid().toString();
        if(favoriteRef.equals(questionUid)){
            favoriteFab.setImageResource(R.drawable.favorite);
            mFavoriteFlag = true;
        }else{
            favoriteFab.setImageResource(R.drawable.notfavorite);
            mFavoriteFlag = false;
        }




        favoriteFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mFavoriteFlag == false){
                    //お気に入り状態でないのでお気に入りにする
                    favoriteFab.setImageResource(R.drawable.favorite);
                    //ここに追記する
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                    DatabaseReference favoriteRef = databaseReference.child(Const.favoritePATH).child(user.getUid()).child(String.valueOf(mQuestion.getQuestionUid()));
                    Map<String, String> data = new HashMap<String, String>();
                    String favoriteUid = mQuestion.getQuestionUid().toString();
                    data.put("genre", String.valueOf(mQuestion.getGenre()));
                    favoriteRef.setValue(data);

                    Snackbar.make(v, "お気に入りに追加しました", Snackbar.LENGTH_LONG).show();

                }else {
                    //お気に入り状態なのでお気に入りから外す
                    favoriteFab.setImageResource(R.drawable.notfavorite);

                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                    DatabaseReference favoriteRef = databaseReference.child(Const.favoritePATH).child(user.getUid()).child(String.valueOf(mQuestion.getQuestionUid()));
                    favoriteRef.setValue(null);

                    Snackbar.make(v, "お気に入りから削除しました", Snackbar.LENGTH_LONG).show();
                }
                mFavoriteFlag = !mFavoriteFlag;
            }
        });



        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        mAnswerRef = databaseReference.child(Const.ContentsPATH).child(String.valueOf(mQuestion.getGenre())).child(mQuestion.getQuestionUid()).child(Const.AnswersPATH);
        mAnswerRef.addChildEventListener(mEventListener);

    }
}
