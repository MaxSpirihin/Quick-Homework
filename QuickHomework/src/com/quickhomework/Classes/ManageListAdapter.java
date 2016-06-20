package com.quickhomework.Classes;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.quickhomework.R;

public class ManageListAdapter extends BaseAdapter {

	final int TEXT_SIZE = 25;

	Context context; // контекст для вызова некоторый ф-й
	LayoutInflater lInflater;
	ArrayList<Subject> subjects;// список товаров для заполнения
	Repository repository;



	public ManageListAdapter(Context context) {
		this.context = context;
		repository = new Repository(context);
		lInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		subjects = repository.GetSubjects();

	}



	@Override
	public int getCount() {
		return subjects.size();
	}



	@Override
	public Object getItem(int pos) {
		return subjects.get(pos);
	}



	@Override
	public long getItemId(int pos) {
		return subjects.get(pos).id;
	}



	// пункт списка
	@SuppressLint("ViewHolder")
	@SuppressWarnings("deprecation")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		// используем созданные, но не используемые view
		View view = lInflater.inflate(R.layout.manage_item, parent, false);
		
		Subject subject = subjects.get(position);
		((TextView) view.findViewById(R.id.tvName)).setText(subject.name);
		((TextView) view.findViewById(R.id.tvName)).setTextSize(
				TypedValue.COMPLEX_UNIT_PX, ((Activity) context)
						.getWindowManager().getDefaultDisplay().getHeight()
						/ TEXT_SIZE);
		CheckBox cb = (CheckBox) view.findViewById(R.id.cbSecure);
		cb.setChecked(subject.isLocked);
		cb.setTag(subject.id);
		cb.setOnCheckedChangeListener((OnCheckedChangeListener) context);


		// кнопкам записываем в tag позицию и присваиваем обработчика нажатий
		Button btnChange = (Button) view.findViewById(R.id.btnChange);
		btnChange.setTag(subject.id);
		btnChange.setOnClickListener((OnClickListener) context);
		btnChange.setTextSize(TypedValue.COMPLEX_UNIT_PX, ((Activity) context)
				.getWindowManager().getDefaultDisplay().getHeight()
				/ TEXT_SIZE);
		Button btnDelete = (Button) view.findViewById(R.id.btnDelete);
		btnDelete.setTag(subject.id);
		btnDelete.setOnClickListener((OnClickListener) context);
		btnDelete.setTextSize(TypedValue.COMPLEX_UNIT_PX, ((Activity) context)
				.getWindowManager().getDefaultDisplay().getHeight()
				/ TEXT_SIZE);

		return view;
	}

}
