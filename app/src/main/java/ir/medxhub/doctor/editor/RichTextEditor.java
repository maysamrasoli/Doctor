/*
 * Copyright (C) 2015 Emanuel Moecklin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ir.medxhub.doctor.editor;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.speech.RecognizerIntent;

import android.text.Editable;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.onegravity.rteditor.RTEditText;
import com.onegravity.rteditor.RTManager;
import com.onegravity.rteditor.RTToolbar;
import com.onegravity.rteditor.api.RTApi;
import com.onegravity.rteditor.api.RTMediaFactoryImpl;
import com.onegravity.rteditor.api.RTProxyImpl;
import com.onegravity.rteditor.api.format.RTFormat;
import com.onegravity.rteditor.toolbar.RTToolbarImageButton;
import com.onegravity.rteditor.utils.Selection;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import android.support.v7.app.AppCompatActivity;

import ir.medxhub.doctor.M;
import ir.medxhub.doctor.R;


public class RichTextEditor extends AppCompatActivity implements View.OnClickListener {

    ArrayList<String> StringImages = new ArrayList<>();
    public static RTManager mRTManager;
    public static EditText edtTitle,
            edtTag;
    public static RTEditText edtContent;
    View.OnClickListener loadItemListener;


    View popUpLayout;
    LinearLayout viewGroup;
    PopupWindow changeStatusPopUp;
    RTToolbarImageButton voiceFa, voiceEn, toolbar_dot, toolbar_faqu, toolbar_enqu, toolbar_hazard, enter, btnQu, toolbar_coma, toolbar_img, btnNum, btnAlign;
    RTToolbar rtToolbar1, rtToolbar2, rtToolbar0;
    ViewGroup toolbarContainer;
    Spinner toolbar_fontsize;
    int currentRowId, position, lastSelectedArticle;

    boolean isDot;

    public final static String sdcard = Environment.getExternalStorageDirectory().getAbsolutePath();
    public final static String path = sdcard + "/test12345/";
    public static SQLiteDatabase database;

    ArrayList<LoadStructures> SavedArticals = new ArrayList<>();





    @Override
    protected void onResume() {
        super.onResume();
        hideLyts();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.editor_rte_demo_2);
        new File(path).mkdirs();
        btnNum = (RTToolbarImageButton) findViewById(R.id.btnNum);
        btnAlign = (RTToolbarImageButton) findViewById(R.id.btnAlign);
        toolbar_fontsize = (Spinner) findViewById(R.id.toolbar_fontsize);

        voiceFa = (RTToolbarImageButton) findViewById(R.id.voiceFa);
        voiceEn = (RTToolbarImageButton) findViewById(R.id.voiceEn);
        toolbar_dot = (RTToolbarImageButton) findViewById(R.id.toolbar_dot);
        enter = (RTToolbarImageButton) findViewById(R.id.enter);
        btnQu = (RTToolbarImageButton) findViewById(R.id.btnQu);
        toolbar_img = (RTToolbarImageButton) findViewById(R.id.toolbar_img);
        toolbar_coma = (RTToolbarImageButton) findViewById(R.id.toolbar_coma);

        edtTitle = (EditText) findViewById(R.id.edtTitle);
        edtTag = (EditText) findViewById(R.id.edtTag);

        // register message editor
        edtContent = (RTEditText) findViewById(R.id.edtContent);

        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolb);
        setSupportActionBar(toolbar);


        btnNum.setOnClickListener(this);
        btnAlign.setOnClickListener(this);
        toolbar_img.setOnClickListener(this);
        voiceFa.setOnClickListener(this);
        voiceEn.setOnClickListener(this);
        btnQu.setOnClickListener(this);

        enter.setOnClickListener(this);

        toolbar_coma.setOnClickListener(this);
        toolbar_dot.setOnClickListener(this);


        View.OnTouchListener listener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                hideLyts();
                return false;
            }
        };

        edtTitle.setOnTouchListener(listener);
        edtTag.setOnTouchListener(listener);
        edtContent.setOnTouchListener(listener);


        // initialize rich text manager
        RTApi rtApi = new RTApi(this, new RTProxyImpl(this), new RTMediaFactoryImpl(this, true));
        mRTManager = new RTManager(rtApi, savedInstanceState);
        toolbarContainer = (ViewGroup) findViewById(R.id.rte_toolbar_container);


        // register toolbar 0 (if it exists)
        rtToolbar0 = (RTToolbar) findViewById(R.id.rte_toolbar);
        if (rtToolbar0 != null) {
            mRTManager.registerToolbar(toolbarContainer, rtToolbar0);
        }

        // register toolbar 1 (if it exists)
        rtToolbar1 = (RTToolbar) findViewById(R.id.rte_toolbar_character);
        if (rtToolbar1 != null) {
            mRTManager.registerToolbar(toolbarContainer, rtToolbar1);
        }

        // register toolbar 2 (if it exists)
        rtToolbar2 = (RTToolbar) findViewById(R.id.rte_toolbar_paragraph);
        if (rtToolbar2 != null) {
            mRTManager.registerToolbar(toolbarContainer, rtToolbar2);
        }


        mRTManager.registerEditor(edtContent, true);
        //edtContent.setRichTextEditing(true, "");
        edtContent.requestFocus();


        //create local data base for save and load articles
        /*
        database = SQLiteDatabase.openOrCreateDatabase(path + "db.sqlite", null);
        database.execSQL("CREATE  TABLE  IF NOT EXISTS article (id INTEGER PRIMARY KEY  NOT NULL  UNIQUE ," +
                " title VARCHAR, content TEXT,tag TEXT, created_time DATETIME)");
*/

        //saved article list listener
        loadItemListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeStatusPopUp.dismiss();
                TextView textView = (TextView) view.findViewById(R.id.txtId);
                int Id = Integer.parseInt(textView.getText().toString());
                lastSelectedArticle = Id;
                loadArticle(Id);

            }
        };

        M.setCustomFont((ViewGroup) getWindow().getDecorView());
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mRTManager != null) {
            mRTManager.onDestroy(true);
        }
    }


    public String getStringImage(Bitmap bmp) {
        if (bmp == null) {
            return "";

        }
        try {


            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 50, baos);
            byte[] imageBytes = baos.toByteArray();
            String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
            return encodedImage;
        } catch (OutOfMemoryError e) {
            return null;

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.editor_main_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.load) {
            View view = this.getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
            SavedArticals.clear();
            Cursor cursor = database.rawQuery("SELECT * FROM  article ORDER BY id ", null);
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                String title = cursor.getString(cursor.getColumnIndex("title"));
                String content = cursor.getString(cursor.getColumnIndex("content"));
                String data = cursor.getString(cursor.getColumnIndex("created_time"));
                //  String tags = cursor.getString(cursor.getColumnIndex("tag"));
                LoadStructures struct = new LoadStructures();
                struct.id = id;
                struct.title = title;
                struct.data = data;
                SavedArticals.add(struct);
            }
            cursor.close();


            int[] location = new int[2];
            currentRowId = position;
            Point point = new Point();
            point.x = location[0];
            point.y = location[1];
            showLoadPopup(RichTextEditor.this, point);


        } else if (itemId == R.id.save) {

            saveArtical();

        } else if (itemId == R.id.send) {

            String title = edtTitle.getText().toString();
            String tag = edtTag.getText().toString();
            String content = edtContent.getText(RTFormat.HTML);

            if (title.length() < 2 || tag.length() < 2 || content.length() < 2) {
                M.customToast("پر کردن تمامی فیلد ها اجباریست.");
            } else {

                sendArticle();

            }
        }

        return false;
    }





    private void ShowPopUpQu(final Activity context, Point p, int num) {

        // Inflate the popup_layout.xml

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        RTToolbar rtToolbar4 = null;
        View layout = null;

        int OFFSET_X = 0;
        int OFFSET_Y = 20;

        switch (num) {
            case 1:


                layout = layoutInflater.inflate(R.layout.editor_pop_up_qu, null);
                toolbar_faqu = (RTToolbarImageButton) layout.findViewById(R.id.toolbar_faqu);
                toolbar_enqu = (RTToolbarImageButton) layout.findViewById(R.id.toolbar_enqu);
                toolbar_hazard = (RTToolbarImageButton) layout.findViewById(R.id.toolbar_hazard);
                toolbar_faqu.setOnClickListener(this);
                toolbar_enqu.setOnClickListener(this);
                toolbar_hazard.setOnClickListener(this);

                OFFSET_Y = 0;
                break;
            case 2:
                layout = layoutInflater.inflate(R.layout.editor_pop_up_align, null);
                //  OFFSET_Y = -180;
                break;
            case 3:
                layout = layoutInflater.inflate(R.layout.editor_pop_up_num, null);
                //  OFFSET_Y = -100;
                break;
            case 4:
                layout = layoutInflater.inflate(R.layout.editor_pop_up_img, null);
                //   OFFSET_Y = -100;
                break;
        }

        rtToolbar4 = (RTToolbar) layout.findViewById(R.id.rtToolbar4);
        mRTManager.registerToolbar(toolbarContainer, rtToolbar4);


        // Creating the PopupWindow
        changeStatusPopUp = new PopupWindow(context);
        changeStatusPopUp.setContentView(layout);

        changeStatusPopUp.setWidth(LinearLayout.LayoutParams.WRAP_CONTENT);
        changeStatusPopUp.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        changeStatusPopUp.setFocusable(true);


        // Some offset to align the popup a bit to the left, and a bit down, relative to button's position.

        //Clear the default translucent background
        changeStatusPopUp.setBackgroundDrawable(new BitmapDrawable());


        int h = layout.getMeasuredHeight();
        // Displaying the popup at the specified location, + offsets.
        changeStatusPopUp.showAtLocation(layout, Gravity.NO_GRAVITY, p.x, p.y);

    }





    private void showLoadPopup(final Activity context, Point p) {

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int height = displaymetrics.heightPixels;
        int width = displaymetrics.widthPixels;

        // Inflate the popup_layout.xml
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        popUpLayout = layoutInflater.inflate(R.layout.editor_filter_popup, null);
        viewGroup = (LinearLayout) popUpLayout.findViewById(R.id.llSortChangePopup);
        TextView txtTitle = (TextView) popUpLayout.findViewById(R.id.txtTitle);
        txtTitle.setTypeface(M.typeFace);


        for (LoadStructures load : SavedArticals) {
            View item = layoutInflater.inflate(R.layout.editor_filter_item, null);
            TextView title = (TextView) item.findViewById(R.id.txtTitle);
            TextView data = (TextView) item.findViewById(R.id.txtData);
            TextView id = (TextView) item.findViewById(R.id.txtId);

            title.setText(load.title);
            data.setText(load.data);
            id.setText("" + load.id);
            item.setOnClickListener(loadItemListener);
            title.setTypeface(M.typeFace);

            viewGroup.addView(item);
        }

        // Creating the PopupWindow
        changeStatusPopUp = new PopupWindow(context);
        changeStatusPopUp.setContentView(popUpLayout);
        changeStatusPopUp.setWidth(width + 10);
        changeStatusPopUp.setHeight(height + 10);
        changeStatusPopUp.setFocusable(true);

        // Some offset to align the popup a bit to the left, and a bit down, relative to button's position.
        int OFFSET_X = -20;
        int OFFSET_Y = 50;

        //Clear the default translucent background
        //  changeStatusPopUp.setBackgroundDrawable(new BitmapDrawable());

        // Displaying the popup at the specified location, + offsets.
        // changeStatusPopUp.showAtLocation(layout, Gravity.CENTER, p.x + OFFSET_X, p.y + OFFSET_Y);
        changeStatusPopUp.showAtLocation(popUpLayout, Gravity.CENTER, 0, 0);

    }


    public static void writeToDB(String title, String content, String tag, String date) {


        String query = "INSERT INTO article(title,content,tag,created_time) " +
                "VALUES ('" + title + "','" + content + "','" + tag + "','" + date + "')";
        database.execSQL(query);
        M.customToast("مقاله با موفقیت ذخیره شد.");


    }

    public static void updateDB(String title, String content, String tag, int id) {

        String query = "UPDATE article SET title = '" + title + "', content = '" + content + "', tag = '" + tag + "' WHERE  id = " + id;
        database.execSQL(query);
        M.customToast("مقاله با موفقیت آپدیت شد.");


    }


    public static void loadArticle(int id) {

        Cursor cursor = database.rawQuery("SELECT * FROM  article WHERE id = " + id + " ", null);
        while (cursor.moveToNext()) {
            String title = cursor.getString(cursor.getColumnIndex("title"));
            String content = cursor.getString(cursor.getColumnIndex("content"));
            String data = cursor.getString(cursor.getColumnIndex("created_time"));
            String tag = cursor.getString(cursor.getColumnIndex("tag"));

            edtTitle.setText(title);
            edtTag.setText(tag);
            edtContent.setRichTextEditing(true, content);
        }
        cursor.close();


    }


    public void sendArticle() {

        final ProgressDialog pDialog = new ProgressDialog(RichTextEditor.this);
        pDialog.setMessage("درحال ارسال اطلاعات ...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        pDialog.show();
        TextView textView = (TextView) pDialog.findViewById(android.R.id.message);
        textView.setTypeface(M.typeFace);
        textView.setTextSize(14);


        String POST_COMMENT_URL = "";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, POST_COMMENT_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String string) {
                        try {
                            JSONObject array = new JSONObject(string);
                            //success = array.getString("success");
                        } catch (JSONException e) {
                            pDialog.hide();

                        }

/*
                            if (success.equals("1")) {
                                G.customToast(message);
                                pDialog.hide();
                                finish();


                            } else {
                                G.customToast(message);
                                pDialog.hide();

                            }

                            */

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        volleyError.getCause();

                    }
                })


        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<String, String>();


                String title = edtTitle.getText().toString();
                String content = edtContent.getText().toString();
                String tag = edtTag.getText().toString();

                params.put("title", title);
                params.put("content", content);
                params.put("tag", tag);

                int i = 1;
                for (String img : StringImages) {
                    params.put("image" + i, img);
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


    class LoadStructures {
        String title;
        String data;
        int id;

    }

    public void getStringImage(String string) {

        StringImages.add(string);

    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

/*
        try {

            if (mRTManager != null && mRTManager.onActivityResult(requestCode, resultCode, data)) {
                RTImage image = (RTImage) data.getSerializableExtra(Constants.RESULT_MEDIA);
                Bitmap bitmap = BitmapFactory.decodeFile(image.getFilePath(null));
                String stringImage = getStringImage(bitmap);
                StringImages.add(stringImage);
                Log.i("maysam" , stringImage);
                Log.i("maysam" , "image");
                return;
            }

        } catch (OutOfMemoryError e) {

            G.customToast("سایز تصویر وارد شده خیلی بزرگ است.");
        }

*/


        if (requestCode == 1 && resultCode == RESULT_OK) {
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            String newString = " " + matches.get(0);
            Selection selection = new Selection(edtContent);
            Editable str = edtContent.getText();

            if (isDot) {
                newString = newString + ". ";
            }

            str.insert(selection.start(), newString);


        }

    }

    public void hideLyts() {

        if (changeStatusPopUp != null) {
            changeStatusPopUp.dismiss();
        }
    }


    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.voiceFa) {
            hideLyts();
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getClass().getPackage().getName());
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "fa-IR");
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "صحبت کنید ...");
            startActivityForResult(intent, 1);
        } else if (view.getId() == R.id.voiceEn) {
            hideLyts();
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speack ...");
            startActivityForResult(intent, 1);
        } else if (view.getId() == R.id.toolbar_faqu) {
            hideLyts();
            Selection selection = new Selection(edtContent);
            Editable str = edtContent.getText();
            str.insert(selection.start(), "؟");
        } else if (view.getId() == R.id.toolbar_enqu) {
            hideLyts();
            Selection selection = new Selection(edtContent);
            Editable str = edtContent.getText();
            str.insert(selection.start(), "?");
        } else if (view.getId() == R.id.toolbar_hazard) {
            hideLyts();
            Selection selection = new Selection(edtContent);
            Editable str = edtContent.getText();
            str.insert(selection.start(), "!");

        } else if (view.getId() == R.id.enter) {
            hideLyts();
            Selection selection = new Selection(edtContent);
            Editable str = edtContent.getText();
            str.insert(selection.start(), "\n");

        } else if (view.getId() == R.id.toolbar_coma) {
            hideLyts();
            Selection selection = new Selection(edtContent);
            Editable str = edtContent.getText();
            str.insert(selection.start(), ",");


        } else if (view.getId() == R.id.toolbar_img) {
            hideLyts();
            int[] location = new int[2];
            currentRowId = position;
            View currentRow = view;
            // Get the x, y location and store it in the location[] array
            // location[0] = x, location[1] = y.
            view.getLocationOnScreen(location);

            //Initialize the Point with x, and y positions
            Point point = new Point();
            point.x = location[0];
            point.y = location[1];

            ShowPopUpQu(RichTextEditor.this, point, 4);


        } else if (view.getId() == R.id.toolbar_dot) {
            hideLyts();
            Selection selection = new Selection(edtContent);
            Editable str = edtContent.getText();
            str.insert(selection.start(), ",");


            isDot = !isDot;
            if (isDot) {
                toolbar_dot.setBackgroundResource(R.drawable.ic_toolbar_dot_checked_light);

            } else {
                toolbar_dot.setBackgroundResource(R.drawable.ic_toolbar_dot_light);

            }


        }


        if (view.getId() == R.id.btnNum) {
            hideLyts();

            int[] location = new int[2];
            currentRowId = position;
            View currentRow = view;
            // Get the x, y location and store it in the location[] array
            // location[0] = x, location[1] = y.
            view.getLocationOnScreen(location);

            //Initialize the Point with x, and y positions
            Point point = new Point();
            point.x = location[0];
            point.y = location[1];
            ShowPopUpQu(RichTextEditor.this, point, 3);


        } else if (view.getId() == R.id.btnAlign) {
            hideLyts();

            int[] location = new int[2];
            currentRowId = position;
            View currentRow = view;
            // Get the x, y location and store it in the location[] array
            // location[0] = x, location[1] = y.
            view.getLocationOnScreen(location);

            //Initialize the Point with x, and y positions
            Point point = new Point();
            point.x = location[0];
            point.y = location[1];
            ShowPopUpQu(RichTextEditor.this, point, 2);


        } else if (view.getId() == R.id.btnQu) {
            hideLyts();

            int[] location = new int[2];
            currentRowId = position;
            View currentRow = view;
            // Get the x, y location and store it in the location[] array
            // location[0] = x, location[1] = y.
            view.getLocationOnScreen(location);

            //Initialize the Point with x, and y positions
            Point point = new Point();
            point.x = location[0];
            point.y = location[1];
            ShowPopUpQu(RichTextEditor.this, point, 1);


        }


    }


    public void saveArtical() {


        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd  'at' HH:mm:ss ");
        String currentDateandTime = sdf.format(new Date());


        String title = edtTitle.getText().toString();
        String tag = edtTag.getText().toString();
        String content = edtContent.getText(RTFormat.HTML);

        if (title.length() < 2 || tag.length() < 2 || content.length() < 2) {
            M.customToast("پر کردن تمامی فیلد ها اجباریست.");
        } else {
            if (lastSelectedArticle == 0) {
                writeToDB(title, content, tag, currentDateandTime);

            } else {


                updateDB(title, content, tag, lastSelectedArticle);
            }


        }
    }

    @Override
    public void onBackPressed() {

        if (edtContent.getText().toString().length() < 2 ||
                edtTag.getText().toString().length() < 2 ||
                edtTitle.getText().toString().length() < 2) {

            super.onBackPressed();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(RichTextEditor.this);
            builder.setTitle("هشدار");
            builder.setMessage("مایل به دخیره مقاله هستید؟");
            builder.setPositiveButton("بله", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                    saveArtical();
                    finish();


                }
            });
            builder.setNegativeButton("خیر", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            builder.setIcon(android.R.drawable.ic_dialog_alert);
            builder.create();

            builder.show();

        }
    }
}
