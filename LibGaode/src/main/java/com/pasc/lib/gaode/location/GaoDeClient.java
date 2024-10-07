package com.pasc.lib.gaode.location;

import android.content.Context;
import android.text.TextUtils;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.pasc.lib.lbs.location.BaseLocationClient;
import com.pasc.lib.lbs.location.LocationException;
import com.pasc.lib.lbs.location.bean.PascLocationData;

class GaoDeClient extends BaseLocationClient implements GeocodeSearch.OnGeocodeSearchListener {

    private Context context;
    private int span;
    private AMapLocationClient locationClient;
    private AMapLocationListener listener;
    private GeocodeSearch geocodeSearch;
    private double mLatitude;
    private double mLongitude;

    public GaoDeClient(Context context, int span) {
        this.context = context;
        this.span = span;
    }

    private void initClient() throws Exception {
        locationClient = new AMapLocationClient(context);
        locationClient.setLocationOption(new AMapLocationClientOption().setLocationMode(
                AMapLocationClientOption.AMapLocationMode.Hight_Accuracy)
                .setInterval(span)
                .setNeedAddress(true)
                .setOnceLocation(0 == span));
        //地理搜索类
        geocodeSearch = new GeocodeSearch(context);
        geocodeSearch.setOnGeocodeSearchListener(this);
    }

    private void destroyClient() {
        if (locationClient != null) {
            locationClient.unRegisterLocationListener(listener);
            locationClient.stopLocation();
            locationClient.onDestroy();
            locationClient = null;
        }
    }

    @Override
    protected void doLocation() {
        destroyClient();
        try {
            initClient();
        } catch (Exception e) {
            e.printStackTrace();
        }
        locationClient.setLocationListener(listener);
        locationClient.stopLocation();
        locationClient.startLocation();
    }

    @Override
    protected void registerListener() {
        if (listener != null) return;
        listener = new AMapLocationListener() {
            @Override public void onLocationChanged(AMapLocation aMapLocation) {
                if (aMapLocation == null) {
                    mLocationResultListener.onFailure(new NotResultException("result is null"));
                    return;
                }
                if (aMapLocation.getErrorCode() != 0) {
                    mLocationResultListener.onFailure(
                        new LocationErrorException(aMapLocation.getErrorCode(),
                            mapperGaoDeErrorMessage(aMapLocation.getErrorCode())));
                    return;
                }
                mLatitude = aMapLocation.getLatitude();
                mLongitude = aMapLocation.getLongitude();
                if (TextUtils.isEmpty(aMapLocation.getProvince())
                    && mLatitude > 0
                    && mLongitude > 0) {
                    GaoDeClient.this.getAddress(
                        new LatLonPoint(aMapLocation.getLatitude(), aMapLocation.getLongitude()));
                } else {
                    PascLocationData data =
                        new PascLocationData(aMapLocation.getProvince(), aMapLocation.getCity());
                    data.setDistrict(aMapLocation.getDistrict());
                    data.setLatitude(mLatitude);
                    data.setLongitude(mLongitude);
                    data.setStreet(aMapLocation.getStreet());
                    data.setStreetNumber(aMapLocation.getStreetNum());
                    data.setAoiName(aMapLocation.getAoiName());
                    data.setAdCode(aMapLocation.getAdCode());
                    data.setAddress(aMapLocation.getAddress());
                    data.setCountry(aMapLocation.getCountry());
                    mLocationResultListener.onSuccess(data);
                }
            }
        };
    }

    @Override
    protected void unRegisterListener() {
        destroyClient();
    }

    public static final String mapperGaoDeErrorMessage(int error) {
        String message;
        switch (error) {
            case 1:
                message = "一些重要参数为空";
                break;
            case 2:
                message = "定位失败，由于仅扫描到单个wifi，且没有基站信息";
                break;
            case 3:
                message = "获取到的请求参数为空，可能获取过程中出现异常";
                break;
            case 4:
                message = "网络情况差";
                break;
            case 5:
                message = "请求被恶意劫持，定位结果解析失败";
                break;
            case 6:
                message = "定位服务返回定位失败。";
                break;
            case 7:
                message = "KEY鉴权失败。";
                break;
            case 8:
                message = "Android exception常规错误";
                break;
            case 9:
                message = "定位初始化时出现异常";
                break;
            case 10:
                message = "定位客户端启动失败";
                break;
            case 11:
                message = "定位时的基站信息错误";
                break;
            case 12:
                message = "缺少定位权限";
                break;
            case 13:
                message = "定位失败，由于未获得WIFI列表和基站信息，且GPS当前不可用";
                break;
            case 14:
                message = "GPS 定位失败，由于设备当前 GPS 状态差";
                break;
            case 15:
                message = "定位结果被模拟导致定位失败";
                break;
            case 16:
                message = "当前POI检索条件、行政区划检索条件下，无可用地理围栏";
                break;
            case 18:
                message = "定位失败，由于手机WIFI功能被关闭同时设置为飞行模";
                break;
            case 19:
                message = "定位失败，由于手机没插sim卡且WIFI功能被关闭";
                break;
            default:
                message = "未知异常";
                break;
        }
        return message;
    }

    @Override
    public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
        if (mLocationResultListener != null && rCode == AMapException.CODE_AMAP_SUCCESS) {
            if (result != null && result.getRegeocodeAddress() != null
                    && result.getRegeocodeAddress().getFormatAddress() != null) {
                result.getRegeocodeAddress().getCity();
                PascLocationData data =
                        new PascLocationData(result.getRegeocodeAddress().getProvince(), result.getRegeocodeAddress().getCity());
                data.setDistrict(result.getRegeocodeAddress().getDistrict());
                data.setStreet(result.getRegeocodeAddress().getStreetNumber().getStreet());
                data.setStreetNumber(result.getRegeocodeAddress().getStreetNumber().getNumber());
                data.setAdCode(result.getRegeocodeAddress().getAdCode());
                data.setAddress(result.getRegeocodeAddress().getFormatAddress());
                data.setLatitude(mLatitude);
                data.setLongitude(mLongitude);
                if (result.getRegeocodeAddress().getAois() != null && result.getRegeocodeAddress().getAois().size() > 0) {
                    data.setAoiName(result.getRegeocodeAddress().getAois().get(0).getAoiName());
                }
                mLocationResultListener.onSuccess(data);
            }
        }
    }

    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

    }


    public static class NotResultException extends LocationException {
        public NotResultException(String message) {
            super(message);
        }
    }

    public static class LocationErrorException extends LocationException {
        public int errorCode;

        public LocationErrorException(int errorCode, String message) {
            super(message);
            this.errorCode = errorCode;
        }
    }

    /**
     * 响应逆地理编码
     */
    private void getAddress(LatLonPoint latLonPoint) {
        // 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
        RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 200,
                GeocodeSearch.AMAP);
        // 设置异步逆地理编码请求
        geocodeSearch.getFromLocationAsyn(query);
    }
}
