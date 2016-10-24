package com.fastaccess.helper;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.widget.EditText;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;

/**
 * Created by kosh20111 on 3/11/2015. CopyRights @ Innov8tif
 * <p>
 * Input Helper to validate stuff related to input fields.
 */
public class InputHelper {


    private static boolean isWhiteSpaces(String s) {
        return s != null && s.matches("\\s+");
    }

    public static boolean isEmpty(String text) {
        return text == null || TextUtils.isEmpty(text) || isWhiteSpaces(text);
    }

    public static boolean isEmpty(Object text) {
        return text == null || TextUtils.isEmpty(text.toString()) || isWhiteSpaces(text.toString());
    }

    public static boolean isEmpty(EditText text) {
        return text == null || isEmpty(text.getText().toString());
    }

    public static boolean isEmpty(TextView text) {
        return text == null || isEmpty(text.getText().toString());
    }

    public static boolean isEmpty(TextInputLayout txt) {
        return txt == null || isEmpty(txt.getEditText());
    }

    public static String toNA(String value) {
        return isEmpty(value) ? "N/A" : value;
    }

    public static String toString(EditText editText) {
        return editText.getText().toString();
    }

    public static String toString(TextView editText) {
        return editText.getText().toString();
    }

    public static String toString(TextInputLayout textInputLayout) {
        return toString(textInputLayout.getEditText());
    }

    @NonNull public static String toString(@NonNull Object object) {
        return !isEmpty(object) ? object.toString() : "";
    }

    public static String formatSize(Context context, long size) {
        return Formatter.formatShortFileSize(context, size);
    }

    public static String formatPrice(double doubleValue) {
        NumberFormat nf = NumberFormat.getCurrencyInstance();
        DecimalFormatSymbols decimalFormatSymbols = ((DecimalFormat) nf).getDecimalFormatSymbols();
        decimalFormatSymbols.setCurrencySymbol("");
        ((DecimalFormat) nf).setDecimalFormatSymbols(decimalFormatSymbols);
        return nf.format(doubleValue).trim();
    }

    public static String ordinal(int i) {
        return i % 100 == 11 || i % 100 == 12 || i % 100 == 13 ? i + "th" : i +
                new String[]{"th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th"}[i % 10];
    }

    @NonNull public static String getTwoLetters(@NonNull String value) {
        return value.length() > 1 ? (String.valueOf(value.charAt(0)) + String.valueOf(value.charAt(1))) : String.valueOf(value.charAt(0));
    }
}
