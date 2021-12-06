package com.example.project;


import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WordHolder {
    TextView meanText;
    TextView spellingText;
    TextView woodImage;
    ImageView wordDeleteView;
    LinearLayout linear;
    View emptyView;

    public WordHolder(View root){
        meanText = root.findViewById(R.id.mean_text);
        spellingText = root.findViewById(R.id.spelling_text);;
        woodImage = root.findViewById(R.id.word_wood_block);
        wordDeleteView = root.findViewById(R.id.word_delete_view);;
        linear = root.findViewById(R.id.word_linear_layout);;
        emptyView = root.findViewById(R.id.empty_view);;
    }


}
