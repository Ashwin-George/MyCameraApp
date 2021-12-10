package com.example.android.mycamera;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class RecognizedTextFragment extends Fragment {
    //This constructor is necessary for instantiating fragment
    public RecognizedTextFragment() {

    }
    private TextView recognizedTextBox;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.fragment_text_recognized,container,false);
        recognizedTextBox=(TextView) rootView.findViewById(R.id.recognizedText);
        recognizedTextBox.setMovementMethod(new ScrollingMovementMethod());
        recognizedTextBox.setText("This is the Fragment to show recognized text");
        return rootView;
    }
    public void setRecognizedText(String text){
        recognizedTextBox.setText(text);
    }


}
