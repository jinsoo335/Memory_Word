package com.example.project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class SettingPage extends AppCompatActivity implements View.OnClickListener{

    FirebaseUser user;
    ReadAndWrite DBHelper;

    Button signMergeBtn;
    Button signDeleteBtn;
    Button signOutBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_page);

        getSupportActionBar().setIcon(R.drawable.tree_icon);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        signMergeBtn = findViewById(R.id.setting_sign_merge_btn);
        signMergeBtn.setOnClickListener(this);

        signDeleteBtn = findViewById(R.id.setting_sign_delete_btn);
        signDeleteBtn.setOnClickListener(this);

        signOutBtn = findViewById(R.id.setting_signOut_btn);
        signOutBtn.setOnClickListener(this);

        user = FirebaseAuth.getInstance().getCurrentUser();

    }

    @Override
    public void onClick(View view) {
        updateUI();
        if(view == signDeleteBtn){
            if(user != null){
                deleteUser(FirebaseAuth.getInstance().getCurrentUser());
                startActivity(new Intent(SettingPage.this, LoginForm.class));
            }
            else{
                Toast toast = Toast.makeText(this, "????????? ?????? ?????? ????????? ????????????. ", Toast.LENGTH_SHORT);
                toast.show();
            }
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
        else if(view == signOutBtn){
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(SettingPage.this, LoginForm.class));
        }
    }

    private void updateUI(){
        Log.d("update", "?????? ??????...");
        user = FirebaseAuth.getInstance().getCurrentUser();

    }


    private void deleteUser(FirebaseUser user){
        String UID = user.getUid();

        try{
            DatabaseReference rt = FirebaseDatabase.getInstance().getReference();
            rt.child(UID).removeValue();
        }
        catch (Exception e){
            if(e instanceof NullPointerException){
                Log.d("NullPointerException", "DB??? ????????? ?????? ??????");
            }
        }
        user.delete();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // signInIntent??? ????????? ?????????.. ?????? requestCode??? ?????? ?????? ?????? requestcode??? ????????? ?????? ?????????
        if (requestCode == 15) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign??? ???????????????, GoogleSignInAccount ???????????? ID????????? ????????????
                // Firebase ????????? ?????? ????????? ???????????? ?????? ????????? ????????? Firebase??? ??????
                GoogleSignInAccount account = task.getResult(ApiException.class);

                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                Log.d("????????? crendtial", credential + "");
                linkAndMerge(credential);

            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
            }
        }
    }

    // ?????? ?????????...
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

                        if(currentUID.equals(prevUID)){
                            return;
                        }

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
                                            Log.d(j + "??? mean List", prevMeanList.get(j));
                                            String mean = prevMeanList.get(j);
                                            String spelling = prevSpellingList.get(j);
                                            currentDBHelper.userDatabase.child(listName).child(spelling).setValue(mean);
                                            currentDBHelper.writeNewWord(listName, mean, spelling);
                                        }
                                    }

                                    deleteUser(prevUser);
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