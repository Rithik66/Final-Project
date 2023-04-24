package com.project.myapplication.utilities;

import android.webkit.JavascriptInterface;

import com.project.myapplication.activities.CallActivity;


public class CustomJavascriptInterface {

    CallActivity callActivity;

    public CustomJavascriptInterface(CallActivity callActivity) {
        this.callActivity = callActivity;
    }

    @JavascriptInterface
    public void onPeerConnected(){

    }
}
