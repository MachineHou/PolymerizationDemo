package com.example.lql.clusterdemo;

import android.Manifest;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.utils.overlay.ClusterMarkerOverlay;
import com.example.lql.clusterdemo.demo.RegionItem;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks
        , ClusterMarkerOverlay.ClusterRender, AMap.OnMapLoadedListener, ClusterClickListener {

    final int RC_CAMERA_AND_WIFI = 0x1;

    MapView mapView;
    private AMap aMap;
    MyLocationStyle myLocationStyle;

    private int clusterRadius = 100; //半径

    private Map<Integer, Drawable> mBackDrawAbles = new HashMap<Integer, Drawable>();

    private ClusterOverlay mClusterOverlay;
    private MapView testmap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        initView(savedInstanceState);
    }

    private void initView(Bundle savedInstanceState) {

        mapView = (MapView) findViewById(R.id.test_map);
        mapView.onCreate(savedInstanceState);
        aMap = mapView.getMap();

        if (null == aMap) {
            aMap = mapView.getMap();
            UiSettings uiSettings = aMap.getUiSettings();
            uiSettings.setLogoBottomMargin(-50);//隐藏logo
            uiSettings.setTiltGesturesEnabled(false);// 禁用倾斜手势。
            uiSettings.setRotateGesturesEnabled(false);// 禁用旋转手势。
            uiSettings.setZoomPosition(AMapOptions.ZOOM_POSITION_RIGHT_CENTER);//放大缩小按钮放在屏幕中间

//            setMapCustomStyleFile(MainActivity.this);
        }
        chackPermission();
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // 必须回调MapView的onSaveInstanceState()方法
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //销毁资源
        mClusterOverlay.onDestroy();
        mapView.onDestroy();
    }


    /**
     * 定义所需要的权限
     */
    String[] perms = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE};

    /**
     * 检查权限
     */
    private void chackPermission() {
        if (EasyPermissions.hasPermissions(this, perms)) {
            Location();
        } else {
            EasyPermissions.requestPermissions(this, "拍照需要摄像头权限", RC_CAMERA_AND_WIFI, perms);
        }
    }


    /**
     * 定位初始化地图
     */
    private void Location() {
        myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE);//定位一次，且将视角移动到地图中心点。
        myLocationStyle.interval(2000); //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        aMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
        aMap.setMyLocationEnabled(true);// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
        aMap.setOnMapLoadedListener(this);
//            addPoint();
//        addLine();
    }


//    private void addLine() {
//
//        List<LatLng> latLngs = new ArrayList<LatLng>();
//        latLngs.add(new LatLng(31.2593572798, 121.1443197727));
//        latLngs.add(new LatLng(31.2583300886, 121.1432147026));
//        latLngs.add(new LatLng(31.2570827699, 121.1426997185));
//        latLngs.add(new LatLng(31.2553309931, 121.1422920227));
//
//        latLngs.add(new LatLng(31.2531481053, 121.1407470703));
//        latLngs.add(new LatLng(31.2518823742, 121.1393952370));
//        latLngs.add(new LatLng(31.2512036418, 121.1384296417));
//        latLngs.add(new LatLng(31.2489656247, 121.1373138428));
//
//        latLngs.add(new LatLng(31.2470577650, 121.1367774010));
//        latLngs.add(new LatLng(31.2460671303, 121.1362838745));
//        latLngs.add(new LatLng(31.2428016314, 121.1337089539));
//        latLngs.add(new LatLng(31.2345823478, 121.1267781258));
//
//        latLngs.add(new LatLng(31.2303623484, 121.1230659485));
//        latLngs.add(new LatLng(31.2248026704, 121.1222076416));
//
//
//        Polyline polyline = aMap.addPolyline(new PolylineOptions().
//                addAll(latLngs).width(10).color(Color.argb(255, 255, 0, 0)));
//    }

//    /**
//     * 添加点
//     */
//    private void addPoint(){
//        //点击可以动态添加点
//        aMap.setOnMapClickListener(new AMap.OnMapClickListener() {
//            @Override
//            public void onMapClick(LatLng latLng) {
//                double lat = Math.random() + 39.474923;
//                double lon = Math.random() + 116.027116;
//                LatLng latLng1 = new LatLng(lat, lon, false);
//                RegionItem regionItem = new RegionItem(latLng1,
//                        "test");
//                mClusterOverlay.addClusterItem(regionItem);
//            }
//        });
//    }
//

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    //成功
    @Override
    public void onPermissionsGranted(int requestCode, List<String> list) {
        // Some permissions have been granted
        Location();
    }

    //失败
    @Override
    public void onPermissionsDenied(int requestCode, List<String> list) {
        // Some permissions have been denied
        Toast.makeText(this, "该应该需要开启定位及获取SD卡相关权限，请手动开启", Toast.LENGTH_SHORT).show();
    }


    /**
     * 初始化地图（勿动）李清林
     *
     * @param context
     */
    private void setMapCustomStyleFile(Context context) {
        String styleName = "style_json.json";
        FileOutputStream outputStream = null;
        InputStream inputStream = null;
        String filePath = null;
        try {
            inputStream = context.getAssets().open(styleName);
            byte[] b = new byte[inputStream.available()];
            inputStream.read(b);

            filePath = context.getFilesDir().getAbsolutePath();
            File file = new File(filePath + "/" + styleName);
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
            outputStream = new FileOutputStream(file);
            outputStream.write(b);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null)
                    inputStream.close();

                if (outputStream != null)
                    outputStream.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        aMap.setCustomMapStylePath(filePath + "/" + styleName);

        aMap.showMapText(true);//是否显示地理位置名称

    }

    /**
     * 地图加载完成之后的回调
     */
    @Override
    public void onMapLoaded() {
        new Thread() {
            public void run() {
                List<ClusterItem> items = new ArrayList<ClusterItem>();
                //随机10000个点
                for (int i = 0; i < 100; i++) {
                    double lat = Math.random() + 39.474923;
                    double lon = Math.random() + 116.027116;
                    LatLng latLng = new LatLng(lat, lon, false);
                    RegionItem regionItem = new RegionItem(latLng, "保利玲珑公馆" + i);
                    items.add(regionItem);
                }

                mClusterOverlay = new ClusterOverlay(aMap, items,
                        dp2px(getApplicationContext(), clusterRadius),
                        getApplicationContext());
//                mClusterOverlay.setClusterRenderer(MainActivity.this);
                mClusterOverlay.setOnClusterClickListener(MainActivity.this);

            }

        }.start();
    }

    @Override
    public Drawable getDrawAble(int clusterNum) {
        int radius = dp2px(getApplicationContext(), 80);
        if (clusterNum == 1) {
            Drawable bitmapDrawable = mBackDrawAbles.get(1);
            if (bitmapDrawable == null) {
                bitmapDrawable =
                        getApplication().getResources().getDrawable(
                                R.drawable.icon_openmap_mark);
                mBackDrawAbles.put(1, bitmapDrawable);
            }

            return bitmapDrawable;
        } else if (clusterNum < 5) {

            Drawable bitmapDrawable = mBackDrawAbles.get(2);
            if (bitmapDrawable == null) {
                bitmapDrawable = new BitmapDrawable(null, drawCircle(radius,
                        Color.argb(159, 210, 154, 6)));
                mBackDrawAbles.put(2, bitmapDrawable);
            }

            return bitmapDrawable;
        } else if (clusterNum < 10) {
            Drawable bitmapDrawable = mBackDrawAbles.get(3);
            if (bitmapDrawable == null) {
                bitmapDrawable = new BitmapDrawable(null, drawCircle(radius,
                        Color.argb(199, 217, 114, 0)));
                mBackDrawAbles.put(3, bitmapDrawable);
            }

            return bitmapDrawable;
        } else {
            Drawable bitmapDrawable = mBackDrawAbles.get(4);
            if (bitmapDrawable == null) {
                bitmapDrawable = new BitmapDrawable(null, drawCircle(radius,
                        Color.argb(235, 215, 66, 2)));
                mBackDrawAbles.put(4, bitmapDrawable);
            }

            return bitmapDrawable;
        }
    }


    private Bitmap drawCircle(int radius, int color) {

        Bitmap bitmap = Bitmap.createBitmap(radius * 2, radius * 2,
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        RectF rectF = new RectF(0, 0, radius * 2, radius * 2);
        paint.setColor(color);
        canvas.drawArc(rectF, 0, 360, true, paint);
        return bitmap;
    }

    @Override
    public void onClick(Marker marker, List<ClusterItem> clusterItems) {
        if (clusterItems.size() == 1) {
            RegionItem regionItem = (RegionItem) clusterItems.get(0);
            initBottomSheetDialog2();
//            Toast.makeText(this, "点击的是" + regionItem.getTitle(), Toast.LENGTH_SHORT).show();
        } else {
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (ClusterItem clusterItem : clusterItems) {
                builder.include(clusterItem.getPosition());
            }
            LatLngBounds latLngBounds = builder.build();
            aMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 0));
        }

    }

    //展示BottomSheetDialog，列表形式
    private void initBottomSheetDialog2() {
        List<String> mList;
        mList = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            mList.add("item " + i);
        }

        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, R.style.AppBottomSheetDialogTheme);
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_dialog_fragment);

        RecyclerView recyclerView = (RecyclerView) bottomSheetDialog.findViewById(R.id.rv_item);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        RecyclerAdapter recyclerAdapter = new RecyclerAdapter(mList, this);
        recyclerView.setAdapter(recyclerAdapter);
        recyclerAdapter.setOnItemClickListener(new RecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClickListener(View item, int position) {
                Toast.makeText(MainActivity.this, "item " + position, Toast.LENGTH_SHORT).show();
                bottomSheetDialog.dismiss();
            }
        });
        bottomSheetDialog.show();
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }


}
