package ir.medxhub.doctor.advertisement;

/**
 * Created by Maysam on 09/02/2016.
 */

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView.ScaleType;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.daimajia.slider.library.Transformers.AccordionTransformer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ir.medxhub.doctor.M;
import ir.medxhub.doctor.R;


public class ActivityViewAds extends AppCompatActivity implements View.OnClickListener {


    static String postId = "";
    static String img_count = "";
    TextView txtTitle,
            txtContent,
            txtUserName,
            txtMail,
            txtTel,
            txtPrice,
            txtDate,
            txtCity;
    SliderLayout sliderShow;
    LinearLayout lytCall, lytSms, lytMail;
    private ProgressDialog pDialog;

    @Override
    protected void onResume() {
        super.onResume();
        M.currentActivity = this;


    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.lytCall) {
            String num = txtTel.getText().toString();
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + num));
            startActivity(intent);
        } else if (view.getId() == R.id.lytSms) {
            String num = txtTel.getText().toString();
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", num, null)));
        } else if (view.getId() == R.id.lytMail) {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                    "mailto", "abc@gmail.com", null));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject");
            emailIntent.putExtra(Intent.EXTRA_TEXT, "Body");
            startActivity(Intent.createChooser(emailIntent, "Send email..."));
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ads_read_one);


        Bundle bnd = getIntent().getExtras();
        postId = bnd.getString("postId").toString();
        img_count = bnd.getString("img_count").toString();

        txtTitle = (TextView) findViewById(R.id.txtTitle);
        txtPrice = (TextView) findViewById(R.id.txtPrice);
        txtContent = (TextView) findViewById(R.id.txtContent);
        txtUserName = (TextView) findViewById(R.id.txtUserName);
        txtMail = (TextView) findViewById(R.id.txtMail);
        txtTel = (TextView) findViewById(R.id.txtTel);
        txtCity = (TextView) findViewById(R.id.txtCity);
        txtDate = (TextView) findViewById(R.id.txtDate);
        lytCall = (LinearLayout) findViewById(R.id.lytCall);
        lytSms = (LinearLayout) findViewById(R.id.lytSms);
        lytMail = (LinearLayout) findViewById(R.id.lytMail);

        lytCall.setOnClickListener(this);
        lytSms.setOnClickListener(this);
        lytMail.setOnClickListener(this);

        updateJSONdata();
        M.setCustomFont((ViewGroup) (getWindow().getDecorView()));

        String url_1 = "http://adinapp.ir/webservice/uploads/" + postId + "_1.png";
        String url_2 = "http://adinapp.ir/webservice/uploads/" + postId + "_2.png";
        String url_3 = "http://adinapp.ir/webservice/uploads/" + postId + "_3.png";
        String url_4 = "http://adinapp.ir/webservice/uploads/" + postId + "_4.png";

        sliderShow = (SliderLayout) findViewById(R.id.slider);
        sliderShow.setPagerTransformer(false, new AccordionTransformer());
        sliderShow.stopAutoCycle();


        int numberOfImg = Integer.parseInt(img_count);


        if (numberOfImg >= 1) {
            DefaultSliderView textSliderView = new DefaultSliderView(this);
            textSliderView

                    .setScaleType(ScaleType.CenterCrop)
                    .image(url_1);

            sliderShow.addSlider(textSliderView);

        }
        if (numberOfImg >= 2) {

            DefaultSliderView textSliderView = new DefaultSliderView(this);
            textSliderView

                    .setScaleType(ScaleType.CenterCrop)
                    .image(url_2);

            sliderShow.addSlider(textSliderView);
            //start auto sliding for upto 2 image
            sliderShow.startAutoCycle();
        }
        if (numberOfImg >= 3) {

            DefaultSliderView textSliderView = new DefaultSliderView(this);
            textSliderView

                    .setScaleType(ScaleType.CenterCrop)
                    .image(url_3);

            sliderShow.addSlider(textSliderView);
        }
        if (numberOfImg >= 4) {
            DefaultSliderView textSliderView = new DefaultSliderView(this);
            textSliderView

                    .setScaleType(ScaleType.CenterCrop)
                    .image(url_4);

            sliderShow.addSlider(textSliderView);


        }
    }

    public void updateJSONdata() {


        pDialog = new ProgressDialog(ActivityViewAds.this);
        pDialog.setMessage("درحال دریافت اطلاعات ...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        pDialog.show();
        TextView textView = (TextView) pDialog.findViewById(android.R.id.message);
        textView.setTypeface(M.typeFace);
        textView.setTextSize(14);


        String READ_COMMENTS_URL = "http://adinapp.ir/webservice/comments.php?action=one&post=" + postId;

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Method.GET, READ_COMMENTS_URL, null, new Listener<JSONObject>() {


                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray mComments = response.getJSONArray("posts");
                            JSONObject c = mComments.getJSONObject(0);

                            final String title = c.getString("title");
                            final String content = c.getString("content");
                            final String username = c.getString("username");
                            final String mail = c.getString("mail");
                            final String tel = c.getString("tel");
                            final String city = c.getString("city");
                            String price = c.getString("price");

                            if (price.equals("0")) {
                                price = "توافقی";
                            } else {
                                price = price + " تومان ";
                            }
                            String time = M.timeDifferenceCalculator(c.getString("time"));

                            txtTitle.setText("" + title);
                            txtContent.setText("" + content);
                            txtUserName.setText("" + username);
                            txtMail.setText("" + mail);
                            txtTel.setText("" + tel);
                            txtCity.setText("" + city);
                            txtDate.setText("" + time);
                            txtPrice.setText("" + price);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        pDialog.hide();

                    }


                }, new ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });

        M.getInstance().addToRequestQueue(jsObjRequest);

    }


}
