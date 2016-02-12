package ir.medxhub.doctor.advertisement;


/**
 * Created by Maysam on 09/02/2016.
 */

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import ir.medxhub.doctor.M;
import ir.medxhub.doctor.R;
import ir.medxhub.doctor.util.Tools;


public class ActivitySendAds extends AppCompatActivity implements OnClickListener {


    static final String POST_COMMENT_URL = "http://adinapp.ir/webservice/addcomment.php";
    static final String TAG_SUCCESS = "success";
    static final String TAG_MESSAGE = "message";

    EditText edtTitle, edtContent, edtEmail, edtTel, edtPrice;
    Spinner spinType, spinCity;
    Button mSubmit;
    ProgressDialog pDialog, loading;
    LinearLayout lytImageBox;
    Animation in;
    TextView txtCityFilter;
    int selectedImg;
    CheckBox chkNoPrice;

    String strTitle,
            strContent,
            strMail,
            strTel,
            strUser,
            strType,
            strCity,
            strPrice,
            success,
            message,
            imageString1,
            imageString2,
            imageString3,
            imageString4;

    int img_count;


    private ImageView btnAddPic1, btnAddPic2, btnAddPic3, btnAddPic4;
    private Bitmap bitmap;
    private int PICK_IMAGE_REQUEST = 1;


    @Override
    public void onPause() {
        super.onPause();

        if ((pDialog != null) && pDialog.isShowing()) {
            pDialog.dismiss();
            pDialog = null;
        }

        if ((loading != null) && loading.isShowing()) {
            loading.dismiss();
            loading = null;
        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ads_send_ads);
        this.getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        txtCityFilter = (TextView) findViewById(R.id.txtCityFilter);
        edtTitle = (EditText) findViewById(R.id.edtTitle);
        edtContent = (EditText) findViewById(R.id.edtContent);
        edtEmail = (EditText) findViewById(R.id.edtEmail);
        edtPrice = (EditText) findViewById(R.id.edtPrice);
        spinCity = (Spinner) findViewById(R.id.spinCity);
        edtTel = (EditText) findViewById(R.id.edtTel);
        spinType = (Spinner) findViewById(R.id.spinType);
        btnAddPic1 = (ImageView) findViewById(R.id.btnAddPic1);
        chkNoPrice = (CheckBox) findViewById(R.id.chkNoPrice);
        btnAddPic2 = (ImageView) findViewById(R.id.btnAddPic2);
        btnAddPic3 = (ImageView) findViewById(R.id.btnAddPic3);
        btnAddPic4 = (ImageView) findViewById(R.id.btnAddPic4);
        mSubmit = (Button) findViewById(R.id.submit);
        lytImageBox = (LinearLayout) findViewById(R.id.lytImageBox);

        mSubmit.setOnClickListener(this);
        btnAddPic1.setOnClickListener(this);


        in = AnimationUtils.loadAnimation(this, R.anim.fade_in);


        chkNoPrice.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean chesk) {

                if (chesk) {
                    edtPrice.setText("");
                    edtPrice.setEnabled(false);

                } else {

                    edtPrice.setEnabled(true);

                }
            }
        });


        //spinner initialize

        String[] cityes = (getResources().getStringArray(R.array.city));
        ArrayAdapter adapterCity = new ArrayAdapter<String>(M.context,
                android.R.layout.simple_list_item_1, cityes) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);

                ((TextView) v).setTypeface(M.typeFace);
                ((TextView) v).setTextColor(Color.BLACK);

                ((TextView) v).setTextSize(14);

                return v;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View v = super.getDropDownView(position, convertView, parent);

                ((TextView) v).setTypeface(M.typeFace);
                ((TextView) v).setTextColor(Color.BLACK);
                v.setBackgroundColor(Color.WHITE);

                ((TextView) v).setTextSize(14);

                return v;
            }
        };
        spinCity.setAdapter(adapterCity);
        spinType.setAdapter(adapterCity);
        //end spinner initialize

        M.setCustomFont((ViewGroup) (getWindow().getDecorView()));

    }


    @Override
    public void onClick(View v) {


        if (v == btnAddPic1) {
            selectedImg = 1;
            showFileChooser();
        }
        if (v == btnAddPic2) {
            selectedImg = 2;
            showFileChooser();
        }
        if (v == btnAddPic3) {
            selectedImg = 3;
            showFileChooser();
        }
        if (v == btnAddPic4) {
            selectedImg = 4;
            showFileChooser();
        }
        if (v == mSubmit) {

            strTitle = edtTitle.getText().toString();
            strContent = edtContent.getText().toString();
            strMail = edtEmail.getText().toString();
            strTel = edtTel.getText().toString();
            strPrice = edtPrice.getText().toString();
            strCity = (String) spinCity.getSelectedItem();
            strType = (String) spinType.getSelectedItem();

            if (strTitle.length() == 0 && strContent.length() == 0) {
                Tools.makeToast(ActivitySendAds.this, getString(R.string.title_empty), Toast.LENGTH_SHORT, -1);

                return;

            }

            sendComment();
        }
    }


    public void sendComment() {

        pDialog = new ProgressDialog(ActivitySendAds.this);
        pDialog.setMessage("درحال ارسال اطلاعات ...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        pDialog.show();
        TextView textView = (TextView) pDialog.findViewById(android.R.id.message);
        textView.setTypeface(M.typeFace);
        textView.setTextSize(14);

        strUser = M.preference.getString("username", "quest");

        StringRequest stringRequest = new StringRequest(Method.POST, POST_COMMENT_URL,
                new Listener<String>() {
                    @Override
                    public void onResponse(String string) {
                        try {
                            JSONObject array = new JSONObject(string);
                            success = array.getString(TAG_SUCCESS);
                            message = array.getString(TAG_MESSAGE);
                        } catch (JSONException e) {
                            pDialog.hide();
                        }
                        if (success.equals("1")) {
                            M.customToast(message);
                            pDialog.hide();
                            finish();
                        } else {
                            M.customToast(message);
                            pDialog.hide();
                        }
                    }
                },
                new ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        volleyError.getCause();
                    }
                })

        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<String, String>();

                if (imageString1 != null && imageString1.length() > 1) {
                    params.put("image1", imageString1);
                }
                if (imageString2 != null && imageString2.length() > 1) {
                    params.put("image2", imageString2);
                }
                if (imageString3 != null && imageString3.length() > 1) {
                    params.put("image3", imageString3);
                }
                if (imageString4 != null && imageString4.length() > 1) {
                    params.put("image4", imageString4);
                }
                if (strUser != null) {
                    params.put("username", strUser);
                }
                if (strTitle != null) {
                    params.put("title", strTitle);
                }
                if (strContent != null) {
                    params.put("content", strContent);
                }
                if (strMail != null) {
                    params.put("mail", strMail);
                }
                if (strTel != null) {
                    params.put("tel", strTel);
                }
                if (strCity != null) {
                    params.put("city", strCity);
                }
                if (strType != null) {
                    params.put("type", strType);
                }

                if (strPrice != null) {
                    if (chkNoPrice.isChecked()) {
                        strPrice = "0";
                    }

                    params.put("price", strPrice);

                    params.put("img_count", "" + img_count);

                }

                return params;
            }
        };


        //start
        //for repair twice data send in volley
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        //end

        M.getInstance().addToRequestQueue(stringRequest, "");

    }


    public String getStringImage(Bitmap bmp) {
        if (bmp == null) {
            return "";
        }
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bmp.compress(CompressFormat.JPEG, 40, baos);
            byte[] imageBytes = baos.toByteArray();
            String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
            return encodedImage;
        } catch (OutOfMemoryError e) {
            return null;

        }
    }


    private static final String TEMP_PHOTO_FILE = "temporary_holder.jpg";

    private Uri getTempUri() {
        return Uri.fromFile(getTempFile());
    }

    private File getTempFile() {

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {

            File file = new File(Environment.getExternalStorageDirectory(), TEMP_PHOTO_FILE);
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return file;
        } else {

            return null;
        }
    }


    private void showFileChooser() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK,
                Media.EXTERNAL_CONTENT_URI);
        photoPickerIntent.setType("image/*");
        photoPickerIntent.putExtra("crop", "true");
        photoPickerIntent.putExtra(MediaStore.EXTRA_OUTPUT, getTempUri());
        photoPickerIntent.putExtra("outputFormat", CompressFormat.JPEG.toString());
        startActivityForResult(photoPickerIntent, PICK_IMAGE_REQUEST);
    }


    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent imageReturnedIntent) {

        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    if (imageReturnedIntent != null) {

                        File tempFile = getTempFile();

                        String filePath = Environment.getExternalStorageDirectory()
                                + "/" + TEMP_PHOTO_FILE;
                        System.out.println("path " + filePath);

                        bitmap = BitmapFactory.decodeFile(filePath);
                        switch (selectedImg) {

                            case 1:
                                btnAddPic1.setImageBitmap(bitmap);
                                btnAddPic2.setVisibility(View.VISIBLE);
                                imageString1 = getStringImage(bitmap);
                                img_count = 1;
                                btnAddPic2.setImageResource(R.drawable.camera);
                                btnAddPic2.setOnClickListener(this);

                                btnAddPic2.startAnimation(in);
                                break;


                            case 2:
                                btnAddPic2.setImageBitmap(bitmap);
                                btnAddPic3.setVisibility(View.VISIBLE);
                                imageString2 = getStringImage(bitmap);
                                img_count = 2;
                                btnAddPic3.setImageResource(R.drawable.camera);

                                btnAddPic3.setOnClickListener(this);


                                break;


                            case 3:
                                btnAddPic3.setImageBitmap(bitmap);
                                btnAddPic4.setVisibility(View.VISIBLE);
                                imageString3 = getStringImage(bitmap);
                                img_count = 3;
                                btnAddPic4.setImageResource(R.drawable.camera);

                                btnAddPic4.setOnClickListener(this);

                                break;


                            case 4:
                                btnAddPic4.setImageBitmap(bitmap);
                                imageString4 = getStringImage(bitmap);
                                img_count = 4;
                                break;
                        }
                        if (tempFile.exists()) tempFile.delete();
                    }
                }
        }
    }


}
