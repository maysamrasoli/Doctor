package ir.medxhub.doctor;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gcm.GCMRegistrar;

import ir.medxhub.doctor.dashboard.Dashboard_tb;
import ir.medxhub.doctor.doctor_profile.UserProfile;
import ir.medxhub.doctor.message.ServerUtilities;
import ir.medxhub.doctor.network.AppController;
import ir.medxhub.doctor.util.ConnectionDetector;
import ir.medxhub.doctor.util.CustomImageLoader;
import ir.medxhub.doctor.util.MySharedPreferences;
import ir.medxhub.doctor.util.Tools;
import ir.medxhub.doctor.util.snack_bar.SnackBar;
import ir.medxhub.doctor.util.views.DrawerArrowDrawable;

public class MainActivity extends ActionBarActivity implements MainSideMenuFragment.NavigationDrawerCallbacks {
    private static final String TAG = "MainActivity";
    private TextView title;
    private Handler handler = new Handler();
    private Runnable runnable;
    public MySharedPreferences sp;
    public ConnectionDetector cd;
    public ImageView refreshBtn;
    public CustomImageLoader imageLoader;
    private int selectedMenuItemId;
    public MyDate mydate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sp = new MySharedPreferences(this);
        cd = new ConnectionDetector(this);
        imageLoader = new CustomImageLoader(this);
        mydate = new MyDate();
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        MainSideMenuFragment mSideMenuFragment = (MainSideMenuFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);

        // Set up the drawer.
        mSideMenuFragment.setUp(R.id.navigation_drawer, drawerLayout);

        // action bar
        Toolbar actionBar = (Toolbar) findViewById(R.id.actionBar);
        setSupportActionBar(actionBar);
        refreshBtn = (ImageView) findViewById(R.id.refresh);
        title = (TextView) findViewById(R.id.action_bar_title);
        Resources resources = getResources();
        final DrawerArrowDrawable drawerArrowDrawable = new DrawerArrowDrawable(resources);
        drawerArrowDrawable.setStrokeColor(resources.getColor(R.color.light_gray));
        ((ImageView) findViewById(R.id.sidebar_toggle_btn)).setImageDrawable(drawerArrowDrawable);


        drawerLayout.setDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                // Sometimes slideOffset ends up so close to but not quite 1 or 0.
                if (slideOffset >= .995)
                    drawerArrowDrawable.setFlip(true);
                else if (slideOffset <= .005)
                    drawerArrowDrawable.setFlip(false);
                drawerArrowDrawable.setParameter(slideOffset);
            }
        });

        checkGCM();
    }

    @Override
    public void onNavigationDrawerItemSelected(final int itemId) {
        // update the main content by replacing fragments
        Log.i("-----------------------", itemId + "");
        switch (itemId) {
            case R.string.dashboard:
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, new Dashboard_tb()).commit();
                        selectedMenuItemId = itemId;
                    }
                };
                handler.postDelayed(runnable, 500);
                title.setText(R.string.app_name);
                break;


            case R.string.profile:
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, new UserProfile()).commit();
                        selectedMenuItemId = itemId;
                    }
                };
                handler.postDelayed(runnable, 500);
                title.setText(R.string.profile);
                break;

            case R.string.exit:
                sp.saveToPreferences("sid", "");
                sp.saveToPreferences("password", "");
                startActivity(new Intent(this, Login.class));
                MainActivity.this.finish();
                overridePendingTransition(R.anim.nude, R.anim.slide_out_to_right);
                break;

            default:
                Tools.showSnack(this, "Not Available Yet", SnackBar.MED_SNACK, -1);
                break;
        }
    }

    private void checkGCM() {
        // check gcm registration
        if (sp.getFromPreferences("gcm").equals("") && !sp.getFromPreferences("userid").equals("") && cd.isConnectingToInternet()) {
            try {
                // Make sure the device has the proper dependencies.
                GCMRegistrar.checkDevice(this);

                // Make sure the manifest was properly set - comment out this line
                // while developing the app, then uncomment it when it's ready.
                GCMRegistrar.checkManifest(this);

                // Get GCM registration id
                final String regId = GCMRegistrar.getRegistrationId(this);

                // Check if regid already presents
                if (regId.equals("")) {
                    // Registration is not present, register now with GCM
                    GCMRegistrar.register(this, Globals.SENDER_ID);
                } else
                // Try to register again if device is not registered on GCM
                {
                    if (!GCMRegistrar.isRegisteredOnServer(this)) {
                        ServerUtilities.register(MainActivity.this, regId);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (handler != null) handler.removeCallbacks(runnable);
        AppController.getInstance().cancelPendingRequests(TAG);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (selectedMenuItemId == R.string.dashboard)
            Tools.exitApp(this);
        else {
            MainSideMenuFragment sideMenuFragment = (MainSideMenuFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
            sideMenuFragment.selectMainItem();
        }
    }

    public void Sft(final int itemId) {
        onNavigationDrawerItemSelected(itemId);
    }
}
