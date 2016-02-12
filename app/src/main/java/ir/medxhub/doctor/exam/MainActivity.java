package ir.medxhub.doctor.exam;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import ir.medxhub.doctor.R;
import ir.medxhub.doctor.util.Tools;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView btnAddPic1, btnAddPic2, btnAddPic3, btnAddPic4, imgRecycle;
    int selectedImg, img_count;
    ProgressDialog pDialog;
    TextView txtNext;
    EditText edtContent, edtTitle, current;

    String imageString1, imageString2, imageString3,
            imageString4, strUser, strTitle, success, message;

    LinearLayout lytNext, lytFa, lytEng, lytDot, lytFaQu, lytEngQu, lytMenu2, lytMulty, lytEnQu,
            lytEnter, lytHazard, lytVirgol;
    LinearLayout lytUndo, lytRedo;


    boolean dotIsActive, isUndo, idRedo;
    int currentWorkNum;
    ArrayList<saveWorks> Works = new ArrayList<>();


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
        if (v == imgRecycle) {
            showAlert();
        }

        if (v == lytNext) {
            if (edtTitle.getText().toString().length() < 1) {
                Tools.makeToast(MainActivity.this, getString(R.string.exam_case_title_empety), Toast.LENGTH_SHORT, -1);


                return;

            }
            Intent intent = new Intent(MainActivity.this, ActivitySecond.class);

            intent.putExtra("img1", imageString1);
            intent.putExtra("img2", imageString2);
            intent.putExtra("img3", imageString3);
            intent.putExtra("img4", imageString4);
            startActivity(intent);

        }


        if (v.getId() == R.id.edtTitle) {
            current = edtTitle;

        } else if (v.getId() == R.id.edtContent) {
            current = edtContent;
        }


    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_exam_first);


        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            toolbar.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }

        btnAddPic1 = (ImageView) findViewById(R.id.btnAddPic1);
        btnAddPic2 = (ImageView) findViewById(R.id.btnAddPic2);
        btnAddPic3 = (ImageView) findViewById(R.id.btnAddPic3);
        btnAddPic4 = (ImageView) findViewById(R.id.btnAddPic4);
        imgRecycle = (ImageView) findViewById(R.id.imgRecycle);
        edtTitle = (EditText) findViewById(R.id.edtTitle);
        edtContent = (EditText) findViewById(R.id.edtContent);
        txtNext = (TextView) findViewById(R.id.txtNext);
        lytNext = (LinearLayout) findViewById(R.id.lytNext);
        lytRedo = (LinearLayout) findViewById(R.id.lytRedo);
        lytUndo = (LinearLayout) findViewById(R.id.lytUndo);
        lytFa = (LinearLayout) findViewById(R.id.lytFa);
        lytEng = (LinearLayout) findViewById(R.id.lytEng);
        lytDot = (LinearLayout) findViewById(R.id.lytDot);
        lytMenu2 = (LinearLayout) findViewById(R.id.lytMenu2);
        lytFaQu = (LinearLayout) findViewById(R.id.lytFaQu);
        lytEngQu = (LinearLayout) findViewById(R.id.lytEngQu);
        lytMulty = (LinearLayout) findViewById(R.id.lytMulty);
        lytEnter = (LinearLayout) findViewById(R.id.lytEnter);
        lytVirgol = (LinearLayout) findViewById(R.id.lytVirgol);
        lytHazard = (LinearLayout) findViewById(R.id.lytHazard);


        btnAddPic1.setOnClickListener(this);
        imgRecycle.setOnClickListener(this);
        lytNext.setOnClickListener(this);
        edtTitle.setOnClickListener(this);
        edtContent.setOnClickListener(this);
        current = edtTitle;

        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (!isUndo && !idRedo) {

                    saveWorks work = new saveWorks();
                    work.titile = edtTitle.getText().toString();
                    work.content = edtContent.getText().toString();
                    Works.add(work);
                    currentWorkNum = Works.size();

                }

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        edtTitle.addTextChangedListener(watcher);
        edtContent.addTextChangedListener(watcher);


        initializeListeners();
        Tools.setCustomFont(MainActivity.this, (ViewGroup) getWindow().getDecorView());


    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 2) {
            if (resultCode == RESULT_OK) {
                if (data != null) {

                    File tempFile = getTempFile();

                    String filePath = Environment.getExternalStorageDirectory()
                            + "/" + TEMP_PHOTO_FILE;
                    System.out.println("path " + filePath);


                    Bitmap bitmap = BitmapFactory.decodeFile(filePath);


                    switch (selectedImg) {

                        case 1:
                            btnAddPic1.setImageBitmap(bitmap);
                            btnAddPic2.setVisibility(View.VISIBLE);
                            imageString1 = getStringImage(bitmap);
                            img_count = 1;
                            btnAddPic2.setImageResource(R.drawable.camera);
                            btnAddPic2.setOnClickListener(this);

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


    public String getStringImage(Bitmap bmp) {
        if (bmp == null) {
            return "";

        }
        try {


            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 40, baos);
            byte[] imageBytes = baos.toByteArray();
            String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
            return encodedImage;
        } catch (OutOfMemoryError e) {
            return null;

        }
    }


    public void showAlert() {

        if (edtTitle.getText().toString().length() < 1 || edtContent.getText().toString().length() < 1) {
            return;
        }
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("هشدار")
                .setMessage("مایل به حذف بحث هستید؟")
                .setPositiveButton("بله", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        edtTitle.setText("");
                        edtContent.setText("");
                        Tools.makeToast(MainActivity.this, getString(R.string.exam_case_deleted), Toast.LENGTH_SHORT, -1);


                    }
                })
                .setNegativeButton("خیر", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }


    private void showFileChooser() {

        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
        photoPickerIntent.setType("image/*");
        photoPickerIntent.putExtra("crop", "true");
        photoPickerIntent.putExtra(MediaStore.EXTRA_OUTPUT, getTempUri());
        photoPickerIntent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());

        startActivityForResult(photoPickerIntent, 2);
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
            }

            return file;
        } else {

            return null;
        }
    }

    public void initializeListeners() {

        edtTitle.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                current = edtTitle;
                return false;
            }
        });
        edtContent.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                current = edtContent;
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
                    lytDot.setBackgroundColor(Color.parseColor("#dddddd"));
                }
                return false;
            }
        });

        lytRedo.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                int size = Works.size();
                currentWorkNum++;
                if (size > 1 && currentWorkNum > 1 && currentWorkNum < size) {
                    idRedo = true;
                    saveWorks work = Works.get(currentWorkNum);
                    edtTitle.setText(work.titile);
                    edtContent.setText(work.content);
                    current.setSelection(current.getText().length());
                    isUndo = false;
                }

                idRedo = false;
                return false;
            }
        });

        lytUndo.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int size = Works.size();
                currentWorkNum--;
                if (size > 1 && currentWorkNum > 1 && currentWorkNum < size) {
                    isUndo = true;

                    saveWorks work = Works.get(currentWorkNum);
                    edtTitle.setText(work.titile);
                    edtContent.setText(work.content);
                    current.setSelection(current.getText().length());
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
                // Get the x, y location and store it in the location[] array
                // location[0] = x, location[1] = y.
                view.getLocationOnScreen(location);

                //Initialize the Point with x, and y positions
                Point point = new Point();
                point.x = location[0];
                point.y = location[1];
                ShowPopUpQu(MainActivity.this, point, 3);


            }
        });

        lytEngQu.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                String oldString = current.getText().toString();
                current.setText(oldString + "?");
                current.setSelection(current.getText().length());

                return false;
            }
        });

        lytFaQu.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                String oldString = current.getText().toString();
                current.setText(oldString + "؟");
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

    }


    private void ShowPopUpQu(final Activity context, Point p, int num) {

        // Inflate the popup_layout.xml

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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

        int OFFSET_X = 0;
        int OFFSET_Y = 0;


        // Creating the PopupWindow
        PopupWindow changeStatusPopUp = new PopupWindow(context);
        changeStatusPopUp.setContentView(layout);

        changeStatusPopUp.setWidth(LinearLayout.LayoutParams.WRAP_CONTENT);
        changeStatusPopUp.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        changeStatusPopUp.setFocusable(true);


        // Some offset to align the popup a bit to the left, and a bit down, relative to button's position.

        //Clear the default translucent background
        changeStatusPopUp.setBackgroundDrawable(new BitmapDrawable());



        // Displaying the popup at the specified location, + offsets.
        changeStatusPopUp.showAtLocation(layout, Gravity.NO_GRAVITY, p.x, p.y);

    }


    public class saveWorks {

        String titile;
        String content;
    }


}
