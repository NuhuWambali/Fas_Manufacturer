package com.technotrade.pts2.pts2testapp;

import android.util.Log;

/// <summary>
/// A class that inherited from Runnable and represents the thread that periodically
/// requesting the PTS2 device status and some additional data
/// </summary>
public class StatusPollerThread implements Runnable {
    private final PTSManager mPTSManager;
    private final Object mLock = new Object();

    public StatusPollerThread(PTSManager ptsManager) {
        mPTSManager = ptsManager;
    }

    @Override
    public void run() {
        while (mPTSManager.isOpened() && !Thread.interrupted()) {
            try {
                synchronized (mLock) {
                    mPTSManager.threadPollerMethod();
                    mLock.wait(2000L);
                }
            } catch (InterruptedException e) {
                //can be stopped via thread.interrupt()
                break;
            } catch (Exception e) {
                Log.e("pts2", "Stopped " + e.getMessage());
            }
        }
    }
}