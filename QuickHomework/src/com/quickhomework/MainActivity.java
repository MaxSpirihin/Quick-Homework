package com.quickhomework;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

@SuppressWarnings("deprecation")
public class MainActivity extends Activity implements OnClickListener {

	
	final private static String PREF_NAME = "Q_H";
	final private static String NUMBER = "number";
	
	final float RELATIVE_WIDTH_OF_BUTTON = 0.75f;
	final float HEIGHT_OF_BUTTON_RELATIVELY_OF_WIDTH = 0.3f;
	// на это чило делится высота экрана, будь осторожен
	final int TEXT_SIZE = 20;
	final int TITLE_TEXT_SIZE = 10;
	//количество картинок на  фон
	final int COUNT_OF_PICTURES = 7;
	

	Button btnShow;
	Button btnAdd;
	Button btnSchedule;
	ImageButton btnPict;
	ImageButton btnInfo;
	int currentPicture = 0;



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// вычисляем высоту и ширину кнопок
		int width = Math.round(getWindowManager().getDefaultDisplay()
				.getWidth() * this.RELATIVE_WIDTH_OF_BUTTON);
		int height = Math.round(width
				* this.HEIGHT_OF_BUTTON_RELATIVELY_OF_WIDTH);

		((TextView) findViewById(R.id.tvTitle)).setTextSize(
				TypedValue.COMPLEX_UNIT_PX, getWindowManager()
						.getDefaultDisplay().getHeight() / TITLE_TEXT_SIZE);

		// находим кнопки, меняем ширину
		btnShow = (Button) findViewById(R.id.btnShow);
		btnShow.setWidth(width);
		btnShow.setHeight(height);
		btnShow.setTextSize(TypedValue.COMPLEX_UNIT_PX, getWindowManager()
				.getDefaultDisplay().getHeight() / TEXT_SIZE);
		btnShow.setOnClickListener(this);

		btnAdd = (Button) findViewById(R.id.btnAdd);
		btnAdd.setWidth(width);
		btnAdd.setHeight(height);
		btnAdd.setTextSize(TypedValue.COMPLEX_UNIT_PX, getWindowManager()
				.getDefaultDisplay().getHeight() / TEXT_SIZE);
		btnAdd.setOnClickListener(this);

		btnSchedule = (Button) findViewById(R.id.btnSchedule);
		btnSchedule.setWidth(width);
		btnSchedule.setHeight(height);
		btnSchedule.setTextSize(TypedValue.COMPLEX_UNIT_PX, getWindowManager()
				.getDefaultDisplay().getHeight() / TEXT_SIZE);
		btnSchedule.setOnClickListener(this);

		btnInfo = (ImageButton) findViewById(R.id.btnInfo);
		btnInfo.setOnClickListener(this);
		btnPict = (ImageButton) findViewById(R.id.btnPict);
		btnPict.setOnClickListener(this);

		currentPicture = getSharedPreferences(PREF_NAME,
				Context.MODE_PRIVATE).getInt(NUMBER, 0);
		SetBackground();
	}



	@Override
	public void onClick(View v) {

		Intent intent;
		switch (v.getId()) {
		case R.id.btnShow:
			intent = new Intent(this, ChooseLessonActivity.class);
			intent.putExtra(ChooseLessonActivity.NEXT_ACTIVITY_TAG,
					ChooseLessonActivity.NEXT_ACTIVITY_SHOW);
			startActivity(intent);
			finish();
			break;
		case R.id.btnAdd:
			intent = new Intent(this, ChooseLessonActivity.class);
			intent.putExtra(ChooseLessonActivity.NEXT_ACTIVITY_TAG,
					ChooseLessonActivity.NEXT_ACTIVITY_ADD);
			startActivity(intent);
			finish();
			break;
		case R.id.btnSchedule:
			intent = new Intent(this, ShowScheduleActivity.class);
			startActivity(intent);
			finish();
			break;
		case R.id.btnInfo:
			intent = new Intent(this, InfoActivity.class);
			startActivity(intent);
			finish();
			break;
		case R.id.btnPict:
			currentPicture = (currentPicture+1)%COUNT_OF_PICTURES;
			getSharedPreferences(PREF_NAME,
					Context.MODE_PRIVATE).edit().putInt(NUMBER, currentPicture).commit();
			SetBackground();
			break;
		}

	}



	private void SetBackground() {
		
		int id = 0;
		
		switch (currentPicture) {
		case 0:
			id = R.drawable.background_0;
			break;
		case 1:
			id = R.drawable.background_1;
			break;
		case 2:
			id = R.drawable.background_2;
			break;
		case 3:
			id = R.drawable.background_3;
			break;
		case 4:
			id = R.drawable.background_4;
			break;
		case 5:
			id = R.drawable.background_5;
			break;
		case 6:
			id = R.drawable.background_6;
			break;
		}
		
		
		// нужно настроить фон, выбираем кусок центра
		Bitmap bmBackground = BitmapFactory.decodeResource(getResources(),
				id);
		bmBackground = GetBitmapCenter(bmBackground, getWindowManager()
				.getDefaultDisplay().getHeight()
				* 1f
				/ getWindowManager().getDefaultDisplay().getWidth());
		((RelativeLayout) this.findViewById(R.id.mainLayout))
				.setBackgroundDrawable(new BitmapDrawable(getResources(),
						bmBackground));
	}



	/* обрезает битмап, возвращая его центр с нужным соотношением сторон */
	public static Bitmap GetBitmapCenter(Bitmap source, float heightToWidth) {
		// оставляем ширину, вычисляем высоту
		int height = Math.round(source.getWidth() * heightToWidth);

		// высота поместилась?
		if (height <= source.getHeight()) {
			return Bitmap.createBitmap(source, 0, source.getHeight() / 2
					- height / 2, source.getWidth(), height);
		} else {
			// оставляем ширину, вычисляем высоту
			int width = Math.round(source.getHeight() / heightToWidth);
			return Bitmap.createBitmap(source, source.getWidth() / 2 - width
					/ 2, 0, width, source.getHeight());

		}
	}

}
