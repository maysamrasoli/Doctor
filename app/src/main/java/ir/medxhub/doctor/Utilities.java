package ir.medxhub.doctor;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.view.Display;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import ir.medxhub.doctor.util.ConnectionDetector;
import ir.medxhub.doctor.util.Tools;
import ir.medxhub.doctor.util.snack_bar.SnackBar;

/**
 * Created by Alireza Eslamifar on 10/10/2015.
 */
public class Utilities {
    public static List<String> resourceStringArrayToList(Activity activity, int res) {
        return Arrays.asList(activity.getResources().getStringArray(res));
    }

    public static int getCountItemPerHeight(Activity activity, int itemHeight) {
        return (int) (getScreenSize(activity).y / convertDpToPixel(itemHeight, activity)) + 2;
    }

    public static Point getScreenSize(Activity activity) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        size.x = display.getWidth();
        size.y = display.getHeight();
        return size;
    }

    public static float convertDpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return px;
    }

    public static boolean isConnectingToInternet(Activity activity) {
        if (activity != null) {
            ConnectionDetector cd = new ConnectionDetector(activity);
            if (cd.isConnectingToInternet())
                return true;
            else {
                Tools.showSnack(activity, activity.getResources().getString(R.string.connection_error), SnackBar.MED_SNACK, -1);
                return false;
            }
        }
        return false;

    }

    public static String getCurrentGregorianDate(){
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
//        System.out.println(dateFormat.format(date)); //2014/08/06 15:59:48
        return dateFormat.format(date).substring(0,10);
    }

    public static boolean isDeviceSupportCamera(Activity activity) {
        if (activity.getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }
}
