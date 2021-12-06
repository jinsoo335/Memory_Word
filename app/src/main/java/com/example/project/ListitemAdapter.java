package com.example.project;
import static android.view.View.GONE;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class ListitemAdapter extends ArrayAdapter<Listitem>{
    String userID;
    ArrayList<String> nameList;
    ArrayList<String> meanList;
    ArrayList<String> spellingList;
    ReadAndWrite DBHelper;

    Button showListBtn;
    Button quizPageMeanBtn;
    Button quizPageSpellingBtn;

    ImageView delView;
    TextView woodImage;
    // 뜻, 스펠링 구분 정수
    int divide;

    Context context;
    int resID;
    ArrayList<Listitem> items;

    android.os.Handler handler = new android.os.Handler();

    public ListitemAdapter(Context context, int resID, ArrayList<Listitem> items, String userID,
                           ArrayList<String> nameList, ArrayList<String> meanList, ArrayList<String> spellingList){
        super(context, resID);
        this.context = context;
        this.resID = resID;
        this.items = items;
        this.userID = userID;

        this.nameList = nameList;
        this.meanList = meanList;
        this.spellingList = spellingList;
        this.userID = userID;

        DBHelper = new ReadAndWrite(userID, nameList, meanList, spellingList);
    }

    //ArrayList크기 리턴, 리스트뷰에 생성될 아이템 수
    @Override
    public int getCount() {
        return items.size();
    }

//    //해당 position의 아이템을 리턴, 아이템 클릭시 아이템이 몇번째인지
////    @Override
////    public Object getItem(int position) {
////        return items.get(position);
////    }
//
    //position리턴
    @Override
    public long getItemId(int position) {
        return position;
    }
//
//    //아이템 추가
//    public void addItem(Listitem item){
//        items.add(item);
//    }

    //이 메서드가 리스트를 어떻게 보여줄 것인가..
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(resID, null);
            ListHolder holder = new ListHolder(convertView);
            convertView.setTag(holder);
        }

        ListHolder holder = (ListHolder) convertView.getTag();

        TextView listNameText = holder.listNameText;
        //TextView listSizeText = holder.listSizeText;
        showListBtn = holder.showListBtn;
        quizPageMeanBtn = holder.quizPageMeanBtn;
        quizPageSpellingBtn = holder.quizPageSpellingBtn;

        woodImage = holder.woodImage;
        delView = holder.delView;

        Listitem item = items.get(position);

        listNameText.setText(item.getListname());
        //listSizeText.setText(item.getListsize());

        delView.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Log.d("클릭...", "--------------");
                String listName = listNameText.getText().toString();
                DBHelper.userDatabase.child(listName).removeValue();

                holder.listNameText.setVisibility(GONE);
                holder.showListBtn.setVisibility(GONE);
                holder.quizPageMeanBtn.setVisibility(GONE);
                holder.quizPageSpellingBtn.setVisibility(GONE);
                holder.delView.setVisibility(GONE);
                holder.woodImage.setVisibility(GONE);



            }
        });


        quizPageMeanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                divide = 0;

                Log.d("showListBtn", "클릭은 됨?");
                Thread holderThread = new Thread("holderThread"){
                    @Override
                    public void run() {
                        super.run();
                        try {
                            // 이 부분 좀 이상함... listNameText가 내가 클릭 한 쪽의 것을 알아서 가져오긴 해서 쓰긴 했음..
                            Log.d(listNameText.getText().toString(), "호출..");
                            String listName = listNameText.getText().toString();
                            DBHelper.addWordEventListener(DBHelper.userDatabase.child(listName));
                            DBHelper.getFirstListListener(listName);

                            Thread.sleep(100);

                            Intent intent = new Intent(context, QuizPage.class);
                            intent.putExtra("nameList", nameList);
                            intent.putExtra("meanList", meanList);
                            intent.putExtra("spellingList", spellingList);
                            intent.putExtra("divide", divide);

                            if(meanList.size() != 0 ){
                                context.startActivity(intent);
                            }
                            else {


                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        AlertDialog.Builder myAlertBuilder =
                                                new AlertDialog.Builder(context);
                                        // alert의 title과 Messege 세팅
                                        myAlertBuilder.setTitle("경고");
                                        myAlertBuilder.setMessage("리스트에 단어를 추가 후, 퀴즈를 실행해 주시기 바랍니다!");
                                        // 버튼 추가 (Ok 버튼 )
                                        myAlertBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                // OK 버튼을 눌렸을 경우
                                                Toast.makeText(context.getApplicationContext(), "Pressed OK",
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                        // Alert를 생성해주고 보여주는 메소드(show를 선언해야 Alert가 생성됨)
                                        myAlertBuilder.show();
                                    }
                                });
                            }

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                };
                holderThread.start();
            }
        });

        quizPageSpellingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                divide = 1;

                Log.d("showListBtn", "클릭은 됨?");
                Thread holderThread = new Thread("holderThread"){
                    @Override
                    public void run() {
                        super.run();
                        try {
                            // 이 부분 좀 이상함... listNameText가 내가 클릭 한 쪽의 것을 알아서 가져오긴 해서 쓰긴 했음..
                            Log.d(listNameText.getText().toString(), "호출..");
                            String listName = listNameText.getText().toString();
                            DBHelper.addWordEventListener(DBHelper.userDatabase.child(listName));
                            DBHelper.getFirstListListener(listName);

                            Thread.sleep(100);

                            Intent intent = new Intent(context, QuizPage.class);
                            intent.putExtra("list name", listName);
                            intent.putExtra("meanList", meanList);
                            intent.putExtra("spellingList", spellingList);
                            intent.putExtra("divide", divide);

                            if(meanList.size() != 0 ){
                                context.startActivity(intent);
                            }
                            else {


                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        AlertDialog.Builder myAlertBuilder =
                                                new AlertDialog.Builder(context);
                                        // alert의 title과 Messege 세팅
                                        myAlertBuilder.setTitle("경고");
                                        myAlertBuilder.setMessage("리스트에 단어를 추가 후, 퀴즈를 실행해 주시기 바랍니다!");
                                        // 버튼 추가 (Ok 버튼 )
                                        myAlertBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                // OK 버튼을 눌렸을 경우
                                                Toast.makeText(context.getApplicationContext(), "Pressed OK",
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                        // Alert를 생성해주고 보여주는 메소드(show를 선언해야 Alert가 생성됨)
                                        myAlertBuilder.show();
                                    }
                                });
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
                holderThread.start();
            }
        });


        showListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("showListBtn", "클릭은 됨?");
                Thread holderThread = new Thread("holderThread"){
                    @Override
                    public void run() {
                        super.run();
                        try {
                            // 이 부분 좀 이상함... listNameText가 내가 클릭 한 쪽의 것을 알아서 가져오긴 해서 쓰긴 했음..
                            Log.d(listNameText.getText().toString(), "호출..");
                            String listName = listNameText.getText().toString();
                            DBHelper.addWordEventListener(DBHelper.userDatabase.child(listName));
                            DBHelper.getFirstListListener(listName);

                            Thread.sleep(100);

                            Intent intent = new Intent(context, MeanAndSpellingPage.class);
                            intent.putExtra("list name", listName);
                            intent.putExtra("meanList", meanList);
                            intent.putExtra("spellingList", spellingList);
                            intent.putExtra("UID", userID);

                            context.startActivity(intent);

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                };
                holderThread.start();
            }
        });

        return convertView;
    }

}
