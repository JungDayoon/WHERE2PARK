package com.example.navi_ver2;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
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
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;
import com.kakao.kakaonavi.KakaoNaviParams;
import com.kakao.kakaonavi.KakaoNaviService;
import com.kakao.kakaonavi.NaviOptions;
import com.kakao.kakaonavi.options.CoordType;
import com.kakao.kakaonavi.options.RpOption;
import com.kakao.kakaonavi.options.VehicleType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Timer;

import noman.googleplaces.NRPlaces;
import noman.googleplaces.Place;
import noman.googleplaces.PlaceType;
import noman.googleplaces.PlacesException;
import noman.googleplaces.PlacesListener;

import static java.util.Collections.sort;


public class MapsActivity extends AppCompatActivity
        implements OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback,PlacesListener, GoogleMap.OnMarkerClickListener {
    public static final String NOTIFICATION_CHANNEL_ID = "10001";
    private String data;
    private Geocoder geocoder;
    private Button button;
    private EditText editText;
    private TextView textView;
    private GoogleMap mMap;
    private Marker currentMarker = null;
    private Button[] ChoiceButton = new Button[4];
    private Iterator<Marker> iterator;

    private int clickflag;
    private TextView[] dist = new TextView[3];
    private static TextView[] light = new TextView[3];

    private static final String TAG = "googlemap_example";
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int UPDATE_INTERVAL_MS = 1000;  // 1초
    private static final int FASTEST_UPDATE_INTERVAL_MS = 500; // 0.5초

    private int DriverChoose;

    // onRequestPermissionsResult에서 수신된 결과에서 ActivityCompat.requestPermissions를 사용한 퍼미션 요청을 구별하기 위해 사용됩니다.
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    boolean needRequest = false;


    // 앱을 실행하기 위해 필요한 퍼미션을 정의합니다.
    String[] REQUIRED_PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};  // 외부 저장소


    Location mCurrentLocatiion;
    LatLng currentPosition;
    LatLng DestPoint;

    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest locationRequest;
    private Location location;


    private View mLayout;  // Snackbar 사용하기 위해서는 View가 필요합니다.
    // (참고로 Toast에서는 Context가 필요했습니다.)
    List<Marker> previous_marker = null;
    List<Marker> sorted_marker = null;

    MarkerOptions DestOptions;
    ArrayList<MarkerInfo> MarkerList;

    static test task;
    String[] AdditionalInfo = new String[3];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_maps);
        Intent intent = getIntent();
        data = intent.getStringExtra("value");
        previous_marker = new ArrayList<Marker>();
//        button = (Button)findViewById(R.id.button);
        ChoiceButton[0] = findViewById(R.id.desbutton);
        ChoiceButton[1] = findViewById(R.id.button1);
        ChoiceButton[2] = findViewById(R.id.button2);
        ChoiceButton[3] = findViewById(R.id.button3);

        dist[0] = findViewById(R.id.distance1);
        dist[1] = findViewById(R.id.distance2);
        dist[2] = findViewById(R.id.distance3);

        light[0] = findViewById(R.id.car1);
        light[1] = findViewById(R.id.car2);
        light[2] = findViewById(R.id.car3);

        mLayout = findViewById(R.id.layout_main);


        locationRequest = new LocationRequest()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL_MS)
                .setFastestInterval(FASTEST_UPDATE_INTERVAL_MS);


        LocationSettingsRequest.Builder builder =
                new LocationSettingsRequest.Builder();

        builder.addLocationRequest(locationRequest);


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        task = new test(this);

        AdditionalInfo[0] = "타워주차장";
        AdditionalInfo[1] = "여성전용주차장";
        AdditionalInfo[2] = "넓은주차공간";

        System.out.println("주차장 정보 " + AdditionalInfo[0]);

        //타이머 객체 생성
        Timer timer = new Timer();

        // 1초후에 2초 간격으로 반복
        timer.schedule(task, 100, 2000);
        try {

        } catch (ActivityNotFoundException e) {
            timer.cancel();
        }


    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        System.out.println("On Map");
        Log.d(TAG, "onMapReady :");


        mMap = googleMap;
        geocoder = new Geocoder(this);
        //런타임 퍼미션 요청 대화상자나 GPS 활성 요청 대화상자 보이기전에
        //지도의 초기위치를 서울로 이동
        setDefaultLocation();

        //런타임 퍼미션 처리
        // 1. 위치 퍼미션을 가지고 있는지 체크합니다.
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);


        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {

            // 2. 이미 퍼미션을 가지고 있다면
            // ( 안드로이드 6.0 이하 버전은 런타임 퍼미션이 필요없기 때문에 이미 허용된 걸로 인식합니다.)


            startLocationUpdates(); // 3. 위치 업데이트 시작


        } else {  //2. 퍼미션 요청을 허용한 적이 없다면 퍼미션 요청이 필요합니다. 2가지 경우(3-1, 4-1)가 있습니다.

            // 3-1. 사용자가 퍼미션 거부를 한 적이 있는 경우에는
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])) {

                // 3-2. 요청을 진행하기 전에 사용자가에게 퍼미션이 필요한 이유를 설명해줄 필요가 있습니다.
                Snackbar.make(mLayout, "이 앱을 실행하려면 위치 접근 권한이 필요합니다.",
                        Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        // 3-3. 사용자게에 퍼미션 요청을 합니다. 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                        ActivityCompat.requestPermissions(MapsActivity.this, REQUIRED_PERMISSIONS,
                                PERMISSIONS_REQUEST_CODE);
                    }
                }).show();


            } else {
                // 4-1. 사용자가 퍼미션 거부를 한 적이 없는 경우에는 퍼미션 요청을 바로 합니다.
                // 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);
            }

        }


        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng latLng) {

                Log.d(TAG, "onMapClick :");
            }
        });
        mMap.setOnMarkerClickListener(this);

        //        button.setOnClickListener(new Button.OnClickListener(){
//            @Override
//            public void onClick(View v){

        System.out.println("button clicked");

        List<Address> addressList = null;
        try {
            // editText에 입력한 텍스트(주소, 지역, 장소 등)을 지오 코딩을 이용해 변환
            addressList = geocoder.getFromLocationName(
                    data, // 주소
                    10); // 최대 검색 결과 개수
        } catch (IOException e) {
            e.printStackTrace();
        }


        String latitude;
        String longitude;

        // 콤마를 기준으로 split
        String[] splitStr = addressList.get(0).toString().split(",");
        String address = splitStr[0].substring(splitStr[0].indexOf("\"") + 1, splitStr[0].length()); // 주소
        System.out.println("address : " +address);

        latitude = splitStr[14].substring(splitStr[14].indexOf("=") + 1); // 위도
        longitude = splitStr[16].substring(splitStr[16].indexOf("=") + 1); // 경도
        System.out.println("latitude : "+latitude);
        System.out.println("longitude : "+longitude);

        // 좌표(위도, 경도) 생성

        DestPoint = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
        // 마커 생성
        DestOptions = new MarkerOptions();
        DestOptions.title("search result");
        DestOptions.snippet(address);
        DestOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        DestOptions.position(DestPoint);
        // 마커 추가
        mMap.addMarker(DestOptions);
        // 해당 좌표로 화면 줌
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(DestPoint, 16));

        showPlaceInformation(DestPoint);


        //}

        //});

        ChoiceButton[0].setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                DriverChoose = 0;
                System.out.println("DriverChoose: " + DriverChoose);
                com.kakao.kakaonavi.Location destination = com.kakao.kakaonavi.Location.newBuilder(
                        data,
                        DestPoint.longitude,
                        DestPoint.latitude).build();

                NaviOptions options = NaviOptions.newBuilder().setCoordType(CoordType.WGS84)
                        .setVehicleType(VehicleType.FIRST).setRpOption(RpOption.SHORTEST).build();

                KakaoNaviParams.Builder builder = KakaoNaviParams.newBuilder(destination).setNaviOptions(options);
                KakaoNaviParams params = builder.build();

                KakaoNaviService.getInstance().navigate(MapsActivity.this, builder.build());


                //alertShow();


            }
        });

        ChoiceButton[1].setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                DriverChoose = 1;
                System.out.println("DriverChoose: " + DriverChoose);
                com.kakao.kakaonavi.Location destination = com.kakao.kakaonavi.Location.newBuilder(
                        previous_marker.get(1).getTitle(),
                        previous_marker.get(1).getPosition().longitude,
                        previous_marker.get(1).getPosition().latitude).build();

                NaviOptions options = NaviOptions.newBuilder().setCoordType(CoordType.WGS84)
                        .setVehicleType(VehicleType.FIRST).setRpOption(RpOption.SHORTEST).build();

                KakaoNaviParams.Builder builder = KakaoNaviParams.newBuilder(destination).setNaviOptions(options);
                KakaoNaviParams params = builder.build();

                KakaoNaviService.getInstance().navigate(MapsActivity.this, builder.build());


            }
        });
        ChoiceButton[2].setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                DriverChoose = 2;
                System.out.println("DriverChoose: " + DriverChoose);
                com.kakao.kakaonavi.Location destination = com.kakao.kakaonavi.Location.newBuilder(
                        previous_marker.get(2).getTitle(),
                        previous_marker.get(2).getPosition().longitude,
                        previous_marker.get(2).getPosition().latitude).build();

                NaviOptions options = NaviOptions.newBuilder().setCoordType(CoordType.WGS84)
                        .setVehicleType(VehicleType.FIRST).setRpOption(RpOption.SHORTEST).build();

                KakaoNaviParams.Builder builder = KakaoNaviParams.newBuilder(destination).setNaviOptions(options);
                KakaoNaviParams params = builder.build();

                KakaoNaviService.getInstance().navigate(MapsActivity.this, builder.build());


            }
        });
        ChoiceButton[3].setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                DriverChoose = 3;
                System.out.println("DriverChoose: " + DriverChoose);
                com.kakao.kakaonavi.Location destination = com.kakao.kakaonavi.Location.newBuilder(
                        previous_marker.get(3).getTitle(),
                        previous_marker.get(3).getPosition().longitude,
                        previous_marker.get(3).getPosition().latitude).build();

                NaviOptions options = NaviOptions.newBuilder().setCoordType(CoordType.WGS84)
                        .setVehicleType(VehicleType.FIRST).setRpOption(RpOption.SHORTEST).build();

                KakaoNaviParams.Builder builder = KakaoNaviParams.newBuilder(destination).setNaviOptions(options);
                KakaoNaviParams params = builder.build();

                KakaoNaviService.getInstance().navigate(MapsActivity.this, builder.build());


            }
        });


        System.out.println("button clicked");

    }


    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);

            List<Location> locationList = locationResult.getLocations();

            if (locationList.size() > 0) {
                location = locationList.get(locationList.size() - 1);
                //location = locationList.get(0);

                currentPosition
                        = new LatLng(location.getLatitude(), location.getLongitude());


                String markerTitle = getCurrentAddress(currentPosition);
                String markerSnippet = "위도:" + String.valueOf(location.getLatitude())
                        + " 경도:" + String.valueOf(location.getLongitude());

                Log.d(TAG, "onLocationResult : " + markerSnippet);


                //현재 위치에 마커 생성하고 이동
                setCurrentLocation(location, markerTitle, markerSnippet);

                mCurrentLocatiion = location;

            }
        }
    };

    private void startLocationUpdates() {

        if (!checkLocationServicesStatus()) {

            Log.d(TAG, "startLocationUpdates : call showDialogForLocationServiceSetting");
            showDialogForLocationServiceSetting();
        } else {

            int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION);
            int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION);


            if (hasFineLocationPermission != PackageManager.PERMISSION_GRANTED ||
                    hasCoarseLocationPermission != PackageManager.PERMISSION_GRANTED) {

                Log.d(TAG, "startLocationUpdates : 퍼미션 안가지고 있음");
                return;
            }


            Log.d(TAG, "startLocationUpdates : call mFusedLocationClient.requestLocationUpdates");

            mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());

            if (checkPermission())
                mMap.setMyLocationEnabled(true);

        }

    }


    @Override
    protected void onStart() {
        super.onStart();

        Log.d(TAG, "onStart");

        if (checkPermission()) {

            Log.d(TAG, "onStart : call mFusedLocationClient.requestLocationUpdates");
            mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);

            if (mMap != null)
                mMap.setMyLocationEnabled(true);

        }


    }


    @Override
    protected void onStop() {

        super.onStop();

        if (mFusedLocationClient != null) {

            Log.d(TAG, "onStop : call stopLocationUpdates");
            mFusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }


    public String getCurrentAddress(LatLng latlng) {

        //지오코더... GPS를 주소로 변환
        geocoder = new Geocoder(this, Locale.getDefault());

        List<Address> addresses;

        try {

            addresses = geocoder.getFromLocation(
                    latlng.latitude,
                    latlng.longitude,
                    1);
        } catch (IOException ioException) {
            //네트워크 문제
            Toast.makeText(this, "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show();
            return "지오코더 서비스 사용불가";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(this, "잘못된 GPS 좌표", Toast.LENGTH_LONG).show();
            return "잘못된 GPS 좌표";

        }


        if (addresses == null || addresses.size() == 0) {

//            Toast.makeText(this, "주소 미발견", Toast.LENGTH_LONG).show();
            return "주소 미발견";

        } else {
//            Toast.makeText(this, "주소 발견!!", Toast.LENGTH_LONG).show();
            Address address = addresses.get(0);
            return address.getAddressLine(0).toString();
        }

    }


    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }


    public void setCurrentLocation(Location location, String markerTitle, String markerSnippet) {


        if (currentMarker != null) currentMarker.remove();


        LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(currentLatLng);
        markerOptions.title(markerTitle);
        markerOptions.snippet(markerSnippet);
        markerOptions.draggable(true);


        currentMarker = mMap.addMarker(markerOptions);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(currentLatLng);
        //mMap.moveCamera(cameraUpdate);

    }


    public void setDefaultLocation() {


        //디폴트 위치, Seoul
        LatLng DEFAULT_LOCATION = new LatLng(37.56, 126.97);
        String markerTitle = "위치정보 가져올 수 없음";
        String markerSnippet = "위치 퍼미션과 GPS 활성 여부 확인하세요";


        if (currentMarker != null) currentMarker.remove();

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(DEFAULT_LOCATION);
        markerOptions.title(markerTitle);
        markerOptions.snippet(markerSnippet);
        markerOptions.draggable(true);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        currentMarker = mMap.addMarker(markerOptions);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION, 16);
        mMap.moveCamera(cameraUpdate);

    }


    //여기부터는 런타임 퍼미션 처리을 위한 메소드들
    private boolean checkPermission() {

        int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);


        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {
            return true;
        }

        return false;

    }


    /*
     * ActivityCompat.requestPermissions를 사용한 퍼미션 요청의 결과를 리턴받는 메소드입니다.
     */
    @Override
    public void onRequestPermissionsResult(int permsRequestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grandResults) {

        if (permsRequestCode == PERMISSIONS_REQUEST_CODE && grandResults.length == REQUIRED_PERMISSIONS.length) {

            // 요청 코드가 PERMISSIONS_REQUEST_CODE 이고, 요청한 퍼미션 개수만큼 수신되었다면

            boolean check_result = true;


            // 모든 퍼미션을 허용했는지 체크합니다.

            for (int result : grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }


            if (check_result) {

                // 퍼미션을 허용했다면 위치 업데이트를 시작합니다.
                startLocationUpdates();
            } else {
                // 거부한 퍼미션이 있다면 앱을 사용할 수 없는 이유를 설명해주고 앱을 종료합니다.2 가지 경우가 있습니다.

                if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[1])) {


                    // 사용자가 거부만 선택한 경우에는 앱을 다시 실행하여 허용을 선택하면 앱을 사용할 수 있습니다.
                    Snackbar.make(mLayout, "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요. ",
                            Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {

                            finish();
                        }
                    }).show();

                } else {


                    // "다시 묻지 않음"을 사용자가 체크하고 거부를 선택한 경우에는 설정(앱 정보)에서 퍼미션을 허용해야 앱을 사용할 수 있습니다.
                    Snackbar.make(mLayout, "퍼미션이 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해야 합니다. ",
                            Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {

                            finish();
                        }
                    }).show();
                }
            }

        }
    }


    //여기부터는 GPS 활성화를 위한 메소드들
    private void showDialogForLocationServiceSetting() {

        AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n"
                + "위치 설정을 수정하실래요?");
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case GPS_ENABLE_REQUEST_CODE:

                //사용자가 GPS 활성 시켰는지 검사
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {

                        Log.d(TAG, "onActivityResult : GPS 활성화 되있음");


                        needRequest = true;

                        return;
                    }
                }

                break;
        }
    }

    @Override
    public void onPlacesFailure(PlacesException e) {

    }

    @Override
    public void onPlacesStart() {

    }

    @Override
    public void onPlacesSuccess(final List<Place> places) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ArrayList<MarkerOptions> markerOptions = new ArrayList<>();
                int i = 0;
                for (Place place : places) {

                    LatLng latLng
                            = new LatLng(place.getLatitude()
                            , place.getLongitude());

                    String markerSnippet = getCurrentAddress(latLng);

                    MarkerOptions tempOption = new MarkerOptions();
                    tempOption.position(latLng);
                    tempOption.title(place.getName());
                    tempOption.snippet(markerSnippet);
                    Marker item = mMap.addMarker(tempOption);
                    previous_marker.add(item);
                    markerOptions.add(tempOption);
                }
                //중복 마커 제거
                HashSet<Marker> hashSet = new HashSet<Marker>();
                hashSet.addAll(previous_marker);
                previous_marker.clear();
                previous_marker.addAll(hashSet);

                iterator = previous_marker.iterator();
                Iterator<MarkerOptions> iterator2 = markerOptions.iterator();

                MarkerList = new ArrayList<>();

                mMap.clear();
                mMap.addMarker(DestOptions); //다른 주차장 지우고 목적지랑 다른 주차장만 띄우게

                i = 0;
                while (iterator.hasNext()) {
                    Marker newMark = iterator.next();
                    //MarkerOptions newMarkerOpt = iterator2.next();
                    MarkerInfo Info = new MarkerInfo(newMark, getDistance(DestPoint, newMark.getPosition()));
                    MarkerList.add(Info);
                    //System.out.println("markerOption 확인 " + Info.getM().getTitle()+ " " + Info.getMOptions().getTitle());
                }

                sort(MarkerList, new Comparator<MarkerInfo>() {
                    @Override
                    public int compare(MarkerInfo markerInfo, MarkerInfo t1) {
                        if (markerInfo.getDistance() > t1.getDistance())
                            return 1;
                        else if (markerInfo.getDistance() == t1.getDistance())
                            return 0;
                        else
                            return -1;
                    }
                });

                for (i = 0; i < MarkerList.size(); i++) {
                    System.out.println("MarkerList: " + MarkerList.get(i).getM().getTitle() + " " + MarkerList.get(i).getDistance());
                }


                ChoiceButton[0].setText("목적지로 바로 안내 : " + data);

                for (i = 1; i <= 3; i++) {
                    String markerSnippet = getCurrentAddress(MarkerList.get(i - 1).getM().getPosition());
                    double per2 = getDistance(currentPosition, MarkerList.get(i - 1).getM().getPosition()) / 1000;
                    double per = Double.parseDouble(String.format("%.2f", per2));
                    MarkerOptions bestOption = new MarkerOptions();
                    bestOption.position(MarkerList.get(i - 1).getM().getPosition());
                    bestOption.title(MarkerList.get(i - 1).getM().getTitle());
                    bestOption.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                    bestOption.snippet(markerSnippet);
                    mMap.addMarker(bestOption);
//                    Marker item = mMap.addMarker(tempOption);
//                    markerOptions.add(tempOption);

                    System.out.println("distance : " + getDistance(currentPosition, MarkerList.get(i - 1).getM().getPosition()) / 1000);
                    ChoiceButton[i].setText("" + MarkerList.get(i - 1).getM().getTitle() + per + "km");
                    ChoiceButton[i].setText("" + MarkerList.get(i - 1).getM().getTitle());
                    dist[i - 1].setText(Double.toString(per) + "km");

                }

                test.ParkingLotInfo[] mapParkingLotInfo = new test.ParkingLotInfo[5];
                boolean[] mapParkingLotCondition = new boolean[5];
                for (i = 2; i < 5; i++) {
                    mapParkingLotInfo[i] = new test.ParkingLotInfo();
                    mapParkingLotInfo[i] = task.myParkingLotInfo[i];
                    mapParkingLotCondition[i] = task.myParkingLotCondition[i];
                    System.out.println("in map :" + i + "- " + mapParkingLotCondition[i] + " " + mapParkingLotInfo[i].empty + "/" + mapParkingLotInfo[i].total);
                    light[i - 2].setText(mapParkingLotInfo[i].empty + "/" + mapParkingLotInfo[i].total);
                    if (mapParkingLotCondition[i] == false) {
                        ChoiceButton[i - 1].setEnabled(false);
                        ChoiceButton[i - 1].setBackgroundColor(0x808080);
                        light[i - 2].setBackgroundColor(0x808080);
                        dist[i - 2].setBackgroundColor(0x808080);
                    } else if (mapParkingLotCondition[i] == true) {
                        ChoiceButton[i - 1].setEnabled(true);
                        ChoiceButton[i - 1].setBackgroundColor(0x000000);
                        light[i - 2].setBackgroundColor(0x000000);
                        dist[i - 2].setBackgroundColor(0x000000);
                    }

                }



            }
        });

    }

    @Override
    public void onPlacesFinished() {

    }

    public void showPlaceInformation(LatLng location) {
        mMap.clear();//지도 클리어

        if (previous_marker != null)
            previous_marker.clear();//지역정보 마커 클리어

        new NRPlaces.Builder()
                .listener(MapsActivity.this)
                .key("AIzaSyBSQaJ6YQdu4uV1DBT9sz95bzVrhVmI_wk")
                .latlng(location.latitude, location.longitude)//검색한 위치
                .radius(500) //500 미터 내에서 검색
                .type(PlaceType.PARKING) //주차장
                .language("ko", "KR")
                .build()
                .execute();


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

        System.out.println("getdistance: " + distance);
        return distance;
    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        Random random = new Random();
        Toast.makeText(this, "* " + marker.getTitle() + "은 " + AdditionalInfo[clickflag++] + "입니다", Toast.LENGTH_LONG).show();

        return true;
    }

    public class MarkerInfo implements Comparator<MarkerInfo> {
        Marker M;
        double distance;

        public MarkerInfo(Marker M, double distance) {
            this.M = M;
            this.distance = distance;
        }

        public void setM(Marker M) {
            this.M = M;
        }

        public Marker getM() {
            return M;
        }

        public void setDistance(double distance) {
            this.distance = distance;
        }

        public double getDistance() {
            return distance;
        }


        @Override
        public int compare(MarkerInfo markerInfo, MarkerInfo t1) {
            if (markerInfo.getDistance() > t1.getDistance())
                return 1;
            else if (markerInfo.getDistance() == t1.getDistance())
                return 0;
            else
                return -1;
        }
    }

    public void setLightAgainFunc() {
        int i;
        test.ParkingLotInfo[] mapParkingLotInfo = new test.ParkingLotInfo[5];
        boolean[] mapParkingLotCondition = new boolean[5];
        for (i = 2; i < 5; i++) {
            mapParkingLotInfo[i] = new test.ParkingLotInfo();
            mapParkingLotInfo[i] = task.myParkingLotInfo[i];
            mapParkingLotCondition[i] = task.myParkingLotCondition[i];
            System.out.println("in map :" + mapParkingLotCondition[i] + " " + mapParkingLotInfo[i].empty + "/" + mapParkingLotInfo[i].total);
            light[i - 2].setText(mapParkingLotInfo[i].empty + "/" + mapParkingLotInfo[i].total);

//            if (mapParkingLotCondition[i] == false) {
//                ChoiceButton[i - 1].setEnabled(false);
//                ChoiceButton[i - 1].setBackgroundColor(0x808080);
//                light[i - 2].setBackgroundColor(0x808080);
//                dist[i - 2].setBackgroundColor(0x808080);
//            } else if (mapParkingLotCondition[i] == true) {
//                ChoiceButton[i - 1].setEnabled(true);
//                ChoiceButton[i - 1].setBackgroundColor(0x000000);
//                light[i - 2].setBackgroundColor(0x000000);
//                dist[i - 2].setBackgroundColor(0x000000);
//            }
        }


    }
}

