package ir.medxhub.doctor.doctor_profile;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import ir.medxhub.doctor.MainActivity;
import ir.medxhub.doctor.MyDate;
import ir.medxhub.doctor.R;
import ir.medxhub.doctor.network.AppController;
import ir.medxhub.doctor.network.CustomRequest;
import ir.medxhub.doctor.util.ConnectionDetector;
import ir.medxhub.doctor.util.MySharedPreferences;
import ir.medxhub.doctor.util.Tools;
import ir.medxhub.doctor.util.snack_bar.SnackBar;

public class DoctorHome extends Fragment {
    private static final String TAG = "DoctorView";
    private View rootView;
    private TextView date;
    private LinearLayout linearLayoutInfo;
    private LayoutInflater inflater;
    private Typeface typeface;
    private MySharedPreferences sp;
    private ConnectionDetector cd;
    MyDate md;

    //    public MyDatePicker parent;
    public DoctorHome() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.inflater = inflater;
        rootView = inflater.inflate(R.layout.medical_history, container, false);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.i("Mohammad--->", "This hear");
        super.onActivityCreated(savedInstanceState);
        sp = new MySharedPreferences(getActivity());
        cd = new ConnectionDetector(getActivity());
        String[] PatientInfoTitle;
        int[] PatientInfoIcon = {
                R.mipmap.ic_aboueme,
                R.mipmap.ic_education,
                R.mipmap.ic_graduatecourses,
                R.mipmap.ic_experience,
                R.mipmap.ic_honor,
                R.mipmap.ic_froms,
                R.mipmap.ic_graduatecourses,
                R.mipmap.ic_article,
        };
        PatientInfoTitle = getResources().getStringArray(R.array.DoctorViews);
        linearLayoutInfo = (LinearLayout) rootView.findViewById(R.id.LinearLayoutInfo);

        for (int i = 0; i < 8; i++) {
            View row = inflater.inflate(R.layout.doctor_view_item, linearLayoutInfo, false);
            ImageView icon = (ImageView) row.findViewById(R.id.ImageViewIcon);
            TextView title = (TextView) row.findViewById(R.id.TextViewTitle);
            icon.setImageResource(PatientInfoIcon[i]);
            title.setText(PatientInfoTitle[i]);
            linearLayoutInfo.addView(row);
        }

        try {
            Log.i("Dis------------>", "START");
            loadViewRecords();
        } catch (Exception e) {
            Log.e(TAG, "----" + e.getMessage());
        }
    }

    private void updateAboutMe(final JSONArray array) {
        final View itemView = linearLayoutInfo.getChildAt(0);
        final LinearLayout children = (LinearLayout) itemView.findViewById(R.id.children);
        View row;
        try {
            row = inflater.inflate(R.layout.doctor_profile_info_about_me, null);
            ((TextView) row.findViewById(R.id.aboutMe)).setText(array.getJSONObject(0).getString("about"));
            children.addView(row);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        itemView.findViewById(R.id.parent).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (itemView.findViewById(R.id.children_container).getVisibility() == View.VISIBLE) {
                    itemView.findViewById(R.id.ImageViewArrow).startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.rotate_up));
                    itemView.findViewById(R.id.children_container).setVisibility(View.GONE);
                } else {
                    itemView.findViewById(R.id.ImageViewArrow).startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.rotate_down));
                    itemView.findViewById(R.id.children_container).setVisibility(View.VISIBLE);
                }
            }
        });

        // add new item
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog_medical_records_drug);

        dialog.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    private void updateEducation(final JSONArray array) {
        final View itemView = linearLayoutInfo.getChildAt(1);
        final LinearLayout children = (LinearLayout) itemView.findViewById(R.id.children);

        View header = inflater.inflate(R.layout.doctor_profile_info_education, null);
        header.findViewById(R.id.LinearLayoutParent).setBackgroundColor(getResources().getColor(R.color.gray));
        children.addView(header);

        View row;
        for (int i = 0; i < array.length(); i++) {
            try {
                row = inflater.inflate(R.layout.doctor_profile_info_education, null);
                header.findViewById(R.id.LinearLayoutParent).setBackgroundColor(getResources().getColor(R.color.light_gray));
                ((TextView) row.findViewById(R.id.university)).setText(array.getJSONObject(i).getString("university"));
                ((TextView) row.findViewById(R.id.evidence)).setText(array.getJSONObject(i).getString("grade"));
                ((TextView) row.findViewById(R.id.expert)).setText(array.getJSONObject(i).getString("expert"));
                ((TextView) row.findViewById(R.id.graduate)).setText(array.getJSONObject(i).getString("end_at_year"));
                children.addView(row);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        itemView.findViewById(R.id.parent).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (itemView.findViewById(R.id.children_container).getVisibility() == View.VISIBLE) {
                    itemView.findViewById(R.id.ImageViewArrow).startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.rotate_up));
                    itemView.findViewById(R.id.children_container).setVisibility(View.GONE);
                } else {
                    itemView.findViewById(R.id.ImageViewArrow).startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.rotate_down));
                    itemView.findViewById(R.id.children_container).setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void updateBoards(final JSONArray array) {
        final View itemView = linearLayoutInfo.getChildAt(2);
        final LinearLayout children = (LinearLayout) itemView.findViewById(R.id.children);

        View header = inflater.inflate(R.layout.doctor_profile_info_board, null);
        header.findViewById(R.id.LinearLayoutParent).setBackgroundColor(getResources().getColor(R.color.gray));
        children.addView(header);

        View row;
        for (int i = 0; i < array.length(); i++) {
            try {
                row = inflater.inflate(R.layout.doctor_profile_info_board, null);
                header.findViewById(R.id.LinearLayoutParent).setBackgroundColor(getResources().getColor(R.color.light_gray));
                ((TextView) row.findViewById(R.id.evidence)).setText(array.getJSONObject(i).getString("grade"));
                ((TextView) row.findViewById(R.id.export)).setText(array.getJSONObject(i).getString("forum"));
                ((TextView) row.findViewById(R.id.year)).setText(array.getJSONObject(i).getString("data"));
                children.addView(row);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        itemView.findViewById(R.id.parent).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (itemView.findViewById(R.id.children_container).getVisibility() == View.VISIBLE) {
                    itemView.findViewById(R.id.ImageViewArrow).startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.rotate_up));
                    itemView.findViewById(R.id.children_container).setVisibility(View.GONE);
                } else {
                    itemView.findViewById(R.id.ImageViewArrow).startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.rotate_down));
                    itemView.findViewById(R.id.children_container).setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void updateExperiences(final JSONArray array) {
        final View itemView = linearLayoutInfo.getChildAt(3);
        final LinearLayout children = (LinearLayout) itemView.findViewById(R.id.children);

        View header = inflater.inflate(R.layout.doctor_profile_info_board, null);
        header.findViewById(R.id.LinearLayoutParent).setBackgroundColor(getResources().getColor(R.color.gray));
        children.addView(header);

        View row;
        for (int i = 0; i < array.length(); i++) {
            try {
                row = inflater.inflate(R.layout.doctor_profile_info_experience, null);
                header.findViewById(R.id.LinearLayoutParent).setBackgroundColor(getResources().getColor(R.color.light_gray));
                ((TextView) row.findViewById(R.id.workplace)).setText(array.getJSONObject(i).getString("place"));
                ((TextView) row.findViewById(R.id.startDate)).setText(array.getJSONObject(i).getString("start_date"));
                ((TextView) row.findViewById(R.id.endDate)).setText(array.getJSONObject(i).getString("end_date"));
                children.addView(row);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        itemView.findViewById(R.id.parent).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (itemView.findViewById(R.id.children_container).getVisibility() == View.VISIBLE) {
                    itemView.findViewById(R.id.ImageViewArrow).startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.rotate_up));
                    itemView.findViewById(R.id.children_container).setVisibility(View.GONE);
                } else {
                    itemView.findViewById(R.id.ImageViewArrow).startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.rotate_down));
                    itemView.findViewById(R.id.children_container).setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void updateHonors(final JSONArray array) {
        final View itemView = linearLayoutInfo.getChildAt(4);
        final LinearLayout children = (LinearLayout) itemView.findViewById(R.id.children);

        View header = inflater.inflate(R.layout.doctor_profile_info_honer, null);
        header.findViewById(R.id.LinearLayoutParent).setBackgroundColor(getResources().getColor(R.color.gray));
        children.addView(header);

        View row;
        for (int i = 0; i < array.length(); i++) {
            try {
                row = inflater.inflate(R.layout.doctor_profile_info_honer, null);
                header.findViewById(R.id.LinearLayoutParent).setBackgroundColor(getResources().getColor(R.color.light_gray));
                ((TextView) row.findViewById(R.id.title)).setText(array.getJSONObject(i).getString("name"));
                ((TextView) row.findViewById(R.id.forum)).setText(array.getJSONObject(i).getString("forum"));
                ((TextView) row.findViewById(R.id.date)).setText(array.getJSONObject(i).getString("date"));
                children.addView(row);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        itemView.findViewById(R.id.parent).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (itemView.findViewById(R.id.children_container).getVisibility() == View.VISIBLE) {
                    itemView.findViewById(R.id.ImageViewArrow).startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.rotate_up));
                    itemView.findViewById(R.id.children_container).setVisibility(View.GONE);
                } else {
                    itemView.findViewById(R.id.ImageViewArrow).startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.rotate_down));
                    itemView.findViewById(R.id.children_container).setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void updateForums(final JSONArray array) {
        final View itemView = linearLayoutInfo.getChildAt(5);
        final LinearLayout children = (LinearLayout) itemView.findViewById(R.id.children);

        View header = inflater.inflate(R.layout.doctor_profile_info_forums, null);
        header.findViewById(R.id.LinearLayoutParent).setBackgroundColor(getResources().getColor(R.color.gray));
        children.addView(header);

        View row;
        for (int i = 0; i < array.length(); i++) {
            try {
                row = inflater.inflate(R.layout.doctor_profile_info_forums, null);
                header.findViewById(R.id.LinearLayoutParent).setBackgroundColor(getResources().getColor(R.color.light_gray));
                ((TextView) row.findViewById(R.id.forum)).setText(array.getJSONObject(i).getString("name"));
                ((TextView) row.findViewById(R.id.date)).setText(array.getJSONObject(i).getString("date"));
                children.addView(row);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        itemView.findViewById(R.id.parent).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (itemView.findViewById(R.id.children_container).getVisibility() == View.VISIBLE) {
                    itemView.findViewById(R.id.ImageViewArrow).startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.rotate_up));
                    itemView.findViewById(R.id.children_container).setVisibility(View.GONE);
                } else {
                    itemView.findViewById(R.id.ImageViewArrow).startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.rotate_down));
                    itemView.findViewById(R.id.children_container).setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void updateGraduateCourses(final JSONArray array) {
        final View itemView = linearLayoutInfo.getChildAt(6);
        final LinearLayout children = (LinearLayout) itemView.findViewById(R.id.children);

        View header = inflater.inflate(R.layout.doctor_profile_info_graduate_courses, null);
        header.findViewById(R.id.LinearLayoutParent).setBackgroundColor(getResources().getColor(R.color.gray));
        children.addView(header);

        View row;
        for (int i = 0; i < array.length(); i++) {
            try {
                row = inflater.inflate(R.layout.doctor_profile_info_graduate_courses, null);
                header.findViewById(R.id.LinearLayoutParent).setBackgroundColor(getResources().getColor(R.color.light_gray));
                ((TextView) row.findViewById(R.id.title)).setText(array.getJSONObject(i).getString("name"));
                ((TextView) row.findViewById(R.id.forum)).setText(array.getJSONObject(i).getString("forum"));
                ((TextView) row.findViewById(R.id.date)).setText(array.getJSONObject(i).getString("data"));
                children.addView(row);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        itemView.findViewById(R.id.parent).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (itemView.findViewById(R.id.children_container).getVisibility() == View.VISIBLE) {
                    itemView.findViewById(R.id.ImageViewArrow).startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.rotate_up));
                    itemView.findViewById(R.id.children_container).setVisibility(View.GONE);
                } else {
                    itemView.findViewById(R.id.ImageViewArrow).startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.rotate_down));
                    itemView.findViewById(R.id.children_container).setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void updateArticle(final JSONArray array) {
        final View itemView = linearLayoutInfo.getChildAt(7);
        final LinearLayout children = (LinearLayout) itemView.findViewById(R.id.children);

        View header = inflater.inflate(R.layout.doctor_profile_info_graduate_article, null);
        header.findViewById(R.id.LinearLayoutParent).setBackgroundColor(getResources().getColor(R.color.gray));
        children.addView(header);

        View row;
        for (int i = 0; i < array.length(); i++) {
            try {
                row = inflater.inflate(R.layout.doctor_profile_info_graduate_article, null);
                header.findViewById(R.id.LinearLayoutParent).setBackgroundColor(getResources().getColor(R.color.light_gray));
                ((TextView) row.findViewById(R.id.title)).setText(array.getJSONObject(i).getString("name"));
                ((TextView) row.findViewById(R.id.journal)).setText(array.getJSONObject(i).getString("journal"));
                ((TextView) row.findViewById(R.id.date)).setText(array.getJSONObject(i).getString("date"));
                children.addView(row);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        itemView.findViewById(R.id.parent).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (itemView.findViewById(R.id.children_container).getVisibility() == View.VISIBLE) {
                    itemView.findViewById(R.id.ImageViewArrow).startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.rotate_up));
                    itemView.findViewById(R.id.children_container).setVisibility(View.GONE);
                } else {
                    itemView.findViewById(R.id.ImageViewArrow).startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.rotate_down));
                    itemView.findViewById(R.id.children_container).setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("XXXXXX", "XXXX");
        if (resultCode == Activity.RESULT_OK && requestCode == 100) {
            Log.i("XXXXXX", "XXXXyyyy");
            if (date != null)
                date.setText(data.getIntExtra("year", 0) + "/" + data.getIntExtra("month", 0) + "/" + data.getIntExtra("day", 0));
        }
    }


    private void loadViewRecords() {
        Log.i("this2--->", "Start loading");
        if (cd.isConnectingToInternet()) {
            Map<String, String> params = new HashMap<>();
            Tools.showLoadingDialog(getActivity());
            MainActivity parent = (MainActivity) getActivity();
            params.put("doctor_id", parent.sp.getFromPreferences("id"));
            Log.i("This2---->", parent.sp.getFromPreferences("id"));
            CustomRequest customRequest = new CustomRequest(getActivity(), "doctor_show_profile", params, new CustomRequest.ResponseAction() {
                @Override
                public void onResponseAction(JSONObject data) {
                    try {
                        if (data.getBoolean("status")) {
                            Log.i("get status-------------", "true");
                            try {
                                JSONObject obj = data.getJSONObject("value");
                                updateAboutMe(obj.getJSONArray("doctor_info"));
                                updateEducation(obj.getJSONArray("education"));
                                updateBoards(obj.getJSONArray("boards"));
                                updateExperiences(obj.getJSONArray("experiences"));
                                updateHonors(obj.getJSONArray("honors"));
                                updateForums(obj.getJSONArray("forums"));
                                updateGraduateCourses(obj.getJSONArray("graduate_courses"));
                                updateArticle(obj.getJSONArray("article"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            if (data.has("message"))
                                Tools.showSnack(getActivity(), data.getString("message"), SnackBar.MED_SNACK, -1);
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "----" + e.getMessage());
                    }
                    Tools.hideLoadingDialog();
                }

                @Override
                public void onErrorAction(VolleyError error) {
                    super.onErrorAction(error);
                }
            });
            AppController.getInstance().addToRequestQueue(customRequest, TAG);
            Tools.hideLoadingDialog();
        } else {
            Tools.showSnack(getActivity(), getString(R.string.connection_error), SnackBar.MED_SNACK, -1);
        }
    }
}