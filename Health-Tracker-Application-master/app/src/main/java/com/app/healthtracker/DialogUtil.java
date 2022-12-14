package com.app.healthtracker;

import android.app.ProgressDialog;
import android.content.Context;

public class DialogUtil {
    private static ProgressDialog dialog;
    public static void showProgress(Context context)
    {
        if(dialog!=null)try {
            dialog.hide();
        }catch (Exception ex){ex.printStackTrace();}
        try {
            dialog = ProgressDialog.show(context, "", "Please wait...");
            dialog.setIndeterminate(true);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
    public static void hideProgress(){
        if(dialog!=null){
            dialog.hide();
            dialog=null;
        }
    }
}
