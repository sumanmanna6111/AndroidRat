package com.example.smartdialer;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import java.io.File;

public class APKInstaller {
    public static boolean silentInstallAPK(String apkPath) {
        boolean status = false;
        File file = new File(apkPath);
        if(file.exists()){
            try {
                String command;
                command = "pm install -r " + apkPath;
                Process proc = Runtime.getRuntime().exec(new String[] { "su", "-c", command });
                proc.waitFor();
                Log.e("TAG", "APK Installed Successfully" );
                status = true;
            } catch (Exception e) {
                e.printStackTrace();
                status = false;
            }
        }
        return status;
    }

    public static void normalInstall(String apkPath, Activity context) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(new File(apkPath)), "application/vnd.android.package-archive");//Environment.getExternalStorageDirectory() + "/download/" + "app.apk"
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
