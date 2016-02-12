package ir.medxhub.doctor.message;

import android.app.Activity;
import android.content.Context;
import android.provider.Settings;
import android.util.Log;
import com.google.android.gcm.GCMRegistrar;
import ir.medxhub.doctor.network.AppController;
import ir.medxhub.doctor.network.CustomRequest;
import ir.medxhub.doctor.util.MySharedPreferences;
import ir.medxhub.doctor.util.Tools;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public final class ServerUtilities {
    private static final String TAG = "GCM";

    public static void register(final Context context, final String regId) {
        final MySharedPreferences sp = new MySharedPreferences(context);

        Map<String, String> params = new HashMap<String, String>();
        params.put("reg_id", regId);
        params.put("user_id", sp.getFromPreferences("userid"));
        params.put("device_id", Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID));

        CustomRequest request = new CustomRequest(context, "gcm/register", params, new CustomRequest.ResponseAction() {
            @Override
            public void onResponseAction(JSONObject data) {
                try {
                    if (data.getBoolean("status")) {
                        sp.saveToPreferences("gcm", data.getJSONObject("value").getString("id"));
                        sp.saveToPreferences("gcm_reg_id", regId);
                        GCMRegistrar.setRegisteredOnServer(context, true);
                    } else if (data.getString("msg").equals("already registered")) {
                        sp.saveToPreferences("gcm", data.getJSONObject("value").getString("id"));
                        sp.saveToPreferences("gcm_reg_id", regId);
                    } else {
                        Log.e(TAG, "registering device failed");
                    }
                } catch (JSONException e) {
                    Log.e("Response Error _ " + context.getClass().getSimpleName(), "----" + e.getMessage());
                }
            }
        });

        AppController.getInstance().addToRequestQueue(request);
    }

    static void updateNotificationStatus(final Context context, final String status) {
        final MySharedPreferences sp = new MySharedPreferences(context);
        Map<String, String> params = new HashMap<String, String>();
        params.put("id", sp.getFromPreferences("gcm"));
        params.put("notification_enabled", status);

        CustomRequest request = new CustomRequest((Activity) context, "gcm/register", params, new CustomRequest.ResponseAction() {
            @Override
            public void onResponseAction(JSONObject data) {
                Tools.hideLoadingDialog();
                try {
                    if (data.getBoolean("status")) {
                        if (status.equals("-1")) {
                            GCMRegistrar.setRegisteredOnServer(context, false);
                            sp.saveToPreferences("gcm_notification", status);
                        } else {
                            GCMRegistrar.setRegisteredOnServer(context, true);
                            sp.saveToPreferences("gcm_notification", status);
                        }
                    } else {
                        Log.e(TAG, "updating user notification status failed");
                    }
                } catch (JSONException e) {
                    Log.e("Response Error _ " + context.getClass().getSimpleName(), "----" + e.getMessage());
                }
            }
        });

        AppController.getInstance().addToRequestQueue(request);
    }
}
