package com.pasc.lib.baidu.location;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.pasc.lib.lbs.location.BaseLocationClient;
import com.pasc.lib.lbs.location.LocationException;
import com.pasc.lib.lbs.location.bean.PascLocationData;

public class BaiduLocationClient extends BaseLocationClient {

    private static final String TAG = "LibLBS_BaiduLocation";
    private LocationClient mLocationClient;
    private BaiduLocationListener mBaiduListener;
    private boolean mIsRegister = false;

    private class BaiduLocationListener extends BDAbstractLocationListener {

        private String getDebugMessage(BDLocation location) {
            StringBuffer sb = new StringBuffer(256);
            sb.append("time : ")
                    .append(location.getTime())
                    .append("\nerror code : ")
                    .append(location.getLocType())
                    .append("\nlatitude : ")
                    .append(location.getLatitude())
                    .append("\nlontitude : ")
                    .append(location.getLongitude());

            if (location.getLocType() == BDLocation.TypeGpsLocation) {
                sb.append("\nspeed : ");
                sb.append(location.getSpeed());
                sb.append("\nsatellite : ");
                sb.append(location.getSatelliteNumber());
            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());
            }

            sb.append("\ntype : " + location.getLocType());

            return sb.toString();
        }

        @Override
        public void onReceiveLocation(BDLocation location) {
            Log.d(TAG, "onReceiveLocation mLocationResultListener = " + mLocationResultListener + ", location = " + location);
            if (mLocationResultListener == null) {
                return;
            }
            if (location == null) {
                mLocationResultListener.onFailure(new LocationException("BDLocation is null"));
                return;
            }
            Log.d(TAG, getDebugMessage(location));

            final int locTpye = location.getLocType();

            if (locTpye == BDLocation.TypeCacheLocation
                    || locTpye == BDLocation.TypeGpsLocation
                    || locTpye == BDLocation.TypeNetWorkLocation
                    || locTpye == BDLocation.TypeOffLineLocation) {
                String province = location.getProvince();
                String city = location.getCity();
                String district = location.getDistrict();
                Log.d(TAG, "province " + province + "  " + city + "  " + district);
                boolean isNeedAddress = "all".equals(mLocationClient.getLocOption().getAddrType());
                if (isNeedAddress && (province == null || city == null || district == null)) {
                    mLocationResultListener.onFailure(new LocationException("Location province, city or district is null"));
                    return;
                }

                PascLocationData data = new PascLocationData(province, city);
                data.setDistrict(district);
                data.setLatitude(location.getLatitude());
                data.setLongitude(location.getLongitude());
                data.setStreet(location.getStreet());
                data.setStreetNumber(location.getStreetNumber());
                data.setRadius(location.getRadius());
                data.setLocationTime(location.getTime());
                data.setAddress(location.getAddrStr());
                data.setAdCode(location.getAdCode());
                data.setCityCode(location.getCityCode());
                data.setCountry (location.getCountry ());
                mLocationResultListener.onSuccess(data);
                return;
            }
            mLocationResultListener.onFailure(new LocationException("loctpye = " + locTpye));
        }
    }

    public BaiduLocationClient(Context context, LocationClientOption option) {
        if (option == null) {
            new BaiduLocationClient(context, 0);
            return;
        }
        mLocationClient = new LocationClient(context);
        mLocationClient.setLocOption(option);
    }

    public BaiduLocationClient(Context context, int scanspan) {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setCoorType("bd09ll");
        option.setIsNeedAddress(true);
        option.setNeedDeviceDirect(false);
        option.setOpenGps(true);

        option.setPriority(LocationClientOption.NetWorkFirst);
        if (scanspan >= 1000) {
            option.setScanSpan(scanspan);
        }
        mLocationClient = new LocationClient(context);
        mLocationClient.setLocOption(option);
    }

    @Override
    protected void doLocation() {
        if (mLocationClient.isStarted()) {
            final int result = mLocationClient.requestLocation();
            if (result == 6) {
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mLocationClient.requestLocation();
                    }
                }, 1000);
            }
        } else {
            mLocationClient.start();
        }
    }

    @Override
    protected void unRegisterListener() {
        if (mLocationClient != null) {
            mLocationClient.stop();
            if (mBaiduListener != null) {
                mLocationClient.unRegisterLocationListener(mBaiduListener);
            }
            mIsRegister = false;
        }
    }

    @Override
    protected void registerListener() {
        if (mBaiduListener == null) {
            mBaiduListener = new BaiduLocationListener();
        }
        if (mLocationClient != null && !mIsRegister) {
            mLocationClient.registerLocationListener(mBaiduListener);
            mIsRegister = true;
        }
    }

}
