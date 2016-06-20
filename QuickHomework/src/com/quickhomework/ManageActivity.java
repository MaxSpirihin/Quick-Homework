package com.quickhomework;

import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;

import com.quickhomework.Classes.ManageListAdapter;
import com.quickhomework.Classes.Repository;
import com.quickhomework.Classes.Subject;

public class ManageActivity extends Activity implements
		OnCheckedChangeListener, OnClickListener {

	private static final int DIALOG = 0;
	ListView lv;
	Subject subject;
	Repository repository;
	Context context;



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_manage);

		lv = (ListView) findViewById(R.id.lvManage);
		lv.setAdapter(new ManageListAdapter(this));
		repository = new Repository(this);
		context = this;
		((Button) findViewById(R.id.btnAdd)).setOnClickListener(this);
		
		if (repository.GetSubjects().size()==0)
		{
			this.onClick((Button) findViewById(R.id.btnAdd));
			finish();
		}
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		lv.setAdapter(new ManageListAdapter(this));
	}



	@Override
	public void onBackPressed() {
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
		finish();
	}



	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

		repository.SetLocking((int) buttonView.getTag(), isChecked);
		lv.setAdapter(new ManageListAdapter(this));

	}



	@SuppressWarnings("deprecation")
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnDelete:
			subject = repository.GetSubject((int) v.getTag());
			Log.d(Subject.LOG_TAG, "sdfds");
			this.showDialog(DIALOG);
			break;
		case R.id.btnChange: {
			Intent intent = new Intent(this, ChangeActivity.class);
			intent.putExtra(ChangeActivity.TAG_ID, (int) v.getTag());
			startActivity(intent);
		}
			break;
		case R.id.btnAdd: {
			Intent intent = new Intent(this, ChangeActivity.class);
			intent.putExtra(ChangeActivity.TAG_ID, ChangeActivity.ADD);
			startActivity(intent);
		}
			break;
		}

	}



	protected Dialog onCreateDialog(int id) {

		AlertDialog.Builder adb = new AlertDialog.Builder(this);
		// заголовок
		adb.setTitle(getResources().getString(R.string.delete_subject) + " "
				+ subject.name + " ?");
		// сообщение
		adb.setMessage(getResources().getString(R.string.are_you_sure));
		// иконка
		adb.setIcon(android.R.drawable.ic_dialog_info);
		// кнопка положительного ответа
		adb.setPositiveButton(getResources().getString(R.string.yes),
				dialogListener);
		// кнопка отрицательного ответа
		adb.setNegativeButton(getResources().getString(R.string.no),
				dialogListener);
		// создаем диалог
		return adb.create();
	}

	DialogInterface.OnClickListener dialogListener = new DialogInterface.OnClickListener() {

		public void onClick(DialogInterface dialog, int which) {
			if (which == Dialog.BUTTON_POSITIVE) {
				// надо удалить предмет зи БД, а заодно и фото его
				repository.DeleteSubject(subject.id);

				for (int i = 0; i < Subject.MAX_COUNT_OF_HOME_WORKS; i++) {
					(new File(Subject.ComputePath(subject.id, 0))).delete();
				}

				lv.setAdapter(new ManageListAdapter(context));
			}
		}
	};

}
