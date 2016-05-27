package com.msk.web;

import java.io.File;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;

import com.msk.smartparking.BookingManager;
import com.msk.smartparking.BookingManager.Booking;

public class ResponseFactory {

	
	public static void getBooking(JSONObject result, int areaCode) {
		ArrayList<Booking> list = new ArrayList<Booking>();
		
		try {
			JSONArray array = result.getJSONArray("bookingdata");
			
			for(int i=0; i<array.length(); i++)
			{
				JSONObject obj = array.getJSONObject(i);
				Booking booking = new Booking();
				booking.slot_no = obj.getInt("slotnumber");
				
				String startTime = obj.getString("starttime");
				String[] fields=startTime.split(":");
				
				booking.start_hour = Integer.parseInt(fields[0]);
				booking.start_min = Integer.parseInt(fields[1]);
				
				String endTime = obj.getString("endtime");
				fields=endTime.split(":");
				
				booking.exit_hour = Integer.parseInt(fields[0]);
				booking.exit_min = Integer.parseInt(fields[1]);
				booking.isAvailable = false;
				
				list.add(booking);
				
			}
			
			BookingManager.setBooking(list,areaCode);
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public static boolean error=false;
	public static String getBookingStatus(Context ctx,JSONObject result) {

		String status=null;
		try {
			error=result.getBoolean("error");
			status=result.getString("message");
			if(!error)
			{
				String qrcodePath = result.getString("qrcodeurl");
				
				downloadFile(ctx,qrcodePath);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return status;
		
	}
	
	public static String QRCodePath = Environment.getExternalStorageDirectory().getPath()+"/qrcode.png";
	/*public static Bitmap getImage() {
		Bitmap val = null;
		URL newurl;
		try {
			newurl = new URL(path);

			val = BitmapFactory.decodeStream(newurl.openConnection()
					.getInputStream());
			
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return val;
	}*/
	
	public static void downloadFile(Context ctx, String uRl) {
	    File direct = new File(QRCodePath);

	    if (direct.exists()) {
	        direct.delete();
	    }

	    String urls= WebWrapper.url+uRl;
	    DownloadManager mgr = (DownloadManager) ctx.getSystemService(Context.DOWNLOAD_SERVICE);

	    Uri downloadUri = Uri.parse(urls);
	    DownloadManager.Request request = new DownloadManager.Request(
	            downloadUri);

	    request.setAllowedNetworkTypes(
	            DownloadManager.Request.NETWORK_WIFI
	                    | DownloadManager.Request.NETWORK_MOBILE)
	            .setAllowedOverRoaming(false).setTitle("Demo")
	            .setDescription("Something useful. No, really.")
	            .setDestinationUri(Uri.fromFile(direct));
	           // .setDestinationInExternalPublicDir("/QRCode", "qrcode.png");

	    mgr.enqueue(request);

	}

	public static String cancelBooking(JSONObject result) {
		
		String status=null;
		try {
			error=result.getBoolean("error");
			status=result.getString("message");
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return status;
	}
}
