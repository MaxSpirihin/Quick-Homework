package com.quickhomework;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.quickhomework.Classes.Repository;

public class ChangeActivity extends Activity implements OnClickListener {

	final static public String TAG_ID = "id";
	final static public int ADD = -1;

	EditText etName;
	Button btnSave;
	Button btnCancel;
	Repository repository;
	int id;



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_change);

		etName = (EditText) findViewById(R.id.etName);
		btnSave = (Button) findViewById(R.id.btnSave);
		btnSave.setOnClickListener(this);
		btnCancel = (Button) findViewById(R.id.btnCancel);
		btnCancel.setOnClickListener(this);
		repository = new Repository(this);

		id = getIntent().getIntExtra(TAG_ID, ADD);
		if (id != ADD) {
			etName.setText(repository.GetSubject(id).name);
		}

	}



	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.btnCancel:
			finish();
			break;
		case R.id.btnSave:
			if (TextUtils.isEmpty(etName.getText().toString())) {
				// если есть ошибки ввода, говорим об этом
				Toast.makeText(getApplicationContext(),
						this.getResources().getString(R.string.enter_name),
						Toast.LENGTH_SHORT).show();
			} else {
				if (id == ADD) {
					repository.AddSubject(etName.getText().toString());
				} else {
					repository.RenameSubject(id, etName.getText().toString());
				}
				finish();

			}
			break;
		}
	}

}
