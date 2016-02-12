package ir.medxhub.doctor.network;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.widget.Toast;

import java.util.List;
import java.util.Locale;

import ir.medxhub.doctor.R;
import ir.medxhub.doctor.util.Tools;

/**
 * Created by Ali Arasteh on 11/5/2014.
 */
public class Share {
    public static final int viber = 1;
    public static final int whatsApp = 2;
    public static final int telegram = 3;
    public static final int line = 4;
    public static final int hike = 5;

    public static void sendMessage(Activity activity, String message, int sharingApp) {
        boolean found = false;
        String packageName;
        String appName;
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");

        switch (sharingApp) {
            case viber:
                packageName = "com.viber.voip";
                appName = "Viber";
                break;
            case whatsApp:
                packageName = "com.whatsapp";
                appName = "WhatsApp";
                break;

            case telegram:
                packageName = "org.telegram.messenger";
                appName = "Telegram";
                break;

            case line:
                packageName = "jp.naver.line.android";
                appName = "Line";
                break;

            case hike:
                packageName = "com.bsb.hike";
                appName = "Hike";
                break;
            default:
                packageName = "";
                appName = "";
        }

        // gets the list of intents that can be loaded.
        List<ResolveInfo> resInfo = activity.getPackageManager().queryIntentActivities(share, 0);
        if (!resInfo.isEmpty()) {
            for (ResolveInfo info : resInfo) {
                if (info.activityInfo.packageName.toLowerCase(Locale.getDefault()).contains(packageName)
                        || info.activityInfo.name.toLowerCase(Locale.getDefault()).contains(packageName)) {
                    share.putExtra(Intent.EXTRA_TEXT, message);
                    share.setPackage(info.activityInfo.packageName);
                    found = true;
                    activity.startActivity(Intent.createChooser(share, "Select"));
                    break;
                }
            }
            if (!found) {
                Tools.makeToast(activity, String.format(activity.getString(R.string.install_app), appName), Toast.LENGTH_LONG, 0);
                PackageManager manager = activity.getPackageManager();
                Intent market = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName));
                List<ResolveInfo> list = manager.queryIntentActivities(market, 0);
                if (list.size() > 0) {
                    activity.startActivity(market);
                }
            }
        }
    }

}
