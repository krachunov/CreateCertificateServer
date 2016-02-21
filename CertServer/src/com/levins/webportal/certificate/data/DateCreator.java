package com.levins.webportal.certificate.data;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateCreator {

	public String createdDateAndTime() {
		DateFormat df = new SimpleDateFormat("dd_MM_yyyy':'HH:mm:");
		Date today = Calendar.getInstance().getTime();
		String reportDate = df.format(today);
		return reportDate;
	}

	/**
	 * Create string by date and use to create folder by current day name
	 * 
	 * @return String with format dd_mm_yyy
	 */
	public String createdDate() {
		DateFormat df = new SimpleDateFormat("dd_MM_yyyy");
		Date today = Calendar.getInstance().getTime();
		String reportDate = df.format(today);
		return reportDate;
	}
}
