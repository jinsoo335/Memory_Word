package com.example.project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class ScoringPage extends AppCompatActivity {

    // QuizPage 액티비티에서 받은 arraylist, UserId 저장 변수들
    ArrayList<String> firebaseNameList;
    ArrayList<String> firebaseMeanList;
    ArrayList<String> firebaseSpellingList;

    ArrayList<String> ScoringFirebaseMeanList = new ArrayList<>();
    ArrayList<String> ScoringFirebaseSpellingList = new ArrayList<>();

    String UserId;

    TextView textView1;
    TextView textView2;
    TextView textView3;
    Button button;
    Button button2;

    // 틀린 문제 번호 저장 리스트
    ArrayList<Integer> wrongCheckList;

    // 전 리스트 전체 문제 갯수
    int i;

    //틀린 문제 페이지 초기화 변수
    // int scoringDivide;


    //뜻, 스펠링 구분
    int divide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scoring_page);

        textView1 = findViewById(R.id.textview1);
        textView2 = findViewById(R.id.textview2);
        textView3 = findViewById(R.id.textview3);
        button = findViewById(R.id.button);
        button2 = findViewById(R.id.button2);


        // int correct = ((QuizPage)QuizPage.context_correct).correct;
        // int wrong = ((QuizPage)QuizPage.context_wrong).wrong;
        // int all = ((QuizPage)QuizPage.context_allQuestion).allQuestion;







    }

    @Override
    protected void onStart() {
        super.onStart();

        firebaseNameList =  (ArrayList<String>) getIntent().getSerializableExtra("nameList");
        firebaseMeanList =  (ArrayList<String>) getIntent().getSerializableExtra("meanList");
        firebaseSpellingList =  (ArrayList<String>) getIntent().getSerializableExtra("spellingList");
        wrongCheckList = (ArrayList<Integer>) getIntent().getSerializableExtra("wrongCheck");
        divide = (Integer) getIntent().getSerializableExtra("divide");
        UserId = (String)getIntent().getSerializableExtra("UID");
        i = (int)getIntent().getSerializableExtra("lastAll");
        Log.d("wrongListSize", wrongCheckList.size() + "");


        // scoringDivide = 100;

        textView2.setText("맞은 갯수="+(i-wrongCheckList.size())+", 틀린 갯수="+wrongCheckList.size());
        textView3.setText((i-wrongCheckList.size())+"/"+i);

        ListView listView;
        ScoringPage_ListViewAdapter adapter;


        // 다 맞을 시 '틀린 문제 다시 풀기 버튼 제거'
        if(wrongCheckList.size() == 0){
            button.setVisibility(View.GONE);
        }

        // Adapter 생성
        adapter = new ScoringPage_ListViewAdapter() ;

        // 리스트뷰 참조 및 Adapter달기
        listView = (ListView) findViewById(R.id.listview1);
        final View header = getLayoutInflater().inflate(R.layout.header_scoringlistview, null, false) ;
        listView.addHeaderView(header) ;

        listView.setAdapter(adapter);


        for(int j = 0; j < wrongCheckList.size(); j++) {
            // 아이템 추가.
            // ArrayList 틀린 문제로 수정
            adapter.addItem(wrongCheckList.get(j)+1,
                    firebaseMeanList.get(wrongCheckList.get(j)), firebaseSpellingList.get(wrongCheckList.get(j)));
            ScoringFirebaseMeanList.add(firebaseMeanList.get(wrongCheckList.get(j)));
            ScoringFirebaseSpellingList.add(firebaseSpellingList.get(wrongCheckList.get(j)));

        }
    }


    public void onClick (View view) {

        // ((QuizPage)QuizPage.context_correct).correct = 0;
        // ((QuizPage)QuizPage.context_wrong).wrong = 0;
        if (view == button) {
            Intent intent = new Intent(ScoringPage.this, WrongPage.class);


            intent.putExtra("nameList", firebaseNameList);
            intent.putExtra("ScoringMeanList", ScoringFirebaseMeanList);
            intent.putExtra("ScoringSpellingList", ScoringFirebaseSpellingList);
            intent.putExtra("divide", divide);
            // intent.putExtra("ScoringDivide", scoringDivide);


            startActivity(intent);
        }
        if(view == button2){

            Intent intent = new Intent(this, MainActivity.class);

            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

            startActivity(intent);
        }
    }



}