package com.example.smartdialer;


import android.app.Application;
import android.util.Log;

import com.example.smartdialer.services.MainService;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
public class SocketConn {
    private static Socket mSocket;
    PrefManager prefManager;
    private static SocketConn ourInstance = new SocketConn();
    {
        try {
            IO.Options opts = new IO.Options();
            opts.reconnection = true;
            opts.reconnectionDelay = 5000;
            opts.reconnectionDelayMax = 999999999;
            prefManager = new PrefManager(MainService.getContextOfApplication());
            //mSocket = IO.socket("http://13.37.112.215:4000");
            mSocket = IO.socket(prefManager.getString("host"));
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
    public static SocketConn getInstance() {
        return ourInstance;
    }
    public Socket getSocket() {
        return mSocket;
    }
}
