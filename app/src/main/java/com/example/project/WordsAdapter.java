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

    View emptyView;
    ImageView wordDeleteView;

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
        }

        TextView meanText = convertView.findViewById(R.id.mean_text);
        TextView spellingText = convertView.findViewById(R.id.spelling_text);

        TextView woodImage = convertView.findViewById(R.id.word_wood_block);
        emptyView = convertView.findViewById(R.id.empty_view);
        LinearLayout linear = convertView.findViewById(R.id.word_linear_layout);

        Listitem item = items.get(position);

        meanText.setText(item.getListsize());
        spellingText.setText(item.getListname());

        wordDeleteView = convertView.findViewById(R.id.word_delete_view);
        wordDeleteView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("삭제 버튼", "누름?");
                Log.d("삭제 키...", spellingText.getText().toString());
                Log.d(listName, DBHelper.userDatabase.getKey());
                DBHelper.deleteWord(listName,spellingText.getText().toString());

                meanText.setVisibility(GONE);
                spellingText.setVisibility(GONE);
                woodImage.setVisibility(GONE);
                wordDeleteView.setVisibility(GONE);
                emptyView.setVisibility(GONE);
                linear.setVisibility(GONE);
            }
        });

        return convertView;
    }



}
