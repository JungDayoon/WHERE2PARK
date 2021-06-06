package com.example.navi_ver2;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.googlemap.R;
import com.google.android.gms.maps.model.LatLng;
import com.kakao.kakaonavi.KakaoNaviParams;
import com.kakao.kakaonavi.KakaoNaviService;
import com.kakao.kakaonavi.NaviOptions;
import com.kakao.kakaonavi.options.CoordType;
import com.kakao.kakaonavi.options.RpOption;
import com.kakao.kakaonavi.options.VehicleType;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MapsActivity extends AppCompatActivity implements MapView.CurrentLocationEventListener, MapView.MapViewEventListener {
    public static final String NOTIFICATION_CHANNEL_ID = "10001";
    private String data;
    private Button[] ChoiceButton = new Button[4];

    private TextView[] dist = new TextView[3];
    private static TextView[] light = new TextView[3];

    private static final String TAG = "kakaoemap_example";
    private static final int UPDATE_INTERVAL_MS = 1000;  // 1초
    private static final int FASTEST_UPDATE_INTERVAL_MS = 500; // 0.5초
    // 앱을 실행하기 위해 필요한 퍼미션을 정의합니다.
    String[] REQUIRED_PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};  // 외부 저장소

    final String BASE_URL = "https://dapi.kakao.com/";
    final String API_KEY = "KakaoAK 42ea0050be8006956ed3ac000f65c165";

    String[] AdditionalInfo = new String[3];

    private static final String LOG_TAG = "MainActivity";
    private MapView mapView;
    private ViewGroup mapViewContainer;
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_CODE = 100;

    Place destination2 = new Place();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_maps);
        Intent intent = getIntent();
        data = intent.getStringExtra("value");

        ChoiceButton[0] = findViewById(R.id.desbutton);
        ChoiceButton[1] = findViewById(R.id.button1);
        ChoiceButton[2] = findViewById(R.id.button2);
        ChoiceButton[3] = findViewById(R.id.button3);

        test job = new test(this);
        Timer jobScheduler = new Timer();
        jobScheduler.scheduleAtFixedRate(job, 3000, 5000);
//
        dist[0] = findViewById(R.id.distance1);
        dist[1] = findViewById(R.id.distance2);
        dist[2] = findViewById(R.id.distance3);
        init(data);

    }
    private void init(String keyword){
        mapView = new MapView(MapsActivity.this);
        mapViewContainer = (ViewGroup) findViewById(R.id.MapView);
        mapViewContainer.addView(mapView);


        mapView.setMapViewEventListener(MapsActivity.this);
        if (!checkLocationServicesStatus()) {
            showDialogForLocationServiceSetting();
        }else {
            checkRunTimePermission();
        }

        searchKeyword(keyword);
    }

    private void searchKeyword(String keyword){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        KakaoAPI kakaoapi = retrofit.create(KakaoAPI.class);
        Call<ResultSearchKeyword> call = kakaoapi.getSearchKeyword(API_KEY, keyword);
        call.enqueue(new Callback<ResultSearchKeyword>() {

            @Override
            public void onResponse(Call<ResultSearchKeyword> call, Response<ResultSearchKeyword> response) {
                Log.d("Test", "Raw : "+ response.raw().toString());
                Place point = response.body().documents.get(0);
                Double longitude = Double.parseDouble(response.body().documents.get(0).x);
                Double latitude = Double.parseDouble(response.body().documents.get(0).y);

                setpin(0, point, longitude, latitude);
                ChoiceButton[0].setText(point.place_name+" 으로 안내하기");
                ChoiceButton[0].setOnClickListener(new Button.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int DriverChoose = 0;
                        Log.i("Button", "DriverChoose: " + DriverChoose);
                        startnavi(point.place_name, longitude, latitude);

                    }
                });
                LatLng destination_latlng = new LatLng(latitude, longitude);
                searchCategory(keyword, destination_latlng);

            }
            @Override
            public void onFailure(Call<ResultSearchKeyword> call, Throwable t) {
                Log.e("Test", "Error");
            }
        });
    }
    private void searchCategory(String keyword, LatLng parking_latlng){

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        KakaoAPI kakaoapi = retrofit.create(KakaoAPI.class);
        Call<ResultSearchKeyword> call = kakaoapi.getSearchCategory(API_KEY, keyword, "PK6");
        call.enqueue(new Callback<ResultSearchKeyword>() {

            @Override
            public void onResponse(Call<ResultSearchKeyword> call, Response<ResultSearchKeyword> response) {

                Log.d("Test", "Raw : "+ response.raw().toString());
                List<MarkerInfo> allplace = new ArrayList<>();
                if(response.body().documents.size()!=0) {
                    for (int i = 0; i < response.body().documents.size(); i++) {
                        Place newplace = response.body().documents.get(i);
                        Double longitude = Double.parseDouble(newplace.x);
                        Double latitude = Double.parseDouble(newplace.y);
                        MarkerInfo marker = new MarkerInfo(newplace, getDistance(parking_latlng, new LatLng(latitude, longitude)));
                        allplace.add(marker);
                    }

                    Collections.sort(allplace);

                    for (int i = 0; i < 3; i++) {
                        Place newplace = allplace.get(i).p;
                        Double longitude = Double.parseDouble(newplace.x);
                        Double latitude = Double.parseDouble(newplace.y);
                        setpin(i + 1, newplace, longitude, latitude);
                        int DriverChoose = i + 1;
                        dist[i].setText(String.format("%.2f m", allplace.get(i).distance));
                        ChoiceButton[i + 1].setText(newplace.place_name);
                        ChoiceButton[i + 1].setOnClickListener(new Button.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Log.i("Button", "DriverChoose: " + DriverChoose);

                                startnavi(newplace.place_name, longitude, latitude);
                            }
                        });
                    }
                }
            }

            @Override
            public void onFailure(Call<ResultSearchKeyword> call, Throwable t) {
                Log.e("Test", "Error");
            }
        });
    }

    void startnavi(String placename, Double longitude, Double latitude){
        AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
        builder.setTitle("안내");
        builder.setMessage(placename+"으로 안내를 시작하시겠습니까?");
        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            public void onClick(
                    DialogInterface dialog, int id) {
                com.kakao.kakaonavi.Location destination = com.kakao.kakaonavi.Location.newBuilder(
                        data,
                        longitude, latitude).build();

                NaviOptions options = NaviOptions.newBuilder().setCoordType(CoordType.WGS84)
                        .setVehicleType(VehicleType.FIRST).setRpOption(RpOption.SHORTEST).build();

                KakaoNaviParams.Builder builder2 = KakaoNaviParams.newBuilder(destination).setNaviOptions(options);
                KakaoNaviParams params = builder2.build();

                KakaoNaviService.getInstance().navigate(MapsActivity.this, builder2.build());

            }
        });
        builder.setNegativeButton("아니오",  new DialogInterface.OnClickListener() {
            public void onClick(
                    DialogInterface dialog, int id) {
                //"아니오" 버튼 클릭시 실행하는 메소드
                Toast.makeText(getBaseContext(),"안내실패", Toast.LENGTH_SHORT).show();
            }
        });
        builder.create().show();

    }

    void setpin(int pinnum, Place place, Double longitude, Double latitude){
        MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(latitude, longitude);

        MapPOIItem marker = new MapPOIItem();
        marker.setItemName(place.place_name);
        marker.setTag(pinnum);
        marker.setMapPoint(mapPoint);
        if(pinnum == 0) {
            marker.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
            mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(latitude, longitude), true);
        }
        else{
            marker.setMarkerType(MapPOIItem.MarkerType.YellowPin);
        }
        marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.
        mapView.addPOIItem(marker);
    }

    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }
    void checkRunTimePermission(){

        //런타임 퍼미션 처리
        // 1. 위치 퍼미션을 가지고 있는지 체크합니다.
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(MapsActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION);

        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED ) {
            // 2. 이미 퍼미션을 가지고 있다면
            // ( 안드로이드 6.0 이하 버전은 런타임 퍼미션이 필요없기 때문에 이미 허용된 걸로 인식합니다.)
            // 3.  위치 값을 가져올 수 있음

        } else {  //2. 퍼미션 요청을 허용한 적이 없다면 퍼미션 요청이 필요합니다. 2가지 경우(3-1, 4-1)가 있습니다.
            // 3-1. 사용자가 퍼미션 거부를 한 적이 있는 경우에는
            if (ActivityCompat.shouldShowRequestPermissionRationale(MapsActivity.this, REQUIRED_PERMISSIONS[0])) {
                // 3-2. 요청을 진행하기 전에 사용자가에게 퍼미션이 필요한 이유를 설명해줄 필요가 있습니다.
                Toast.makeText(MapsActivity.this, "이 앱을 실행하려면 위치 접근 권한이 필요합니다.", Toast.LENGTH_LONG).show();
                // 3-3. 사용자게에 퍼미션 요청을 합니다. 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(MapsActivity.this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);
            } else {
                // 4-1. 사용자가 퍼미션 거부를 한 적이 없는 경우에는 퍼미션 요청을 바로 합니다.
                // 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(MapsActivity.this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);
            }
        }
    }
    private void showDialogForLocationServiceSetting() {

        AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n"
                + "위치 설정을 수정하시겠습니까?");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent
                        = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapViewContainer.removeAllViews();
    }

    @Override
    public void onCurrentLocationUpdate(MapView mapView, MapPoint currentLocation, float accuracyInMeters) {
        MapPoint.GeoCoordinate mapPointGeo = currentLocation.getMapPointGeoCoord();
        Log.i(LOG_TAG, String.format("MapView onCurrentLocationUpdate (%f,%f) accuracy (%f)", mapPointGeo.latitude, mapPointGeo.longitude, accuracyInMeters));
    }
    @Override
    public void onCurrentLocationDeviceHeadingUpdate(MapView mapView, float v) {
    }

    @Override
    public void onCurrentLocationUpdateFailed(MapView mapView) {
    }

    @Override
    public void onCurrentLocationUpdateCancelled(MapView mapView) {
    }


    private void onFinishReverseGeoCoding(String result) {
//        Toast.makeText(LocationDemoActivity.this, "Reverse Geo-coding : " + result, Toast.LENGTH_SHORT).show();
    }

    // ActivityCompat.requestPermissions를 사용한 퍼미션 요청의 결과를 리턴받는 메소드
    @Override
    public void onRequestPermissionsResult(int permsRequestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grandResults) {

        if ( permsRequestCode == PERMISSIONS_REQUEST_CODE && grandResults.length == REQUIRED_PERMISSIONS.length) {

            // 요청 코드가 PERMISSIONS_REQUEST_CODE 이고, 요청한 퍼미션 개수만큼 수신되었다면
            boolean check_result = true;

            // 모든 퍼미션을 허용했는지 체크합니다.
            for (int result : grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }

            if ( check_result ) {
                Log.d("@@@", "start");
                //위치 값을 가져올 수 있음

            }
            else {
                // 거부한 퍼미션이 있다면 앱을 사용할 수 없는 이유를 설명해주고 앱을 종료합니다.2 가지 경우가 있다
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])) {
                    Toast.makeText(MapsActivity.this, "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요.", Toast.LENGTH_LONG).show();
                    finish();
                }else {
                    Toast.makeText(MapsActivity.this, "퍼미션이 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해야 합니다. ", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @Override
    public void onMapViewInitialized(MapView mapView) {

    }

    @Override
    public void onMapViewCenterPointMoved(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewZoomLevelChanged(MapView mapView, int i) {

    }

    @Override
    public void onMapViewSingleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDoubleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewLongPressed(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragStarted(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragEnded(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewMoveFinished(MapView mapView, MapPoint mapPoint) {

    }
    public double getDistance(LatLng L1, LatLng L2) {
        double distance = 0;
        Location A = new Location("A");
        Location B = new Location("B");
        A.setLatitude(L1.latitude);
        A.setLongitude(L1.longitude);
        B.setLatitude(L2.latitude);
        B.setLongitude(L2.longitude);

        distance = B.distanceTo(A);

        return distance;
    }

    public class MarkerInfo implements Comparable<MarkerInfo>{
        Place p;
        Double distance;

        public MarkerInfo(Place p, double distance) {
            this.p = p;
            this.distance = distance;
        }

        public double getDistance() {
            return distance;
        }

        @Override
        public int compareTo(MarkerInfo o) {
            if (this.getDistance() > o.getDistance())
                return 1;
            else if (this.getDistance() == o.getDistance())
                return 0;
            else
                return -1;
        }
    }


}

