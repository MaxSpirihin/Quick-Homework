package com.quickhomework;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.quickhomework.Classes.Subject;
import com.quickhomework.Classes.ZoomableImageView;

public class ShowScheduleActivity extends Activity implements OnClickListener {

	public static final String SCHEDULE_PATH = Subject.PATH + "schedule.jpg";

	Button btnChange;
	Button btnAdd;



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_schedule);

		btnChange = (Button) findViewById(R.id.btnChange);
		btnChange.setOnClickListener(this);
		btnAdd = (Button) findViewById(R.id.btnAdd);
		btnAdd.setOnClickListener(this);
		btnAdd.setVisibility(View.GONE);

		try {
			File file = new File(SCHEDULE_PATH);
			Bitmap bmp = BitmapFactory.decodeFile(file.getAbsolutePath());

			if (bmp.getWidth() > bmp.getHeight())
				bmp = ShowActivity.RotateBitmap(bmp, 90);

			((ZoomableImageView) this.findViewById(R.id.ivShow))
					.setImageBitmap(bmp);

		} catch (Exception ex) {

			// видимо изображения нет.
			btnChange.setVisibility(View.GONE);
			btnAdd.setVisibility(View.VISIBLE);
			((RelativeLayout)findViewById(R.id.rl)).setBackgroundResource(R.drawable.second_background);
			

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
		Intent intent = new Intent(this, AddScheduleActivity.class);
		startActivity(intent);
		finish();
	}

}
