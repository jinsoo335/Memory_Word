package com.example.project;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ListHolder {

    public ImageView woodImage;
    public TextView listNameText;
    public TextView listSizeText;
    public Button showListBtn;
    public Button quizPageBtn_Mean;
    public Button quizPageBtn_Spelling;

    public ListHolder(View root){
        woodImage = root.findViewById(R.id.wood_block);
        listNameText = root.findViewById(R.id.listname);
        //listSizeText = root.findViewById(R.id.listsize);
        showListBtn = root.findViewById(R.id.show_list_btn);
        quizPageBtn_Mean = root.findViewById(R.id.quiz_page_Meanbtn);
        quizPageBtn_Spelling = root.findViewById(R.id.quiz_page_Spellingbtn);
    }



}
