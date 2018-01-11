package top.madev.cmapslib;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import com.google.gson.Gson;

import cn.finalteam.okhttpfinal.BaseHttpRequestCallback;
import cn.finalteam.okhttpfinal.HttpRequest;
import cn.finalteam.okhttpfinal.OkHttpFinal;
import cn.finalteam.okhttpfinal.OkHttpFinalConfiguration;
import cn.finalteam.okhttpfinal.RequestParams;
import top.madev.clocationlib.NetworkLocation;

/**
 * Created by liaokai on 2018/1/11.
 */

public class CMapsTools {
    public interface OnLocation{
        void location(double lat, double lon);
    }
    public static void queryAddress(final Context context, String address, final OnLocation onLocation){
        if (TextUtils.isEmpty(address))
            return;
        String key = NetworkLocation.getMainKey(context);
        if(TextUtils.isEmpty(key)){
            Toast.makeText(context, "Google geocoding key error", Toast.LENGTH_LONG).show();
            return;
        }
        OkHttpFinalConfiguration.Builder builder = new OkHttpFinalConfiguration.Builder();
        OkHttpFinal.getInstance().init(builder.build());
        String url = "https://maps.googleapis.com/maps/api/geocode/json";
        RequestParams params = new RequestParams();
        params.addFormDataPart("address", address);
        params.addFormDataPart("key", key);
        HttpRequest.get(url, params, new BaseHttpRequestCallback<String>(){
            @Override
            protected void onSuccess(String result) {
                super.onSuccess(result);
                Gson gson = new Gson();
                GeocodingModel geocodingModel = gson.fromJson(result, GeocodingModel.class);
                if(geocodingModel.getStatus().equals("OK")) {
                    onLocation.location(geocodingModel.getResults().get(0).getGeometry().getLocation().getLat(),
                            geocodingModel.getResults().get(0).getGeometry().getLocation().getLng());
                }else {
                    Toast.makeText(context, "找不到该地址", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(int errorCode, String msg) {
                super.onFailure(errorCode, msg);
            }
        });
    }
}
