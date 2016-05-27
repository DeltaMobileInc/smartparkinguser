package com.msk.smartparking;



import com.msk.web.WebWrapper;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class HomeActivity extends Activity {

	private String mobileNo;
	private String ipServer;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		
	}

	@Override
	protected void onResume() {
		super.onResume();
		SharedPreferences pref = getSharedPreferences(
				"number", MODE_PRIVATE);

		mobileNo=pref.getString("number", null);
		if(mobileNo==null)
			displayDialog();

		readIP();

	}

	private void readIP()
	{
		SharedPreferences pref = getSharedPreferences(
				"number", MODE_PRIVATE);
		ipServer = pref.getString("ip", null);
		if(ipServer==null)
			displayIP();
		else
			WebWrapper.setUrls(ipServer);
	}
	public void makeBooking(View v)
	{
		if(mobileNo ==null)
			displayDialog();
		else
		{
			Intent i = new Intent(this, MainActivity.class);
			startActivity(i);
		}
			
		
	}
	
	public void showCode(View v)
	{
		Intent i = new Intent(this, DisplayActivity.class);
		startActivity(i);
	}
	
	public void onCancel(View v)
	{
		Intent i = new Intent(this, CancelActivity.class);
		startActivity(i);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		menu.add(0,1,0,"Phone Number");
		menu.add(0,2,0,"Server IP");

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		switch(id)
		{
		case 1:
		displayDialog();
		break;
		case 2:
			displayIP();
			break;
		}
		return true;
	}


	
	private Dialog displayIP() {
		LayoutInflater factory = LayoutInflater.from(this);
		final View textEntryView = factory.inflate(R.layout.ip_dialog,
				null);
		final EditText numberE = (EditText) textEntryView
				.findViewById(R.id.numberE);
		SharedPreferences pref = getSharedPreferences("number",
				MODE_PRIVATE);
		String set_number = pref.getString("ip", "");
		numberE.setText(set_number);
		return new AlertDialog.Builder(this)
				.setIcon(android.R.drawable.alert_dark_frame)
				.setTitle("Enter server IP address")
				.setView(textEntryView)
				.setCancelable(false)
				.setPositiveButton("OK",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {

								/* User clicked OK so do some stuff */
								String number = numberE.getText()
										.toString();

								if (number.length() <=0) {

									Toast.makeText(HomeActivity.this,
											"Enter valid IP",
											Toast.LENGTH_LONG).show();

								} else {
									// mobile_number = number;
									SharedPreferences pref = getSharedPreferences(
											"number", MODE_PRIVATE);
									SharedPreferences.Editor editor = pref
											.edit();
									editor.putString("ip", number);
									editor.commit();
									
									WebWrapper.setUrls(number);
									dialog.cancel();
								}
							}
						})

				.show();

		
	}

	protected Dialog displayDialog() {
		// TODO Auto-generated method stub
		// return super.onCreateDialog(id);
		
			LayoutInflater factory = LayoutInflater.from(this);
			final View textEntryView = factory.inflate(R.layout.phone_dialog,
					null);
			final EditText numberE = (EditText) textEntryView
					.findViewById(R.id.numberE);
			SharedPreferences pref = getSharedPreferences("number",
					MODE_PRIVATE);
			String set_number = pref.getString("number", "");
			numberE.setText(set_number);
			return new AlertDialog.Builder(this)
					.setIcon(android.R.drawable.alert_dark_frame)
					.setTitle("Enter your phone number")
					.setView(textEntryView)
					.setCancelable(false)
					.setPositiveButton("OK",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {

									/* User clicked OK so do some stuff */
									String number = numberE.getText()
											.toString();

									if (number.length() != 10) {

										Toast.makeText(HomeActivity.this,
												"Enter valid mobile number",
												Toast.LENGTH_LONG).show();

									} else {
										// mobile_number = number;
										SharedPreferences pref = getSharedPreferences(
												"number", MODE_PRIVATE);
										SharedPreferences.Editor editor = pref
												.edit();
										editor.putString("number", number);
										editor.commit();
										mobileNo=number;
										/*Intent i = new Intent(HomeActivity.this, MainActivity.class);
										startActivity(i);*/
										Toast.makeText(
												HomeActivity.this,
												"You entered mobile number "
														+ number
														+ "\nOn this number SMS will be sent for status",
												Toast.LENGTH_LONG).show();
										dialog.cancel();
									}
								}
							})

					.show();
		
	}
}
