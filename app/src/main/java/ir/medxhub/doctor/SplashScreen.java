package ir.medxhub.doctor;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;

import java.io.File;
import java.io.IOException;

import ir.medxhub.doctor.database.DBHelper;
import ir.medxhub.doctor.util.MySharedPreferences;
import ir.medxhub.doctor.util.Tools;

public class SplashScreen extends Activity {
    Class targetClass;
    MySharedPreferences sp;
    String app_version = null;
    Handler handler = new Handler();
    Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        sp = new MySharedPreferences(this);

        try {
            app_version = "medxhub_" + getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        //clear download directory
        File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "MedxHub");
        if (dir.isDirectory()) Tools.deleteDir(dir);

        // define target activity
        // todo login or intro
        targetClass = sp.getFromPreferences("intro").equals("") ? Intro.class : MainActivity.class;

        if (!sp.getFromPreferences("username").equals("") && !sp.getFromPreferences("password").equals("") && !sp.getFromPreferences(app_version).equals("")) {
            runnable = new Runnable() {
                @Override
                public void run() {
                    sp.saveToPreferences("sid", "");
                    sp.saveToPreferences("type", "user");
                    startActivity(new Intent(SplashScreen.this, MainActivity.class));
                    SplashScreen.this.finish();
                    SplashScreen.this.overridePendingTransition(R.anim.fade_in, R.anim.nude);
                }
            };
            handler.postDelayed(runnable, 2000);
        } else {
            sp.saveToPreferences("type", "");

            // check if application is loaded for the first time
            if (sp.getFromPreferences(app_version).equals("")) {
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        // save screen size and scale
                        float scale = SplashScreen.this.getResources().getDisplayMetrics().density;
                        sp.saveToPreferences("ScreenScale", String.valueOf(scale));
                        sp.saveToPreferences("ScreenWidth", String.valueOf((int) (325 * scale + 0.5f)));

                        // copy database for first time
                        try {
                            DBHelper db = new DBHelper(SplashScreen.this);
                            boolean dbExist = db.checkDataBase();
                            String DB_PATH = getFilesDir().getPath() + "/" + Globals.dbNAME;
                            if (dbExist) {
                                Log.i("database", "----- database already exists");
                                File dir = new File(DB_PATH);
                                boolean result = Tools.deleteDir(dir);
                                Log.i("database deletion", result ? "----- previous database deleted" : "----- previous database deletion failed");
                            }
                            db.createDataBase();
                            db.close();
                            sp.saveToPreferences(app_version, "done");
                        } catch (IOException e) {
                            Log.e("create database", "----" + e.getMessage());
                            return;
                        }

                        startActivity(new Intent(SplashScreen.this, targetClass));
                        SplashScreen.this.finish();
                        SplashScreen.this.overridePendingTransition(R.anim.fade_in, R.anim.nude);
                    }
                };
                handler.postDelayed(runnable, 1000);
            } else {
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(SplashScreen.this, targetClass));
                        SplashScreen.this.finish();
                        SplashScreen.this.overridePendingTransition(R.anim.fade_in, R.anim.nude);
                    }
                };
                handler.postDelayed(runnable, 3000);
            }
        }

    }

    @Override
    public void onBackPressed() {
        if (handler != null) handler.removeCallbacks(runnable);
        super.onBackPressed();
    }

    //    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        Log.i("test", requestCode + " _ " + resultCode);
//        if (requestCode == 100 && resultCode == RESULT_OK) {
//            Tools.makeToast(SplashScreen.this, data.getStringExtra("response"), Toast.LENGTH_SHORT, 1);
//        }
//    }
}
