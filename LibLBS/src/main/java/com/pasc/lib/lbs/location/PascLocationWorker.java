package com.pasc.lib.lbs.location;

import android.util.Log;

import com.pasc.lib.lbs.location.bean.PascLocationData;

import java.util.ArrayList;
import java.util.Iterator;

public final class PascLocationWorker {
    private static final String TAG = "PascLocationWorker";
    public static final int SCAN_SPAN = 1100;
    private static final int LOCATION_DATA_VALID_TIME = 30 * 1000;

    private BaseLocationClient mBaseLocationClient;
    private ArrayList<PascLocationListener> mPascLocationListeners = new ArrayList<>();
    private boolean mIsLocating = false;
    private BaseLocationClient.LocationResultListener mBaseListener;
    private int mScanSpan = SCAN_SPAN; //持续性定位扫描间隔（单位毫秒）
    private PascLocationData mLastLoacationData;
    private long mLastLocationTime;

    public PascLocationWorker(int scanSpan, BaseLocationClient client) {
        mBaseLocationClient = client;
        mScanSpan = scanSpan;
    }

    private void initLocation() {
        if (mBaseListener == null) {
            mBaseListener = new BaseLocationClient.LocationResultListener() {
                @Override
                public void onSuccess(PascLocationData data) {
                    if (mScanSpan < 1000) {
                        mIsLocating = false;
                    }
                    mLastLoacationData = data;
                    mLastLocationTime = System.currentTimeMillis();
                    locationSuccess(mPascLocationListeners, data);
                }

                @Override
                public void onFailure(LocationException e) {
                    Log.d(TAG, "onFailure " + e);
                    if (mScanSpan < 1000) {
                        mIsLocating = false;
                    }
                    locationFailure(mPascLocationListeners, e);
                }
            };
            mBaseLocationClient.setLocationResultListener(mBaseListener);
        }

    }

    private synchronized void locationSuccess(ArrayList<PascLocationListener> listeners, PascLocationData data) {
        if (listeners != null) {
            ArrayList<PascLocationListener> copyList = new ArrayList<>(listeners);
            Iterator<PascLocationListener> iterator = copyList.iterator();
            while (iterator.hasNext()) {
                PascLocationListener pascLocationListener = iterator.next();
                if (pascLocationListener != null) {
                    pascLocationListener.onLocationSuccess(data);
                }
            }
        }
    }

    private synchronized void locationFailure
            (ArrayList<PascLocationListener> listeners, LocationException e) {
        if (listeners != null) {
            ArrayList<PascLocationListener> copyList = new ArrayList<>(listeners);
            Iterator<PascLocationListener> iterator = copyList.iterator();
            while (iterator.hasNext()) {
                PascLocationListener pascLocationListener = iterator.next();
                if (pascLocationListener != null) {
                    pascLocationListener.onLocationFailure(e);
                }
            }
        }
    }

    public synchronized void doLocation(PascLocationListener locationListener) {
        Log.d(TAG, "doLocation ");
        if (locationListener != null) {
            if (findPascLocationListener(locationListener) == null) {
                mPascLocationListeners.add(locationListener);
            }
            if (mIsLocating && mLastLoacationData != null && System.currentTimeMillis() - mLastLocationTime <= LOCATION_DATA_VALID_TIME) {
                locationListener.onLocationSuccess(mLastLoacationData);
                return;
            }
        }
        mIsLocating = true;
        initLocation();
        Log.d(TAG, "doLocation " + mIsLocating + "  " + mPascLocationListeners);
        mBaseLocationClient.launchLocation();
    }


    private synchronized PascLocationListener findPascLocationListener(PascLocationListener listener) {
        if (listener == null) return null;
        if (mPascLocationListeners == null || mPascLocationListeners.size() == 0) return null;
        final int count = mPascLocationListeners.size();
        for (int i = 0; i < count; i++) {
            if (mPascLocationListeners.get(i) == listener) {
                return listener;
            }
        }
        return null;
    }

    public synchronized void removeLocationListener(PascLocationListener locationListener) {
        if (mPascLocationListeners != null) {
            if (locationListener != null) {
                if (mPascLocationListeners.contains(locationListener)) {
                    mPascLocationListeners.remove(locationListener);
                }
            }
            if (mPascLocationListeners.size() == 0) {
                stopLocation();
            }
        }
    }

    /**
     * 停止定位&释放资源
     */
    private void stopLocation() {
        mIsLocating = false;
        if (mBaseLocationClient != null) {
            mBaseLocationClient.removeLocationResultListener();
            mBaseListener = null;
            mBaseLocationClient.unRegisterListener();
        }
    }

    public PascLocationData getLastLoacationData() {
        return mLastLoacationData;
    }

    public long getLastLocationTime() {
        return mLastLocationTime;
    }


}
