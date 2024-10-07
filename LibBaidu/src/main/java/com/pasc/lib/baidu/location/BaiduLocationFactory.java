package com.pasc.lib.baidu.location;

import android.content.Context;
import android.util.Log;

import com.baidu.location.LocationClientOption;
//import com.baidu.location.g.g;
import com.pasc.lib.lbs.location.BaseLocationClient;
import com.pasc.lib.lbs.location.ILocationFactory;

import java.lang.reflect.Field;

public class BaiduLocationFactory implements ILocationFactory {

    private Context mContext;
    private LocationClientOption mOption;

    public BaiduLocationFactory(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("new BaiduLocationFactory(context) the context is null");
        }
        //safeyCheck();
        mContext = context;
    }

//    private void safeyCheck(){
//        try {
//            Class clazz = g.class;
//            //getDeclaredField获取所有字段
//            Field field = clazz.getDeclaredField("ay");
//            //去掉private影响
//            field.setAccessible(true);
//            field.set(clazz,"https://loc.map.baidu.com/tcu.php");
//        } catch (NoSuchFieldException e) {
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        }
//    }

    public void setOption(LocationClientOption option) {
        mOption = option;
    }


    @Override
    public BaseLocationClient create(int scanSpan) {
        if (mOption != null) {
            if (scanSpan < 1000) {
                scanSpan = 0;
            }
            mOption.setScanSpan(scanSpan);
            return new BaiduLocationClient(mContext, mOption);
        } else {
            return new BaiduLocationClient(mContext, scanSpan);
        }
    }

}
