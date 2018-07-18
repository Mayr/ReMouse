package com.mayurm.remouse;

import android.content.Context;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

import java.io.IOException;
import java.net.Socket;

class CustomGestureDetector extends GestureDetector.SimpleOnGestureListener implements GestureDetector.OnDoubleTapListener {

    public Client client;
    private BrokenPipe brokenPipe;

    public interface BrokenPipe{
        public void onBrokenPipe();
    }

    public void setup(Context context){
        brokenPipe = (BrokenPipe) context;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        new Thread() {
            @Override
            public void run() {
                try {
                    client.outToServer.writeBytes("l:" + '\n');
                    client.outToServer.flush();
                } catch (IOException io) {
                    brokenPipe.onBrokenPipe();
                }
            }
        }.start();
        return true;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        new Thread() {
            @Override
            public void run() {
                try {
                    client.outToServer.writeBytes("d:" + '\n');
                    client.outToServer.flush();
                } catch (IOException io) {
                    brokenPipe.onBrokenPipe();
                }
            }
        }.start();
        return true;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, final float distanceX, final float distanceY) {
        if(e2.getPointerCount() == 1) {
            new Thread() {
                @Override
                public void run() {
                    if (Math.abs(distanceX) > 2 || Math.abs(distanceY) > 2) {
                        try {
                            client.outToServer.writeBytes("m:" + (-1) * distanceY + ":" + distanceX + '\n');
                            client.outToServer.flush();
                        } catch (IOException io) {
                            brokenPipe.onBrokenPipe();
                        }
                    }
                }
            }.start();
        }else if(e2.getPointerCount()==2){
            new Thread() {
                @Override
                public void run() {
                    if (Math.abs(distanceX) > 1 || Math.abs(distanceY) > 1) {
                        int dir=1;
                        if(distanceX < 1){
                            dir = 1;
                        }else{
                            dir = -1;
                        }
                        try {
                            client.outToServer.writeBytes("s:"+dir+"\n");
                            client.outToServer.flush();
                            Log.d("a","p");
                        } catch (IOException io) {
                            brokenPipe.onBrokenPipe();
                        }
                    }
                }
            }.start();
        }
        return true;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        System.out.println("onLongPress");
        new Thread() {
            @Override
            public void run() {
                try {
                    client.outToServer.writeBytes("r:" + '\n');
                    client.outToServer.flush();
                } catch (IOException io) {
                }
            }
        }.start();
    }

}