package ir.medxhub.doctor;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import ir.medxhub.doctor.network.AppController;
import ir.medxhub.doctor.network.CustomRequest;
import ir.medxhub.doctor.util.ConnectionDetector;
import ir.medxhub.doctor.util.MySharedPreferences;
import ir.medxhub.doctor.util.Tools;
import ir.medxhub.doctor.util.progress.SmoothProgressBar;
import ir.medxhub.doctor.util.snack_bar.SnackBar;
import ir.medxhub.doctor.util.views.CheckBox;
import ir.medxhub.doctor.util.views.RippleView;

/**
 * Created by Alireza Eslamifar on 09/08/2015.
 */
public class Login extends Activity {
    CheckBox chbMemberMe;
    boolean RememberMe = false;
    EditText etUsername;
    EditText etPassword;
    RippleView rvLogin;
    SmoothProgressBar progressBar;
    TextView tvLogin;
    ConnectionDetector cd;
    MySharedPreferences sp;
    private String TAG = "Login";
    static Login signInInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        chbMemberMe = (CheckBox) findViewById(R.id.remember_me);
        etPassword = (EditText) findViewById(R.id.password);
        etUsername = (EditText) findViewById(R.id.username);

        rvLogin = (RippleView) findViewById(R.id.login);
        progressBar = (SmoothProgressBar) findViewById(R.id.progressBar);
        tvLogin = (TextView) findViewById(R.id.tv_login);
        cd = new ConnectionDetector(this);
        sp = new MySharedPreferences(this);

        rvLogin.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
//                startActivity(new Intent(Login.this,GuestMainActivity.class));
//                if (true)return;
//                SnackBar  snackBar = (SnackBar) findViewById(R.id.main_sn);
//                snackBar.applyStyle(R.style.SnackBarSingleLine);
//                snackBar.show();
                if (CheckForNotNull() && CheckForInternet()) {
                    CapsuleLoginBtn();
                }
            }
        });

    }

    @Override
    protected void onResume() {
        etPassword.setText(sp.getFromPreferences("password"));
        etUsername.setText(sp.getFromPreferences("username"));
        super.onResume();
    }

    public void CapsuleLoginBtn() {
        ScaleAnimation anim = new ScaleAnimation(1, 1, 1, 0);
        anim.setDuration(500);
        tvLogin.startAnimation(anim);
        progressBar.setVisibility(View.VISIBLE);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                rvLogin.setEnabled(false);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                tvLogin.setVisibility(View.GONE);
                sp.saveToPreferences("sid", "");
                RememberMe = chbMemberMe.isChecked();
                Map<String, String> params = new HashMap<String, String>();
                params.put("type", "doctor");
                params.put("username", etUsername.getText().toString());
                params.put("password", etPassword.getText().toString());
                CustomRequest customRequest = new CustomRequest(Login.this, "login", params, new CustomRequest.ResponseAction() {
                    @Override
                    public void onResponseAction(JSONObject data) {
                        Log.e("login", String.valueOf(data));
                        try {
                            if (data.getBoolean("status")) {
                                JSONObject value = data.getJSONObject("value");
                                if (RememberMe) {
                                    sp.editor.putString("username", etUsername.getText().toString());
                                    sp.editor.putString("password", etPassword.getText().toString());
                                } else {
                                    sp.editor.putString("username", "");
                                    sp.editor.putString("password", "");
                                }
                                sp.editor.putString("name", value.getString("name"));
                                sp.editor.putString("family", value.getString("family"));
                                sp.editor.putString("gender", value.getString("gender"));
                                sp.editor.putString("id", value.getString("id"));
                                sp.editor.putString("email", value.getString("email"));
                                sp.editor.putString("avatar", value.getString("pic"));
                                sp.editor.putString("specialty", value.getString("specialty"));
                                sp.editor.putString("user_group", value.getString("user_group"));
                                sp.editor.putString("birth_date", value.getString("birth_date"));
//                                sp.editor.putString("code", value.getString("code"));
//                                sp.editor.putString("blood_type", value.getString("blood_type"));
//                                sp.editor.putString("tall", value.getString("tall"));
//                                sp.editor.putString("weight", value.getString("weight"));
//                                sp.editor.putString("emergency_call_number", value.getString("emergency_call_number"));
//                                sp.editor.putString("emergency_call_full_name", value.getString("emergency_call_full_name"));
                                sp.editor.commit();
                                startActivity(new Intent(Login.this, MainActivity.class));
                                Login.this.finish();
                            } else {
                                if (data.has("message"))
                                    Tools.showSnack(Login.this, data.getString("message"), SnackBar.MED_SNACK, -1);
                                ExpendLoginBtn();
                            }
                        } catch (JSONException e) {
                            Log.e("Response Error _ " + Login.this.getClass().getSimpleName(), "----" + e.getMessage());
                            ExpendLoginBtn();
                        }
                    }

                    @Override
                    public void onErrorAction(VolleyError error) {
                        super.onErrorAction(error);
                        ExpendLoginBtn();
                    }
                });
                AppController.getInstance().addToRequestQueue(customRequest, TAG);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public void StartHomeActivity() {
        progressBar.setProgress(100);
        startActivity(new Intent(Login.this, MainActivity.class));
        Login.this.finish();
    }

    public void ExpendLoginBtn() {
        Animation anim = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slide_down);
        tvLogin.setText(getString(R.string.try_again));
        tvLogin.startAnimation(anim);
        progressBar.setVisibility(View.GONE);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                tvLogin.setVisibility(View.VISIBLE);
                rvLogin.setEnabled(true);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public boolean CheckForNotNull() {
        if (etUsername.getText().toString().equals("")) {
            Tools.showSnack(Login.this, getString(R.string.error_username_null), SnackBar.MED_SNACK, -1);
            return false;
        }
        if (etPassword.getText().toString().equals("")) {
            Tools.showSnack(Login.this, getString(R.string.error_password_null), SnackBar.MED_SNACK, -1);
            return false;
        }
        return true;
    }

    public boolean CheckForInternet() {
        if (cd.isConnectingToInternet())
            return true;
        else {
            Tools.showSnack(Login.this, getString(R.string.connection_error), SnackBar.MED_SNACK, -1);
            return false;
        }
    }

    public static Login getInstance() {
        return signInInstance;
    }
}
