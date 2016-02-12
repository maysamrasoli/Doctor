package ir.medxhub.doctor;

/**
 * Created by Maysam on 09/02/2016.
 */

import android.app.Activity;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;


public class M extends Application {

    public static final String TAG = M.class.getSimpleName();
    public final static String sdcard = Environment.getExternalStorageDirectory().getAbsolutePath();
    public final static String path = sdcard + "/android/data/ir/medxhub/doctor/";
    public static Typeface typeFace;
    public static Typeface typeFaceIcon;
    public static SharedPreferences preference;
    public static Context context;
    public static Activity currentActivity;
    public static LayoutInflater inflater;
    public static ProgressDialog dialog;
    public static Resources resources;
    public static String packageName;
    private static RequestQueue mRequestQueue;
    private static M mInstance;


    @Override
    public void onCreate() {

        super.onCreate();
        context = getApplicationContext();
        typeFace = Typeface.createFromAsset(getAssets(), "fonts/yekan.ttf");

        preference = PreferenceManager.getDefaultSharedPreferences(context);
        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mInstance = this;
        resources = getResources();
        packageName = getPackageName();


    }

    public static synchronized M getInstance() {
        return mInstance;
    }

    //////////////////////////////////////////    custom font set   \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
    public static void setCustomFont(ViewGroup viewGroup) {
        int childCount = viewGroup.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = viewGroup.getChildAt(i);
            if (child instanceof ViewGroup) {
                setCustomFont((ViewGroup) child);
                continue;
            }

            if (child instanceof TextView) {
                String tag = (String) child.getTag();
                if (tag != null && tag.equalsIgnoreCase("icon")) {
                    ((TextView) child).setTypeface(typeFaceIcon);
                } else {
                    ((TextView) child).setTypeface(typeFace);

                }
            }
        }

    }

    //////////////////////////////////////////    custom toast   \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
    public static void customToast(String string) {
        Toast toast = Toast.makeText(M.context, string, Toast.LENGTH_SHORT);
        ViewGroup group = (ViewGroup) toast.getView();
        TextView messageTextView = (TextView) group.getChildAt(0);
        messageTextView.setTextSize(16);
        messageTextView.setTypeface(M.typeFace);
        toast.show();
    }

    //////////////////////////////////////////    custom toast   \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
    public static String timeDifferenceCalculator(String string) {

        long input = Long.parseLong(string);
        long now = System.currentTimeMillis();
        long difference = now - input;
        long sec = difference / 1000;

        if (0 < sec && sec < 3600) {
            long min = sec / 60;
            if (min == 0) {
                return "چند لحضه پیش";
            }
            return min + " دقیقه پیش";
        } else if (3600 < sec && sec < 86400) {
            long hour = sec / 3600;
            return hour + " ساعت پیش";
        } else if (86400 < sec && sec < 2592000) {
            long day = sec / 86400;
            return day + " روز پیش";
        } else if (2592000 < sec && sec < 31100000) {
            long mounth = sec / 604800;
            return mounth + " ماه پیش";
        } else if (31100000 < sec) {
            long year = sec / 31100000;
            return year + " سال پیش";
        }

        return "";

    }

    //////////////////////////////////////////    change persian number to english   \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
    public static String changeFarsiNumberToEng(String faNumber) {
        return faNumber.
                replace("۰", "0").
                replace("۱", "1").
                replace("۲", "2").
                replace("۳", "3").
                replace("۴", "4").
                replace("۵", "5").
                replace("۶", "6").
                replace("۷", "7").
                replace("۸", "8").
                replace("۹", "9");

    }

    //////////////////////////////////////////    volley   \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }


    //////////////////////////////////////////    check for bazar inistaled or not  \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
    public static boolean isAppInstalled() {
        try {
            context.getPackageManager().getApplicationInfo("com.farsitel.bazaar", 0);
            return true;
        }
        catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    //////////////////////////////////////////    check for myket inistaled or not  \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
    public static boolean isMayketInstalled() {
        try {
            context.getPackageManager().getApplicationInfo("ir.mservices.market", 0);
            return true;
        }
        catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }



}
