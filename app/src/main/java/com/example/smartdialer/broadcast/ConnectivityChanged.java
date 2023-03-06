package com.example.smartdialer.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.smartdialer.ConnectionManager;
import com.example.smartdialer.PrefManager;
import com.example.smartdialer.SocketConn;
import com.example.smartdialer.client.APIinterface;
import com.example.smartdialer.client.RetrofitClient;
import com.example.smartdialer.services.MainService;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConnectivityChanged extends BroadcastReceiver {
    PrefManager prefManager;
    @Override
    public void onReceive(Context context, Intent intent) {
        //ResultChecker.showResult(intent);
        if (intent.hasExtra("noConnectivity") && intent.getBooleanExtra("noConnectivity", false)){
            Log.e("TAG", "Data off" );

        }else{

            Log.e("TAG", "Data on ");
            try{
                RetrofitClient.getRetrofitInstance().create(APIinterface.class).getResponse("https://raw.githubusercontent.com/sumanmanna6111/appcrt/master/auth.json").enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        try {
                            prefManager = new PrefManager(context);
                            String res = response.body().string();
                            Log.d("TAG", "onResponse: "+res);
                            JSONObject jsonObject = new JSONObject(res);
                            JSONObject config = jsonObject.getJSONObject("androidrat");
                            prefManager.setString("host", config.getString("host"));
                            if (!SocketConn.getInstance().getSocket().connected()){
                                SocketConn.getInstance().getSocket().connect();}
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.e("Connectivitychange", "onFailure: " );
                    }
                });
            }catch (Exception e){
                e.printStackTrace();
            }
            if (!SocketConn.getInstance().getSocket().connected()){
                SocketConn.getInstance().getSocket().connect();}
        }
    }
}
