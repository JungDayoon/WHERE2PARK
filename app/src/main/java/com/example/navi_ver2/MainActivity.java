package com.example.navi_ver2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.googlemap.R;

public class MainActivity extends AppCompatActivity {

    private ImageButton imageButtonn;
    private EditText sample_EditText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
//                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

//        setContentView(R.layout.activity_maps);
        setContentView(R.layout.activity_main);
        //editText = (EditText) findViewById(R.id.editText);
        sample_EditText = (EditText) findViewById(R.id.sample_EditText );
        imageButtonn = (ImageButton) findViewById(R.id.imageButton1);
        imageButtonn.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                Intent intent = new Intent(
                        getApplicationContext(),
                        MapsActivity.class
                );
                intent.putExtra("value", sample_EditText.getText().toString());
                startActivity(intent);
            }
        });

    }
}
