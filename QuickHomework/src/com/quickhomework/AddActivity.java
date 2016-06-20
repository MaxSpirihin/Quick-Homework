package com.quickhomework;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ZoomControls;

import com.quickhomework.Classes.Repository;
import com.quickhomework.Classes.Subject;

//экран добавления ДЗ, содержит экран камеры, можно фотать, брать из галереи и сохранять заметки
public class AddActivity extends Activity implements OnClickListener,
		SurfaceHolder.Callback, Camera.PictureCallback, Camera.PreviewCallback,
		Camera.AutoFocusCallback {

	public static final String TAG_ID = "id";
	public static final String IS_CHANGE_TAG = "is_change";

	// размер фото, относительно размера экрана
	final float SIZE = 2f;

	int id; // id предмета
	int currentNumber; // показывает сколько предметов было добавлено
	Repository repository;

	// элементы экрана
	EditText etText;
	Button btnTakePhoto;
	Button btnSave;
	Button btnGallery;
	Button btnShow;

	private Camera camera;
	private SurfaceHolder surfaceHolder;
	private SurfaceView preview;
	int currentZoomLevel = 0, maxZoomLevel = 0;

	boolean done; // показывает произошло ли обращение к БД
	boolean photoDone; // оказывает было ли сделано фото



	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add);

		// инициализируем важные переменные и объекты
		done = false;
		photoDone = false;
		id = getIntent().getIntExtra(TAG_ID, 0);
		repository = new Repository(this);
		currentNumber = repository.GetSubject(id).countOfHomeWork;

		// находим элементы экрана, ставим обработчиков
		etText = (EditText) findViewById(R.id.etText);
		btnSave = (Button) findViewById(R.id.btnSave);
		btnSave.setOnClickListener(this);
		btnTakePhoto = (Button) findViewById(R.id.btnTakePhoto);
		btnTakePhoto.setOnClickListener(this);
		btnGallery = (Button) findViewById(R.id.btnGallery);
		btnGallery.setOnClickListener(this);
		btnShow = (Button) findViewById(R.id.btnShow);
		btnShow.setOnClickListener(this);
		btnShow.setVisibility(View.GONE);

		// наше SurfaceView и surfaceHolder для камеры
		preview = (SurfaceView) findViewById(R.id.surfaceView);
		surfaceHolder = preview.getHolder();
		surfaceHolder.addCallback(this);
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

		// если активити ывзвано из просмотра ДЗ
		if (getIntent().getBooleanExtra(IS_CHANGE_TAG, false)) {
			done = true;
			btnShow.setVisibility(View.VISIBLE);
			currentNumber--;
		}
	}



	@SuppressWarnings("deprecation")
	@Override
	protected void onResume() {
		super.onResume();
		// открываем камеру
		camera = Camera.open();

		// задаем ей нужные параметры
		Camera.Parameters p = camera.getParameters();
		p.set("jpeg-quality", 100);
		p.set("orientation", "landscape");
		p.set("rotation", 90);
		p.setPictureFormat(PixelFormat.JPEG);
		p.setPictureSize(Math.round((getWindowManager().getDefaultDisplay()
				.getHeight() - this.getStatusBarHeight()) * SIZE),
				Math.round(getWindowManager().getDefaultDisplay().getWidth()
						* SIZE));
		camera.setParameters(p);

	}



	@Override
	protected void onPause() {
		super.onPause();

		if (camera != null) {
			camera.setPreviewCallback(null);
			camera.stopPreview();
			camera.release();
			camera = null;
		}
	}



	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

		// здесь настраивается зум
		ZoomControls zoomControls = (ZoomControls) findViewById(R.id.zoomControls);

		Camera.Parameters params = camera.getParameters();

		if (params.isZoomSupported()) {
			maxZoomLevel = params.getMaxZoom();

			zoomControls.setIsZoomInEnabled(true);
			zoomControls.setIsZoomOutEnabled(true);
			zoomControls.setOnZoomInClickListener(new OnClickListener() {

				public void onClick(View v) {
					if (currentZoomLevel < maxZoomLevel) {
						currentZoomLevel++;

						Camera.Parameters parameters = camera.getParameters();
						parameters.setZoom(currentZoomLevel);
						camera.setParameters(parameters);
					}
				}
			});

			zoomControls.setOnZoomOutClickListener(new OnClickListener() {

				public void onClick(View v) {
					if (currentZoomLevel > 0) {
						currentZoomLevel--;
						Camera.Parameters parameters = camera.getParameters();
						parameters.setZoom(currentZoomLevel);
						camera.setParameters(parameters);
					}
				}
			});

		} else {
			zoomControls.setVisibility(View.GONE);
		}

	}



	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		try {
			camera.setPreviewDisplay(holder);
			camera.setPreviewCallback(this);
		} catch (IOException e) {
			e.printStackTrace();
		}

		Size previewSize = camera.getParameters().getPreviewSize();
		float aspect = (float) previewSize.width / previewSize.height;

		int previewSurfaceHeight = preview.getHeight();

		LayoutParams lp = preview.getLayoutParams();

		// здесь корректируем размер отображаемого preview, чтобы не было
		// искажений

		// портретный вид
		camera.setDisplayOrientation(90);
		lp.height = previewSurfaceHeight;
		lp.width = (int) (previewSurfaceHeight / aspect);

		preview.setLayoutParams(lp);
		camera.startPreview();
	}



	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
	}



	@Override
	public void onClick(View v) {

		switch (v.getId()) {

		case R.id.btnSave:

			if (TextUtils.isEmpty(etText.getText().toString())) {
				// если есть ошибки ввода, говорим об этом
				Toast.makeText(getApplicationContext(),
						this.getResources().getString(R.string.enter_text),
						Toast.LENGTH_SHORT).show();
			} else {
				if (done) {
					repository
							.SaveTextHomeWork(id, etText.getText().toString());

				} else {

					// есть вариант, что все ДЗ перезаписывабтся, при этом юзер
					// не сохранит фото, учтем его отдельно (говнокод пиздец, но
					// бля)
					if (currentNumber == Subject.MAX_COUNT_OF_HOME_WORKS) {
						String path = Subject.ComputePath(id, 0);
						File file = new File(path);
						file.delete();

						for (int i = 1; i < Subject.MAX_COUNT_OF_HOME_WORKS; i++) {
							path = Subject.ComputePath(id, i);
							file = new File(path);
							file.renameTo(new File(Subject.ComputePath(id,
									i - 1)));
						}

						photoDone = true;
					}

					repository.SaveNewTextHomeWork(id, etText.getText()
							.toString());
					done = true;
					btnShow.setVisibility(View.VISIBLE);
				}

				// убираем экранную клаву и выводим, что все ок.
				etText.setText("");
				Context context = getApplicationContext();
				InputMethodManager imm = (InputMethodManager) context
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(etText.getWindowToken(), 0);
				Toast.makeText(getApplicationContext(),
						this.getResources().getString(R.string.text_saved),
						Toast.LENGTH_SHORT).show();
			}
			break;

		case R.id.btnTakePhoto:
			// делаем фото
			camera.autoFocus(this);
			break;
		case R.id.btnGallery:
			Intent photoPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
			photoPickerIntent.setType("image/*");
			startActivityForResult(photoPickerIntent, 1);
			break;
		case R.id.btnShow:
			Intent intent = new Intent(this, ShowActivity.class);
			intent.putExtra(ShowActivity.TAG_ID, id);
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



	@Override
	public void onAutoFocus(boolean paramBoolean, Camera paramCamera) {
		if (paramBoolean) {
			// если удалось сфокусироваться, делаем снимок
			paramCamera.takePicture(null, null, null, this);
		}
	}



	@Override
	public void onPictureTaken(byte[] paramArrayOfByte, Camera paramCamera) {
		try {
			// если нет папки создаем ее
			File saveDir = new File(Subject.PATH);

			if (!saveDir.exists()) {
				saveDir.mkdirs();
			}

			if (!photoDone) {
				if (currentNumber != Subject.MAX_COUNT_OF_HOME_WORKS) {

					// всех фото еще нет
					String path = Subject.ComputePath(id, currentNumber);

					FileOutputStream os = new FileOutputStream(path);
					os.write(paramArrayOfByte);
					os.close();

				}

				else {
					// 3 фото уже есть, их надо переписывать
					// удалим нулевую, и сместим остальноые, переименоваав их
					String path = Subject.ComputePath(id, 0);
					File file = new File(path);
					file.delete();

					for (int i = 1; i < Subject.MAX_COUNT_OF_HOME_WORKS; i++) {
						path = Subject.ComputePath(id, i);
						file = new File(path);
						file.renameTo(new File(Subject.ComputePath(id, i - 1)));
					}

					// теперь можно сохранять
					FileOutputStream os = new FileOutputStream(
							Subject.ComputePath(id, currentNumber - 1));
					os.write(paramArrayOfByte);
					os.close();

					// вдруг он текст не добавит
					repository.SaveNewTextHomeWork(id, null);
					done = true;
					btnShow.setVisibility(View.VISIBLE);

				}// end if curnum<max

				if (!done) {
					(new Repository(this)).PhotoWasAdded(id);
					done = true;
					btnShow.setVisibility(View.VISIBLE);
				}
				photoDone = true;

			}// end if !done
			else {
				// фото делают вновь, просто перезаписываем, учитывая кол-во

				FileOutputStream os;
				if (currentNumber == Subject.MAX_COUNT_OF_HOME_WORKS) {
					os = new FileOutputStream(Subject.ComputePath(id,
							currentNumber - 1));
				} else {
					os = new FileOutputStream(Subject.ComputePath(id,
							currentNumber));
				}
				os.write(paramArrayOfByte);
				os.close();

			}

		} catch (Exception e) {

		}

		Toast.makeText(getApplicationContext(),
				this.getResources().getString(R.string.photo_saved),
				Toast.LENGTH_SHORT).show();

		// после того, как снимок сделан, показ превью отключается. необходимо
		// включить его
		if (paramCamera != null)
			paramCamera.startPreview();

	}



	@Override
	public void onPreviewFrame(byte[] arg0, Camera arg1) {
		// TODO Auto-generated method stub

	}



	public int getStatusBarHeight() {
		int result = 0;
		int resourceId = getResources().getIdentifier("status_bar_height",
				"dimen", "android");
		if (resourceId > 0) {
			result = getResources().getDimensionPixelSize(resourceId);
		}
		return result;
	}



	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		case 1: {
			if (resultCode == RESULT_OK) {

				// подготавливаемся к записи
				Uri selectedImage = data.getData();
				InputStream imageStream = null;
				try {
					imageStream = getContentResolver().openInputStream(
							selectedImage);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				Bitmap yourSelectedImag = BitmapFactory
						.decodeStream(imageStream);

				float relative = yourSelectedImag.getHeight() * 1.0f
						/ yourSelectedImag.getWidth();

				@SuppressWarnings("deprecation")
				Bitmap yourSelectedImage = Bitmap.createScaledBitmap(
						yourSelectedImag,
						Math.round(getWindowManager().getDefaultDisplay()
								.getWidth() * SIZE),
						Math.round(getWindowManager().getDefaultDisplay()
								.getWidth() * relative * SIZE), true);

				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				yourSelectedImage.compress(Bitmap.CompressFormat.JPEG, 100,
						stream);
				byte[] byteArray = stream.toByteArray();

				// то, что дальше, как и раньше в фото
				this.onPictureTaken(byteArray, null);

			}
			break;
		}
		}
	}

}
