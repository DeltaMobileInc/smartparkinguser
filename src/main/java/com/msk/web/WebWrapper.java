package com.msk.web;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;





import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class WebWrapper {
	
	public static String ip="";
	public static String urlS= "http://"+ip+"/smartparking/home.php/book";
	public static String urlN= "http://"+ip+"/smartparking/home.php/book/nearest";
	public static String url = "http://"+ip+"/";

	public static void setUrls(String ipServer) {
		// TODO Auto-generated method stub
		url = "http://"+ipServer+"/";
		urlS= "http://"+ipServer+"/smartparking/home.php/book";
		urlN= "http://"+ipServer+"/smartparking/home.php/book/nearest";
	}
	
	public static String getLevelCode(int level)
	{
		String levelS = null;
		switch(level)
		{
		case 1:
			levelS = "G";
			break;
		case 2:
			levelS= "UB";
			break;
		case 3:
			levelS = "LB";
			break;
		}
		return levelS;
	}
	
	public static String getTypeCode(int type)
	{
		String typeS = null;
		switch(type)
		{
		case 1:
			typeS = "TW";
			break;
		case 2:
			typeS = "FW";
			break;
		}
		
		return typeS;
	}
	public static String connectAndGetResponse(String finalurl,HashMap<String, String> params, String method) {
		String response = null;

		try {
			
			URL url = new URL(finalurl);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			//con.setRequestMethod("GET");
			con.setReadTimeout(10000 /* milliseconds */);
			con.setConnectTimeout(15000 /* milliseconds */);
			
			con.setRequestMethod(method);
			con.addRequestProperty("Referer", "http://www.msk.com");
			//con.setRequestProperty("Content-Type","text/xml");
			con.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
			
			con.setDoInput(true);
			//con.setDoOutput(true);
			
			

			// Post parm
			

			if(params!=null && params.size()>0)
			{
				con.setDoOutput(true);
				
				String paramsQ = getQuery(params);
				con.setRequestProperty("Content-Length", "" + 
			               Integer.toString(paramsQ.getBytes().length));
			/*OutputStream os = con.getOutputStream();
			BufferedWriter writer = new BufferedWriter(
			        new OutputStreamWriter(os, "UTF-8"));
			writer.write(getQuery(params));
			writer.flush();
			writer.close();*/
				
				 DataOutputStream wr = new DataOutputStream (
		                  con.getOutputStream ());
		      wr.writeBytes (paramsQ);
		      wr.flush ();
		      wr.close ();
			//os.close();
			}
			// Start the query
			con.connect();
			// Check if task has been interrupted
			if (Thread.interrupted())
				throw new InterruptedException();
			
			StringBuilder sb = new StringBuilder();

			// Read results from the query
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					con.getInputStream(), "UTF-8"));
		//	String payload = reader.readLine();
			String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

			reader.close();

			/*
			 * String line; StringBuilder builder = new StringBuilder();
			 * BufferedReader reader = new BufferedReader(new
			 * InputStreamReader(connection.getInputStream())); while((line =
			 * reader.readLine()) != null) { builder.append(line); }
			 */

			
			//json = new JSONObject(sb.toString());
			response = sb.toString();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.e("connectAndGetResponse", e.getMessage());
		}
		return response;
	}
	
	private static String getQuery(HashMap<String, String> params) throws UnsupportedEncodingException
	{
	    StringBuilder result = new StringBuilder();
	    boolean first = true;

		Set<String> keys = params.keySet();
	    for (String key : keys)
	    {
	        if (first)
	            first = false;
	        else
	            result.append("&");

	        result.append(URLEncoder.encode(key, "UTF-8"));
	        result.append("=");
	        result.append(URLEncoder.encode(params.get(key), "UTF-8"));
	    }

	    return result.toString();
	}
}
