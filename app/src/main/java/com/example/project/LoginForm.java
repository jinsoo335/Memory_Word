package com.example.project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
//import com.nhn.android.naverlogin.OAuthLogin;
//import com.nhn.android.naverlogin.OAuthLoginHandler;

import java.util.ArrayList;

public class LoginForm extends AppCompatActivity implements View.OnClickListener{

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 10;

    Button googleSignBtn;
    Button annonymousBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_form);

        // 서버의 클라이언트 ID를 requestIdToken메소드에 전달해야 한다.
// <string name="default_web_client_id" translatable="false">773840236497-q3tpj1nlqgh0ekm7u4m8avdurhk7q2pn.apps.googleusercontent.com</string>
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("773840236497-q3tpj1nlqgh0ekm7u4m8avdurhk7q2pn.apps.googleusercontent.com")
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // FirebaseAuth 객체의 공유 인스턴스를 가져온다.
        mAuth = FirebaseAuth.getInstance();

        annonymousBtn = findViewById(R.id.anonymous_btn);
        annonymousBtn.setOnClickListener(this);

        googleSignBtn = findViewById(R.id.Button_glogin);
        googleSignBtn.setOnClickListener(this);

    }

    // 생명주기 함수로 onCreate() 이후 실행 된다.
    // mAuth.getCurrentUser()메서드로 현재 로그인 되어 있는지 확인한다. -> 로그인 되어 있으면 사용자의 계정 데이터를 가져옴
    // Button이 눌리는 상태는 resumed 상태...
    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        Log.d("LoginForm의 ", "user " + getUser());
        updateUI(currentUser);

        if(getUser() != null){
            Log.d("LoginForm의 ", "user " + getUser().getUid());

        }
    }

    // SignInIntent를 구글 클라이언트 측에 보낸다. startActivityForResult메서드로 인텐트를 보내서
    // 보낸 인텐트에 대해 결과를 onActivityResult로 받을 준비를 한다.
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    //startActivityForResult에 대해 result값을 받는 함수...
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // signInIntent로 보낸게 맞는지.. 옆에 requestCode를 같이 보내 같은 requestcode를 받으면 동일 인텐트
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign이 성공했다면, GoogleSignInAccount 객체에서 ID토큰을 가져와서
                // Firebase 사용자 인증 정보로 교환하고 해당 정보를 사용해 Firebase에 인증
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());


            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
            }
        }
    }

    // 사용자가 정상적으로 로그인 하면 GoogleSignInAccout 객체에서 ID 토큰을 가져와서 Firebase 사용자 인증 정보로
    // 교환하고 해당 정보를 사용해 Firebase에 인증한다.
    private void firebaseAuthWithGoogle(String idToken) {
        // signInWithCredential 에 대한 호출이 성공하면
        // getCurrentUser 메소드로 사용자의 계정 데이터를 가져올 수 있다.
        // 구글 로그인 결과로 파이어베이스에 로그인
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign이 성공하면 , 현재 유저 정보를 보낸다.
                            // FirebaseUser 객체에서 사용자의 기본 프로필 정보를 가져올 수 있더,
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // sign이 실패 했을 때는 null을 보낸다,,,
                            updateUI(null);
                        }
                    }
                });
    }

    // 익명으로 로그인 하는 부분
    // 로그인이 맞긴 한데.. 기본적으로 익명 계정을 하나 생성함...
    // 이전에 쓰던 익명 계정을 사용하려면 그냥 getCurrentUser()로 현재 로그인 된 것을 호출하자
    // 로그 아웃 하거나 앱을 삭제 하지 않는 한 기본적으로 이전 로그인 정보가 남는다.
    private void signInAnonymously(){
        mAuth.signInAnonymously().addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Log.d("익명 로그인", "성공했다");
                    FirebaseUser user = mAuth.getCurrentUser();
                    updateUI(user);
                }
                else{
                    Log.d("익명", "실패함");
                    updateUI(null);
                }
            }
        });
    }


    public void unlink(String providerId){
        mAuth.getCurrentUser().unlink(providerId)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Log.d("유저 삭제","성공");
                        }
                    }
                });
    }

    // user 삭제
    public void deleteUser(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        user.delete();
    }


    private void updateUI(FirebaseUser user) {

    }


    // 현재 유저 정보 얻기..
    public static FirebaseUser getUser(){
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    @Override
    public void onClick(View view) {
        if(view == googleSignBtn){
            signIn();
            Thread signThread = new Thread("signThread"){
                @Override
                public void run() {
                    super.run();
                    try {
                        ArrayList<String> nameList = new ArrayList<>();
                        Thread.sleep(1800);
                        ReadAndWrite DBHelper = new ReadAndWrite(getUser().getUid(), nameList, new ArrayList<>(), new ArrayList<>());
                        DBHelper.getFirstListListener();

                        Thread.sleep(500);
                        Intent intent = new Intent(LoginForm.this, MainActivity.class);
                        intent.putExtra("nameList", nameList);
                        startActivity(intent);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            };
            signThread.start();

        }
        else if(view == annonymousBtn){
            signInAnonymously();
            Thread signThread = new Thread("signThread"){
                @Override
                public void run() {
                    super.run();
                    try {
                        ArrayList<String> nameList = new ArrayList<>();
                        Thread.sleep(1500);
                        ReadAndWrite DBHelper = new ReadAndWrite(FirebaseAuth.getInstance().getCurrentUser().getUid(), nameList, new ArrayList<>(), new ArrayList<>());
                        DBHelper.getFirstListListener();

                        Thread.sleep(100);
                        Intent intent = new Intent(LoginForm.this, MainActivity.class);
                        intent.putExtra("nameList", nameList);
                        startActivity(intent);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            };
            signThread.start();
        }
    }
}