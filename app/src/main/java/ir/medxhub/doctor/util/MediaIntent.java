package ir.medxhub.doctor.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import ir.medxhub.doctor.R;
import ir.medxhub.doctor.Utilities;
import ir.medxhub.doctor.util.snack_bar.SnackBar;

/**
 * Created by Alireza Ealamifar on 05/11/2015.
 */
public class MediaIntent extends Activity {

    AlertDialog.Builder builder;
    View.OnClickListener onClickListener;
    CharSequence[] charSequences;
    ImageView imageView;
    public static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    public static final int GALLERY_IMAGE_REQUEST_CODE = 200;
    public static final int CAMERA_CAPTURE_VIDEO_REQUEST_CODE = 300;
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    public static final String IMAGE_DIRECTORY_NAME = "Medxhub";
    public Uri fileUri;
    public String imageEncoded;
    MySharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_media_intent);
        sp = new MySharedPreferences(getApplication());
        setupDialog();

    }

    public void setupDialog() {
        charSequences = new CharSequence[]{getString(R.string.select_from_gallery), getString(R.string.camera)};
        TextView item1 = (TextView) findViewById(R.id.tv_item1);
        TextView item2 = (TextView) findViewById(R.id.tv_item2);
        this.imageView = (ImageView) findViewById(R.id.image);
        item1.setText(charSequences[0]);
        item2.setText(charSequences[1]);
        item1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getImageFromGallery();
            }
        });
        item2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utilities.isDeviceSupportCamera(MediaIntent.this)) {
                    captureImage();
                } else {
                    //camera not available
                }
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
                if (fileUri.toString().equals("")) {
                    Tools.showSnack(MediaIntent.this, getString(R.string.first_select_image), SnackBar.MED_SNACK, -1);
                    return;
                }
                Intent intent = new Intent();
                intent.putExtra("uri", fileUri.toString());
                intent.putExtra("path", fileUri.getPath());
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });
    }

    private void captureImage() {
//        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//
//        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
//
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
//
//        // start the image capture Intent
//        MediaIntent.this.startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);


        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        File photo = new File(Environment.getExternalStorageDirectory(), "Pic.jpg");
        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                Uri.fromFile(photo));
        fileUri = Uri.fromFile(photo);
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }

    private void getImageFromGallery() {
        // GET IMAGE FROM THE GALLERY
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        Intent chooser = Intent.createChooser(intent, MediaIntent.this.getString(R.string.select_please));
        MediaIntent.this.startActivityForResult(chooser, GALLERY_IMAGE_REQUEST_CODE);
    }

    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    private static File getOutputMediaFile(int type) {

        // External sdcard location
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), IMAGE_DIRECTORY_NAME);
        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(IMAGE_DIRECTORY_NAME, "Oops! Failed create "
                        + IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpg");
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }
        return mediaFile;
    }

    private void recordVideo() {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

        fileUri = getOutputMediaFileUri(MEDIA_TYPE_VIDEO);

        // set video quality
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file
        // name

        // start the video capture Intent
        MediaIntent.this.startActivityForResult(intent, CAMERA_CAPTURE_VIDEO_REQUEST_CODE);
    }

    private Bitmap getCapturedImage(Uri uri) {
        Bitmap bitmap = null;
        try {
            // bitmap factory
            BitmapFactory.Options options = new BitmapFactory.Options();

            // downsizing image as it throws OutOfMemory Exception for larger
            // images
            options.inSampleSize = 8;

            bitmap = BitmapFactory.decodeFile(uri.getPath(), options);

        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    private Bitmap getSelectedImage(Uri uri) {

        String[] filePathColumn = {MediaStore.Images.Media.DATA};

        if (uri == null)
            return null;
        Cursor cursor = MediaIntent.this.getContentResolver().query(uri, filePathColumn, null, null, null);
        cursor.moveToFirst();

        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String imgDecodeAbleStr = cursor.getString(columnIndex);
        cursor.close();
        if (imgDecodeAbleStr.equals("")) return null;
        return (BitmapFactory.decodeFile(imgDecodeAbleStr));
    }

    private void previewVideo() {
        try {
//            // hide image preview
//            imgPreview.setVisibility(DoctorView.GONE);
//
//            videoPreview.setVisibility(DoctorView.VISIBLE);
//            videoPreview.setVideoPath(fileUri.getPath());
//            // start playing
//            videoPreview.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setImageViewByCapturedBitmap(Uri uri) {
        imageView.setImageBitmap(getCapturedImage(uri));
    }

    public void setImageViewBySelectedBitmap(Uri uri) {
        imageView.setImageBitmap(getSelectedImage(uri));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == MediaIntent.CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                getContentResolver().notifyChange(fileUri, null);
                ContentResolver cr = getContentResolver();
                Bitmap bitmap;
                try {
                    bitmap = MediaStore.Images.Media
                            .getBitmap(cr, fileUri);
                    imageEncoded = getStringImage(bitmap);
                    imageView.setImageBitmap(bitmap);
                    Toast.makeText(this, fileUri.toString(), Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    Toast.makeText(this, "Failed to load", Toast.LENGTH_SHORT)
                            .show();
                    Log.e("Camera", e.toString());
                }

            } else if (resultCode == RESULT_CANCELED) {
//                    Toast.makeText(getApplicationContext(),
//                            "User cancelled image capture", Toast.LENGTH_SHORT)
//                            .show();
            } else {
                // failed to capture image
                Toast.makeText(getApplicationContext(), getString(R.string.error_occured), Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == MediaIntent.CAMERA_CAPTURE_VIDEO_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // video successfully recorded
                // preview the recorded video
//                previewVideo();
            } else if (resultCode == RESULT_CANCELED) {
                // user cancelled recording
//                Toast.makeText(getApplicationContext(),
//                        "User cancelled video recording", Toast.LENGTH_SHORT)
//                        .show();
            } else {
                // failed to record video
                Toast.makeText(getApplicationContext(), getString(R.string.error_occured), Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == MediaIntent.GALLERY_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                fileUri = data.getData();
                imageEncoded = getStringImage(getSelectedImage(fileUri));
                setImageViewBySelectedBitmap(fileUri);

            } else if (resultCode == RESULT_CANCELED) {

            } else {
                // failed to select image
                Toast.makeText(getApplicationContext(), getString(R.string.error_occured), Toast.LENGTH_SHORT).show();
            }
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putString("uri", fileUri.toString());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        fileUri = Uri.parse(savedInstanceState.getString("uri"));
    }

    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }
}
