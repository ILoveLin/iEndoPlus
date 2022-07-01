package com.company.iendo.bean.model;

import java.util.List;

/**
 * 数据实体类 外部类是省份数据，内部类是城市数据
 * 外部类继承了ParentItem类，表示这个类中的数据是要展示列表的父级item的数据
 */
public class Province extends ParentItem {
    //省份名称
    private String provinceName;
    //城市列表
    private List<City> cities;

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public List<City> getCities() {
        return cities;
    }

    public void setCities(List<City> cities) {
        this.cities = cities;
    }

    //内部类继承了ChildItem类，表示这个类中的数据是要展示列表的子级item的数据
    public static class City extends ChildItem {
        //城市名称
        private String cityName;//等于szName
        private String iD;
        private String iParentId;
        private String szName;
        private String szEndoDesc;
        private String szTherapy;
        private String szResult;
        private String EndoType;
//        private String iD;
//        private String iParentId;
//        private String szName;
//        private String szEndoDesc;
//        private String szTherapy;
//        private String szResult;
//        @SerializedName("EndoType")
//        private String EndoType;

        public String getiD() {
            return iD;
        }

        public void setiD(String iD) {
            this.iD = iD;
        }

        public String getiParentId() {
            return iParentId;
        }

        public void setiParentId(String iParentId) {
            this.iParentId = iParentId;
        }

        public String getSzName() {
            return szName;
        }

        public void setSzName(String szName) {
            this.szName = szName;
        }

        public String getSzEndoDesc() {
            return szEndoDesc;
        }

        public void setSzEndoDesc(String szEndoDesc) {
            this.szEndoDesc = szEndoDesc;
        }

        public String getSzTherapy() {
            return szTherapy;
        }

        public void setSzTherapy(String szTherapy) {
            this.szTherapy = szTherapy;
        }

        public String getSzResult() {
            return szResult;
        }

        public void setSzResult(String szResult) {
            this.szResult = szResult;
        }

        public String getEndoType() {
            return EndoType;
        }

        public void setEndoType(String endoType) {
            EndoType = endoType;
        }

        public String getProvinceName() {
            return super.parentName;
        }

        public void setProvinceName(String provinceName) {
            super.parentName = provinceName;
        }

        public String getCityName() {
            return cityName;
        }

        public void setCityName(String cityName) {
            this.cityName = cityName;
        }

        @Override
        public String toString() {
            return "City{" +
                    "cityName='" + cityName + '\'' +
                    ", iD='" + iD + '\'' +
                    ", iParentId='" + iParentId + '\'' +
                    ", szName='" + szName + '\'' +
                    ", szEndoDesc='" + szEndoDesc + '\'' +
                    ", szTherapy='" + szTherapy + '\'' +
                    ", szResult='" + szResult + '\'' +
                    ", EndoType='" + EndoType + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "Province{" +
                "provinceName='" + provinceName + '\'' +
                ", cities=" + cities +
                '}';
    }
}
