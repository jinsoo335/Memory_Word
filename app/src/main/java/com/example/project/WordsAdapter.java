package com.example.project;

import static android.view.View.GONE;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class WordsAdapter extends ArrayAdapter<Listitem>{

    String userID;
    String listName;
    ArrayList<String> meanList;
    ArrayList<String> spellingList;
    ReadAndWrite DBHelper;

    Context context;
    int resID;
    ArrayList<Listitem> items;

    TextView woodImage;
    ImageView wordDeleteView;
    LinearLayout linear;
    View emptyView;

    public WordsAdapter(Context context, int resID, ArrayList<Listitem> items, String userID,
                        String listName, ArrayList<String> meanList, ArrayList<String> spellingList){
        super(context, resID);

        this.context = context;
        this.resID = resID;
        this.items = items;
        this.userID = userID;

        this.listName = listName;
        this.meanList = meanList;
        this.spellingList = spellingList;
        this.userID = userID;

        DBHelper = new ReadAndWrite(userID, new ArrayList<>(), meanList, spellingList);

    }

    @Override
    public int getCount() {
        return items.size();
    }

    //이 메서드가 리스트를 어떻게 보여줄 것인가..
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(resID, null);
            WordHolder holder = new WordHolder(convertView);
            convertView.setTag(holder);
        }

        WordHolder holder = (WordHolder) convertView.getTag();

        TextView meanText = holder.meanText;
        TextView spellingText = holder.spellingText;

        woodImage = holder.woodImage;
        emptyView = holder.emptyView;
        linear = holder.linear;
        wordDeleteView = holder.wordDeleteView;

        Listitem item = items.get(position);

        meanText.setText(item.getListsize());
        spellingText.setText(item.getListname());


        wordDeleteView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("삭제 키...", spellingText.getText().toString());
                Log.d(listName, DBHelper.userDatabase.getKey());
                String spellingName = spellingText.getText().toString();
                DBHelper.userDatabase.child(listName).child(spellingName).removeValue();

                holder.meanText.setVisibility(GONE);
                holder.spellingText.setVisibility(GONE);
                holder.woodImage.setVisibility(GONE);
                holder.wordDeleteView.setVisibility(GONE);
                holder.emptyView.setVisibility(GONE);
                holder.linear.setVisibility(GONE);
            }
        });

        return convertView;
    }



}
