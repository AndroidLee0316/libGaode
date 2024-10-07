package com.pasc.lib.lbs.location;

import com.pasc.lib.lbs.location.bean.PascLocationData;

public abstract class BaseLocationClient {

    private static final String TAG = "BaseLocationClient";
    protected LocationResultListener mLocationResultListener;

    protected interface LocationResultListener {
        public void onSuccess(PascLocationData data);
        public void onFailure(LocationException e);
    }

    protected abstract void doLocation();

    protected abstract void registerListener();

    protected abstract void unRegisterListener();

    protected final void launchLocation() {
        registerListener();
        doLocation();
    }

    protected final void setLocationResultListener(LocationResultListener listener) {
        if (listener == null) {
            throw new LocationException("BaseLocationClient setLocationResultListener(listener) is null");
        }
        mLocationResultListener = listener;
    }

    protected final void removeLocationResultListener() {
        mLocationResultListener = null;
    }

}
