package com.twtchnz.superscannr.superscanner.utils;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class DateFormatter {

    public static String formatDate(String fromFormat, String dateString, String toFormat) {
        SimpleDateFormat from = new SimpleDateFormat(fromFormat);
        SimpleDateFormat to = new SimpleDateFormat(toFormat);
        String date = dateString;

        try {
            date = to.format(from.parse(dateString));
        } catch (ParseException e) {
            Log.e(Utils.APP_TAG, e.getMessage());
        }

        return date;
    }
}
