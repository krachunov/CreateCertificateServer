package com.levins.webportal.certificate.data;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateCreator {

	/**
	 * Create string by date and use to create folder by current day name
	 * 
	 * @return String with format dd_mm_yyy
	 */
	public String createdDate(String format) {
		DateFormat df = new SimpleDateFormat(format);
		Date today = Calendar.getInstance().getTime();
		String reportDate = df.format(today);
		return reportDate;
	}
}
