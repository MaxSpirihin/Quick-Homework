package com.quickhomework;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.net.Uri;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.ZoomControls;

import com.quickhomework.Classes.Subject;

public class AddScheduleActivity extends Activity implements OnClickListener,
		SurfaceHolder.Callback, Camera.PictureCallback, Camera.PreviewCallback,
		Camera.AutoFocusCallback {

	// размер фото, относительно размера экрана
	final float SIZE = 2f;

	Button btnCancel;
	Button btnGallery;
	Button btnTakePhoto;

	private Camera camera;
	private SurfaceHolder surfaceHolder;
	private SurfaceView preview;
	int currentZoomLevel = 0, maxZoomLevel = 0;



	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_schedule);

		btnCancel = (Button) findViewById(R.id.btnCancel);
		btnCancel.setOnClickListener(this);
		btnGallery = (Button) findViewById(R.id.btnGallery);
		btnGallery.setOnClickListener(this);
		btnTakePhoto = (Button) findViewById(R.id.btnTakePhoto);
		btnTakePhoto.setOnClickListener(this);

		// наше SurfaceView и surfaceHolder для камеры
		preview = (SurfaceView) findViewById(R.id.surfaceView);
		surfaceHolder = preview.getHolder();
		surfaceHolder.addCallback(this);
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
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

			FileOutputStream os = new FileOutputStream(
					ShowScheduleActivity.SCHEDULE_PATH);
			os.write(paramArrayOfByte);
			os.close();

			Intent intent = new Intent(this, ShowScheduleActivity.class);
			startActivity(intent);
			finish();

		} catch (Exception e) {

		}

	}



	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnCancel:
			Intent intent = new Intent(this, ShowScheduleActivity.class);
			startActivity(intent);
			finish();
			break;
		case R.id.btnGallery:
			Intent photoPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
			photoPickerIntent.setType("image/*");
			startActivityForResult(photoPickerIntent, 1);
			break;
		case R.id.btnTakePhoto:
			// делаем фото
			camera.autoFocus(this);
			break;
		}

	}



	@Override
	public void onBackPressed() {
		Intent intent = new Intent(this, ShowScheduleActivity.class);
		startActivity(intent);
		finish();
	}



	@Override
	public void onPreviewFrame(byte[] arg0, Camera arg1) {
		// TODO Auto-generated method stub

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



	public int getStatusBarHeight() {
		int result = 0;
		int resourceId = getResources().getIdentifier("status_bar_height",
				"dimen", "android");
		if (resourceId > 0) {
			result = getResources().getDimensionPixelSize(resourceId);
		}
		return result;
	}

}
