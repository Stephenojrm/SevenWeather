package com.stephen.sevenday.model;

/**
 * Created by gritor on 2016/2/24.
 */
public class Position {
    private String province;
    private String city;
    private String district;


    public String getProvinceName() {
        return province;
    }

    public void setProvinceName(String provinceName) {
        this.province = provinceName;
    }

    public String getCityName() {
        return city;
    }

    public void setCityName(String cityName) {
        this.city = cityName;
    }

    public String getDistrictName() {
        return district;
    }

    public void setDistrictName(String districtName) {
        this.district = districtName;
    }


}
