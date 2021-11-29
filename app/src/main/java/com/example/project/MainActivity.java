package com.example.project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    public static int check = 0;


    FirebaseUser user;
    String userID;
    String listName;
    ReadAndWrite DBHelper;

    Button quizBtn;
    Button setting_btn;

    EditText listname;
    EditText meanView;
    EditText spellingView;
    Button saveBtn;
    Button loadBtn;
    Button deleteBtn;
    ListView listView;



    ArrayList<String> nameList;
    ArrayList<String> meanList;
    ArrayList<String> spellingList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setIcon(R.drawable.tree_icon);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        quizBtn = findViewById(R.id.quiz_btn);
        quizBtn.setOnClickListener(this);

        setting_btn =findViewById(R.id.setting_btn);
        setting_btn.setOnClickListener(this);

        if( getIntent().getSerializableExtra("nameList") == null){
            nameList = new ArrayList<>();
        }
        else{
            nameList = (ArrayList<String>) getIntent().getSerializableExtra("nameList");
        }
        meanList = new ArrayList<>();
        spellingList = new ArrayList<>();


    }


    // 현재 LoginForm액티비티를 onCreate()에서 실행시킨다...
    // 메인 엑티비티는 oncreate -> onStart -> resumed -> paused -> stopped 상태를 지나 restart가 호출되면서
    // 다시 onStart()가 실행된다.
    // onCreate()에서 userID를 받는 행위를 할 경우 정상적으로 받아지지 않을 가능성이 존재한다.
    // 앞에서의 로그인 과정을 수행한 후 다시 onStart()가 호출 했을 때 userID를 받아 현재 유저를 갱신하는 일을 한다.
    @Override
    protected void onStart() {
        super.onStart();
        updateUI();
    }

    private void updateUI(){
        Log.d("update", "먼저 호출...");
        if(user == null){
            user = FirebaseAuth.getInstance().getCurrentUser();
        }
        if(user != null && DBHelper == null){
            userID = user.getUid();
            DBHelper = new ReadAndWrite(userID, nameList, meanList, spellingList);
            DBHelper.getFirstListListener();
        }
        else if(DBHelper != null){
            DBHelper.getFirstListListener();
        }
    }

    // 넘어가는데 있어 파이어 베이스에서 데이터를 가져오는 시간이 걸리는 것 같다.
    // Thread.sleep()으로 시간을 강제로 지연시키면 전달이 잘 되는 것으로 보아
    // 파이어 베이스에 데이터가 많을 경우 더 많은 시간이 지연 될 수 있을 것으로 보인다.
    @Override
    public void onClick(View view) {
        updateUI();

        if(view == quizBtn){
            // 스레드 생성할 때 이렇게...
            Thread meanThread = new Thread("mean Thread"){
                public void run(){
                    //DBHelper.getFirstListListener(nameList.get(0));
                    //DBHelper.addWordEventListener(DBHelper.userDatabase.child(nameList.get(0)));
                    Log.d("mean list", "" + spellingList.size());

                    try {
                        Thread.sleep(100);
                        Intent intent = new Intent(MainActivity.this, List.class);
                        intent.putExtra("nameList", nameList);
                        intent.putExtra("meanList", meanList);
                        intent.putExtra("spellingList", spellingList);
                        intent.putExtra("UID", userID);

                        startActivity(intent);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            meanThread.start();
        }

        else if(view == setting_btn){
            Intent intent = new Intent(this, SettingPage.class);
            startActivity(intent);

        }

    }
}