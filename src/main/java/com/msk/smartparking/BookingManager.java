package com.msk.smartparking;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class BookingManager {

	public static void setAlarm(Context ctx,int startH, int startM) {
		Intent notificationIntent = new Intent(ctx, MyReceiver.class);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(ctx, 0, notificationIntent, PendingIntent.FLAG_ONE_SHOT);

		Date dat  = new Date();//initializes to now
		Calendar cal=Calendar.getInstance();
		cal.setTime(dat);
		cal.set(Calendar.HOUR_OF_DAY, startH);
		cal.set(Calendar.MINUTE,startM);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		//long futureInMillis = (startH*60+startM-15)*60*1000;
		AlarmManager alarmManager = (AlarmManager)ctx.getSystemService(Context.ALARM_SERVICE);

		alarmManager.set(AlarmManager.RTC, cal.getTimeInMillis()-15*60*1000, pendingIntent);
	}



	public static class Booking
	{
		public int slot_no;
		public boolean isAvailable = true;
		public int start_hour;
		public int start_min;
		public int exit_hour;
		public int exit_min;
	}

	public static int adjustIndex(int area)
	{
		switch(area)
		{
			case 0:
				return 0;
			case 1:
				return 8;
			case 2:
				return 16;
			case 3:
				return 24;
		}
		return 0;
	}
	private static ArrayList<Booking> bookingList= new ArrayList<Booking>();
	
	public static void setBooking(ArrayList<Booking> list, int area)
	{
		int area_index = adjustIndex(area);
		ArrayList<Booking> arrangedList = new ArrayList<Booking>();
		for(int i =0; i<8; i++)
		{
			int slot= i+1+area_index;
			int match = -1;
			for(int j=0; j<list.size(); j++)
			{
				Booking booking = list.get(j);
				if(booking.slot_no == slot)
				{
					match = j;
					break;
				}
			}
			
			if(match!=-1)
				arrangedList.add(list.get(match));
			else
			{
				Booking booking = new Booking();
				booking.slot_no = slot;
				booking.isAvailable = true;
				arrangedList.add(booking);
			}
				
		}
		bookingList = arrangedList;
	}
	
	public static boolean isAvailable(int slot_no, int area)
	{
		int area_index = adjustIndex(area);

		int index = slot_no -1-area_index;
		
		return bookingList.get(index).isAvailable;
	}
	
	public static int getStartH(int slot_no,int area)
	{
		int area_index = adjustIndex(area);

		int index = slot_no -1-area_index;
		return bookingList.get(index).start_hour;
	}
	
	public static int getStartM(int slot_no,int area)
	{
		int area_index = adjustIndex(area);

		int index = slot_no -1-area_index;
		return bookingList.get(index).start_min;
	}
	
	/*public static int getExitH(int slot_no)
	{
		return bookingList.get(slot_no-1).exit_hour;
	}
	
	public static int getExitM(int slot_no)
	{
		return bookingList.get(slot_no-1).exit_min;
	}
*/}
