package com.example.navi_ver2;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.example.googlemap.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Timer;

public class MainActivity extends AppCompatActivity {

    private ImageButton imageButtonn;
    private EditText sample_EditText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
