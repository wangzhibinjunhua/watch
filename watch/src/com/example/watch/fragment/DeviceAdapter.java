package com.example.watch.fragment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.baidu.platform.comapi.map.p;
import com.example.watch.R;

import android.R.integer;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

public class DeviceAdapter extends BaseAdapter {

	/**
	 * �����Ķ���
	 */
	private Context context = null;

	/**
	 * ���ݼ���
	 */
	private List<DeviceBean> datas = null;
	
	private int state=0;
	/**
	 * CheckBox �Ƿ�ѡ��Ĵ洢����,key �� position , value �Ǹ�position�Ƿ�ѡ��
	 */
	private Map<Integer, Boolean> isCheckMap = new HashMap<Integer, Boolean>();

	public DeviceAdapter(Context context, List<DeviceBean> datas,int state) {
		this.datas = datas;
		this.context = context;
		this.state=state;
		// ��ʼ��,Ĭ�϶�û��ѡ��
		configCheckMap(false);
		Log.d("wzb","state="+state);
	}

	/**
	 * ����,Ĭ�������,������Ŀ����û��ѡ�е�.������г�ʼ��
	 */
	public void configCheckMap(boolean bool) {

		for (int i = 0; i < datas.size(); i++) {
			isCheckMap.put(i, bool);
		}

	}

	@Override
	public int getCount() {
		return datas == null ? 0 : datas.size();
	}

	@Override
	public Object getItem(int position) {
		return datas.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		ViewGroup layout = null;
		Log.d("wzb","getView:"+datas.size());
		/**
		 * ����ListView ���Ż�
		 */
		if (convertView == null) {
			layout = (ViewGroup) LayoutInflater.from(context).inflate(
					R.layout.listview_item_layout, parent, false);
		} else {
			layout = (ViewGroup) convertView;
		}

		DeviceBean bean = datas.get(position);

		/*
		 * ��ø�item �Ƿ�����ɾ��
		 */
		boolean canRemove = bean.isCanRemove();

		/*
		 * ����ÿһ��item���ı�
		 */
		TextView tvTitle = (TextView) layout.findViewById(R.id.tvTitle);
		tvTitle.setText(bean.getMessage());

		/*
		 * ��õ�ѡ��ť
		 */
		CheckBox cbCheck = (CheckBox) layout.findViewById(R.id.cbCheckBox);
		
		ImageButton callButton=(ImageButton)layout.findViewById(R.id.callButton);
		
		callButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//call(getPhoneNumberByID(position));
				Log.d("wzb","position="+position+" phone number="+getPhoneNumberByID(position));
				call("10086");
			}
		});
		

		/*
		 * ���õ�ѡ��ť��ѡ��
		 */
		cbCheck.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {

				/*
				 * ��ѡ������ص�map����Ĵ�
				 */
				isCheckMap.put(position, isChecked);
			}
		});

		if (!canRemove) {
			// ���ص�ѡ��ť,��Ϊ�ǲ���ɾ����
			cbCheck.setVisibility(View.GONE);
			cbCheck.setChecked(false);
		} else {
			cbCheck.setVisibility(View.VISIBLE);

			if (isCheckMap.get(position) == null) {
				isCheckMap.put(position, false);
			}

			cbCheck.setChecked(isCheckMap.get(position));

			ViewHolder holder = new ViewHolder();

			holder.cbCheck = cbCheck;

			holder.tvTitle = tvTitle;

			/**
			 * �����ݱ��浽tag
			 */
			layout.setTag(holder);
		}
		if(state==2){
			cbCheck.setVisibility(View.GONE);
			callButton.setVisibility(View.VISIBLE);
		}


		return layout;
	}
	
	public String getPhoneNumberByID(int id){
		String phoneNumber="";
		SharedPreferences sp=context.getSharedPreferences("DEVICE_INFO", Activity.MODE_PRIVATE);
		String info=sp.getString("user_"+String.valueOf(id), ",,");
		String tokens[] = info.trim().split("\\,");
		phoneNumber=tokens[1];
		return phoneNumber;
	}
	
	public void call(String phoneNumber){
		if(phoneNumber!=null && !phoneNumber.equals("")){
			Intent intent = new Intent(Intent.ACTION_CALL,Uri.parse("tel:"+phoneNumber));
            context.startActivity(intent);
		}
	}
	/**
	 * ����һ���ʱ��
	 */
	public void add(DeviceBean bean) {
		this.datas.add(0, bean);

		// ��������Ŀ��Ϊ��ѡ��
		configCheckMap(false);
	}

	// �Ƴ�һ����Ŀ��ʱ��
	public void remove(int position) {
		this.datas.remove(position);
	}

	public Map<Integer, Boolean> getCheckMap() {
		return this.isCheckMap;
	}

	public static class ViewHolder {

		public TextView tvTitle = null;

		public CheckBox cbCheck = null;
		public Object data = null;

	}

	public List<DeviceBean> getDatas() {
		return datas;
	}

}
