package com.example.watch;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import junit.framework.Test;

import android.R.integer;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.watch.R;
import com.example.watch.fragment.LeftFragment;
import com.example.watch.util.HttpManager;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;

import com.baidu.lbsapi.BMapManager;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.overlayutil.WalkingRouteOverlay;
import com.baidu.mapapi.search.core.RouteLine;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.baidu.mapapi.utils.CoordinateConverter.CoordType;
import com.baidu.navisdk.ui.widget.NewerGuideDialog;

/**
 * AUTHER wzb<wangzhibin_x@foxmail.com> 2015-12-14����03:00:47
 */
public class MainActivity extends SlidingFragmentActivity implements
		OnClickListener, OnGetRoutePlanResultListener, OnMapClickListener,
		OnGetGeoCoderResultListener {

	private ImageView topButton;
	private ImageView topLocationButton;
	private TextView topTextView;
	private boolean flag = false;
	Thread mThread;
	private HttpManager hm;
	// ��λ���
	LocationClient mLocClient;
	public MyLocationListenner myListener = new MyLocationListenner();
	BitmapDescriptor mCurrentMarker;
	BitmapDescriptor mDeviceMarker;

	MapView mMapView;

	BaiduMap mBaiduMap;
	//boolean isFirstLoc = true;// �Ƿ��״ζ�λ
	static int isFirstLoc=0;
	// @InjectView(R.id.customicon)
	Button customicon;
	private GeoCoder mGeoSearch;

	// �������
	RoutePlanSearch mSearch = null;
	private BitmapDescriptor bd;
	AsyncTask<Void, LatLng, String> task;
	
	// test
	double lat = 22.53;
	double lon = 113.95;
	LatLng sourceLatLng=new LatLng(22.5357f, 113.920111f);
	LatLng desLatLng=new LatLng(22.5357f, 113.920111f);
	// test

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		init_baidu_map();
		Log.d("wzb", "MainActivity onCreate");
		hm=new HttpManager(this);
		initSlidingMenu(savedInstanceState);
		initView();
		

	}

	private void init_baidu_map() {
		// �ȳ�ʼ��BMapManager
		MyApplication app = (MyApplication) this.getApplication();
		if (app.mBMapManager == null) {
			app.mBMapManager = new BMapManager(app);

			app.mBMapManager.init(new MyApplication.MyGeneralListener());
		}
		initMapDetail();

	}

	private void initMapDetail() {
		mMapView = (MapView) findViewById(R.id.baidumapView);
		mBaiduMap = mMapView.getMap();
		// ������λͼ��
		mBaiduMap.setMyLocationEnabled(true);
		mBaiduMap.setMapStatus(MapStatusUpdateFactory.zoomTo(15.0f));

		mCurrentMarker = BitmapDescriptorFactory
				.fromResource(R.drawable.icon_geo);

		mDeviceMarker = BitmapDescriptorFactory
				.fromResource(R.drawable.device_location);

		mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(null,
				false, null));
		// ��λ��ʼ��
		mLocClient = new LocationClient(getApplicationContext());
		mLocClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		option.setIsNeedAddress(true);
		option.setOpenGps(true);// ��gps
		option.setCoorType("bd09ll"); // ������������
		option.setScanSpan(1000);
		mLocClient.setLocOption(option);
		mLocClient.start();
		mBaiduMap.setOnMapClickListener(this);

		// ��ʼ������ģ�飬ע���¼�����
		mSearch = RoutePlanSearch.newInstance();
		mSearch.setOnGetRoutePlanResultListener(this);
		mGeoSearch = GeoCoder.newInstance();
		mGeoSearch.setOnGetGeoCodeResultListener(this);
		bd = BitmapDescriptorFactory.fromResource(R.drawable.icon_gcoding);

	}

	/**
	 * ��λSDK��������
	 */
	public class MyLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			// map view ���ٺ��ڴ����½��յ�λ��

			if (location == null || mMapView == null)
				return;
			// mCity = location.getCity();
			// T.showLong(mContext, mCity);
			MyLocationData locData = new MyLocationData.Builder()
					.accuracy(location.getRadius())
					// �˴����ÿ����߻�ȡ���ķ�����Ϣ��˳ʱ��0-360
					.direction(100).latitude(location.getLatitude())
					.longitude(location.getLongitude()).build();
			mBaiduMap.setMyLocationData(locData);
			Log.d("wzb", "lati:" + location.getLatitude() + "long:"
					+ location.getLongitude());
			// if (isFirstLoc) {
			if (isFirstLoc==0) {
				isFirstLoc = -1;
				// mStartPt = new LatLng(location.getLatitude(),
				// location.getLongitude());
				

				//LatLng ll = new LatLng(location.getLatitude(),
						//location.getLongitude());
				desLatLng=new LatLng(location.getLatitude(),
						location.getLongitude());
				MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(desLatLng);
				mBaiduMap.animateMapStatus(u);
			}else if(isFirstLoc==1){
				MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(desLatLng);
				mBaiduMap.animateMapStatus(u);
				isFirstLoc=-1;
			}
		}

		public void onReceivePoi(BDLocation poiLocation) {
		}
	}

	private void requestLocation() {
		if (mLocClient != null && mLocClient.isStarted()) {
			isFirstLoc = 1;
			mLocClient.requestLocation();
		}
	}

	private void addMarker(double arg1, double arg2) {
		if (mBaiduMap != null) {
			LatLng deviceLng = new LatLng(arg1, arg2);
			try {
				//mBaiduMap.clear();
				OverlayOptions ooA = new MarkerOptions().position(deviceLng)
				.zIndex(9).icon(mDeviceMarker);
				mBaiduMap.addOverlay(ooA);
			} catch (Exception e) {
				// TODO: handle exception
				Log.d("wzb","baidumap clear error");
			}
			
		
		}
	}
	private long firstTime = 0;
	
//	@Override
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		if (keyCode == KeyEvent.KEYCODE_BACK) {
//			long secondTime = System.currentTimeMillis();
//			if (secondTime - firstTime > 2000) {// ������ΰ���ʱ��������2�룬���˳�
//				Toast.makeText(this,getString(R.string.exit_dialog), 666).show();
//				firstTime = secondTime;// ����firstTime
//				return true;
//			} else {//���ΰ���С��2��ʱ���˳�Ӧ��
//				System.exit(0);
//				System.gc();
//				return false;
//			}
//		} else {
//			return super.onKeyDown(keyCode, event);
//		}
//	}


	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0000:
				lat += 0.0002;
				lon += 0.0003;
				Log.d("wzb", "thread 0000");
				
				Log.d("wzb","xxx :"+desLatLng.latitude+","+desLatLng.longitude);
				addMarker(desLatLng.latitude,desLatLng.longitude);
				
				break;
			default:
				break;
			}
		}
	};
	
	private void gpsTobaidu(String lat,String lon){
		Log.d("wzb","gps to  baidu");
		String lat_temp1=lat.substring(lat.length()-8,lat.length());
		String lat_temp2=lat.substring(1,lat.length()-8);
		String lat_temp3=lat.substring(0,1);
		Double lat_temp4=(Double.valueOf(lat_temp2)+Double.valueOf(lat_temp1)/60);	
		if(lat_temp3.equals("S")) lat_temp4=-lat_temp4;
		
		String lon_temp1=lon.substring(lon.length()-8,lon.length());
		String lon_temp2=lon.substring(1,lon.length()-8);
		String lon_temp3=lon.substring(0,1);
		Double lon_temp4=(Double.valueOf(lon_temp2)+Double.valueOf(lon_temp1)/60);	
		if(lon_temp3.equals("W")) lon_temp4=-lon_temp4;
		Log.d("wzb"," "+lat_temp1+","+lat_temp2+","+lat_temp3);
		Log.d("wzb"," "+lon_temp1+","+lon_temp2+","+lon_temp3);
		Log.d("wzb","lat="+lat_temp4+" lon="+lon_temp4);
		// ��GPS�豸�ɼ���ԭʼGPS����ת���ɰٶ�����  
		CoordinateConverter converter  = new CoordinateConverter();  
		converter.from(CoordType.GPS);  
		// sourceLatLng��ת������  
		sourceLatLng=new LatLng(lat_temp4, lon_temp4);
		converter.coord(sourceLatLng);  
		desLatLng = converter.convert();
	}

	private Thread locationThread = new Thread() {
		public void run() {
			while (flag) {
				try {
					sleep(3000);
					String data[]={"1","1","1","1"};
					//data=hm.getHttpData();
					data=hm.getHttpData("3721","2015");
					//gpsTobaidu(data[2],data[1]);
				} catch (Exception e) {
					e.printStackTrace();
				}
				mHandler.sendEmptyMessage(0000);
			}
		}
	};

	@Override
	protected void onPause() {
		mMapView.onPause();
		super.onPause();
		flag = false;
		mThread = null;
	}

	@Override
	protected void onResume() {
		mMapView.onResume();
		super.onResume();
		flag = true;
		mThread = new Thread(locationThread);
		mThread.start();
	}

	@Override
	protected void onDestroy() {

		// �˳�ʱ���ٶ�λ
		mLocClient.stop();
		// �رն�λͼ��
		mBaiduMap.setMyLocationEnabled(false);
		mMapView.onDestroy();
		mMapView = null;
		flag = false;
		mThread = null;
		super.onDestroy();
	}

	private void initView() {
		topButton = (ImageView) findViewById(R.id.topButton);
		topButton.setOnClickListener(this);
		topTextView = (TextView) findViewById(R.id.topTv);
		topLocationButton=(ImageView)findViewById(R.id.locationButton);
		topLocationButton.setOnClickListener(this);

	}

	private void initSlidingMenu(Bundle savedInstanceState) {
		setBehindContentView(R.layout.menu_frame_left);
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.menu_frame, new LeftFragment()).commit();

		// ʵ���������˵�����
		SlidingMenu sm = getSlidingMenu();

		// ���ÿ������һ����Ĳ˵�
		sm.setMode(SlidingMenu.LEFT);
		// ���û�����Ӱ�Ŀ��
		sm.setShadowWidthRes(R.dimen.shadow_width);
		// ���û����˵���Ӱ��ͼ����Դ
		sm.setShadowDrawable(null);
		// ���û����˵���ͼ�Ŀ��
		sm.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		// ���ý��뽥��Ч����ֵ
		sm.setFadeDegree(0.35f);
		// ���ô�����Ļ��ģʽ,��������Ϊȫ��
		sm.setTouchModeAbove(SlidingMenu.LEFT);
		// �����·���ͼ���ڹ���ʱ�����ű���
		sm.setBehindScrollScale(0.0f);

	}
	void test(){
		try {
			Socket socket=new Socket("120.24.36.177",1234);
			String messageString="abcdefg";
			try{
			PrintWriter out = new PrintWriter(  
                    new BufferedWriter(new OutputStreamWriter(  
                            socket.getOutputStream())), true);  
			out.println(messageString); 
			}catch(Exception e){
				Log.d("wzb","111");
			}finally{
				socket.close();
			}
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.topButton:
			 toggle();
			break;
		case R.id.locationButton:
			requestLocation();
			break;
		default:
			break;
		}

	}

	@Override
	public void onGetDrivingRouteResult(DrivingRouteResult arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onGetTransitRouteResult(TransitRouteResult arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onGetWalkingRouteResult(WalkingRouteResult arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onMapClick(LatLng arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onMapPoiClick(MapPoi arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onGetGeoCodeResult(GeoCodeResult arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onGetReverseGeoCodeResult(ReverseGeoCodeResult arg0) {
		// TODO Auto-generated method stub

	}

}
