package ir.medxhub.doctor.doctor_profile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.Rating;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import ir.medxhub.doctor.R;
import ir.medxhub.doctor.adapter.PagerAdapter;
import ir.medxhub.doctor.network.AppController;
import ir.medxhub.doctor.network.CustomRequest;
import ir.medxhub.doctor.util.ConnectionDetector;
import ir.medxhub.doctor.util.CustomImageLoader;
import ir.medxhub.doctor.util.MySharedPreferences;
import ir.medxhub.doctor.util.Tools;
import ir.medxhub.doctor.util.snack_bar.SnackBar;

/**
 * Created by mohammad on 1/22/2016.
 */
public class Profile extends FragmentActivity implements TabHost.OnTabChangeListener, ViewPager.OnPageChangeListener {
    public static final String TAG = "Profile";
    private TabHost mTabHost;
    private ViewPager mViewPager;
    private HorizontalScrollView mHorizontalScroll;
    private ConnectionDetector cd;
    private CustomImageLoader customImageLoader;
    private PagerAdapter mPagerAdapter;
    private List<Fragment> fragments = new Vector<>(4);
    protected static String doctorId;
    public MySharedPreferences sp;
    ImageView avatar;
    Rating rating;
    TextView countRating;
    TextView specialty;
    TextView specialty2;
    TextView address;
    TextView fullName;
    TextView experience;
    TextView num1;
    TextView num2;
    TextView num3;
    TextView num4;
    ViewPager viewPager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.doctor_profile);

        cd = new ConnectionDetector(this);
        sp = new MySharedPreferences(this);
        customImageLoader = new CustomImageLoader(this);

        Intent intent = getIntent();
        doctorId = intent.getStringExtra("doctor_id");
        avatar = (ImageView) findViewById(R.id.user_avatar);
//        rating = rootView.findViewById(R.id.rating);
        countRating = (TextView) findViewById(R.id.countRating);
        fullName = (TextView) findViewById(R.id.fullName);
        specialty = (TextView) findViewById(R.id.specialty);
        specialty2 = (TextView) findViewById(R.id.specialty2);
        address = (TextView) findViewById(R.id.address);
        experience = (TextView) findViewById(R.id.experience);
        num1 = (TextView) findViewById(R.id.num1);
        num2 = (TextView) findViewById(R.id.num2);
        num3 = (TextView) findViewById(R.id.num3);
        num4 = (TextView) findViewById(R.id.num4);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        Toolbar actionBar = (Toolbar) findViewById(R.id.actionBar);
        ((TextView) findViewById(R.id.action_bar_title)).setText(getString(R.string.make_appointment));
        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Profile.this.finish();
                overridePendingTransition(R.anim.nude, R.anim.slide_out_to_right);
            }
        });

        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mViewPager.setOffscreenPageLimit(7);
        mPagerAdapter = new PagerAdapter(getSupportFragmentManager(), fragments);
        mViewPager.setAdapter(mPagerAdapter);

        // Initialise the TabHost
        mTabHost = (TabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup();
        mTabHost.getTabWidget().setDividerDrawable(null);
        mTabHost.setOnTabChangedListener(this);
        viewPager.setVerticalScrollBarEnabled(true);
        mHorizontalScroll = (HorizontalScrollView) findViewById(R.id.horizontal_scroll_view);
        AddTab(this, this, this.mTabHost, this.mTabHost.newTabSpec("Tab1").setIndicator(createTabView(getString(R.string.view))));
        AddTab(this, this, this.mTabHost, this.mTabHost.newTabSpec("Tab1").setIndicator(createTabView(getString(R.string.view))));
//
//viewPager.scroll
        initializeViewPager();
        LoadProfile();
    }

    private void LoadProfile() {
        Tools.showLoadingDialog(this);
        if (cd.isConnectingToInternet()) {
            Map<String, String> params = new HashMap<>();
            params.put("doctor_id", doctorId);
            CustomRequest customRequest = new CustomRequest(this, "doctor_show_profile", params, new CustomRequest.ResponseAction() {
                @Override
                public void onResponseAction(JSONObject data) {
                    Tools.hideLoadingDialog();
                    try {
                        if (data.getBoolean("status")) {
                            try {
                                JSONObject Profile = data.getJSONObject("value");
                                JSONArray userInfo = Profile.getJSONArray("doctor_info");
                                JSONArray subSpecialty = Profile.getJSONArray("sub_specialty");
                                for (int j = 0; j < subSpecialty.length(); j++) {
                                    JSONObject Array2 = subSpecialty.getJSONObject(j);
                                    specialty2.setText(specialty2.getText() + Array2.getString("name") + ", ");
                                }
                                for (int i = 0; i < userInfo.length(); i++) {
                                    JSONObject Array = userInfo.getJSONObject(i);
                                    customImageLoader.DisplayImage(sp.getFromPreferences("avatar"), R.drawable.profile_avatar, avatar);

                                    fullName.setText(Array.getString("full_name"));
                                    fullName.setText(Array.getString("full_name"));
                                    experience.setText(Array.getString("year_experience") + " سال");
                                    specialty.setText(Array.getString("specialty"));
                                    address.setText(Array.getString("province") + " - " + Array.getString("city"));
//                                specialty.setText(userInfo.getString("specialty"));
                                    num1.setText(Array.getString("count_accept"));
                                    num2.setText(Array.getString("count_article"));
                                    num3.setText(Array.getString("count_thank"));
                                    num4.setText(Array.getString("count_answer"));

//
                                }


//                                 Initialise ViewPager
//                                initializeViewPager();

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            if (data.has("message"))
                                Tools.showSnack(Profile.this, data.getString("message"), SnackBar.MED_SNACK, -1);
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "----" + e.getMessage());
                    }
                }

                @Override
                public void onErrorAction(VolleyError error) {
                    super.onErrorAction(error);
                }
            });
            AppController.getInstance().addToRequestQueue(customRequest, TAG);
        } else {
            Tools.showSnack(Profile.this, getString(R.string.connection_error), SnackBar.MED_SNACK, -1);
        }
    }

    public void onTabChanged(String tag) {
        int pos = this.mTabHost.getCurrentTab();
        this.mViewPager.setCurrentItem(pos);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        Log.i("SCROLLED--->", "DONE1");
        View tabView = mTabHost.getTabWidget().getChildAt(position);
        if (tabView != null) {
            final int width = mHorizontalScroll.getWidth();
            final int scrollPos = tabView.getLeft() - (width - tabView.getWidth()) / 2;
            mHorizontalScroll.scrollTo(scrollPos, 0);
        } else {
            mHorizontalScroll.scrollBy(positionOffsetPixels, 0);
        }
    }

    @Override
    public void onPageSelected(int position) {
        Log.i("SCROLLED--->", "DONE2");
        this.mTabHost.setCurrentTab(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        Log.i("SCROLLED--->", "DONE3");
    }

    private static void AddTab(Profile ref, Activity activity, TabHost tabHost, TabHost.TabSpec tabSpec) {
        tabSpec.setContent(ref.new TabFactory(activity));
        tabHost.addTab(tabSpec);
    }

    class TabFactory implements TabHost.TabContentFactory {
        private final Context mContext;

        public TabFactory(Context context) {
            mContext = context;
        }

        public View createTabContent(String tag) {
            View v = new View(mContext);
            v.setMinimumWidth(0);
            v.setMinimumHeight(0);
            return v;
        }
    }

    public View createTabView(final String text) {
        View view = LayoutInflater.from(this).inflate(R.layout.tab_with_text, null);
        TextView tv = (TextView) view.findViewById(R.id.tab_text);
        tv.setText(text);
        return view;
    }


    private void initializeViewPager() {
//        List<Fragment> fragments = new Vector<>();
        try {
            fragments.add(Fragment.instantiate(this, DoctorHome.class.getName()));
            PagerAdapter mPagerAdapter = new PagerAdapter(getSupportFragmentManager(), fragments);

            this.mViewPager.setAdapter(mPagerAdapter);
            this.mViewPager.setOnPageChangeListener(this);
        } catch (Exception e) {
            Log.i("failed--->", e.getMessage());
        }
    }

//    mPager.setOnTouchListener(new View.OnTouchListener() {
//
//        int dragthreshold = 30;
//        int downX;
//        int downY;
//
//        @Override
//        public boolean onTouch(View v, MotionEvent event) {
//
//            switch (event.getAction()) {
//                case MotionEvent.ACTION_DOWN:
//                    downX = (int) event.getRawX();
//                    downY = (int) event.getRawY();
//                    break;
//                case MotionEvent.ACTION_MOVE:
//                    int distanceX = Math.abs((int) event.getRawX() - downX);
//                    int distanceY = Math.abs((int) event.getRawY() - downY);
//
//                    if (distanceY > distanceX && distanceY > dragthreshold) {
//                        mPager.getParent().requestDisallowInterceptTouchEvent(false);
//                        mScrollView.getParent().requestDisallowInterceptTouchEvent(true);
//                    } else if (distanceX > distanceY && distanceX > dragthreshold) {
//                        mPager.getParent().requestDisallowInterceptTouchEvent(true);
//                        mScrollView.getParent().requestDisallowInterceptTouchEvent(false);
//                    }
//                    break;
//                case MotionEvent.ACTION_UP:
//                    mScrollView.getParent().requestDisallowInterceptTouchEvent(false);
//                    mPager.getParent().requestDisallowInterceptTouchEvent(false);
//                    break;
//            }
//            return false;
//        }
//    });

    private void hideViews() {
//        mToolbar.animate().translationY(-mToolbar.getHeight()).setInterpolator(new AccelerateInterpolator(2));
//
//        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mFabButton.getLayoutParams();
//        int fabBottomMargin = lp.bottomMargin;
//        mFabButton.animate().translationY(mFabButton.getHeight()+fabBottomMargin).setInterpolator(new AccelerateInterpolator(2)).start();
    }

    private void showViews() {
//        mToolbar.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2));
//        mFabButton.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
    }

}