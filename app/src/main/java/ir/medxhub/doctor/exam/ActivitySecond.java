package ir.medxhub.doctor.exam;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ir.medxhub.doctor.R;
import ir.medxhub.doctor.network.AppController;
import ir.medxhub.doctor.util.Tools;

import static android.view.ViewGroup.LayoutParams.FILL_PARENT;

public class ActivitySecond extends AppCompatActivity {

    ArrayList<String> currentTags = new ArrayList<String>();
    ArrayList<String> selectedTags = new ArrayList<String>();
    LayoutInflater layoutInflater;
    CompoundButton.OnCheckedChangeListener tagListener;
    public static Typeface typeFace;


    static final String POST_COMMENT_URL = "http://adinapp.ir/webservice/addcomment.php";
    static final String TAG_SUCCESS = "success";
    static final String TAG_MESSAGE = "message";
    private static final String TAG = "DoctorView";

    ProgressDialog pDialog;


    EditText edtOption1, edtOption2, edtOption3, edtOption4, current, edtNoOption;
    RadioButton radioOption, radioNoOption;
    LinearLayout lytNoOption, lytOption, lytNext, lytFa, lytEng, lytDot, lytMulty, lytMenu2, lytFaQu, lytEnQu;
    LinearLayout lytTakhasos;
    LinearLayout lytAnatomi;
    LinearLayout lytEnter, lytHelp, lytHazard, lytVirgol;
    LinearLayout lytUndo, lytRedo;
    RadioGroup radioGroup;
    ImageView imgRecycle;
    boolean dotIsActive, isUndo = false, idRedo = false;
    int currentWorkNum;
    String imageString1, imageString2, imageString3,
            imageString4, strUser, strTitle, success, message;
    PopupWindow popUpTag;
    PopupWindow popUpQu;
    ArrayList<saveWorks> Works = new ArrayList<>();


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_second);
        typeFace = Typeface.createFromAsset(getAssets(), "fonts/yekan.ttf");

        layoutInflater = (LayoutInflater) ActivitySecond.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            toolbar.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }
        imgRecycle = (ImageView) toolbar.findViewById(R.id.imgRecycle);
        edtOption1 = (EditText) findViewById(R.id.edtOption1);
        edtOption2 = (EditText) findViewById(R.id.edtOption2);
        edtOption3 = (EditText) findViewById(R.id.edtOption3);
        edtOption4 = (EditText) findViewById(R.id.edtOption4);
        edtNoOption = (EditText) findViewById(R.id.edtNoOption);
        radioOption = (RadioButton) findViewById(R.id.radioOption);
        radioNoOption = (RadioButton) findViewById(R.id.radioNoOption);
        lytOption = (LinearLayout) findViewById(R.id.lytOption);
        lytNext = (LinearLayout) findViewById(R.id.lytNext);
        lytNoOption = (LinearLayout) findViewById(R.id.lytNoOption);
        lytMenu2 = (LinearLayout) findViewById(R.id.lytMenu2);
        lytHelp = (LinearLayout) findViewById(R.id.lytHelp);
        lytRedo = (LinearLayout) findViewById(R.id.lytRedo);
        lytUndo = (LinearLayout) findViewById(R.id.lytUndo);
        lytFa = (LinearLayout) findViewById(R.id.lytFa);
        lytEng = (LinearLayout) findViewById(R.id.lytEng);
        lytDot = (LinearLayout) findViewById(R.id.lytDot);
        lytMulty = (LinearLayout) findViewById(R.id.lytMulty);
        lytEnter = (LinearLayout) findViewById(R.id.lytEnter);
        lytVirgol = (LinearLayout) findViewById(R.id.lytVirgol);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        current = edtOption1;
        radioNoOption.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        radioOption.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        radioOption.setChecked(true);

        Bundle extras = getIntent().getExtras();


        if (extras != null) {

            imageString1 = extras.getString("img1");
            imageString2 = extras.getString("img2");
            imageString3 = extras.getString("img3");
            imageString4 = extras.getString("img4");
            /*
        Log.i("maysam1",imageString1);
        Log.i("maysam2",imageString2);
        Log.i("maysam3",imageString3);
        Log.i("maysam4",imageString4);
        */
        }


        initializeListeners();


        tagListener = new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                CheckBox view = (CheckBox) buttonView;
                String string = view.getText().toString();
                if (isChecked) {
                    selectedTags.add(string);

                } else {
                    if (selectedTags.contains(string)) {
                        selectedTags.remove(string);

                    }

                }


            }
        };

        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {


            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!isUndo && !idRedo) {

                    saveWorks work = new saveWorks();
                    work.option1 = edtOption1.getText().toString();
                    work.option2 = edtOption2.getText().toString();
                    work.option3 = edtOption3.getText().toString();
                    work.option4 = edtOption4.getText().toString();
                    work.noOption = edtNoOption.getText().toString();
                    Works.add(work);
                    currentWorkNum = Works.size();


                }
            }
        };

        edtOption1.addTextChangedListener(watcher);
        edtOption2.addTextChangedListener(watcher);
        edtOption3.addTextChangedListener(watcher);
        edtOption4.addTextChangedListener(watcher);
        edtNoOption.addTextChangedListener(watcher);


        saveWorks workFirst = new saveWorks();
        workFirst.option1 = edtOption1.getText().toString();
        workFirst.option2 = edtOption2.getText().toString();
        workFirst.option3 = edtOption3.getText().toString();
        workFirst.option4 = edtOption4.getText().toString();
        workFirst.noOption = edtNoOption.getText().toString();
        Works.add(workFirst);


        Tools.setCustomFont(ActivitySecond.this, (ViewGroup) getWindow().getDecorView());

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            String newString = " " + matches.get(0);
            String oldString = current.getText().toString();
            if (dotIsActive) {
                current.setText(oldString + newString + ". ");

            } else {
                current.setText(oldString + newString);
            }
            current.setSelection(current.getText().length());

        }

    }


    public void showAlert() {

        if (edtOption1.getText().toString().length() < 1) {
            return;
        }
        new AlertDialog.Builder(ActivitySecond.this)
                .setTitle("اخطار")
                .setMessage("مایل به حذف بحث هستید؟")
                .setPositiveButton("بله", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        edtOption1.setText("");
                        edtOption2.setText("");
                        edtOption3.setText("");
                        edtOption4.setText("");
                        edtNoOption.setText("");
                        Tools.makeToast(ActivitySecond.this, getString(R.string.exam_case_deleted), Toast.LENGTH_SHORT, -1);


                    }
                })
                .setNegativeButton("خیر", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    //initialize listeners
    public void initializeListeners() {

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {


                if (checkedId == R.id.radioOption) {

                    lytOption.setVisibility(View.VISIBLE);
                    lytNoOption.setVisibility(View.GONE);
                    lytHelp.setVisibility(View.VISIBLE);
                    edtOption1.requestFocus();
                    current = edtOption1;

                } else if (checkedId == R.id.radioNoOption) {
                    lytOption.setVisibility(View.GONE);
                    lytNoOption.setVisibility(View.VISIBLE);
                    lytHelp.setVisibility(View.GONE);
                    edtNoOption.requestFocus();
                    current = edtNoOption;

                }

            }
        });


        edtOption1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                current = edtOption1;
                return false;
            }
        });
        edtOption2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                current = edtOption2;
                return false;
            }
        });
        edtOption3.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                current = edtOption3;
                return false;
            }
        });
        edtOption4.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                current = edtOption4;
                return false;
            }
        });
        edtNoOption.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                current = edtNoOption;


                return false;
            }
        });


        lytDot.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (dotIsActive) {
                    dotIsActive = false;
                    lytDot.setBackgroundColor(Color.parseColor("#ffffff"));
                } else {
                    dotIsActive = true;
                    lytDot.setBackgroundColor(Color.parseColor("#bbbbbb"));
                }
                return false;
            }
        });


        lytRedo.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int size = Works.size();
                if (size >= 1 && currentWorkNum >= 0 && currentWorkNum <= size - 1) {
                    currentWorkNum++;
                    idRedo = true;

                    saveWorks work = Works.get(currentWorkNum - 1);
                    edtOption1.setText(work.option1);
                    edtOption2.setText(work.option2);
                    edtOption3.setText(work.option3);
                    edtOption4.setText(work.option4);
                    edtNoOption.setText(work.noOption);
                    current.setSelection(current.getText().length());
                    Log.i("maysam", "injaaaaaa ");


                    idRedo = false;
                }
                // Works.size();
                return false;
            }
        });

        lytUndo.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int size = Works.size();


                if (size >= 1 && currentWorkNum >= 1 && currentWorkNum <= size) {
                    currentWorkNum--;
                    isUndo = true;

                    try {
                        saveWorks work = Works.get(currentWorkNum - 1);
                        edtOption1.setText(work.option1);
                        edtOption2.setText(work.option2);
                        edtOption3.setText(work.option3);
                        edtOption4.setText(work.option4);
                        edtNoOption.setText(work.noOption);
                        current.setSelection(current.getText().length());


                    } catch (IndexOutOfBoundsException w) {

                        isUndo = false;


                    }
                    isUndo = false;

                }


                return false;
            }
        });


        lytEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldString = current.getText().toString();
                current.setText(oldString + "\n");
                current.setSelection(current.getText().length());

            }
        });


        lytMulty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int currentRowId, position = 0;
                int[] location = new int[2];
                currentRowId = position;
                View currentRow = view;
                view.getLocationOnScreen(location);
                Point point = new Point();
                point.x = location[0];
                point.y = location[1];
                ShowPopUpQu(ActivitySecond.this, point, 3);


            }
        });


        lytVirgol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldString = current.getText().toString();
                current.setText(oldString + ",");
                current.setSelection(current.getText().length());

            }
        });


        lytFa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getClass().getPackage().getName());
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "fa-IR");
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "صحبت کنید ...");
                startActivityForResult(intent, 1);


            }
        });


        lytEng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speack ...");
                startActivityForResult(intent, 1);

            }
        });

        lytNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                int currentRowId, position = 0;
                int[] location = new int[2];
                currentRowId = position;
                View currentRow = view;
                view.getLocationOnScreen(location);
                Point point = new Point();
                point.x = location[0];
                point.y = location[1];
                ShowPopUpTag(ActivitySecond.this, point, 3);

            }
        });

        imgRecycle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAlert();


            }
        });


    }


    private void ShowPopUpQu(final Activity context, Point p, int num) {


        View layout = layoutInflater.inflate(R.layout.exam_pop_up_qu, null);

        lytHazard = (LinearLayout) layout.findViewById(R.id.lytHazard);
        lytFaQu = (LinearLayout) layout.findViewById(R.id.lytFaQu);
        lytEnQu = (LinearLayout) layout.findViewById(R.id.lytEngQu);


        lytEnQu.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                String oldString = current.getText().toString();
                current.setText(oldString + "؟");
                current.setSelection(current.getText().length());

                return false;
            }
        });

        lytFaQu.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                String oldString = current.getText().toString();
                current.setText(oldString + "?");
                current.setSelection(current.getText().length());

                return false;
            }
        });

        lytHazard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldString = current.getText().toString();
                current.setText(oldString + "!");
                current.setSelection(current.getText().length());

            }
        });
        PopupWindow changeStatusPopUp = new PopupWindow(context);
        changeStatusPopUp.setContentView(layout);

        changeStatusPopUp.setWidth(LinearLayout.LayoutParams.WRAP_CONTENT);
        changeStatusPopUp.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        changeStatusPopUp.setFocusable(true);

        changeStatusPopUp.showAtLocation(layout, Gravity.NO_GRAVITY, p.x, p.y);

    }


    @TargetApi(Build.VERSION_CODES.M)
    private void ShowPopUpTag(final Activity context, Point p, int num) {


        final LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = layoutInflater.inflate(R.layout.exam_pop_up_tag, null);
        final LinearLayout lyt = (LinearLayout) layout.findViewById(R.id.lyt);
        lytTakhasos = (LinearLayout) layout.findViewById(R.id.lytTakhasos);
        lytAnatomi = (LinearLayout) layout.findViewById(R.id.lytAnatomi);
        LinearLayout lytSubmit = (LinearLayout) layout.findViewById(R.id.lytSubmit);
        LinearLayout lytCansel = (LinearLayout) layout.findViewById(R.id.lytCansel);
        lytAnatomi.setBackgroundColor(getColor(R.color.tabColorDark));


        String[] anatomy = getResources().getStringArray(R.array.anatomy);

        for (String anAnatomy : anatomy) {
            if (!currentTags.contains(anAnatomy)) {

                View item = layoutInflater.inflate(R.layout.exam_tag_item, null);
                CheckBox checkBox = (CheckBox) item.findViewById(R.id.checkBox);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    checkBox.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                }
                if (anAnatomy.contains("@")) {
                    TextView textView = new TextView(ActivitySecond.this);
                    textView.setText(anAnatomy.replace("@", ""));

                    textView.setBackgroundColor(getColor(R.color.colorPrimary));
                    textView.setTextColor(Color.parseColor("#ffffff"));
                    textView.setWidth(FILL_PARENT);
                    textView.setHeight(96);
                    textView.setGravity(Gravity.CENTER);
                    lyt.addView(textView);

                } else {
                    checkBox.setText(anAnatomy);
                    lyt.addView(item);
                }


            }


        }

        lytCansel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popUpTag.dismiss();
            }
        });

        lytSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sendComment();
            }
        });


        lytAnatomi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lyt.removeAllViews();


                lytAnatomi.setBackgroundColor(getColor(R.color.tabColorDark));
                lytTakhasos.setBackgroundColor(getColor(R.color.tabColor));

                String[] anatomy = getResources().getStringArray(R.array.anatomy);

                for (String anAnatomy : anatomy) {
                    if (!currentTags.contains(anAnatomy)) {


                        View item = layoutInflater.inflate(R.layout.exam_tag_item, null);
                        CheckBox checkBox = (CheckBox) item.findViewById(R.id.checkBox);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                            checkBox.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                        }
                        if (anAnatomy.contains("@")) {
                            TextView textView = new TextView(ActivitySecond.this);
                            textView.setText(anAnatomy.replace("@", ""));

                            textView.setBackgroundColor(getColor(R.color.colorPrimary));
                            textView.setTextColor(Color.parseColor("#ffffff"));
                            textView.setWidth(FILL_PARENT);
                            textView.setHeight(96);
                            textView.setGravity(Gravity.CENTER);
                            lyt.addView(textView);

                        } else {
                            checkBox.setText(anAnatomy);
                            lyt.addView(item);
                        }


                    }


                }


            }
        });


        lytTakhasos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                lyt.removeAllViews();


                lytAnatomi.setBackgroundColor(getColor(R.color.tabColor));
                lytTakhasos.setBackgroundColor(getColor(R.color.tabColorDark));

                String[] speciality = getResources().getStringArray(R.array.speciality);

                for (String anSpeciality : speciality) {
                    if (!currentTags.contains(anSpeciality)) {


                        View item = layoutInflater.inflate(R.layout.exam_tag_item, null);
                        CheckBox checkBox = (CheckBox) item.findViewById(R.id.checkBox);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                            checkBox.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                        }


                        checkBox.setText(anSpeciality);
                        checkBox.setOnCheckedChangeListener(tagListener);
                        lyt.addView(item);


                    }


                }


            }
        });



        popUpTag = new PopupWindow(context);
        popUpTag.setContentView(layout);
        popUpTag.setWidth(LinearLayout.LayoutParams.FILL_PARENT);
        popUpTag.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        popUpTag.setFocusable(true);
       // popUpTag.setBackgroundDrawable(new BitmapDrawable());
        popUpTag.showAtLocation(layout, Gravity.CENTER, 0, 0);

    }


    public void sendComment() {
        pDialog = new ProgressDialog(ActivitySecond.this);
        pDialog.setMessage("درحال ارسال اطلاعات ...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        pDialog.show();
        TextView textView = (TextView) pDialog.findViewById(android.R.id.message);
        textView.setTypeface(typeFace);
        textView.setTextSize(14);


        StringRequest stringRequest = new StringRequest(Request.Method.POST, POST_COMMENT_URL,
                new Response.Listener<String>() {
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
                            Tools.makeToast(ActivitySecond.this, message, Toast.LENGTH_SHORT, -1);


                            pDialog.hide();
                            finish();


                        } else {
                            Tools.makeToast(ActivitySecond.this, message, Toast.LENGTH_SHORT, -1);
                            pDialog.hide();

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        volleyError.getCause();
                    }
                }) {
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

                if (edtNoOption.getText() != null) {
                    params.put("title", edtNoOption.getText().toString());
                }

                if (edtOption1.getText() != null) {
                    params.put("title", edtOption1.getText().toString());
                }

                if (edtOption2.getText() != null) {
                    params.put("title", edtOption2.getText().toString());
                }

                if (edtOption3.getText() != null) {
                    params.put("title", edtOption3.getText().toString());
                }

                if (edtOption4.getText() != null) {
                    params.put("title", edtOption4.getText().toString());
                }


                if (selectedTags != null) {
                    for (int i = 1; i <= selectedTags.size(); i++) {
                        params.put("tag" + i, strTitle);
                    }

                }

                return params;
            }
        };
        //for repair twice data send in volley
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        AppController.getInstance().addToRequestQueue(stringRequest, TAG);

    }


    public class saveWorks {

        String option1;
        String option2;
        String option3;
        String option4;
        String noOption;
    }

}
