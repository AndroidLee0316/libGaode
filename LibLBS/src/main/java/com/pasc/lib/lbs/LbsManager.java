package com.pasc.lib.lbs;

import android.util.Log;
import android.util.SparseArray;

import com.pasc.lib.lbs.location.ILocationFactory;
import com.pasc.lib.lbs.location.PascLocationListener;
import com.pasc.lib.lbs.location.PascLocationWorker;
import com.pasc.lib.lbs.location.bean.PascLocationData;

public class LbsManager {
    private SparseArray<PascLocationWorker> mPascLocationWorkers;
    private boolean mIsInit;
    private ILocationFactory mFactory;

    public static LbsManager getInstance() {
        return SingletonHolder.instance;
    }

    private LbsManager() {
        mPascLocationWorkers = new SparseArray<>();
    }

    public void initLbs(ILocationFactory factory) {
        if (factory == null) {
            throw new IllegalArgumentException("method initsLb() the factory is null");
        }
        mIsInit = true;
        mFactory = factory;
    }

    /**
     * 静态内部类,只有在装载该内部类时才会去创建单例对象
     */
    private static class SingletonHolder {
        private static final LbsManager instance = new LbsManager();
    }

    /**
     * 定位功能 根据定位频率提供响应的定位管理对象。
     * @param scanSpan 定位扫描的频率 单位毫秒 单次定位scanSpan = 0, 持续性定位频率需大于1000。
     * @param locationListener 定位结果回调
     */
    public synchronized void doLocation(int scanSpan, PascLocationListener locationListener) {
        if (!mIsInit) {
            Log.e("LibLBS", "LbsManager is not initLbs, please init first");
            return;
        }
        if (scanSpan < 1000) {
            scanSpan = 0;
        }
        PascLocationWorker worker = mPascLocationWorkers.get(scanSpan);
        if (worker == null) {
            synchronized (LbsManager.class) {
                if (mPascLocationWorkers.get(scanSpan) == null) {
                    worker = new PascLocationWorker(scanSpan, mFactory.create(scanSpan));
                    mPascLocationWorkers.put(scanSpan, worker);
                }
            }
        }
        worker.doLocation(locationListener);
    }

    /**
     * 停止定位
     * @param scanSpan 定位扫描的频率 单位毫秒 单次定位scanSpan = 0, 持续性定位频率需大于1000。
     * @param locationListener 移除定位时添加的定位结果回调对象
     */
    public synchronized void stopLocation(int scanSpan, PascLocationListener locationListener) {
        if (!mIsInit) {
            Log.e("LibLBS", "LbsManager is not initLbs, please init first");
            return;
        }
        if (scanSpan < 1000) {
            scanSpan = 0;
        }
        PascLocationWorker worker = mPascLocationWorkers.get(scanSpan);
        if (worker != null) {
            worker.removeLocationListener(locationListener);
        }
    }

    public PascLocationData getLastLocationData() {
        int size = mPascLocationWorkers.size();
        PascLocationData locationData = null;
        long lastTime = 0;
        try {
            for (int i = 0; i < size; i++) {
                PascLocationWorker worker = mPascLocationWorkers.valueAt(i);
                if (worker.getLastLocationTime() > lastTime && worker.getLastLoacationData() != null) {
                    locationData = worker.getLastLoacationData();
                    lastTime = worker.getLastLocationTime();
                }
            }
        } catch (Exception e) {

        }
        return locationData;
    }


    public long getLastLocationTime() {
        int size = mPascLocationWorkers.size();
        long lastTime = 0;
        try {
            for (int i = 0; i < size; i++) {
                PascLocationWorker worker = mPascLocationWorkers.valueAt(i);
                if (worker.getLastLocationTime() > lastTime && worker.getLastLoacationData() != null) {
                    lastTime = worker.getLastLocationTime();
                }
            }
        } catch (Exception e) {

        }
        return lastTime;
    }
}
