package com.example.project;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class MeanAndSpellingPage extends AppCompatActivity implements View.OnClickListener{

    TextView title;
    ListView listView;

    String listName;
    ArrayList<String> meanList;
    ArrayList<String> spellingList;
    String userID;

    //1
    EditText meanView;
    EditText spellingView;
    Button saveBtn;
    ReadAndWrite DBHelper;

    Button testFixBtn;
    Button testDelBtn;

    Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mean_and_spelling_page);

        title = findViewById(R.id.title);
        listView = findViewById(R.id.mean_spelling_list);

        listName =  (String) getIntent().getSerializableExtra("list name");
        meanList =  (ArrayList<String>) getIntent().getSerializableExtra("meanList");
        spellingList =  (ArrayList<String>) getIntent().getSerializableExtra("spellingList");
        userID = (String)getIntent().getSerializableExtra("UID");

        DBHelper = new ReadAndWrite(userID, new ArrayList<>(), meanList, spellingList);
        Log.d("list name 은 ", listName);

        ArrayList<Listitem> items = new ArrayList<>();
        for(int i =0; i < spellingList.size(); i++){
            items.add(new Listitem(spellingList.get(i), meanList.get(i)));
        }

//        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, meanList);
//        listView.setAdapter(arrayAdapter);

        WordsAdapter wordsAdapter = new WordsAdapter(this, R.layout.word_listview, items, userID, listName, meanList, spellingList);
        listView.setAdapter(wordsAdapter);


        //2
        meanView= findViewById(R.id.mean_view);
        spellingView= findViewById(R.id.spelling_view);
        saveBtn = findViewById(R.id.save_btn);

        //3
        saveBtn.setOnClickListener(this);

        testFixBtn = findViewById(R.id.test_fix_btn);
        testDelBtn = findViewById(R.id.test_del_btn);

        testFixBtn.setOnClickListener(this);
        testDelBtn.setOnClickListener(this);

    }

    //4
    @Override
    public void onClick(View view){
        String mean = meanView.getText().toString();
        String spelling = spellingView.getText().toString();

        if(view == saveBtn){
            if(!mean.equals("") && !spelling.equals("")){
                DBHelper.writeNewWord(listName, mean, spelling);
            }
        }
        else if(view == testFixBtn){
            if(!mean.equals("") && !spelling.equals("")){
                DBHelper.writeNewWord(listName, mean, spelling);
            }

        }
        else if(view == testDelBtn){
            if(!mean.equals("") && !spelling.equals("")){
                DBHelper.deleteWord(listName, spelling);
                DBHelper.getFirstListListener(listName);
            }
        }

        Thread wordLoadThread = new Thread("wordLoadThread"){
            @Override
            public void run() {
                super.run();
                try {
                    Thread.sleep(100);
                    ArrayList<Listitem> items = new ArrayList<>();
                    for(int i =0; i < spellingList.size(); i++){
                        items.add(new Listitem(spellingList.get(i), meanList.get(i)));
                    }

                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            WordsAdapter wordsAdapter = new WordsAdapter(MeanAndSpellingPage.this, R.layout.word_listview, items, userID, listName, meanList, spellingList);
                            listView.setAdapter(wordsAdapter);
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        wordLoadThread.start();
    }
}