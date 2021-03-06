package com.example.project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class FirstPage extends AppCompatActivity{

    FirebaseAuth mAuth;
    FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_page);

//        getSupportActionBar().setIcon(R.drawable.tree_icon);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
//        getSupportActionBar().setDisplayUseLogoEnabled(true);


    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();

        Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(mUser != null){
                    Log.d("user", mUser.getUid());

                    ArrayList<String> nameList = new ArrayList<>();
                    ReadAndWrite DBHelper = new ReadAndWrite(mUser.getUid(), nameList, new ArrayList<>(), new ArrayList<>());
                    DBHelper.getFirstListListener();

                    Thread slowThread = new Thread("slowThread"){
                        @Override
                        public void run() {
                            super.run();
                            try {
                                Thread.sleep(1000);
                                Intent intent = new Intent(FirstPage.this, MainActivity.class);
                                intent.putExtra("nameList", nameList);
                                startActivity(intent);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    slowThread.start();


                }
                else{
                    Log.d("user", null + "");
                    startActivity(new Intent(FirstPage.this, LoginForm.class));
                }
            }
        }, 1000);
    }
}