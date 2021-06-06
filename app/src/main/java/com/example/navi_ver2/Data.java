package com.example.navi_ver2;

import com.google.gson.annotations.SerializedName;

public class Data {

    @SerializedName("msg1")
    private String msg1;

    @SerializedName("msg2")
    private String msg2;

    @SerializedName("msg3")
    private String msg3;

    public String getMsg1() {
        return msg1;
    }

    public String getMsg2() {
        return msg2;
    }

    public String getMsg3() {
        return msg3;
    }
}
