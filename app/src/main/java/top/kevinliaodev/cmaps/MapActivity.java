package top.kevinliaodev.cmaps;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import org.osmdroid.api.IMapController;
import org.osmdroid.events.MapListener;
import org.osmdroid.events.ScrollEvent;
import org.osmdroid.events.ZoomEvent;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import top.madev.clocationlib.GetLocation;
import top.madev.cmapslib.CMaps;

public class MapActivity extends AppCompatActivity {

    public static final String CURRENT_LATITUDE = "currentLatitude";
    public static final String CURRENT_LONGITUDE = "currentLongitude";

    private final static String TAG = "MapActivity";
    private MapView mMapView;
    private IMapController mController;
    private double currentLatitude;
    private double currentLongitude;
    private boolean isQueryAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        setTitle("地图");
        currentLatitude = getIntent().getDoubleExtra(CURRENT_LATITUDE, 0.0);
        currentLongitude = getIntent().getDoubleExtra(CURRENT_LONGITUDE, 0.0);
        if(currentLatitude != 0.0 || currentLongitude != 0.0){
            isQueryAddress = true;
        }
        mMapView = findViewById(R.id.map_osm);
        CMaps.getInstance().setmMapView(mMapView);
        init();
    }

    private void init(){
        initMap();
        if(!isQueryAddress) CMaps.getInstance().mapLocation(getApplication(), this);
        findViewById(R.id.ib_location_osm_google_tile_map).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CMaps.getInstance().mapLocation(getApplication(),MapActivity.this);
            }
        });
        findViewById(R.id.ib_location_zoomin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mController.zoomIn();
            }
        });
        findViewById(R.id.ib_location_zoomout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mController.zoomOut();
            }
        });
    }

    private void initMap(){
        mController = mMapView.getController();
        mMapView.setMultiTouchControls(true);
        mMapView.setPressed(true);
        mController.setZoom(18);
        if(isQueryAddress){
            mController.setCenter(new GeoPoint(currentLatitude, currentLongitude));
        }else {
            mController.setCenter(new GeoPoint(31.1977134790, 121.6231640845));//默认地址
        }
        mMapView.setMapListener(new MapListener() {
            @Override
            public boolean onScroll(ScrollEvent scrollEvent) {
                currentLatitude = mMapView.getMapCenter().getLatitude();
                currentLongitude = mMapView.getMapCenter().getLongitude();
                Log.d(TAG, "移动到:" + currentLatitude + "," + currentLongitude);
                return false;
            }

            @Override
            public boolean onZoom(ZoomEvent zoomEvent) {
                return false;
            }
        });
        //可以自行判断能否使用google.com。true使用google.cn，false使用goole.com
        //google.com源地图更丰富，在墙外网络通畅的情况下建议使用google.com
//        CMaps.getInstance().selectTileSource(false);
        //根据ip选择地图源，这样墙内墙外均可以使用
        CMaps.getInstance().selectTileSource();
    }

    @Override
    protected void onStop() {
        super.onStop();
        GetLocation.getInstance().onStop();
    }

}
