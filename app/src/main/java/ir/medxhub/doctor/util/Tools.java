package ir.medxhub.doctor.util;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Vibrator;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.util.Patterns;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.RotateAnimation;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import ir.medxhub.doctor.Globals;
import ir.medxhub.doctor.R;
import ir.medxhub.doctor.network.AppController;
import ir.medxhub.doctor.network.CustomRequest;
import ir.medxhub.doctor.network.DownloadApk;
import ir.medxhub.doctor.util.snack_bar.SnackBar;

/**
 * Created by Ali Arasteh on 9/17/14.
 */

public class Tools {

    private static ProgressDialog pDialog;
    public static boolean exitCalled;
    private static Toast toast;



    public static void setCustomFont(Activity activity , ViewGroup viewGroup) {

        Typeface  typeFace = Typeface.createFromAsset(activity.getAssets(), "fonts/yekan.ttf");

        int childCount = viewGroup.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = viewGroup.getChildAt(i);
            if (child instanceof ViewGroup) {
                setCustomFont(activity,(ViewGroup) child);
                continue;
            }

            if (child instanceof TextView) {
                String tag = (String) child.getTag();
                if (tag != null && tag.equalsIgnoreCase("icon")) {
                    //  ((TextView) child).setTypeface(typeFaceIcon);
                } else {
                    ((TextView) child).setTypeface(typeFace);

                }
            }
        }

    }


    public static Toast makeToast(Activity activity, String text, int duration, int type) {
        LayoutInflater inflater = activity.getLayoutInflater();
        View layout = inflater.inflate(R.layout.toast_layout, (ViewGroup) activity.findViewById(R.id.toast_layout_root));
        TextView tv = (TextView) layout.findViewById(R.id.text);
        tv.setText(text);
        switch (type) {
            case 0:
                tv.setTextColor(activity.getResources().getColor(R.color.dark_gray));
                break;
            case -1:
                tv.setTextColor(activity.getResources().getColor(R.color.error_color));
                break;
            case 1:
                tv.setTextColor(activity.getResources().getColor(R.color.success_color));
                break;
            default:
                break;
        }

        Toast toast = new Toast(activity.getApplicationContext());
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(duration);
        toast.setView(layout);
        toast.show();

        return toast;
    }

    public static void showSnack(Activity activity, String message, short duration, int type) {
        int bgColor;
        switch (type) {
            case 0:
                bgColor = R.color.sb__snack_bkgnd;
                break;
            case -1:
                bgColor = R.color.sb__snack_alert_bkgnd;
                break;
            case 1:
                bgColor = R.color.sb__snack_succes_bkgnd;
                break;
            default:
                bgColor = R.color.sb__snack_bkgnd;
        }
        new SnackBar.Builder(activity)
                .withMessage(message)
                .withBackgroundColorId(bgColor)
                .withDuration(duration)
                .withTypeFace(Typeface.createFromAsset(activity.getAssets(), Globals.appFont))
                .show();
    }

    public static void showLoadingDialog(Context context) {
        showLoadingDialog(context, context.getString(R.string.loading));
    }

    public static void showLoadingDialog(Context context, String text) {
        pDialog = new ProgressDialog(context);
        pDialog.setMessage(text);
        pDialog.setInverseBackgroundForced(false);
        pDialog.show();
        Typeface typeFace = Typeface.createFromAsset(context.getAssets(), Globals.appFont);
        TextView tv = (TextView) pDialog.findViewById(android.R.id.message);
        tv.setTypeface(typeFace);
        tv.setTextColor(context.getResources().getColor(R.color.dark_gray));
    }

    public static void hideLoadingDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }

    public static int getDPI(float size, DisplayMetrics metrics) {
        return (int) (size * metrics.densityDpi) / DisplayMetrics.DENSITY_DEFAULT;
    }

    public static int dpToPx(float dp, Resources resources) {
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.getDisplayMetrics());
        return (int) px;
    }

    public static void vibrate(Context context, int duration) {
        ((Vibrator) context.getSystemService(Activity.VIBRATOR_SERVICE)).vibrate(duration);
    }

    public static String limitString(String input, int limit) {
        return input.length() > limit ? input.substring(0, limit - 3) + "..." : input;
    }

    public static String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time);
        return DateFormat.format("dd-MM-yyyy", cal).toString();
    }

    public static boolean isValidEmail(CharSequence target) {
        return target != null && Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public static boolean isValidPhone(CharSequence target) {
        return target != null && Patterns.PHONE.matcher(target).matches();
    }

    public static void exitApp(Activity activity) {
        if (exitCalled) {
            if (toast != null) toast.cancel();
            (new MySharedPreferences(activity)).saveToPreferences("status", "off");
            activity.finish();
        } else {
            toast = makeToast(activity, activity.getString(R.string.alert_exit), Toast.LENGTH_SHORT, 0);
            exitCalled = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exitCalled = false;
                }
            }, 2000);
        }
    }

    public static void getLastVersion(final Activity activity) {
        try {
            String versionName = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0).versionName;
            int versionCode = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0).versionCode;

            Map<String, String> params = new HashMap<String, String>();
            params.put("version_name", versionName);
            params.put("version_code", "" + versionCode);

            CustomRequest request = new CustomRequest(activity, "update", params, new CustomRequest.ResponseAction() {
                @Override
                public void onResponseAction(JSONObject data) {
                    try {
                        if (data.getBoolean("status")) {
                            JSONObject value = data.getJSONObject("value");
                            switch (value.getInt("state")) {
                                case 0:

                                    new DownloadApk(activity, value.getString("url")).execute();
                                    break;

                                case 1:
                                    Tools.makeToast(activity, activity.getString(R.string.last_version_installed), Toast.LENGTH_SHORT, 0);
                                    break;

                                default:
                                    break;
                            }
                        } else {
                            Tools.makeToast(activity, activity.getString(R.string.error_occured), Toast.LENGTH_SHORT, -1);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            AppController.getInstance().addToRequestQueue(request);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (String aChildren : children) {
                boolean success = deleteDir(new File(dir, aChildren));
                if (!success) {
                    return false;
                }
            }
        }
        return dir != null && dir.delete();
    }
}
