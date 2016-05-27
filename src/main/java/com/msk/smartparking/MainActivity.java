package com.msk.smartparking;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.RadioGroup;
import android.widget.Spinner;

public class MainActivity extends Activity implements AdapterView.OnItemSelectedListener {
	
	private RadioGroup rGroup1, rGroup2;

	private Spinner areaSp;
	private int areaCode;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		areaSp = (Spinner) findViewById(R.id.spinner);
		areaSp.setOnItemSelectedListener(this);
		rGroup1 = (RadioGroup) findViewById(R.id.radioGroup1);
		rGroup2 = (RadioGroup) findViewById(R.id.radioGroup2);
	}

	
	
	public void newBooking(View v)
	{
		int type = 1; // two-wheeler
		int id =rGroup1.getCheckedRadioButtonId();
		if(id == R.id.radio0)
			type =1;
		else
			type = 2;// 4wheeler
		
		int level = 1; // ground
		
		id = rGroup2.getCheckedRadioButtonId();
		switch(id)
		{
		case R.id.radio0:
			level = 1;
			break;
		case R.id.radio1:
			level = 2;
			break;
		case R.id.radio2:
			level = 3;
			break;
		}
		
		Intent i = new Intent(this, BookingActivity.class);
		i.putExtra("type", type);
		i.putExtra("level", level);
		i.putExtra("area",areaCode);
		startActivity(i);
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		areaCode =position;
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {

	}
}
