package com.pasc.debug.component.lbs;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.baidu.mapapi.SDKInitializer;
import com.pasc.lib.baidu.location.BaiduLocationFactory;
import com.pasc.lib.gaode.location.GaoDeLocationFactory;
import com.pasc.lib.lbs.LbsManager;
import com.pasc.lib.lbs.location.ILocationFactory;

public class TheApplication extends MultiDexApplication {

    public static final String SP_NAME = "sp_location";
    public static final String SP_LOCATION_EVEN = "even";
    public static final String BAIDU_EVEN = "baidu";
    public static final String GAODE_EVEN = "gaode";
    public static final String DEFAULT_EVEN = BAIDU_EVEN;
    public static boolean mIsUseBaidu = true;

    @Override
    public void onCreate() {
        super.onCreate();

        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(TheApplication.SP_NAME, Context.MODE_PRIVATE);
        String even = sharedPreferences.getString(SP_LOCATION_EVEN, DEFAULT_EVEN);
        mIsUseBaidu = BAIDU_EVEN.equals(even);

        ILocationFactory factory = null;
        if (mIsUseBaidu) {
            //百度定位初始化
            factory = new BaiduLocationFactory(getApplicationContext());
            Log.d("location", "LbsManager baidu");
        } else {
            //高德定位初始化
            factory = new GaoDeLocationFactory(getApplicationContext());
            Log.d("location", "LbsManager gaode");
        }
        LbsManager.getInstance().initLbs(factory);
        SDKInitializer.initialize(this);
    }
}
