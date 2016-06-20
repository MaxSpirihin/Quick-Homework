package com.quickhomework;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.quickhomework.Classes.MyAdapter;
import com.quickhomework.Classes.Repository;
import com.quickhomework.Classes.Subject;

public class ChooseLessonActivity extends Activity implements OnClickListener {

	final int TEXT_SIZE = 25;

	public final static String NEXT_ACTIVITY_TAG = "next";
	public final static int NEXT_ACTIVITY_SHOW = 1;
	public final static int NEXT_ACTIVITY_ADD = 2;

	Repository repo;
	ArrayList<Subject> subjects;
	String[] names;
	Button btnManage;
	Button btnAdd;
	int mode;
	Context context;



	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chooselesson);

		repo = new Repository(this);
		btnManage = (Button) this.findViewById(R.id.btnManageSubjects);
		btnManage.setOnClickListener(this);
		btnManage.setTextSize(TypedValue.COMPLEX_UNIT_PX, (getWindowManager()
				.getDefaultDisplay().getHeight() / TEXT_SIZE));
		btnAdd = (Button) this.findViewById(R.id.btnAdd);
		btnAdd.setOnClickListener(this);
		((TextView) findViewById(R.id.tvChooseTitle)).setTextSize(
				TypedValue.COMPLEX_UNIT_PX, (getWindowManager()
						.getDefaultDisplay().getHeight() / TEXT_SIZE));
		mode = getIntent().getIntExtra(NEXT_ACTIVITY_TAG, 0);
	}



	@Override
	protected void onResume() {
		super.onResume();

		btnAdd.setVisibility(View.GONE);

		context = this;

		if (mode == NEXT_ACTIVITY_SHOW)
			btnManage.setVisibility(View.GONE);
		else
			btnManage.setVisibility(View.VISIBLE);
		subjects = repo.GetSubjects();

		if (subjects.size() != 0) {
			((TextView) findViewById(R.id.tvChooseTitle)).setText(this
					.getResources().getString(R.string.choose_lesson));
			
			
			

			names = new String[subjects.size()];
			for (int i = 0; i < subjects.size(); i++) {
				names[i] = subjects.get(i).name;
			}

			// создаем адаптер
			ArrayAdapter<String> adapter = new MyAdapter<String>(this,
					R.layout.subject_item, names);

			ListView lv = ((ListView) findViewById(R.id.lvSubjects));
			lv.setAdapter(adapter);

			lv.setOnItemClickListener(new OnItemClickListener() {

				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					Intent intent = null;
					if (mode == ChooseLessonActivity.NEXT_ACTIVITY_ADD) {
						intent = new Intent(context, AddActivity.class);
						intent.putExtra(AddActivity.TAG_ID,
								subjects.get(position).id);
					} else {
						intent = new Intent(context, ShowActivity.class);
						intent.putExtra(ShowActivity.TAG_ID,
								subjects.get(position).id);
					}
					startActivity(intent);
					finish();
				}
			});
		} else {

			// предметов нет, заставим юзера их добавить в жесткой форме
			btnManage.setVisibility(View.GONE);
			btnAdd.setVisibility(View.VISIBLE);
			((TextView) findViewById(R.id.tvChooseTitle)).setText(this
					.getResources().getString(R.string.no_subject));
		}
	}



	@Override
	public void onBackPressed() {
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
		finish();
	}



	@Override
	public void onClick(View v) {
		Intent intent;
		switch (v.getId()) {

		case R.id.btnManageSubjects:
			intent = new Intent(this, ManageActivity.class);
			startActivity(intent);
			finish();
			break;
		case R.id.btnAdd:
			// нажатие на ту единственную кнопку
			intent = new Intent(this, ManageActivity.class);
			startActivity(intent);

			break;
		}
	}

}
