package com.msk.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class RequestFactory {

	
	
	public static final HashMap<String, String> getBookingRequestParams(String vehicleNo, String mobileNo, int startH, int startM, int exitH, int exitM)
	{
		HashMap<String,String> map = new HashMap<>();
		map.put("starttime", startH + ":" + startM);
		map.put("endtime", exitH + ":" + exitM);
		map.put("vehiclenumber", vehicleNo);
		map.put("mobilenumber", mobileNo);
		/*List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("starttime", startH+":"+startM));
		params.add(new BasicNameValuePair("endtime", exitH+":"+exitM));
		params.add(new BasicNameValuePair("vehiclenumber", vehicleNo));
		params.add(new BasicNameValuePair("mobilenumber", mobileNo));
		*/
		return map;
	}
	
	
	
}
