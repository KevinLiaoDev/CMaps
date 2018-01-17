package top.madev.cmapslib;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.Toast;

import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.MapTile;
import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.mylocation.DirectedLocationOverlay;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import cn.finalteam.okhttpfinal.BaseHttpRequestCallback;
import cn.finalteam.okhttpfinal.HttpRequest;
import cn.finalteam.okhttpfinal.OkHttpFinal;
import cn.finalteam.okhttpfinal.OkHttpFinalConfiguration;
import cn.finalteam.okhttpfinal.RequestParams;
import top.madev.clocationlib.GetLocation;
import top.madev.clocationlib.bean.MyLocation;
import top.madev.clocationlib.bean.Point;
import top.madev.clocationlib.utils.BoundaryCheck;
import top.madev.clocationlib.utils.CoordinateConversion;

/**
 * Created by liaokai on 2018/1/10.
 */

public class CMaps {
    private final static String TAG = "CMaps";
    private MapView mMapView;
    private long updateTime;
    private double latitude;
    private double longitude;
    private float accuracy;
    private float bearing;

    private static class SingletonHolder {
        private static CMaps instance = new CMaps();
    }

    private CMaps() {

    }

    public static CMaps getInstance() {
        return SingletonHolder.instance;
    }

    public void setmMapView(MapView mapView){
        this.mMapView = mapView;
    }

    public void selectTileSource(){
        OkHttpFinalConfiguration.Builder builder = new OkHttpFinalConfiguration.Builder();
        OkHttpFinal.getInstance().init(builder.build());
        OkHttpFinal.getInstance().updateCommonHeader("User-Agent","dev");
        String url = "http://ip.taobao.com/service/getIpInfo.php";
        RequestParams params = new RequestParams();
        params.addFormDataPart("ip", "myip");
        HttpRequest.get(url, params, new BaseHttpRequestCallback<IPInfoModel>(){
            @Override
            protected void onSuccess(IPInfoModel ipInfoModel) {
                super.onSuccess(ipInfoModel);
                if(ipInfoModel.getCode()==0){
                    if(ipInfoModel.getData().getCountry_id().equals("CN")){
                        selectTileSource(true);
                    }else {
                        selectTileSource(false);
                    }
                }
            }

            @Override
            public void onFailure(int errorCode, String msg) {
                super.onFailure(errorCode, msg);
            }
        });
    }

    public void selectTileSource(boolean isChinaMainland){
        //走google.cn取瓦片地图
        if (isChinaMainland) {
            ITileSource tileSource = new OnlineTileSourceBase("Google", 3, 20, 256, "",
                    new String[]{"https://mt1.google.cn/vt/lyrs=m,traffic&hl=zh-CN&gl=cn&scale=2&x=1713&y=839&z=11"}) {
                @Override
                public String getTileURLString(MapTile mapTile) {
                    Log.d(TAG, "x=" + mapTile.getX() + "&y=" + mapTile.getY() + "&z=" + mapTile.getZoomLevel());
                    Random random = new Random();
                    String url = "https://mt" + random.nextInt(3) + ".google.cn/vt/lyrs=m,traffic&hl=zh-CN&gl=cn&scale=2&x=" + mapTile.getX() + "&y=" + mapTile.getY() + "&z=" + mapTile.getZoomLevel();
                    return url;
                }

            };
            mMapView.setTileSource(tileSource);
        } else {
            //走google.com取瓦片地图
            ITileSource tileSource = new OnlineTileSourceBase("Google", 3, 20, 256, "",
                    new String[]{"https://mt1.google.com/vt/lyrs=m,traffic&hl=zh-CN&gl=cn&scale=2&x=1713&y=839&z=11"}) {
                @Override
                public String getTileURLString(MapTile mapTile) {
                    Log.d(TAG, "x=" + mapTile.getX() + "&y=" + mapTile.getY() + "&z=" + mapTile.getZoomLevel());
                    Random random = new Random();
                    String url = "https://mt" + random.nextInt(3) + ".google.com/vt/lyrs=m,traffic&hl=zh-CN&gl=cn&scale=2&x=" + mapTile.getX() + "&y=" + mapTile.getY() + "&z=" + mapTile.getZoomLevel();
                    return url;
                }

            };
            mMapView.setTileSource(tileSource);
        }
    }

    public void setMapLocationUrl(String url){
        GetLocation.getInstance().setNetworkLocationUrl(url);
    }

    public void mapLocation(final Application context, final Activity activity){
        GetLocation.getInstance().setMultiLocationListener(new GetLocation.OnMultiLocationListener() {
            @Override
            public void onMultiLocation(double multilatitude, double multilongitude, long multiupdateTime, float multiaccuracy, float multibearing) {
                MyLocation myLocation = new MyLocation(multilatitude, multilongitude);
                if(BoundaryCheck.getInstance().IsInsideChina(myLocation)) {
                    Point point = CoordinateConversion.wgs_gcj_encrypts(multilatitude, multilongitude);
                    latitude = point.getLat();
                    longitude = point.getLng();
                }else {
                    latitude = multilatitude;
                    longitude = multilongitude;
                }
                accuracy = multiaccuracy;
                bearing = multibearing;
                locationComplete(context, multiupdateTime);
            }

            @Override
            public void onFailed(int errorCode, String msg) {
                Toast.makeText(context, "定位失败，请坚持网络和权限！", Toast.LENGTH_LONG).show();
            }
        });
        //当你在GPS定位模式下，开启follow可以进行跟随定位。
        GetLocation.getInstance().startMultiLocation(context, activity, true);
    }

    private void locationComplete(Context context, Long time){
        if(updateTime != time){
            updateTime = time;
            if(mMapView!=null) {
                mMapView.getOverlays().clear();
                GeoPoint geoPoint = new GeoPoint(latitude, longitude);
                DirectedLocationOverlay directedLocationOverlay = new DirectedLocationOverlay(context);
                directedLocationOverlay.setDirectionArrow(BitmapFactory.decodeResource(context.getResources(), R.mipmap.icon_google_location));
                directedLocationOverlay.setLocation(geoPoint);
                directedLocationOverlay.setAccuracy((int)accuracy);
                directedLocationOverlay.setShowAccuracy(true);
                if(bearing!=-1.0) directedLocationOverlay.setBearing(bearing);
                mMapView.getOverlays().add(directedLocationOverlay);
            }

            if(time != 0) Log.d(TAG, "更新时间：" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(time)));
        }

        if (mMapView!=null && mMapView.getController()!=null){
            mMapView.getController().animateTo(new GeoPoint(latitude,longitude));
        }
    }

    public void onStop(){
        GetLocation.getInstance().onStop();
    }

}
