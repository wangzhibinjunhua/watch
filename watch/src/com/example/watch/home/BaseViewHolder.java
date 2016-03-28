package com.example.watch.home;

import android.util.SparseArray;
import android.view.View;

/**
 * AUTHER wzb<wangzhibin_x@foxmail.com>
 * 2016-2-23����03:04:42	
 */
public class BaseViewHolder {
	
	
	public static <T extends View> T get(View view, int id) {
        SparseArray<View> viewHolder = (SparseArray<View>) view.getTag();
        if (viewHolder == null) {
            viewHolder = new SparseArray<View>();
            view.setTag(viewHolder);
        }
        View childView = viewHolder.get(id);
        if (childView == null) {
            childView = view.findViewById(id);
            viewHolder.put(id, childView);
        }
        return (T) childView;
    }
	
}
