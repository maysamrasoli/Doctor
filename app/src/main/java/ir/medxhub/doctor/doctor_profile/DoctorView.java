package ir.medxhub.doctor.doctor_profile;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;

import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import ir.medxhub.doctor.R;
import ir.medxhub.doctor.network.AppController;
import ir.medxhub.doctor.network.CustomRequest;
import ir.medxhub.doctor.util.ConnectionDetector;
import ir.medxhub.doctor.util.MySharedPreferences;
import ir.medxhub.doctor.util.Tools;
import ir.medxhub.doctor.util.snack_bar.SnackBar;

public class DoctorView extends Fragment {
    private android.view.View rootView;
    private LinearLayout linearLayoutInfo;
    private LayoutInflater inflater;
    private MySharedPreferences sp;
    private ConnectionDetector cd;
    private String uploadGroupId, selectedAttachmentId, selectedAttachmentUrl;
    private LinearLayout currentContainer;
    private android.view.View currentAttachmentView;
    private Profile parent;
    private static String TAG = "DoctorView";
    private InputMethodManager methodManager;


    public DoctorView() {
    }

    @Override
    public android.view.View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.inflater = inflater;
        rootView = inflater.inflate(R.layout.medical_history, container, false);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        sp = new MySharedPreferences(getActivity());
        cd = new ConnectionDetector(getActivity());

        parent = (Profile) getActivity();
        methodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        linearLayoutInfo = (LinearLayout) rootView.findViewById(R.id.LinearLayoutInfo);

        LoadView();
    }

    private void LoadView() {

        if (cd.isConnectingToInternet()) {
            Map<String, String> params = new HashMap<>();
            params.put("doctor_id", parent.doctorId);
            Log.i("This2---->", parent.doctorId);
            CustomRequest customRequest = new CustomRequest(getActivity(), "doctor_show_profile", params, new CustomRequest.ResponseAction() {
                @Override
                public void onResponseAction(JSONObject data) {
                    Tools.hideLoadingDialog();
                    try {
                        if (data.getBoolean("status")) {
                            try {

                                JSONObject Profile = data.getJSONObject("value");
                                JSONArray sub_specialty = Profile.getJSONArray("sub_specialty");

                                for (int i = 0; i < sub_specialty.length(); i++) {
                                    Log.i("jsonName--->", sub_specialty.getJSONObject(i).getString("name"));
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            if (data.has("message"))
                                Tools.showSnack(getActivity(), data.getString("message"), SnackBar.MED_SNACK, -1);
                        }
                    } catch (JSONException e) {
                    }
                }

                @Override
                public void onErrorAction(VolleyError error) {
                    super.onErrorAction(error);
                }
            });
            AppController.getInstance().addToRequestQueue(customRequest, DoctorView.TAG);
        } else {
            Tools.showSnack(getActivity(), getString(R.string.connection_error), SnackBar.MED_SNACK, -1);
        }
    }

}