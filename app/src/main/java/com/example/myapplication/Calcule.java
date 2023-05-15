package com.example.myapplication;

import android.os.Handler;
import android.os.HandlerThread;



    public class Calcule extends HandlerThread {
        Handler h;
        public Calcule(String name) {
            super(name);
        }
        public void postTask(Runnable task) {
            h.post(task);
        }
        public void prepareHandler() {
            h = new Handler(getLooper());
        }


}
