package com.quickhomework.Classes;

import android.app.Activity;
import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class MyAdapter<T> extends ArrayAdapter<T> {

	// на это чило делитс€ высота экрана, будь осторожен
	final int TEXT_SIZE = 25;
	Context context;


	public MyAdapter(Context context, int resource, T[] objects) {
		super(context, resource, objects);
		this.context = context;
	}



	@SuppressWarnings("deprecation")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = super.getView(position, convertView, parent);

		((TextView) view).setTextSize(
				TypedValue.COMPLEX_UNIT_PX, ((Activity) context).getWindowManager()
				.getDefaultDisplay().getHeight() / TEXT_SIZE);
		return view;
	}

}
