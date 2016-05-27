package com.msk.smartparking;

import java.io.File;

import com.msk.web.ResponseFactory;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.widget.ImageView;
import android.widget.Toast;

public class DisplayActivity extends Activity {
	
	private ImageView iv;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.qrcode);
		
		iv = (ImageView) findViewById(R.id.imageView1);
		
		String path = ResponseFactory.QRCodePath;
		File file = new File(path);
		if(file.exists())
		{
		Uri uri = Uri.fromFile(file);
		
		iv.setImageURI(uri);
		}
		else
		{
			finish();
			Toast.makeText(this, "No QRCode available", Toast.LENGTH_LONG).show();
		}
	}

}
