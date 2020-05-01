package com.application.anywherenamaaz;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;


public class NameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_name);

    }

    public void nameSubmit(View v){
        EditText editText = findViewById(R.id.userName);
        String name = editText.getText().toString();


        Log.d("The user name is", name);
    }

}
