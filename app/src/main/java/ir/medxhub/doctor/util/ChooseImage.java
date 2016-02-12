package ir.medxhub.doctor.util;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ir.medxhub.doctor.R;
import ir.medxhub.doctor.util.snack_bar.SnackBar;

/**
 * Created by Alireza Ealamifar on 05/11/2015.
 */
public class ChooseImage extends Activity {
    static final int MEDIA_TYPE_IMAGE = 1;
    static final int MEDIA_TYPE_THUMBNAIL = 2;
    static final int PICK_FROM_CAMERA = 1;
    static final int PICK_FROM_FILE = 2;
    static final int CROP_IMAGE_FILE = 3;
    static final String TAG = "ChooseImage";
    static final String IMAGE_DIRECTORY_NAME = "medxhub";
    private ImageView imageView;
    private Uri fileUri, thumbNail;
    private boolean crop;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_media_intent);
        imageView = (ImageView) findViewById(R.id.image);

        crop = getIntent().getBooleanExtra("crop", false);

        // choose from gallery
        findViewById(R.id.tv_item1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
                thumbNail = getOutputMediaFileUri(MEDIA_TYPE_THUMBNAIL, timeStamp);
                fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE, timeStamp);

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Complete action using"), PICK_FROM_FILE);
            }
        });

        //choose from camera
        findViewById(R.id.tv_item2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
                thumbNail = getOutputMediaFileUri(MEDIA_TYPE_THUMBNAIL, timeStamp);
                fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE, timeStamp);

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                startActivityForResult(intent, PICK_FROM_CAMERA);
            }
        });
        findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                setResult(Activity.RESULT_CANCELED, intent);
                finish();
            }
        });
        findViewById(R.id.btn_send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (thumbNail != null && new File(thumbNail.getPath()).exists()) {
                    Intent intent = new Intent();
                    intent.putExtra("path", thumbNail.getPath());
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                } else {
                    Tools.showSnack(ChooseImage.this, getString(R.string.first_select_image), SnackBar.MED_SNACK, -1);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK)
            return;

        switch (requestCode) {
            case PICK_FROM_CAMERA:
                if (crop) doCrop();
                else {
                    thumbNail = fileUri;
                    updatePreview();
                }
                break;

            case PICK_FROM_FILE:
                if (crop) {
                    fileUri = data.getData();
                    doCrop();
                } else {
                    Uri selectedImage = data.getData();
                    if (new File(selectedImage.getPath()).exists()) {
                        thumbNail = selectedImage;
                    } else {
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};

                        Cursor cursor = getContentResolver().query(
                                selectedImage, filePathColumn, null, null, null);
                        cursor.moveToFirst();

                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        String filePath = cursor.getString(columnIndex);
                        thumbNail = Uri.fromFile(new File(filePath));
                    }
                    updatePreview();
                }
                break;

            case CROP_IMAGE_FILE:
                try {
                    Bundle extras = data.getExtras();
                    if (extras != null) {
                        Bitmap photo = extras.getParcelable("data");
                        File imageFile = new File(thumbNail.getPath());
                        FileOutputStream out = new FileOutputStream(imageFile);
                        photo.compress(Bitmap.CompressFormat.PNG, 100, out);
                        out.flush();
                        out.close();
                    }
                } catch (Exception e) {
                    Log.e(TAG, "-----Crop: " + e.getMessage());
                }
                updatePreview();
                break;
            default:
                Log.e(TAG, "-----wrong activity result code");
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("file_uri", fileUri);
        outState.putParcelable("thumbnail_uri", thumbNail);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        fileUri = savedInstanceState.getParcelable("file_uri");
        thumbNail = savedInstanceState.getParcelable("thumbnail_uri");
        updatePreview();
    }

    private static Uri getOutputMediaFileUri(int type, String img_name) {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), IMAGE_DIRECTORY_NAME);

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(IMAGE_DIRECTORY_NAME, "Oops! Failed create " + IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + img_name + ".png");
        } else if (type == MEDIA_TYPE_THUMBNAIL) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "Thumb_" + img_name + ".png");
        } else {
            return null;
        }
        return Uri.fromFile(mediaFile);
    }

    private void doCrop() {
        try {
            Intent intent = new Intent("com.android.camera.action.CROP");
            intent.setType("image/*");

            //Check if there is image cropper app installed.
            List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent, 0);
            int size = list.size();

            //If there is no image cropper app, display warning message
            if (size == 0) {
                Tools.makeToast(ChooseImage.this, getString(R.string.crop_app_not_found), Toast.LENGTH_SHORT, -1);
            } else {
                intent.setData(fileUri);
                intent.putExtra("outputX", 300);
                intent.putExtra("outputY", 300);
                intent.putExtra("aspectX", 1);
                intent.putExtra("aspectY", 1);
//                intent.putExtra("scale", true);
                intent.putExtra("return-data", true);

                Intent i = new Intent(intent);
                ResolveInfo res = list.get(0);
                i.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));

                startActivityForResult(i, CROP_IMAGE_FILE);
            }
        } catch (Exception e) {
            Log.e("ChooseImage", "-----" + e.getMessage());
        }
    }

    private void updatePreview() {
        if (thumbNail != null) {
            imageView.setImageURI(thumbNail);
        } else {
            imageView.setImageResource(R.drawable.default_image);
        }
    }
}
