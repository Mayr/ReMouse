package com.mayurm.remouse;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class MainActivity extends AppCompatActivity implements MainFragment.MainFragmentListeners,
        MouseFragment.OnClickListener, MouseFragment.OnTouchListener, CustomGestureDetector.BrokenPipe{

    private final String TAG = "ReMouse";

    private MainFragment mainFragment;
    private MouseFragment mouseFragment;
    private GestureDetector gestureDetector;
    private CustomGestureDetector customGestureDetector;
    private RelativeLayout rootLayout;

    private Client client;

    private void postConnect(){
        Log.d(TAG,"A");
        final RelativeLayout relativeLayout = findViewById(R.id.rootLayout);
        boolean bufferReady = client.setUpBuffers();

        if(bufferReady) {
            customGestureDetector.client = client;
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.placeholder, mouseFragment);
            fragmentTransaction.commit();
        }else{
            try {
                client.socket.close();

                Snackbar snackbar = Snackbar
                        .make(relativeLayout,
                                "Can't establish buffers. Please try again.",
                                Snackbar.LENGTH_LONG);
                snackbar.show();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private void tryToConnect(final String ip){
        final RelativeLayout relativeLayout = findViewById(R.id.rootLayout);
        final Button button = findViewById(R.id.connectButton);
        final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);

        final Thread connection = new Thread() {
            @Override
            public void run() {
                try{
                    Log.d(TAG+" TRYCONENCT","Trying to connect");

                    client.socket = new Socket();
                    client.socket.connect(new InetSocketAddress(ip, 44340),10000);
                    BufferedReader in = new BufferedReader(new InputStreamReader(client.socket.getInputStream()));

                    String hb = in.readLine();

                    if(hb.equals("HB")) {
                        Log.d(TAG,hb);
                        progressDialog.dismiss();
                    }

                    if(isInterrupted()){
                        Log.d(TAG+" TRYCONNECT","interrupted trying to close");
                        if(client.socket!=null){
                            client.socket.close();
                        }
                    }else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                postConnect();
                            }
                        });
                    }

                }catch (ConnectException e){
                    Log.e(TAG+" TRYCONNECT","CE "+e.getMessage());
                    progressDialog.dismiss();

                    Snackbar snackbar = Snackbar
                            .make(relativeLayout, "Can't connect to server.", Snackbar.LENGTH_LONG)
                            .setAction("RETRY", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    button.performClick();
                                }
                            });

                    snackbar.show();

                }catch(IOException e){
                    Log.e(TAG+" TRYCONNECT","IO "+e.getMessage());
                    progressDialog.dismiss();

                    Snackbar snackbar = Snackbar
                            .make(relativeLayout, "Can't connect to server.", Snackbar.LENGTH_LONG)
                            .setAction("RETRY", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    button.performClick();
                                }
                            });
                    snackbar.show();

                }catch(NullPointerException e){
                    Log.e(TAG+" TRYCONNECT","NULL "+e.getMessage());
                    progressDialog.dismiss();

                    Snackbar snackbar = Snackbar
                            .make(relativeLayout, "Can't connect to server.", Snackbar.LENGTH_LONG)
                            .setAction("RETRY", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    button.performClick();
                                }
                            });
                    snackbar.show();

                }
            }
        };

        progressDialog.setMessage("Trying to establish connection.");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(true);

        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                connection.interrupt();
            }
        });
        progressDialog.show();

        connection.start();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main);

        rootLayout = findViewById(R.id.rootOfAll);

        client = new Client();
        mainFragment = MainFragment.newInstance();
        mouseFragment = MouseFragment.newInstance();

        customGestureDetector = new CustomGestureDetector();
        customGestureDetector.setup(this);
        gestureDetector = new GestureDetector(this, customGestureDetector);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.placeholder, mainFragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onConnectClick(String ip){
        tryToConnect(ip);
    }

    @Override
    public void onDismissClick() {
        try {
            client.socket.close();
            Log.d(TAG+" DISMISS","closed");

            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.placeholder, mainFragment);
            fragmentTransaction.commit();
        }catch (IOException io){
            Log.e(TAG+" DISMISS","IO "+io);
        }
    }

    @Override
    public boolean onViewTouch(MotionEvent event, float maxWidth, float maxHeight) {
        gestureDetector.onTouchEvent(event);
        return true;
    }

    @Override
    public void onBrokenPipe() {
        try{
            client.socket.close();
        } catch (IOException e) {
            Log.d(TAG+ " BROKENPIPE",e.getMessage());
        }

        Snackbar snackbar = Snackbar
                .make(rootLayout, "Server has been closed!", Snackbar.LENGTH_LONG);

        snackbar.show();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.placeholder, mainFragment);
        fragmentTransaction.commit();
    }
}
