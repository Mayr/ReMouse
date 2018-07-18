package com.mayurm.remouse;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.Socket;

public class Client extends Thread {

    public DataOutputStream outToServer;
    public BufferedReader inFromServer;
    public Socket socket;

    private boolean connectSuccess = false;


    @Override
    public void interrupt() {
        try{
            socket.close();
            Log.d("a",""+socket.isClosed());
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            super.interrupt();
        }
    }

    public boolean setUpBuffers(){

        try {
            outToServer = new DataOutputStream(socket.getOutputStream());
            inFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            return true;
        }catch (IOException io){
            Log.e("SETUPBUFFER","IO "+io.getMessage());
            return false;
        }

    }

}
