package ir.medxhub.doctor.network;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;
import ir.medxhub.doctor.Globals;
import ir.medxhub.doctor.R;
import ir.medxhub.doctor.util.Tools;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Ali on 3/9/2015.
 */
public class DownloadApk extends AsyncTask<String, String, String> {
    public static final String AppName = "temp.apk";
    ProgressDialog pDialog;
    File filePath;
    URL url;
    Activity activity;

    public DownloadApk(Activity activity, String url) {
        try {
            this.activity = activity;
            this.url = new URL(url);
            this.filePath = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "BozorgMehr");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    private void showDialog() {
        // create dialog
        Typeface typeFace = Typeface.createFromAsset(activity.getAssets(), Globals.appFont);
        this.pDialog = new ProgressDialog(activity);
        this.pDialog.setMessage(activity.getString(R.string.downloading));
        this.pDialog.setIndeterminate(false);
        this.pDialog.setMax(100);
        this.pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        this.pDialog.setCancelable(true);
        this.pDialog.show();
        TextView tv = (TextView) pDialog.findViewById(android.R.id.message);
        tv.setTextColor(activity.getResources().getColor(R.color.dark_gray));
        tv.setTypeface(typeFace);
        tv.setGravity(Gravity.CENTER);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        showDialog();
    }

    @Override
    protected String doInBackground(String... strings) {
        int count;
        try {
            URLConnection connection = url.openConnection();
            connection.connect();
            // getting file length
            int lengthOfFile = connection.getContentLength();

            // input stream to read file - with 8k buffer
            InputStream input = new BufferedInputStream(url.openStream(), 8192);

            // Output stream to write file
            OutputStream output;
            boolean success = true;
            if (!filePath.exists()) {
                success = filePath.mkdir();
            }
            if (success) {
                output = new FileOutputStream(new File(filePath, AppName));
            } else {
                Tools.makeToast(activity, activity.getString(R.string.error_occured), Toast.LENGTH_SHORT, -1);
                return null;
            }

            byte data[] = new byte[1024];

            long total = 0;

            while ((count = input.read(data)) != -1) {
                total += count;
                // publishing the progress....
                // After this onProgressUpdate will be called
                publishProgress("" + (int) ((total * 100) / lengthOfFile));

                // writing data to file
                output.write(data, 0, count);
            }

            // flushing output
            output.flush();

            // closing streams
            output.close();
            input.close();

        } catch (Exception e) {
            Log.e("Error: ", e.getMessage());
        }

        return null;
    }

    protected void onProgressUpdate(String... progress) {
        // setting progress percentage
        pDialog.setProgress(Integer.parseInt(progress[0]));
    }

    @Override
    protected void onPostExecute(String file_url) {
        // dismiss the dialog after the file was downloaded
        pDialog.dismiss();

        // install application
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(new File(filePath, AppName)), "application/vnd.android.package-archive");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivityForResult(intent, 100);
    }

}