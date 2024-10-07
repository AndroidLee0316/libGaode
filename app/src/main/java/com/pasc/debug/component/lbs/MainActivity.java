package com.pasc.debug.component.lbs;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.baidu.mapapi.map.TextureMapView;
import com.pasc.lib.base.permission.PermissionUtils;
import com.pasc.lib.lbs.LbsManager;
import com.pasc.lib.lbs.location.LocationException;
import com.pasc.lib.lbs.location.PascLocationListener;
import com.pasc.lib.lbs.location.bean.PascLocationData;

import java.util.Calendar;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class MainActivity extends Activity implements RadioGroup.OnCheckedChangeListener{
    private TextView mLocationDataOnceTv;
    private TextView mLocationDataMoreTv;
    private int printCount;
    private int printCount1;

    private EditText mScanSpanEditText;

    private Button mOnceLocationBt;
    private Button mMoreLoactionBt;
    private boolean isMoreLocation = false;
    private int mScanSpan;

    private SharedPreferences mSharedPreferences;
    private TextView mTvCurrentEven;
    private RadioButton mBaiduRadio;
    private RadioButton mGaodeRadio;

    private DemoLocationListener mOnceLocationListener;
    private DemoLocationListener mMoreLocationListener;
    private static final String DISPLAY_TEXTVIEW_NAME_ONCE = "单次定位按钮";
    private static final String DISPLAY_TEXTVIEW_NAME_MORE = "持续性定位";
    private static final int COLOR_1 = Color.parseColor("#ff008f");
    private static final int COLOR_2 = Color.parseColor("#4a8f8f");
    private CompositeDisposable disposables = new CompositeDisposable();


    private void printError(String name, int scanspan, LocationException e) {
        TextView textView = selectTextView(name);
        if (e != null) {
            StringBuilder builder = new StringBuilder();
            builder.append("name :" + name + " " + scanspan + "\n")
                    .append("time : " + Calendar.getInstance().getTime().toString() + "\n")
                    .append(e.toString());
            textView.setText(builder.toString());
        } else {
            textView.setText("");
        }
    }

    private void printLocation(String name, int scanspan, PascLocationData data) {
        TextView textView = selectTextView(name);
        if (data != null) {
            StringBuilder builder = new StringBuilder();
            builder.append("name :" + name + " " + scanspan + "\n")
                    .append("time : " + Calendar.getInstance().getTime().toString() + "\n")
                    .append(getPrintString(data));
            textView.setText(builder.toString());
        } else {
            textView.setText("");
        }
    }

    private TextView selectTextView(String name) {
        switch (name) {
            case DISPLAY_TEXTVIEW_NAME_ONCE:
                if (printCount % 2 == 0) {
                    mLocationDataOnceTv.setTextColor(COLOR_1);
                } else {
                    mLocationDataOnceTv.setTextColor(COLOR_2);
                }
                printCount++;
                return mLocationDataOnceTv;
            case DISPLAY_TEXTVIEW_NAME_MORE:
                if (printCount1 % 2 == 0) {
                    mLocationDataMoreTv.setTextColor(COLOR_1);
                } else {
                    mLocationDataMoreTv.setTextColor(COLOR_2);
                }
                printCount1++;
                return mLocationDataMoreTv;
        }
        return mLocationDataOnceTv;
    }

    TextureMapView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSharedPreferences = getApplicationContext().getSharedPreferences(TheApplication.SP_NAME, Context.MODE_PRIVATE);

        checkLocPermission();

        mLocationDataOnceTv = findViewById(R.id.location_text_once);
        mOnceLocationBt = findViewById(R.id.once_location_bt);
        mMoreLoactionBt = findViewById(R.id.more_location_bt);
        mMoreLoactionBt.setText("开始持续性定位");
        mapView = (TextureMapView) findViewById(R.id.nearby_search_map_view);

        mLocationDataMoreTv = findViewById(R.id.location_text_more);

        mScanSpanEditText = findViewById(R.id.scancpan_et);

        mOnceLocationBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnceLocationListener == null) {
                    mOnceLocationListener = new DemoLocationListener(DISPLAY_TEXTVIEW_NAME_ONCE, 0);
                }
                LbsManager.getInstance().doLocation(0, mOnceLocationListener);
            }
        });

        mMoreLoactionBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isMoreLocation) {
                    isMoreLocation = false;
                    mMoreLoactionBt.setText("开始持续性定位");
                    if (mMoreLocationListener != null) {
                        LbsManager.getInstance().stopLocation(mScanSpan, mMoreLocationListener);
                    }
                } else {
                    mLocationDataMoreTv.setText("");
                    String edit = mScanSpanEditText.getText().toString();
                    if (TextUtils.isEmpty(edit)) {
                        mLocationDataMoreTv.setText("定位频率不能为空");
                        return;
                    }
                    int scanspan = 0;
                    try {
                        scanspan = Integer.valueOf(edit);
                    } catch (NumberFormatException e) {
                        mLocationDataMoreTv.setText("定位频率必须为int整数，且不可越界");
                        return;
                    }
                    isMoreLocation = true;
                    mScanSpan = scanspan;
                    mMoreLoactionBt.setText("停止持续性定位");
                    if (mMoreLocationListener == null) {
                        mMoreLocationListener = new DemoLocationListener(DISPLAY_TEXTVIEW_NAME_MORE, scanspan);
                    }
                    mMoreLocationListener.scanspan = mScanSpan;
                    LbsManager.getInstance().doLocation(scanspan, mMoreLocationListener);
                }
            }
        });


        mTvCurrentEven = findViewById(R.id.location_even);
        mTvCurrentEven.setText(TheApplication.mIsUseBaidu ? "百度定位" : "高德定位");

        mBaiduRadio = findViewById(R.id.baidu);
        mGaodeRadio = findViewById(R.id.gaode);

        String saveEven = mSharedPreferences.getString(TheApplication.SP_LOCATION_EVEN, TheApplication.DEFAULT_EVEN);
        if (TheApplication.BAIDU_EVEN.equals(saveEven)) {
            mBaiduRadio.setChecked(true);
        } else {
            mGaodeRadio.setChecked(true);
        }

        RadioGroup group = (RadioGroup) this.findViewById(R.id.radioGroup);
        group.setOnCheckedChangeListener(this);
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int id) {
        switch (id ){
            case R.id.baidu:
                mSharedPreferences.edit().putString(TheApplication.SP_LOCATION_EVEN, TheApplication.BAIDU_EVEN).commit();
                break;
            case R.id.gaode:
                mSharedPreferences.edit().putString(TheApplication.SP_LOCATION_EVEN, TheApplication.GAODE_EVEN).commit();
                break;
        }
    }

    public class DemoLocationListener implements PascLocationListener {
        public String name;
        public int scanspan;

        public DemoLocationListener(String name, int scanspan) {
            this.name = name;
            this.scanspan = scanspan;
        }

        @Override
        public void onLocationSuccess(PascLocationData data) {
            printLocation(name, scanspan, data);
            if (scanspan < 1000) {
                LbsManager.getInstance().stopLocation(0, this);
            }
        }

        @Override
        public void onLocationFailure(LocationException e) {
            if (scanspan < 1000) {
                LbsManager.getInstance().stopLocation(0, this);
            }
            printError(name, scanspan, e);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LbsManager.getInstance().stopLocation(0, mOnceLocationListener);
        LbsManager.getInstance().stopLocation(mScanSpan, mMoreLocationListener);
        disposables.clear();
    }

    private String getPrintString(PascLocationData data) {
        if (data == null) {
            return "";
        }
        StringBuffer sb = new StringBuffer(256);
        sb.append("地址 ").append(data.getProvince())
                .append(" ").append(data.getCity())
                .append(" ").append(data.getDistrict())
                .append(" ").append(data.getStreet())
                .append(" ").append(data.getStreetNumber())
                .append("\nlatitude : ").append(data.getLatitude())
                .append("\nlongitude : ").append(data.getLongitude());
        return sb.toString();
    }

    private void checkLocPermission() {
        Disposable disposable = PermissionUtils.request(this, PermissionUtils.Groups.LOCATION)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {

                    }
                });
        disposables.add(disposable);
    }
}
