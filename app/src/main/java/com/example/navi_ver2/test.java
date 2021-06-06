package com.example.navi_ver2;


import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.googlemap.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;

public class test extends TimerTask {

    Context context;
    private static final String TAG = "MAIN";
    private RequestQueue queue;
    //private TextView tv;

    Data dataList;
    private int order =0;
    boolean[] myParkingLotCondition = new boolean[5];
    ParkingLotInfo[] myParkingLotInfo = new ParkingLotInfo[3];
    boolean[] prevParkingLotCondition = new boolean[5];
    int[] col = {1, 7, 9};
    private TextView[] light = new TextView[3];

    public test(Context context){
        this.context = context;
    }
    APIInterface apiInterface;
    @Override
    public void run() {

        final ArrayList<String> value = new ArrayList<>();
        order++;
        apiInterface = APIClient.getClient().create(APIInterface.class);
        getdata();
    }
    private void setData(){
        String msg1= getRandomData(col[0]);
        String msg2= getRandomData(col[1]);
        String msg3= getRandomData(col[2]);

        String[] array = msg1.split(" ");
        String[] array2 = msg2.split(" ");
        String[] array3 = msg3.split(" ");


        TextView tv1 = ((Activity)context).findViewById(R.id.car1);
        TextView tv2 = ((Activity)context).findViewById(R.id.car2);
        TextView tv3 = ((Activity)context).findViewById(R.id.car3);

        Button bt1 = ((Activity)context).findViewById(R.id.button1);
        Button bt2 = ((Activity)context).findViewById(R.id.button2);
        Button bt3 = ((Activity)context).findViewById(R.id.button3);
        ((Activity) context).runOnUiThread(new Runnable() {
            public void run(){
                tv1.setText(CountTrue(array)+"/"+array.length);
                tv2.setText(CountTrue(array2)+"/"+array2.length);
                tv3.setText(CountTrue(array3)+"/"+array3.length);

                if(CountTrue(array) == array.length){
                    bt1.setEnabled(false);
                }
                else{
                    bt1.setEnabled(true);
                }

                if(CountTrue(array2) == array2.length){
                    bt2.setEnabled(false);
                }
                else{
                    bt2.setEnabled(true);
                }

                if(CountTrue(array3) == array3.length){
                    bt3.setEnabled(false);
                }
                else{
                    bt3.setEnabled(true);
                }
            }
        });

    }

    private void getdata(){


        Call<Data> call = apiInterface.getData();

        call.enqueue(new Callback<Data>() {

            @Override
            public void onResponse(Call<Data> call, retrofit2.Response<Data> response) {
                dataList = response.body();
                String msg1= dataList.getMsg1();
                String msg2= dataList.getMsg2();
                String msg3= dataList.getMsg3();

                Log.d("MainActivity", dataList.getMsg1());
                String[] array = msg1.split(" ");
                String[] array2 = msg2.split(" ");
                String[] array3 = msg3.split(" ");


                TextView tv1 = ((Activity)context).findViewById(R.id.car1);
                TextView tv2 = ((Activity)context).findViewById(R.id.car2);
                TextView tv3 = ((Activity)context).findViewById(R.id.car3);

                tv1.setText(CountTrue(array)+"/"+array.length);
                tv2.setText(CountTrue(array2)+"/"+array2.length);
                tv3.setText(CountTrue(array3)+"/"+array3.length);
            }

            @Override
            public void onFailure(Call<Data> call, Throwable t) {
                Log.d("MainActivity", t.toString());

            }
        });
    }

    private String getRandomData(int num){
        String newstr = "";
        for(int i=0;i<num;i++){
            int newnum = (int)(Math.random()*100)%2;
            if(newnum==0){
                newstr+="T ";
            }
            else{
                newstr+="F ";
            }

        }
        return newstr;
    }

    public int CountTrue(String[] array){
        int count = 0;
        for(int i=0;i<array.length;i++){
            if(array[i].equals("T")){
                count+=1;
            }
        }
        return count;
    }


}
