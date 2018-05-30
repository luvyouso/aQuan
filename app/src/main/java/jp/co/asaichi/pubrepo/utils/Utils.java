package jp.co.asaichi.pubrepo.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jp.co.asaichi.pubrepo.common.Constants;


/**
 * Created by nguyentu on 9/21/17.
 */

public class Utils {
    /**
     * @param json
     * @return
     */
//    public static BaseModel getBaseModel(JsonObject json) {
//        BaseModel baseModel = null;
//        if (json == null) {
//            return baseModel;
//        }
//        try {
//            if (!json.has(Constants.PARAM_MESSAGE)
//                    || !json.has(Constants.PARAM_CODE)
//                    || !json.has(Constants.PARAM_DATA)) {
//                return baseModel;
//            }
//
////            if (!json.get(Constants.PARAM_SUCCESS).getAsBoolean()) {
////                return baseModel;
////            }
//
//            baseModel = new BaseModel();
//            baseModel.setMessage(json.get(Constants.PARAM_MESSAGE).getAsString());
//            baseModel.setCode(json.get(Constants.PARAM_CODE).getAsInt());
//            if (json.get(Constants.PARAM_DATA) == null || json.get(Constants.PARAM_DATA).toString().equals("[]")) {
//                baseModel.setData(null);
//            } else {
////                baseModel.setData(json.get(Constants.PARAM_DATA).getAsJsonObject().toString());
//                baseModel.setData(json.get(Constants.PARAM_DATA).toString());
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            return baseModel;
//        }
//        return baseModel;
//    }

    /**
     * @param baseModel
     * @return
     */
//    public static boolean callApiIsSuccessful(BaseModel baseModel) {
//        if (baseModel.getCode() == Constants.HTTP_SUCCESS_CODE) {
//            return true;
//        }
//        return false;
//    }

    /**
     * @param messageServer
     * @param code
     * @return
     */
    public static String getMessageError(String messageServer, int code) {
        String message = "";
        if (TextUtils.isEmpty(messageServer)) {
            switch (code) {
                case Constants.HTTP_BAD_REQUEST_CODE:
                    message = Constants.HTTP_BAD_REQUEST;
                    break;
                case Constants.HTTP_UNAUTHORIZED_CODE:
                    message = Constants.HTTP_UNAUTHORIZED;
                    break;
                case Constants.HTTP_FORBIDDEN_CODE:
                    message = Constants.HTTP_FORBIDDEN;
                    break;
                case Constants.HTTP_NOT_FOUND_CODE:
                    message = Constants.HTTP_NOT_FOUND;
                    break;
                case Constants.HTTP_INTERNAL_SERVER_ERROR_CODE:
                    message = Constants.HTTP_INTERNAL_SERVER_ERROR;
                    break;
                case Constants.HTTP_SERVICE_UNAVAILABLE_CODE:
                    message = Constants.HTTP_SERVICE_UNAVAILABLE;
                    break;
                case Constants.HTTP_GATEWAY_TIMEOUT_CODE:
                    message = Constants.HTTP_GATEWAY_TIMEOUT;
                    break;
            }

        } else {
            return messageServer;
        }
        return message;
    }

    public static void hideSoftKeyboard(Activity act) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) act.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View v = act.getCurrentFocus();
        if (v != null) {
            inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

    /**
     * @param value
     * @return
     */
    public static String getValue(String value) {
        return value == null || TextUtils.isEmpty(value) ? "" : value;
    }

    /**
     * Convert Date object to string of date in specified format
     *
     * @param date   date object need to convert to string
     * @param format format of string date that want to be converted to
     * @return string of date
     */
    public static String dateToString(Date date, String format) {
        String strDate = "";
        if (date != null) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
            strDate = simpleDateFormat.format(date);
        }
        return strDate;
    }

    /**
     * Convert string of date to Date object in default format {Constants.DEFAULT_DATE_FORMAT}
     *
     * @param strDate string of a date and it must have format as specified by default format
     *                {Constants.DEFAULT_DATE_FORMAT}
     * @return date object
     */
    public static Date stringToDate(String strDate) {
        return stringToDate(strDate, Constants.DATE_FORMAT_DB);
    }

    /**
     * Convert string of date to Date object
     *
     * @param strDate string of a date and it must have format as specified by {@param format}
     * @param format  format of string date that need to convert to date object. if not specified
     *                it will be set as Default format for date and not sure that the same
     *                format with {@param strDate}
     * @return date object
     */
    public static Date stringToDate(String strDate, String format) {
        Date date = null;
        if (!TextUtils.isEmpty(strDate)) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
            try {
                date = simpleDateFormat.parse(strDate);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return date;
    }

    public static ArrayList<String> getSettingPermissions(Context context) {
        ArrayList<String> list = new ArrayList<String>();
        PackageInfo packageInfo = null;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_PERMISSIONS);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packageInfo == null || packageInfo.requestedPermissions == null) return list;

        for (String permission : packageInfo.requestedPermissions) {
            list.add(permission);
        }
        return list;
    }

    /**
     * @param context
     * @param permission
     * @return
     */
    public static boolean hasSelfPermission(Context context, String permission) {
        if (Build.VERSION.SDK_INT < 23) return true;
        return context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * @param email
     * @return
     */
    public static boolean isEmailValid(String email) {
        String regExpn =
                "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                        + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                        + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                        + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$";

        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(regExpn, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);

        if (matcher.matches())
            return true;
        else
            return false;
    }

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    public static String dateFromMillisecond(String format, Long millisecond) {
        return dateToString(new Date(millisecond * 1000), format);
    }


}
