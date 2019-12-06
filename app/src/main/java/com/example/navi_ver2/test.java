package com.example.navi_ver2;


import android.content.Context;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.TimerTask;

public class test extends TimerTask {

    Context context;
    private static final String TAG = "MAIN";
    private RequestQueue queue;
    //private TextView tv;

    private int order =0;
    boolean[] myParkingLotCondition = new boolean[5];
    ParkingLotInfo[] myParkingLotInfo = new ParkingLotInfo[5];
    boolean[] prevParkingLotCondition = new boolean[5];

    private TextView[] light = new TextView[3];

    public test(Context context){
        this.context = context;
    }

    @Override
    public void run() {

        final ArrayList<String> value = new ArrayList<>();
        order++;
        //tv = ((Activity)context).findViewById(R.id.tvMain);
        queue = Volley.newRequestQueue(context);
        String url = "http://ja03129.cafe24.com/test.html";

        final StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                String[] words = response.split("\n");
                int check=0;
//                for(int i = 2;i<5;i++)
//                {
//                    prevParkingLotCondition[i] = false;
//                }


                for(int i=2;i<5;i++)
                {
                    System.out.println("prev "+ prevParkingLotCondition[i]);
                }
                for( String wo : words){
                    if(check!=0&&check!=1){
                        value.add(wo);
                    }
                    else
                    {
                        check++;
                        continue;
                    }

                    //System.out.println("wo: " + wo);
                    myParkingLotInfo[check] = new ParkingLotInfo();
                    //System.out.println("check = " + check);
                    for(int i = 0;i<wo.length();i++)
                    {
                        //System.out.println("한글자 " + wo.charAt(i));
                        if(wo.charAt(i) == 'y') // 주차가능
                        {
                            if(i== 0)//주차장
                            {
                                myParkingLotCondition[check] = true;
                                //System.out.println("주차장" + check + myParkingLotCondition[check]);
                            }
                            else//차
                            {
                                myParkingLotInfo[check].total++;
                                myParkingLotInfo[check].empty++;
                                //System.out.println(myParkingLotInfo[check].empty + " / " + myParkingLotInfo[check].total);
                            }
                        }
                        else if(wo.charAt(i) == 'n')
                        {
                            if(i== 0)// 주차장
                            {
                                myParkingLotCondition[check] = false;
                            }
                            else//차
                            {
                                myParkingLotInfo[check].total++;
                            }
                        }
                        else if(wo.charAt(i) == ' ')
                        {
                            continue;
                        }
                    }
                    check++;
                }
                boolean flag = true;
                for(int i = 2;i<5;i++)
                {
                    System.out.println("prev: " + prevParkingLotCondition[i] + " now: " + myParkingLotCondition[i]);
                    if(prevParkingLotCondition[i] != myParkingLotCondition[i])
                    {
                        flag = false;
                    }
                    System.out.println("여부: " + myParkingLotCondition[i] + " 가능대수: "
                            + myParkingLotInfo[i].empty + " / " + myParkingLotInfo[i].total);

                }
                System.out.println("flag = " + flag + "order" + order);

                if(!flag && order !=1)
                {
                    //새로 띄우기
//                    MapsActivity.setLightAgain mySetLight = new MapsActivity.setLightAgain();
//                    mySetLight.setLightAgainFunc();
                    MapsActivity newMap = new MapsActivity();
                    newMap.setLightAgainFunc();

                }

                for(int i = 2;i<5;i++)
                {
                    prevParkingLotCondition[i] = myParkingLotCondition[i];
                }

                //tv.setText(value.toString());
                System.out.println("value: " + value.toString());

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        stringRequest.setTag(TAG);
        queue.add(stringRequest);
    }

    public static class ParkingLotInfo
    {
        int total;
        int empty;

        public ParkingLotInfo()
        {
            total = 0;
            empty = 0;
        }
    }


}
