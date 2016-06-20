package com.quickhomework;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.quickhomework.Classes.Repository;
import com.quickhomework.Classes.Subject;
import com.quickhomework.Classes.ZoomableImageView;

public class ShowActivity extends Activity implements OnClickListener {

	public static final String TAG_ID = "id";

	final int TEXT_SIZE = 22;

	Repository repository;
	Subject subject;
	TextView tvText;
	TextView tvTitle;
	RelativeLayout rLay;
	int currentNumber;
	Button btnNext, btnPrev;
	Button btnChange;
	Bitmap bmp = null;
	ZoomableImageView image;
	ScrollView sv;



	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show);

		btnNext = (Button) this.findViewById(R.id.btnNext);
		btnNext.setOnClickListener(this);
		btnPrev = (Button) this.findViewById(R.id.btnPrev);
		btnPrev.setOnClickListener(this);
		btnChange = (Button) this.findViewById(R.id.btnChange);
		btnChange.setOnClickListener(this);
		image = (ZoomableImageView) findViewById(R.id.ivShow);
		sv = (ScrollView) findViewById(R.id.sv);
		rLay = (RelativeLayout) findViewById(R.id.rl);
		repository = new Repository(this);
		subject = repository.GetSubject(getIntent().getIntExtra(TAG_ID, 0));

		tvText = (TextView) findViewById(R.id.tvText);
		tvTitle = (TextView) findViewById(R.id.tvTitle);
		tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, (getWindowManager()
				.getDefaultDisplay().getHeight() / TEXT_SIZE));

		if (subject.countOfHomeWork == 0) {
			sv.setVisibility(View.GONE);
			btnNext.setVisibility(View.GONE);
			btnPrev.setVisibility(View.GONE);
			rLay.setBackgroundResource(R.drawable.second_background);
			tvTitle.setVisibility(View.VISIBLE);
			tvTitle.setText(this.getResources()
					.getString(R.string.no_home_work));
		} else {

			currentNumber = subject.countOfHomeWork - 1;
			if (currentNumber == 0) {
				btnNext.setVisibility(View.GONE);
				btnPrev.setVisibility(View.GONE);
			}

			try {
				File file = new File(Subject.ComputePath(subject.id,
						currentNumber));
				bmp = BitmapFactory.decodeFile(file.getAbsolutePath());
			} catch (Exception e) {
			}
			Show();
		}

	}



	private void Show() {
		if (currentNumber == subject.countOfHomeWork - 1) {
			btnChange.setVisibility(View.VISIBLE);
		} else {
			btnChange.setVisibility(View.GONE);
		}
		if (subject.textOfHomeWork.get(currentNumber) == null) {
			sv.setVisibility(View.GONE);
		} else {
			sv.setVisibility(View.VISIBLE);
			tvText.setText(subject.textOfHomeWork.get(currentNumber));
		}
		if (bmp != null) {
			rLay.setBackgroundColor(Color.BLACK);
			image.setVisibility(View.VISIBLE);
			if (bmp.getWidth() > bmp.getHeight())
				bmp = RotateBitmap(bmp, 90);
			image.setImageBitmap(bmp);
			tvTitle.setVisibility(View.GONE);

		} else {
			rLay.setBackgroundResource(R.drawable.second_background);
			tvTitle.setVisibility(View.VISIBLE);
			sv.setVisibility(View.GONE);
			tvTitle.setText(subject.textOfHomeWork.get(currentNumber));
			image.setVisibility(View.GONE);
		}
	}



	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.btnNext:
			if (currentNumber != subject.countOfHomeWork - 1) {
				currentNumber++;
				try {
					File file = new File(Subject.ComputePath(subject.id,
							currentNumber));
					bmp = BitmapFactory.decodeFile(file.getAbsolutePath());
				} catch (Exception e) {
				}
			}
			if (subject.countOfHomeWork != 0)
				Show();
			break;
		case R.id.btnPrev:
			if (currentNumber != 0) {
				currentNumber--;
				try {
					File file = new File(Subject.ComputePath(subject.id,
							currentNumber));
					bmp = BitmapFactory.decodeFile(file.getAbsolutePath());
				} catch (Exception e) {
				}
			}

			if (subject.countOfHomeWork != 0)
				Show();
			break;

		case R.id.btnChange:
			Intent intent = new Intent(this, AddActivity.class);
			intent.putExtra(AddActivity.IS_CHANGE_TAG, true);
			intent.putExtra(AddActivity.TAG_ID,
					getIntent().getIntExtra(TAG_ID, 0));
			startActivity(intent);
			finish();
			break;

		}

	}



	@Override
	public void onBackPressed() {
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
		finish();
	}



	public static Bitmap RotateBitmap(Bitmap source, float angle) {
		Matrix matrix = new Matrix();
		matrix.postRotate(angle);
		return Bitmap.createBitmap(source, 0, 0, source.getWidth(),
				source.getHeight(), matrix, true);
	}
}
