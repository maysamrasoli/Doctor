package ir.medxhub.doctor.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class MySharedPreferences {
    Context context;
    public SharedPreferences sharedPreferences;
    public SharedPreferences.Editor editor;

    public MySharedPreferences(Context c) {
        this.context = c;
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        this.editor = sharedPreferences.edit();
    }

    public void saveToPreferences(String key, String value) {
        editor.putString(key, value);
        editor.commit();
    }

    public String getFromPreferences(String key) {
        return sharedPreferences.getString(key, "");
    }
}
