package com.example.watch.fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.example.watch.R;
import com.example.watch.fragment.DeviceAdapter.ViewHolder;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * AUTHER wzb<wangzhibin_x@foxmail.com> 2015-12-21����02:18:56
 */
public class DeviceManagerActivity extends Activity implements OnClickListener,
		OnItemClickListener {


	private Button btnAdd = null;
	private Button btnDelete = null;

	/**
	 * ListView�б�
	 */
	private ListView lvListView = null;
	
	private ImageView backView;
	private TextView titleView;

	/**
	 * �������
	 */
	private DeviceAdapter adpAdapter = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_devicemanager);

		initView();

	

	}

	/**
	 * ��ʼ���ؼ�
	 */
	private void initView() {


		 btnAdd = (Button) findViewById(R.id.btnAdd);
		 btnAdd.setOnClickListener(this);
		
		backView=(ImageView)findViewById(R.id.title_back);
		backView.setOnClickListener(this);
		titleView=(TextView)findViewById(R.id.title_text);
		titleView.setText(getString(R.string.profile_name));

		btnDelete = (Button) findViewById(R.id.btnDelete);
		btnDelete.setOnClickListener(this);

		lvListView = (ListView) findViewById(R.id.lvListView);
		lvListView.setOnItemClickListener(this);

	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		initData();
	}

	/**
	 * ��ʼ����ͼ
	 */
	private void initData() {
		List<DeviceBean> deviceDatas = new ArrayList<DeviceBean>();
		//��ȡxml�洢����
		SharedPreferences sp=getSharedPreferences("DEVICE_INFO", Activity.MODE_PRIVATE);
		//SharedPreferences.Editor editor=sp.edit();
		for(int i=0;i<5;i++){
			String str=sp.getString("user_"+String.valueOf(i), "000,000,000");
			Log.d("wzb","str="+str);
			if(!str.equals("000,000,000")){
				String tokens[] = str.trim().split("\\,");
				Log.d("wzb","len="+tokens.length+" tokens="+tokens);
				deviceDatas.add(new DeviceBean(tokens[0],tokens[1],tokens[2],true));
			}
		}
	

		adpAdapter = new DeviceAdapter(this, deviceDatas,1);

		lvListView.setAdapter(adpAdapter);

	}

	@Override
	public void onClick(View v) {

		if (v == backView) {
			finish();

		}

		if (v == btnAdd) {

			//adpAdapter.add(new DeviceBean("����", true));

			//adpAdapter.notifyDataSetChanged();
			startActivity(new Intent(DeviceManagerActivity.this,DeviceAdd.class));
		}

		/*
		 * �����ɾ����ʱ��
		 */
		if (v == btnDelete) {

			/*
			 * ɾ���㷨���,�õ�checkBoxѡ��Ĵ�map
			 */
			Map<Integer, Boolean> map = adpAdapter.getCheckMap();

			// ��ȡ��ǰ����������
			int count = adpAdapter.getCount();
			
			int delete_positon=4;
			// ���б���
			for (int i = 0; i < count; i++) {

				// ��ΪList������,ɾ����2��item,��3���2,��������Ҫ���������Ļ���,�����õ�ɾ����������position
				int position = i - (count - adpAdapter.getCount());

				if (map.get(i) != null && map.get(i)) {

					DeviceBean bean = (DeviceBean) adpAdapter.getItem(position);

					if (bean.isCanRemove()) {
						adpAdapter.getCheckMap().remove(i);
						adpAdapter.remove(position);
						delete_positon=position;
					} else {
						map.put(position, false);
					}

				}
			}
			//del xml data
			SharedPreferences sp=getSharedPreferences("DEVICE_INFO", Activity.MODE_PRIVATE);
			SharedPreferences.Editor editor=sp.edit();
			while(delete_positon<(count-1)){
				editor.putString("user_"+String.valueOf(delete_positon),sp.getString("user_"+String.valueOf(delete_positon+1), "000,000,000") );
				delete_positon++;
			}
			editor.putString("user_"+String.valueOf(count-1), "000,000,000");
			editor.commit();
			
			//end

			adpAdapter.notifyDataSetChanged();

		}

//		/*
//		 * �����ȫѡ��ʱ��
//		 */
//		if (v == btnSelectAll) {
//
//			if (btnSelectAll.getText().toString().trim().equals("ȫѡ")) {
//
//				// ������Ŀȫ��ѡ��
//				adpAdapter.configCheckMap(true);
//
//				adpAdapter.notifyDataSetChanged();
//
//				btnSelectAll.setText("ȫ��ѡ");
//			} else {
//
//				// ������Ŀȫ����ѡ��
//				adpAdapter.configCheckMap(false);
//
//				adpAdapter.notifyDataSetChanged();
//
//				btnSelectAll.setText("ȫѡ");
//			}
//
//		}
	}

	/**
	 * ��ListView ��������ʱ��
	 */
	@Override
	public void onItemClick(AdapterView<?> listView, View itemLayout,
			int position, long id) {

		if (itemLayout.getTag() instanceof ViewHolder) {

			ViewHolder holder = (ViewHolder) itemLayout.getTag();

			// ���Զ�����CheckBox��checked�¼�
			//��ѡ
			adpAdapter.configCheckMap(false);
			adpAdapter.notifyDataSetChanged();
			holder.cbCheck.toggle();

		}

	}

}
