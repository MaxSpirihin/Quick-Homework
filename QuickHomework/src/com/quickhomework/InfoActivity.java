package com.quickhomework;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;

public class InfoActivity extends Activity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_info);
		
		((TextView) findViewById(R.id.textEMail)).setOnClickListener(this);
		((TextView) findViewById(R.id.textVkPage)).setOnClickListener(this);
		((TextView) findViewById(R.id.textTouchGame)).setOnClickListener(this);
		((TextView) findViewById(R.id.textPogugli)).setOnClickListener(this);
		
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
		switch (v.getId())
		{
		case R.id.textEMail:
			intent = new Intent(Intent.ACTION_VIEW,
					Uri.parse("http://maaaks777@mail.ru"));
			startActivity(intent);
			break;
		case R.id.textVkPage:
			intent = new Intent(Intent.ACTION_VIEW,
					Uri.parse("http://vk.com/maaaks777"));
			startActivity(intent);
			break;
		case R.id.textTouchGame:
			intent = new Intent(
					Intent.ACTION_VIEW,
					Uri.parse("https://play.google.com/store/apps/details?id=com.thetouchgame"));
			startActivity(intent);
			break;
		case R.id.textPogugli:
			intent = new Intent(
					Intent.ACTION_VIEW,
					Uri.parse("https://play.google.com/store/apps/details?id=com.maaaks777.googlingit"));
			startActivity(intent);
			break;
		}
		
	}

}
