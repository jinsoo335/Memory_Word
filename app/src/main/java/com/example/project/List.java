package com.example.project;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class List extends AppCompatActivity implements View.OnClickListener{
    ListView listView;

    TextView showText;

    ReadAndWrite DBHelper;

    ArrayList<String> nameList;
    ArrayList<String> meanList;
    ArrayList<String> spellingList;
    String userID;

    //1
    EditText listname;
    Button saveBtn;

    Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        showText = findViewById(R.id.show_list_btn);

        nameList =  (ArrayList<String>) getIntent().getSerializableExtra("nameList");
        userID = (String)getIntent().getSerializableExtra("UID");
        meanList =  (ArrayList<String>) getIntent().getSerializableExtra("meanList");
        spellingList =  (ArrayList<String>) getIntent().getSerializableExtra("spellingList");
        DBHelper = new ReadAndWrite(userID, nameList, meanList, spellingList);


        listView = findViewById(R.id.listview);

        ArrayList<Listitem> items = new ArrayList<>();
        for(int i =0; i < nameList.size(); i++){
            Log.d("list name get", nameList.get(i));
            items.add(new Listitem(nameList.get(i), ""));
        }

        ListitemAdapter ListAdapter = new ListitemAdapter(this, R.layout.item_listview, items,
                userID ,new ArrayList<>(), meanList, spellingList);

        listView.setAdapter(ListAdapter);

        //listView.setOnItemClickListener(this);


        //2
        listname= findViewById(R.id.listname);
        saveBtn = findViewById(R.id.save_btn);

        //3
        saveBtn.setOnClickListener(this);

    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    public void onClick(View view){
        String listName = listname.getText().toString();

        if(view == saveBtn){
            if(!listName.equals("")){
                DBHelper.writeNewList(listName);
            }
        }

        Thread itemLoadThread = new Thread("itemLoadThread"){
            @Override
            public void run() {
                super.run();
                try {
                    Thread.sleep(100);
                    ArrayList<Listitem> items = new ArrayList<>();
                    for(int i =0; i < nameList.size(); i++){
                        Log.d("list name get", nameList.get(i));
                        items.add(new Listitem(nameList.get(i), ""));
                    }

                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            ListitemAdapter ListAdapter = new ListitemAdapter(List.this, R.layout.item_listview, items,
                                    userID ,new ArrayList<>(), meanList, spellingList);

                            listView.setAdapter(ListAdapter);
                        }
                    });

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        itemLoadThread.start();



    }

}