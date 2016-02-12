package ir.medxhub.doctor;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ir.medxhub.doctor.network.AppController;
import ir.medxhub.doctor.network.CustomRequest;
import ir.medxhub.doctor.util.ConnectionDetector;
import ir.medxhub.doctor.util.DatePicker;
import ir.medxhub.doctor.util.MySharedPreferences;
import ir.medxhub.doctor.util.Tools;
import ir.medxhub.doctor.util.picker.PersianCalendar;
import ir.medxhub.doctor.util.snack_bar.SnackBar;
import ir.medxhub.doctor.util.views.RippleView;

/**
 * Created by Alireza Eslamifar on 15/08/2015.
 */
public class SignUp extends ActionBarActivity {
    MySharedPreferences sp;
    ConnectionDetector cd;
    private String TAG = "Register";
    ArrayList<city> list_province = new ArrayList<>();
    ArrayList<city> list_city = new ArrayList<>();
    EditText name, family, phone, pass, repass, email;
    RadioGroup gender;
    Button birthDay;
    Spinner spinnerProvince;
    Spinner spinnerCity;
    private boolean ProvinceIsSet;
    String SelectedProvince = "";
    String SelectedCity = "";
    private boolean CityIsSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);
        this.overridePendingTransition(R.anim.slide_in_from_right, R.anim.nude);

        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignUp.this.finish();
                overridePendingTransition(R.anim.nude, R.anim.slide_out_to_right);
            }
        });
        ((TextView) findViewById(R.id.action_bar_title)).setText(getString(R.string.signup));

        sp = new MySharedPreferences(this);
        cd = new ConnectionDetector(this);
        ((RippleView) findViewById(R.id.rv_register)).setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                requestForSignUp();
            }
        });

        Typeface typeFace = Typeface.createFromAsset(this.getAssets(), Globals.appFont);
        spinnerProvince = (Spinner) findViewById(R.id.sp_province);
        spinnerCity = (Spinner) findViewById(R.id.sp_city);
        name = (EditText) findViewById(R.id.et_name);
        family = (EditText) findViewById(R.id.et_family);
        pass = (EditText) findViewById(R.id.et_password);
        repass = (EditText) findViewById(R.id.et_rePassword);
        phone = (EditText) findViewById(R.id.et_mobile);
        email = (EditText) findViewById(R.id.et_email);
        birthDay = (Button) findViewById(R.id.btn_birthday);
        gender = (RadioGroup) findViewById(R.id.gender_radio_group);
        ((RadioButton) findViewById(R.id.radio_male)).setTypeface(typeFace);
        ((RadioButton) findViewById(R.id.radio_female)).setTypeface(typeFace);

        spinnerCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != -1 && !SelectedProvince.equals("")) {
                    SelectedCity = list_city.get(position).getId();
                    CityIsSet = true;
                    Log.d("id_c", SelectedCity + "");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinnerProvince.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position != 0) {
                    SelectedProvince = list_province.get(position).getId();
                    ProvinceIsSet = true;
                    SetCityList();
                    Log.d("id_p", SelectedProvince + "");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        final PersianCalendar persianCalendar = new PersianCalendar();
        birthDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUp.this, DatePicker.class);
                intent.putExtra("title", getString(R.string.choose_appointment_date));
                if (birthDay.getText().toString().equals("")) {
                    intent.putExtra("year", persianCalendar.getPersianYear());
                    intent.putExtra("month", persianCalendar.getPersianMonth());
                    intent.putExtra("day", persianCalendar.getPersianDay());
                } else {
                    String[] array = birthDay.getText().toString().split("/");
                    intent.putExtra("year", Integer.parseInt(array[0]));
                    intent.putExtra("month", Integer.parseInt(array[1]));
                    intent.putExtra("day", Integer.parseInt(array[2]));
                }
                startActivityForResult(intent, 100);
            }
        });

        SetProvinceList();
    }

    private void requestForSignUp() {
        if (CheckFields() && CheckForInternet()) {
            Map<String, String> params = new HashMap<>();
            params.put("first_name", name.getText().toString());
            params.put("last_name", family.getText().toString());
            params.put("phone", phone.getText().toString());
            params.put("pass", pass.getText().toString());
            params.put("email", email.getText().toString());
            params.put("gender", gender.getCheckedRadioButtonId() == R.id.radio_male ? "MALE" : "FEMALE");
            params.put("birth_date", birthDay.getText().toString());
            params.put("province", SelectedProvince);
            params.put("city", SelectedCity);
            CustomRequest customRequest = new CustomRequest(SignUp.this, "patient_signup", params, new CustomRequest.ResponseAction() {
                @Override
                public void onResponseAction(JSONObject data) {
                    try {
                        if (data.getBoolean("status")) {
                            sp.saveToPreferences("username", phone.getText().toString());
                            sp.saveToPreferences("password", pass.getText().toString());
                            SignUp.this.finish();
                        } else {
                            if (data.has("message"))
                                Tools.showSnack(SignUp.this, data.getString("message"), SnackBar.MED_SNACK, -1);
                        }
                    } catch (JSONException e) {
                        Log.e("Response Error _ " + SignUp.this.getClass().getSimpleName(), "----" + e.getMessage());
                    }
                }

                @Override
                public void onErrorAction(VolleyError error) {
                    super.onErrorAction(error);
                }
            });
            AppController.getInstance().addToRequestQueue(customRequest, TAG);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == 100) {
            birthDay.setText(data.getIntExtra("year", 0) + "/" + data.getIntExtra("month", 0) + "/" + data.getIntExtra("day", 0));
        }
    }

    public void SetProvinceList() {
        if (!sp.getFromPreferences("province").equals("")) {
            try {
                JSONArray province = new JSONArray(sp.getFromPreferences("province"));
                sp.saveToPreferences("province", province.toString());
                ArrayList<String> strings = new ArrayList<>();
                city city;
                for (int i = 0; i < province.length(); i++) {
                    city = new city();
                    city.setId(province.getJSONObject(i).getString("id"));
                    city.setName(province.getJSONObject(i).getString("name"));
                    list_province.add(city);
                    strings.add(city.getName());
                }
                spinnerProvince.setAdapter(new ArrayAdapter<>(SignUp.this, R.layout.spinner_bg, strings));
                return;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (cd.isConnectingToInternet()) {
            Map<String, String> params = new HashMap<>();
            CustomRequest customRequest = new CustomRequest(SignUp.this, "get_provinces", params, new CustomRequest.ResponseAction() {
                @Override
                public void onResponseAction(JSONObject data) {
                    try {
                        if (data.getBoolean("status")) {
                            JSONArray province = data.getJSONArray("value");
                            sp.saveToPreferences("province", province.toString());
                            ArrayList<String> strings = new ArrayList<>();
                            city city;
                            for (int i = 0; i < province.length(); i++) {
                                city = new city();
                                city.setId(province.getJSONObject(i).getString("id"));
                                city.setName(province.getJSONObject(i).getString("name"));
                                list_province.add(city);
                                strings.add(city.getName());
                            }
                            spinnerProvince.setAdapter(new ArrayAdapter<>(SignUp.this, R.layout.spinner_bg, strings));
                        } else {
                            Tools.showSnack(SignUp.this, data.getString("message"), SnackBar.MED_SNACK, -1);
                        }
                    } catch (JSONException e) {
                        Log.e("Response Error _ " + TAG, "----" + e.getMessage());
                    }
                }

                @Override
                public void onErrorAction(VolleyError error) {
                    super.onErrorAction(error);
                }
            });
            AppController.getInstance().addToRequestQueue(customRequest, TAG);
        } else {
            Tools.showSnack(SignUp.this, getString(R.string.connection_error), SnackBar.MED_SNACK, -1);
        }
    }

    public void SetCityList() {
        if (CheckForInternet()) {
            Map<String, String> params = new HashMap<String, String>();
            params.put("province_id", SelectedProvince);
            CustomRequest customRequest = new CustomRequest(SignUp.this, "get_cities", params, new CustomRequest.ResponseAction() {
                @Override
                public void onResponseAction(JSONObject data) {
                    try {
                        if (data.getBoolean("status")) {
                            JSONArray cities = data.getJSONArray("value");
                            ArrayList<String> strings = new ArrayList<String>();
                            city city;
                            for (int i = 0; i < cities.length(); i++) {
                                city = new city();
                                city.setId(cities.getJSONObject(i).getString("id"));
                                city.setName(cities.getJSONObject(i).getString("name"));
                                list_city.add(city);
                                strings.add(city.getName());
                            }
                            spinnerCity.setAdapter(new ArrayAdapter<>(SignUp.this, R.layout.spinner_bg, strings));

                        } else {
                            Tools.showSnack(SignUp.this, data.getString("message"), SnackBar.MED_SNACK, -1);
                        }
                    } catch (JSONException e) {
                        Log.e("Response Error _ " + TAG, "----" + e.getMessage());
                    }
                }

                @Override
                public void onErrorAction(VolleyError error) {
                    super.onErrorAction(error);
                }
            });
            AppController.getInstance().addToRequestQueue(customRequest, TAG);
        }
    }

    public boolean CheckFields() {
        if (name.getText().toString().equals("")) {
            Tools.showSnack(SignUp.this, getResources().getString(R.string.nameIsNull), SnackBar.MED_SNACK, -1);
            name.requestFocus();
            return false;
        }
        if (family.getText().toString().equals("")) {
            Tools.showSnack(SignUp.this, getResources().getString(R.string.familyIsNull), SnackBar.MED_SNACK, -1);
            family.requestFocus();
            return false;
        }
        if (phone.getText().toString().equals("")) {
            Tools.showSnack(SignUp.this, getResources().getString(R.string.phoneIsNull), SnackBar.MED_SNACK, -1);
            phone.requestFocus();
            return false;
        }
        if (pass.getText().toString().equals("")) {
            Tools.showSnack(SignUp.this, getResources().getString(R.string.passIsNull), SnackBar.MED_SNACK, -1);
            pass.requestFocus();
            return false;
        }
        if (repass.getText().toString().equals("")) {
            Tools.showSnack(SignUp.this, getResources().getString(R.string.repassIsNull), SnackBar.MED_SNACK, -1);
            repass.requestFocus();
            return false;
        }
        if (!pass.getText().toString().equals(repass.getText().toString())) {
            Tools.showSnack(SignUp.this, getResources().getString(R.string.passRepass), SnackBar.MED_SNACK, -1);
            repass.requestFocus();
            return false;
        }
        if (!ProvinceIsSet) {
            Tools.showSnack(SignUp.this, getResources().getString(R.string.selectProvince), SnackBar.MED_SNACK, -1);
            return false;
        }
        if (!CityIsSet) {
            Tools.showSnack(SignUp.this, getResources().getString(R.string.selectCity), SnackBar.MED_SNACK, -1);
            return false;
        }
        if (birthDay.getText().toString().equals("")) {
            Tools.showSnack(SignUp.this, getResources().getString(R.string.insertBirthDay), SnackBar.MED_SNACK, -1);
            return false;
        }
        return true;
    }

    public boolean CheckForInternet() {
        if (cd.isConnectingToInternet())
            return true;
        else {
            Tools.showSnack(SignUp.this, getResources().getString(R.string.connection_error), SnackBar.MED_SNACK, -1);
            return false;
        }
    }

    public class city {
        String name;
        String id;

        city() {
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }

}
