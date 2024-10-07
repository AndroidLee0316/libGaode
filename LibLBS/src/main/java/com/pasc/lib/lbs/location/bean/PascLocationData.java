package com.pasc.lib.lbs.location.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class PascLocationData implements Parcelable {
    public static final String LOC_PROVINCE = "省";
    public static final String LOC_COUNTY = "县";
    public static final String LOC_CITY = "市";
    public static final String LOC_AREA = "区";
    public static final String LOC_AUTONOMOUS = "自治";
    private double latitude;
    private double longitude;
    private String province;
    private String city;
    private String district;
    private String street;
    private String streetNumber;
    private float radius;
    private String locationTime;
    private String address;
    private String cityCode;
    private String adCode;
    private String aoiName;
    private String country;
    public PascLocationData(String province, String city) {
        this.province = province;
        this.city = city;
    }

    public void setCountry(String county) {
        this.country = county;
    }

    public String getCountry() {
        return country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getStreetNumber() {
        return streetNumber;
    }

    public void setStreetNumber(String streetNumber) {
        this.streetNumber = streetNumber;
    }

    public String getLocationTime() {
        return locationTime;
    }

    public void setLocationTime(String locationTime) {
        this.locationTime = locationTime;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public String getAdCode() {
        return adCode;
    }

    public void setAdCode(String adCode) {
        this.adCode = adCode;
    }

    public String getAoiName() {
        return aoiName;
    }

    public void setAoiName(String aoiName) {
        this.aoiName = aoiName;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer(256);
        sb.append("city : ").append(city)
                .append("\nlatitude : ").append(latitude)
                .append("\nlongitude : ").append(longitude)
                .append("\nprovince : ").append(province)
                .append("\ndistrict : ").append(district)
                .append("\nstreet : ").append(street)
                .append("\nstreetNumber : ").append(streetNumber)
                .append("\nradius : ").append(radius)
                .append("\nreal time : ").append(locationTime);
        return sb.toString();
    }

    @Override public int describeContents() {
        return 0;
    }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(this.latitude);
        dest.writeDouble(this.longitude);
        dest.writeString(this.province);
        dest.writeString(this.city);
        dest.writeString(this.district);
        dest.writeString(this.street);
        dest.writeString(this.streetNumber);
        dest.writeFloat(this.radius);
        dest.writeString(this.locationTime);
        dest.writeString(this.address);
        dest.writeString(this.cityCode);
        dest.writeString(this.adCode);
    }

    protected PascLocationData(Parcel in) {
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
        this.province = in.readString();
        this.city = in.readString();
        this.district = in.readString();
        this.street = in.readString();
        this.streetNumber = in.readString();
        this.radius = in.readFloat();
        this.locationTime = in.readString();
        this.address = in.readString();
        this.cityCode = in.readString();
        this.adCode = in.readString();
    }

    public static final Creator<PascLocationData> CREATOR = new Creator<PascLocationData>() {
        @Override public PascLocationData createFromParcel(Parcel source) {
            return new PascLocationData(source);
        }

        @Override public PascLocationData[] newArray(int size) {
            return new PascLocationData[size];
        }
    };
}
