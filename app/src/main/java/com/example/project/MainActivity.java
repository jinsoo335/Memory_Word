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

    EditText listname;
    EditText meanView;
    EditText spellingView;
    Button saveBtn;
    Button loadBtn;
    Button deleteBtn;
    ListView listView;

    Button signPageBtn;
    Button signMergeBtn;
    Button signDeleteBtn;

    ArrayList<String> nameList;
    ArrayList<String> meanList;
    ArrayList<String> spellingList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listname= findViewById(R.id.listname);
        meanView = findViewById(R.id.mean_view);
        spellingView = findViewById(R.id.spelling_view);
        saveBtn = findViewById(R.id.save_btn);
        loadBtn = findViewById(R.id.load_btn);
        deleteBtn = findViewById(R.id.delete_btn);
        listView = findViewById(R.id.list_view);

        quizBtn = findViewById(R.id.quiz_btn);
        quizBtn.setOnClickListener(this);

        signPageBtn = findViewById(R.id.sign_page);
        signPageBtn.setOnClickListener(this);

        signMergeBtn = findViewById(R.id.sign_merge_btn);
        signMergeBtn.setOnClickListener(this);

        signDeleteBtn = findViewById(R.id.sign_delete_btn);
        signDeleteBtn.setOnClickListener(this);

        saveBtn.setOnClickListener(this);
        loadBtn.setOnClickListener(this);
        deleteBtn.setOnClickListener(this);

        nameList = new ArrayList<>();
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

        String listName = listname.getText().toString();
        String mean = meanView.getText().toString();
        String spelling = spellingView.getText().toString();

        if(view == saveBtn){
            if(!listName.equals("") && !mean.equals("") && !spelling.equals("")){
                DBHelper.writeNewWord(listName, mean, spelling);
            }
        }
        else if(view == loadBtn){
            //DBHelper.writeNewList("new!");
            //DBHelper.writeNewWord("new!", mean, spelling);
            //DBHelper.updateWord(listName, mean, spelling);
        }
        else if(view == signPageBtn){
            startActivity(new Intent(this, LoginForm.class));
        }
        else if(view == deleteBtn){
            if(!listName.equals("") && !mean.equals("") && !spelling.equals("")){
                DBHelper.deleteWord(listName, spelling);
            }
        }
        else if(view == quizBtn){
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

        else if(view == signMergeBtn){
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken("773840236497-q3tpj1nlqgh0ekm7u4m8avdurhk7q2pn.apps.googleusercontent.com")
                    .requestEmail()
                    .build();

            GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, 15);
        }
        else if(view == signDeleteBtn){
            if(user != null){
                deleteUser();
            }
            else{
                Toast toast = Toast.makeText(this, "로그인 되어 있는 유저가 없습니다. ", Toast.LENGTH_SHORT);
                toast.show();
            }
        }

        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, meanList);
        listView.setAdapter(arrayAdapter);
    }

    private void deleteUser(){
        String UID = user.getUid();
        user.delete();

        DBHelper.rootDatabase.child(UID).removeValue();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // signInIntent로 보낸게 맞는지.. 옆에 requestCode를 같이 보내 같은 requestcode를 받으면 동일 인텐트
        if (requestCode == 15) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign이 성공했다면, GoogleSignInAccount 객체에서 ID토큰을 가져와서
                // Firebase 사용자 인증 정보로 교환하고 해당 정보를 사용해 Firebase에 인증
                GoogleSignInAccount account = task.getResult(ApiException.class);

                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                Log.d("합병쪽 crendtial", credential + "");
                linkAndMerge(credential);

            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
            }
        }
    }

    // 계정 합치기...
    public void linkAndMerge(AuthCredential credential){
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        FirebaseUser prevUser = FirebaseAuth.getInstance().getCurrentUser();
        String prevUID = prevUser.getUid();

        FirebaseAuth.getInstance().signOut();
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        FirebaseUser currentUser = task.getResult().getUser();
                        String currentUID = currentUser.getUid();
                        Log.d("current user", currentUID);
                        Log.d("prev user", prevUID);

                        ArrayList<String> prevNameList = new ArrayList<>();
                        ArrayList<String> prevMeanList = new ArrayList<>();
                        ArrayList<String> prevSpellingList = new ArrayList<>();

                        ArrayList<String> currentNameList = new ArrayList<>();
                        ArrayList<String> currentMeanList = new ArrayList<>();
                        ArrayList<String> currentSpellingList = new ArrayList<>();

                        ReadAndWrite prevDBHelper = new ReadAndWrite(prevUID, prevNameList, prevMeanList, prevSpellingList);
                        ReadAndWrite currentDBHelper = new ReadAndWrite(currentUID, currentNameList, currentMeanList, currentSpellingList);
                        prevDBHelper.getFirstListListener();
                        currentDBHelper.getFirstListListener();

                        Thread copyThread = new Thread("copyThread"){
                            @Override
                            public void run() {
                                super.run();
                                try {
                                    Thread.sleep(10);
                                    for(int i =0; i < prevDBHelper.nameList.size(); i++){
                                        String listName = prevDBHelper.nameList.get(i);
                                        prevDBHelper.getFirstListListener(listName);
                                        currentDBHelper.getFirstListListener(listName);
                                        Thread.sleep(100);

                                        for(int j =0; j < prevDBHelper.meanList.size(); j++){
                                            Log.d(j + "번 mean List", prevMeanList.get(j));
                                            String mean = prevMeanList.get(j);
                                            String spelling = prevSpellingList.get(j);
                                            currentDBHelper.userDatabase.child(listName).child(spelling).setValue(mean);
                                            currentDBHelper.writeNewWord(listName, mean, spelling);
                                        }
                                    }
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        };
                        copyThread.start();



                    }
                });
    }

}