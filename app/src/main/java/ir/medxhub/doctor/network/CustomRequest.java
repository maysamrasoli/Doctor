package ir.medxhub.doctor.network;

import android.app.Activity;
import android.content.Context;
import android.provider.Settings.Secure;
import android.util.Log;
import android.widget.Toast;
import com.android.volley.*;
import com.android.volley.toolbox.StringRequest;
import ir.medxhub.doctor.Globals;
import ir.medxhub.doctor.R;
import ir.medxhub.doctor.util.MySharedPreferences;
import ir.medxhub.doctor.util.Tools;
import org.apache.http.entity.mime.MultipartEntity;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ali Arasteh on 11/22/2014.
 */

public class CustomRequest extends StringRequest {
    private Context context;
    private Map<String, String> params;
    private MultipartEntity entity;


    public CustomRequest(final Context context, final String url, final Map<String, String> params, final ResponseAction responseAction) {
        super(Method.POST, Globals.apiUrl + url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Tools.hideLoadingDialog();
                        Log.d("Volley Response - " + context.getClass().getSimpleName(), "----" + response);
                        try {
                            JSONObject res = new JSONObject(response);

                            //set sid if not already set
                            if (res.has("sid")) {
                                (new MySharedPreferences(context)).saveToPreferences("sid", res.getString("sid"));
                            }

                            // set custom response tasks
                            responseAction.onResponseAction(res);

                        } catch (JSONException e) {
                            Tools.hideLoadingDialog();
                            Log.e("Volley Response - " + context.getClass().getSimpleName(), "----" + e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Tools.hideLoadingDialog();
                        Log.e("Error.Response On Main Server - " + context.getClass().getSimpleName(), "----" + error);
                        responseAction.onErrorAction(error);
                    }
                });
        this.context = context;
        this.params = params;
        this.setRetryPolicy(new DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    public CustomRequest(final Activity activity, final String url, final Map<String, String> params, final ResponseAction responseAction) {
        super(Method.POST, Globals.apiUrl + url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Tools.hideLoadingDialog();
                        Log.d("Volley Response - " + activity.getClass().getSimpleName(), "----" + response);
                        try {
                            JSONObject res = new JSONObject(response);

                            //set sid if not already set
                            if (res.has("sid")) {
                                (new MySharedPreferences(activity)).saveToPreferences("sid", res.getString("sid"));
                            }

                            // set custom response tasks
                            responseAction.onResponseAction(res);

                        } catch (JSONException e) {
                            Tools.hideLoadingDialog();
                            Tools.makeToast(activity, activity.getString(R.string.error_occured), Toast.LENGTH_SHORT, -1);
                            Log.e("Volley Response - " + activity.getClass().getSimpleName(), "----" + e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Error.Response On Main Server - " + activity.getClass().getSimpleName(), "----" + error);
                        Tools.hideLoadingDialog();
                        Tools.makeToast(activity, activity.getString(R.string.error_occured), Toast.LENGTH_SHORT, -1);
                        responseAction.onErrorAction(error);
                    }
                });
        this.context = activity;
        this.params = params;
        this.setRetryPolicy(new DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    public CustomRequest(final Activity activity, final String url, final MultipartEntity multipartEntity, final ResponseAction responseAction) {
        super(Method.POST, Globals.apiUrl + url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Tools.hideLoadingDialog();
                        Log.d("Volley Response - " + activity.getClass().getSimpleName(), "----" + response);
                        try {
                            JSONObject res = new JSONObject(response);

                            //set sid if not already set
                            if (res.has("sid")) {
                                (new MySharedPreferences(activity)).saveToPreferences("sid", res.getString("sid"));
                            }

                            // set custom response tasks
                            responseAction.onResponseAction(res);

                        } catch (JSONException e) {
                            Tools.hideLoadingDialog();
                            Tools.makeToast(activity, activity.getString(R.string.error_occured), Toast.LENGTH_SHORT, -1);
                            Log.e("Volley Response - " + activity.getClass().getSimpleName(), "----" + e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Error.Response On Main Server - " + activity.getClass().getSimpleName(), "----" + error);
                        Tools.hideLoadingDialog();
                        Tools.makeToast(activity, activity.getString(R.string.error_occured), Toast.LENGTH_SHORT, -1);
                        responseAction.onErrorAction(error);
                    }
                });
        this.context = activity;
        this.entity = multipartEntity;
        this.setRetryPolicy(new DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> headers = new HashMap<String, String>();
        MySharedPreferences sp = new MySharedPreferences(context);
        headers.put("Medxhub_SID", sp.getFromPreferences("sid"));
        headers.put("Medxhub_Device", Secure.getString(context.getContentResolver(), Secure.ANDROID_ID));
        return headers;
    }

    @Override
    protected Map<String, String> getParams() {
        return this.params;
    }

    @Override
    public String getBodyContentType() {
        if (entity != null) {
            return entity.getContentType().getValue();
        } else {
            return super.getBodyContentType();
        }
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        if (entity != null) {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            try {
                entity.writeTo(bos);
            } catch (IOException e) {
                VolleyLog.e("IOException writing to ByteArrayOutputStream");
            }
            return bos.toByteArray();
        } else {
            return super.getBody();
        }
    }

    public static abstract class ResponseAction {
        public abstract void onResponseAction(JSONObject data);

        public void onErrorAction(VolleyError error) {
        }
    }
}
