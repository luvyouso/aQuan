/**
 * SharedPreference
 * A class insert and update db on app.
 *
 * @author Briswell - Nguyen Van Tu
 * @version 1.0 2016-08-12
 */


package jp.co.asaichi.pubrepo.utils;


import android.content.Context;
import android.content.SharedPreferences;

import jp.co.asaichi.pubrepo.common.Constants;


public class SharedPreference {

    private static SharedPreference mInstance = new SharedPreference();
    private static SharedPreferences mSharedPreferences;
    private static SharedPreferences.Editor mEditor;
    private static Context mContext;


    /**
     * Private constructor, force user to call getInstance
     */
    private SharedPreference() {
    }

    /**
     * The only way to get the only one instance
     *
     * @param context
     * @return
     */
    public static synchronized SharedPreference getInstance(Context context) {
        if (mContext == null) {
            mContext = context;
        }
        if (null == mSharedPreferences) {
            mSharedPreferences = mContext.getSharedPreferences(Constants.SHARED_PREFERENCES_KEY,
                    Context.MODE_PRIVATE);
        }
        return mInstance;
    }

    public static void clearSharedPreferences() {
        if (mSharedPreferences != null) {
            mEditor = mSharedPreferences.edit();
            mEditor.clear();
            mEditor.commit();
        }
        mSharedPreferences = null;
        mContext = null;
    }

    public void removeAllData() {
        if (mSharedPreferences != null) {
            mEditor = mSharedPreferences.edit();
            mEditor.clear();
            mEditor.commit();
        }
    }

    public void saveData(String key, String data) {
        mEditor = mSharedPreferences.edit();
        mEditor.putString(key, data);
        mEditor.commit();
    }

    public String getData(String key) {
        return mSharedPreferences.getString(key, null);
    }

    public void removeData(String key) {
        mEditor = mSharedPreferences.edit();
        mEditor.remove(key);
        mEditor.commit();
    }

}