package com.msk.smartparking;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


import org.json.JSONException;
import org.json.JSONObject;


import com.msk.web.RequestFactory;
import com.msk.web.ResponseFactory;
import com.msk.web.WebWrapper;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class BookingActivity extends Activity implements OnTimeSetListener, LocationListener {
	
	private ProgressDialog dlg;
	private EditText vehicleE;
	private String vehicleNo=null, mobileNo=null;
	private int slotNo=1;
	private Button slot1, slot2, slot3,slot4,slot5, slot6, slot7, slot8;
	private int type, level, areaCode;
	private Button findNearest;
	ProgressDialog locationDialog;
	private LocationManager locationManager;
	String lat;
	String lng;
	Boolean doIhaveLocation = false;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.booking);

		//BookingManager.setAlarm(this, 13, 05);
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			// TODO: Consider calling
			//    ActivityCompat#requestPermissions
			// here to request the missing permissions, and then overriding
			//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
			//                                          int[] grantResults)
			// to handle the case where the user grants the permission. See the documentation
			// for ActivityCompat#requestPermissions for more details.
			//return;
		}

		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 10, this);
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 3000, 10, this);

		vehicleE = (EditText) findViewById(R.id.vehicleE);
		findNearest = (Button)findViewById(R.id.findNearest);
		findNearest.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!doIhaveLocation) {
					Toast.makeText(BookingActivity.this, "No Location yet. Try again later. Is GPS switched on?", Toast.LENGTH_LONG).show();
					return;
				}
				dlg = ProgressDialog.show(BookingActivity.this, "Getting Nearest", "Please wait");
				// get booking status
				NearestSlot task = new NearestSlot();
				task.execute(type, level);
			}
		});
		slot1 = (Button) findViewById(R.id.button1);
		slot2 = (Button) findViewById(R.id.button2);
		slot3 = (Button) findViewById(R.id.button3);
		slot4 = (Button) findViewById(R.id.button4);
		slot5 = (Button) findViewById(R.id.button5);
		slot6 = (Button) findViewById(R.id.button6);
		slot7 = (Button) findViewById(R.id.button7);
		slot8 = (Button) findViewById(R.id.button8);
		
		SharedPreferences pref = getSharedPreferences(
				"number", MODE_PRIVATE);
		
		mobileNo = pref.getString("number", null);
		
		
		Intent i = getIntent();
		
		 type = i.getIntExtra("type", 1);
		 level = i.getIntExtra("level", 1);
		areaCode = i.getIntExtra("area",0);
		
		dlg = ProgressDialog.show(this, "Getting Booking status", "Please wait");
		// get booking status
		 BookingStatus task = new BookingStatus();
		 task.execute(type, level);

		setSlotTitles();
	}

	@Override
	public void onLocationChanged(Location location) {

		String str = "Latitude: " + location.getLatitude() + "Longitude: " + location.getLongitude();
		lat = Double.toString(location.getLatitude());
		lng = Double.toString(location.getLongitude());
		doIhaveLocation = true;
		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			// TODO: Consider calling
			//    ActivityCompat#requestPermissions
			// here to request the missing permissions, and then overriding
			//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
			//                                          int[] grantResults)
			// to handle the case where the user grants the permission. See the documentation
			// for ActivityCompat#requestPermissions for more details.
			return;
		}
		locationManager.removeUpdates(this);
		Toast.makeText(getBaseContext(), str, Toast.LENGTH_LONG).show();

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {

	}

	@Override
	public void onProviderEnabled(String provider) {

	}

	@Override
	public void onProviderDisabled(String provider) {

	}

	private void setSlotTitles() {
		if(areaCode!=0)

		{
			int area_index = BookingManager.adjustIndex(areaCode);
			slot1.setText(1+area_index+"");
			slot2.setText(2+area_index+"");
			slot3.setText(3+area_index+"");
			slot4.setText(4+area_index+"");
			slot5.setText(5+area_index+"");
			slot6.setText(6+area_index+"");
			slot7.setText(7+area_index+"");
			slot8.setText(8+area_index+"");
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
			int level = params[0];
			int type = params[1];
			int slot = params[2];
			
			String typeS = WebWrapper.getTypeCode(type);
				
			String levelS = WebWrapper.getLevelCode(level);
			
			String url = WebWrapper.urlS +"/"+levelS+"/"+typeS+"/"+slot;
		//	url="http://192.168.1.2/trial/trial.php";

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
			dlg.cancel();
			String status=null;
			if(result!=null)
			{
				status=ResponseFactory.getBookingStatus(BookingActivity.this, result);
				Toast.makeText(BookingActivity.this, "Status:"+status, Toast.LENGTH_LONG).show();
				if(!ResponseFactory.error) {
					setColor(slotNo, true);
					BookingManager.setAlarm(getApplicationContext(),startH, startM);
				}
			}
				
		}
		
	}


	private class BookingStatus extends AsyncTask<Integer, Void, JSONObject>
	{

		@Override
		protected JSONObject doInBackground(Integer... params) {
			
			JSONObject result=null;
			int type = params[0];
			int level = params[1];
			
			String typeS = WebWrapper.getTypeCode(type);
			
			
			String levelS = WebWrapper.getLevelCode(level);
			
			
			String url = WebWrapper.urlS +"/"+levelS+"/"+typeS;
			
			String response= WebWrapper.connectAndGetResponse(url, null, "GET");
			
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
			dlg.cancel();
			if(result!=null)
			{
				ResponseFactory.getBooking(result,areaCode);
				setColor();
			}
		}
		
	}

	private class NearestSlot extends AsyncTask<Integer, Void, JSONObject>
	{

		@Override
		protected JSONObject doInBackground(Integer... params) {

			JSONObject result=null;
			int type = params[0];
			int level = params[1];

			String typeS = WebWrapper.getTypeCode(type);

			String levelS = WebWrapper.getLevelCode(level);



			String url = WebWrapper.urlN +"/"+levelS+"/"+typeS+"/"+lat+"/"+lng;

			String response= WebWrapper.connectAndGetResponse(url, null, "GET");

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
			dlg.cancel();
			String nearest="";
			if(result!=null)
			{
				try {

					nearest = result.getString("nearest");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				TextView tv = (TextView)findViewById(R.id.nearest);
				tv.setText("Slot " + nearest + " seems to be closest to you!");

			}
		}

	}
	
	private void setColor() {
		Button slotB = null;
		int area_index = BookingManager.adjustIndex(areaCode);
		for (int i = 1; i <= 8; i++) {
			switch (i) {
			case 1:
				slotB = slot1;
				break;
			case 2:
				slotB = slot2;
				break;
			case 3:
				slotB = slot3;
				break;
			case 4:
				slotB = slot4;
				break;
			case 5:
				slotB = slot5;
				break;
			case 6:
				slotB = slot6;
				break;
			case 7:
				slotB = slot7;
				break;
			case 8:
				slotB = slot8;
				break;
			}

			boolean isBooked = BookingManager.isAvailable(i+area_index,areaCode);
			if (!isBooked)
				slotB.setBackgroundResource(R.color.booked);
			else
				slotB.setBackgroundResource(R.color.available);
		}
	}
	private void setColor(int slot, boolean isBooked)
	{
		Button slotB=null;
		int area_index = BookingManager.adjustIndex(areaCode);
		slot= slot-area_index;
		switch(slot)
		{
		case 1:
			slotB = slot1;
			break;
		case 2:
			slotB = slot2;
			break;
		case 3:
			slotB = slot3;
			break;
		case 4:
			slotB = slot4;
			break;
		case 5:
			slotB = slot5;
			break;
		case 6:
			slotB = slot6;
			break;
		case 7:
			slotB = slot7;
			break;
		case 8:
			slotB = slot8;
			break;
		}
		
		if(isBooked)
			slotB.setBackgroundResource(R.color.booked);
		else
			slotB.setBackgroundResource(R.color.available);
	}

	public void doBooking(View v)
	{
		
		vehicleNo= vehicleE.getText().toString();

		
		if(vehicleNo.length()>0)
		{
			
			slotNo=Integer.parseInt(((Button) v).getText().toString());
			/*int slotB = v.getId();
			switch(slotB)
			{
			case R.id.button1:
				//slotNo =1;
				slotNo = Integer.parseInt(slot1.getText().toString());
				break;
			case R.id.button2:
				slotNo =2;
				break;
			case R.id.button3:
				slotNo =3;
				break;
			case R.id.button4:
				slotNo =4;
				break;
			case R.id.button5:
				slotNo =5;
				break;
			case R.id.button6:
				slotNo =6;
				break;
			case R.id.button7:
				slotNo =7;
				break;
			case R.id.button8:
				slotNo =8;
				break;
			}*/
			
			//check type of slot, clicked
			if(BookingManager.isAvailable(slotNo,areaCode))
			{
		TimePickerDialog dlg = new TimePickerDialog(this, this, 8, 0, true);
		dlg.setTitle("Select time");
		dlg.show();
			}
			else
			{
				Intent i = new Intent(this, BookingOverlapActivity.class);
				i.putExtra("type", type);
				i.putExtra("level", level);
				i.putExtra("slot", slotNo);
				i.putExtra("vehicle", vehicleNo);
				i.putExtra("mobile", mobileNo);
				i.putExtra("area",areaCode);
				startActivity(i);
			}
		}
		else
			Toast.makeText(this, "Pls enter vehicle number", Toast.LENGTH_LONG).show(); 
	}

	@Override
	public void onTimeSet(TimePicker view, final int hod, final int mm) {
		
		 if (view.isShown()) {
		int currentH= Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
		int currentM = Calendar.getInstance().get(Calendar.MINUTE);
		
		if(hod> currentH)
		{
			int diff = hod - currentH;
			if(diff>4)
				Toast.makeText(this, "Pls book 4hours before", Toast.LENGTH_LONG).show();
			else 
			{
				//for credit card

				LayoutInflater layoutInflater = LayoutInflater.from(BookingActivity.this);
				View promptView = layoutInflater.inflate(R.layout.activity_payment, null);
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(BookingActivity.this);
				alertDialogBuilder.setView(promptView);
				// setup a dialog window
				alertDialogBuilder.setCancelable(false)
						.setPositiveButton("OK", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dlg = ProgressDialog.show(BookingActivity.this, "", "Pls wait; Booking is in progress");
								BookingTask task = new BookingTask(vehicleNo, mobileNo, hod, mm, 0, 0);
								//BookingTask task = new BookingTask(vehicleNo, mobileNo, hod, mm, hod+duration, mm);

								task.execute(level, type, slotNo);

							}
						})
						.setNegativeButton("Cancel",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int id) {
										dialog.cancel();
									}
								});

				// create an alert dialog
				AlertDialog alert = alertDialogBuilder.create();
				alert.show();


				//do booking

			}
		}

		else
		{
			Toast.makeText(this, "Invalid booking time", Toast.LENGTH_LONG).show();
		}
		 }
		
	}
}
