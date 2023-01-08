package com.example.smartdialer.contacts;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.jar.JarException;

public class ContactsManager {
    public static JSONArray getContacts(Context context) {
        JSONArray list = new JSONArray();
        try {
            Cursor cur = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, new String[]{ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER}, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
            int indexName = cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
            int indexNumber = cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            while (cur.moveToNext()) {
                JSONObject contactInfo = new JSONObject();
                String name = cur.getString(indexName);
                String number = cur.getString(indexNumber);
                contactInfo.put("name", name);
                contactInfo.put("number", number);
                list.put(contactInfo);
            }

        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return list;
    }
}
