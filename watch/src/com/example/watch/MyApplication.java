package com.example.watch;

import com.baidu.lbsapi.BMapManager;
import com.baidu.lbsapi.MKGeneralListener;
import com.baidu.mapapi.SDKInitializer;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;

/**
 * AUTHER wzb<wangzhibin_x@foxmail.com> 2015-12-14����07:37:45
 */
public class MyApplication extends Application {

	public static Context mContext;
	public BMapManager mBMapManager = null;

	private static MyApplication mInstance;

	public static MyApplication getInstance() {
		return mInstance;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		mContext = this.getApplicationContext();
		SDKInitializer.initialize(getApplicationContext());
		if (mBMapManager == null) {
			mBMapManager = new BMapManager(mContext);
		}
		if (!mBMapManager.init(new MyGeneralListener())) {
			Toast.makeText(
					MyApplication.getInstance().getApplicationContext(),
					"BMapManager  ��ʼ������!", Toast.LENGTH_LONG).show();
		}
	}
	
	// �����¼���������������ͨ�������������Ȩ��֤�����
	public static class MyGeneralListener implements MKGeneralListener {

		@Override
		public void onGetPermissionState(int iError) {
			// ����ֵ��ʾkey��֤δͨ��
			if (iError != 0) {
//				// ��ȨKey����
//				Toast.makeText(
//						BaseApplication.getInstance().getApplicationContext(),
//						"����AndoridManifest.xml��������ȷ����ȨKey,������������������Ƿ�������error: "
//								+ iError, Toast.LENGTH_LONG).show();
			} else {
//				Toast.makeText(
//						BaseApplication.getInstance().getApplicationContext(),
//						"key��֤�ɹ�", Toast.LENGTH_LONG).show();
			}
		}
	}

}
