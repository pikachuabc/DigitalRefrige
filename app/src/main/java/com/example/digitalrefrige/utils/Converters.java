package com.example.digitalrefrige.utils;

import android.util.Log;
import android.widget.EditText;

import androidx.databinding.InverseMethod;
import androidx.room.TypeConverter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Converters {
    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }


    public static String dateToString(Date date) {
        // Converts long to String.
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(Calendar.YEAR) + "-" + (c.get(Calendar.MONTH) + 1) + "-" + c.get(Calendar.DAY_OF_MONTH);
    }

    public static Date strToDate(String date) {
        // Converts long to String.
        try {
            return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new Date();
    }

    @InverseMethod("strToQuantity")
    public static String quantityToStr(int quantity) {
//        Log.d("Converters","get "+quantity);
        return quantity + "";
    }

    public static int strToQuantity(String quantity) {
//        Log.d("Converters","set "+quantity);
        try {
            quantity = replaceBlank(quantity);
            return Integer.parseInt(quantity);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return 0;
    }


    public static String replaceBlank(String str) {
            String dest = "";
            if (str!=null) {
                Pattern p = Pattern.compile("\\s*|\t|\r|\n");
                Matcher m = p.matcher(str);
                dest = m.replaceAll("");
            }
            return dest;
        }



}
