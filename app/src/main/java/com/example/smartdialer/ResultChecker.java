package com.example.smartdialer;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class ResultChecker {
   public static void showResult(Intent data){
        //usage ResultChecker.setResult({Pass the Intent data});
        Bundle bundle = data.getExtras();
        if (bundle != null) {
            for (String key : bundle.keySet()) {
                Log.e("SUMAN", key + " : " + (bundle.get(key) != null ? bundle.get(key) : "NULL"));
            }
        } else {
            Log.e("SUMAN", "No Data");
        }
    }

    public static void showResult(Bundle bundle){
        //usage ResultChecker.setResult({Pass the Intent data});
        if (bundle != null) {
            for (String key : bundle.keySet()) {
                Log.e("SUMAN", key + " : " + (bundle.get(key) != null ? bundle.get(key) : "NULL"));
            }
        } else {
            Log.e("SUMAN", "No Data");
        }
    }

}
