package com.msk.smartparking;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;


import com.msk.web.RequestFactory;
import com.msk.web.ResponseFactory;
import com.msk.web.WebWrapper;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.TimePicker.OnTimeChangedListener;

public class BookingOverlapActivity extends Activity implements OnTimeChangedListener {
	
	private TimePicker sTime, eTime;
	private int startHod, endHod, startMM, endMM;
	private int bookingHod, bookingMM;
	
	private int type, level, slotNo,areaCode;
	private String vehicleNo;
	private String mobileNo;
	private TextView statT;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.booked);

		statT = (TextView) findViewById(R.id.status);
		sTime = (TimePicker) findViewById(R.id.timePicker1);
		eTime = (TimePicker) findViewById(R.id.timePicker2);
		
		sTime.setOnTimeChangedListener(this);
		eTime.setOnTimeChangedListener(this);
		
		Intent i = getIntent();
		type = i.getIntExtra("type", 1);
		level = i.getIntExtra("level", 1);
		slotNo = i.getIntExtra("slot", 1);
		areaCode = i.getIntExtra("area",0);
		vehicleNo = i.getStringExtra("vehicle");
		mobileNo= i.getStringExtra("mobile");
		getBookingTime();
	}
	
	private void getBookingTime() {

		bookingHod= BookingManager.getStartH(slotNo,areaCode);
		bookingMM = BookingManager.getStartM(slotNo,areaCode);

		statT.append(bookingHod+":"+bookingMM);
		
	}

	public void doneClick(View v)
	{
		int currentH= Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
		int currentM = Calendar.getInstance().get(Calendar.MINUTE);
		
		if(startHod> currentH)
		{
			int diff = startHod - currentH;
			if(diff>4)
				Toast.makeText(this, "Pls book 4hours before", Toast.LENGTH_LONG).show();
			else if(endHod<=bookingHod)
			{
				// do booking
				BookingTask task = new BookingTask(vehicleNo, mobileNo, startHod, startMM, endHod, endMM);
				task.execute(level, type, slotNo);
			}
			else
				Toast.makeText(this, "Booking time is overlapping; booking not done", Toast.LENGTH_LONG).show();
		}
		
		else
		{
			Toast.makeText(this, "Invalid booking time", Toast.LENGTH_LONG).show();
		}
	}

	private class BookingTask extends AsyncTask<Integer, Void, JSONObject>
	{
		private String vehicle, mobileNo;
		private int startH, startM, exitH, exitM;
		public BookingTask(String vehicle,String mobileNo, int startH, int startM, int exitH, int exitM) {
			this.vehicle = vehicle;
			this.mobileNo = mobileNo;
			this.startH = startH;
			this.startM = startM;
			this.exitH = exitH;
			this.exitM = exitM;
		}
		@Override
		protected JSONObject doInBackground(Integer... params) {
			JSONObject result=null;
			int type = params[0];
			int level = params[1];
			int slot = params[2];
			
			String typeS = WebWrapper.getTypeCode(type);
				
			String levelS = WebWrapper.getLevelCode(level);
			
			String url = WebWrapper.urlS +"/"+levelS+"/"+typeS+"/"+slot;

			HashMap<String, String> reqParams = RequestFactory.getBookingRequestParams(vehicle, mobileNo, startH, startM, exitH, exitM);
			String response= WebWrapper.connectAndGetResponse(url, reqParams, "POST");
			
			if(response !=null)
				try {
					result = new JSONObject(response);
				} catch (JSONException e) {
					Log.e("BookingActivity", e.getMessage());
				}
			
			return result;
		}
		
		@Override
		protected void onPostExecute(JSONObject result) {
			String status=null;
			if(result!=null)
			{
				status=ResponseFactory.getBookingStatus(BookingOverlapActivity.this, result);
				Toast.makeText(BookingOverlapActivity.this, "Status:"+status, Toast.LENGTH_LONG).show();
			}
				
		}
		
	}
	
	@Override
	public void onTimeChanged(TimePicker v, int hod, int mm) {
		if(v== sTime)
		{
			startHod = hod;
			startMM =mm;
		}
		else if(v== eTime)
		{
			endHod = hod;
			endMM = mm;
		}
		
	}

}
