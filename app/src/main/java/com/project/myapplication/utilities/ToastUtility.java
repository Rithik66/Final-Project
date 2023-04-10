package com.project.myapplication.utilities;

import android.content.Context;
import android.widget.Toast;

public class ToastUtility {

    private static ToastUtility toastUtility;
    private static Context context;
    private ToastUtility(){}

    public static ToastUtility getInstance(Context context){
        ToastUtility.context=context;
        if(toastUtility==null){
            toastUtility = new ToastUtility();
        }
        return toastUtility;
    }

    public void shortToast(String message){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public void longToast(String message){
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    public void exceptionToast(String error,String from){
        Toast.makeText(context, from+" : "+error, Toast.LENGTH_SHORT).show();
    }
}
