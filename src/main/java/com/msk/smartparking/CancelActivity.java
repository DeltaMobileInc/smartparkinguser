package com.msk.smartparking;
import org.json.JSONException;
import org.json.JSONObject;

import com.msk.web.ResponseFactory;
import com.msk.web.WebWrapper;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


public class CancelActivity extends Activity {

	private EditText vehicleE;
	private ProgressDialog dlg;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cancellation);
		vehicleE = (EditText) findViewById(R.id.vehicleE);
		
	}
	
	public void cancelClick(View v)
	{
		String vehicle = vehicleE.getText().toString();
		
		if(vehicle.length()>0)
		{
			// do cancellation
			dlg = ProgressDialog.show(this, "Cancelling booking", "Pls waits" );
			CancelTask task = new CancelTask();
			task.execute(vehicle);
		}
		else
			Toast.makeText(this, "Pls enter vehicle no", Toast.LENGTH_LONG).show();
	}
	
	private class CancelTask extends AsyncTask<String, Void, JSONObject>
	{

		@Override
		protected JSONObject doInBackground(String... params) {
			JSONObject result=null;
			String vehicleNo = params[0];
			
			String url = WebWrapper.urlS +"/"+vehicleNo;
			
			String response= WebWrapper.connectAndGetResponse(url, null, "DELETE");
			
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
				String status=ResponseFactory.cancelBooking(result);
				Toast.makeText(CancelActivity.this, "Status:"+status, Toast.LENGTH_LONG).show();
				
			}
		}
	}
}
