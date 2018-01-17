# CMaps-中国国行安卓手机使用Google地图服务解决方案

[![License](https://img.shields.io/badge/License%20-Apache%202-337ab7.svg)](https://www.apache.org/licenses/LICENSE-2.0)
[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-osmdroid-brightgreen.svg?style=flat)](https://android-arsenal.com/details/1/279)

* 国行安卓手机Google定位服务解决方案---[CLocation](https://github.com/KevinLiaoDev/CLocation)
## 简介
* 相信很多Android开发人员都有这样的痛点，当国内Android用户在国外要使用地图时，好像十分尴尬，没有非常成熟的地图解决方案。
* 由于众所周知的原因，谷歌的很多优秀服务在国内无法使用，其中国行手机由于阉割无法使用谷歌地图服务是一个非常让人头痛的问题。主要问题有：
  - 一方面，国内地图服务提供商（百度地图、高德地图、腾讯地图）在境外的地图资源少得可怜。
  - 另一方面，国行安卓手机由于系统缺少谷歌三大件，使得国行手机使用谷歌地图服务几乎变得不可能。
* 本项目是为了解决该问题建立，围绕Google给出的API接口资源，通过一系列技术方案，使得国行手机在没有谷歌三大件的情况下也可以使用谷歌的地图服务。主要特征有：
  - 使用[osmdroid](https://github.com/osmdroid/osmdroid)根据定位智能选择服务器加载谷歌瓦片地图资源，境内境外均可使用。
  - 集成基于谷歌数据的多重定位方案，在国外实地测试定位准确率要明显高于国内定位服务。---[CLocation](https://github.com/KevinLiaoDev/CLocation)
  - 提供地址地理编码查询和地图定位功能。
  - 封装后的库体积小、逻辑清晰、使用简单，项目内还有多种相关实用工具可供使用。

## 使用前准备
### 需要的权限
```xml
<uses-permission android:name="android.permission.CHANGE_WIFI_STATE" />
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.WAKE_LOCK" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
```
### 获取Google Location API KEY
* 由于定位数据来源于谷歌，所以需要在谷歌控制台获取API KEY。详情参考[Google Maps Geolocation API](https://developers.google.com/maps/documentation/geolocation/intro?hl=zh_CN)
* 同时地理位置编码也需要在控制台申请调用权限。详情参考[Google Maps Geocoding API](https://developers.google.com/maps/documentation/geocoding/start?hl=zh-cn)
```xml
<meta-data
    android:name="GOOGLE_LOCATION_API_KEY"
    android:value="YOUR_API_KEY" />
```

## 使用过程
### 初始化
```java
CMaps.getInstance().setmMapView(mMapView);
```
### 切换定位域名（非必须）
```java
CMaps.getInstance().setMapLocationUrl("https://googleapis.xxxxxx.com/");
```
* 虽然该方案最主要的目标用户是在境外的国行手机，那么访问谷歌接口理应没有太大问题，但是不排除调试或者部分人需要在国内使用，特意封装了修改定位域名的方法。
  - 如果你想在中国境内使用本方案中的网络定位，可在境外服务器反向代理如下接口：https://www.googleapis.com/geolocation/v1/geolocate?key=YOUR_API_KEY
  - 也可根据使用场景接入国内定位服务，在境外时使用CLocation，在境内时使用国内定位服务
### 设置瓦片地图源
```java
//可以自行判断能否使用google.com。true使用google.cn，false使用goole.com
//google.com源地图更丰富，在墙外网络通畅的情况下建议使用google.com
//CMaps.getInstance().selectTileSource(false);
//根据ip选择地图源，这样墙内墙外均可以使用
CMaps.getInstance().selectTileSource();
```
### 进行多重定位
```java
CMaps.getInstance().mapLocation(getApplication(), this);
```
### 地理位置编码查询
```java
CMapsTools.queryAddress(InputAddressActivity.this, address, new CMapsTools.OnLocation() {
    @Override
    public void location(double lat, double lon) {
        progressDialog.dismiss();
        Intent intent = new Intent(InputAddressActivity.this, MapActivity.class);
        intent.putExtra(MapActivity.CURRENT_LATITUDE, lat);
        intent.putExtra(MapActivity.CURRENT_LONGITUDE, lon);
        startActivity(intent);
    }
});
```
### 释放资源
在activity生命周期stop中一定要释放资源并且停止定位，以防资源浪费和异常奔溃
```java
@Override
protected void onStop() {
    super.onStop();
    CMaps.getInstance().onStop();
}
```

## 关于
* 有任何建议或者使用中遇到问题都可以给我发邮件
* Email：kevinliaodev@163.com
