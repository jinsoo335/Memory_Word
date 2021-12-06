package com.example.project;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ListHolder {

    public TextView woodImage;
    public TextView listNameText;
    public TextView listSizeText;
    public Button showListBtn;
    public Button quizPageMeanBtn;
    public Button quizPageSpellingBtn;
    public ImageView delView;

    public ListHolder(View root){
        woodImage = root.findViewById(R.id.wood_block);
        listNameText = root.findViewById(R.id.listname);
        //listSizeText = root.findViewById(R.id.listsize);
        showListBtn = root.findViewById(R.id.show_list_btn);
        quizPageMeanBtn = root.findViewById(R.id.quiz_page_Meanbtn);
        quizPageSpellingBtn = root.findViewById(R.id.quiz_page_Spellingbtn);
        delView = root.findViewById(R.id.del_view);
    }


}
