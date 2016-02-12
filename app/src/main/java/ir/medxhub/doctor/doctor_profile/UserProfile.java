package ir.medxhub.doctor.doctor_profile;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import ir.medxhub.doctor.MainActivity;
import ir.medxhub.doctor.R;
import ir.medxhub.doctor.adapter.PagerAdapter;
import ir.medxhub.doctor.dashboard.DashboardListItem;
import ir.medxhub.doctor.util.ConnectionDetector;
import ir.medxhub.doctor.util.CustomImageLoader;
import ir.medxhub.doctor.util.MyDatePicker;

/**
 * Created by mohammad on 1/4/2016.
 */
public class UserProfile extends Fragment implements TabHost.OnTabChangeListener, ViewPager.OnPageChangeListener {
    public static final String TAG = "UserProfile";
    private TabHost mTabHost;
    private ViewPager mViewPager;
    private HorizontalScrollView mHorizontalScroll;
    protected List<DashboardListItem> articleListItem = new ArrayList<>();
    MainActivity parent;
    private ConnectionDetector cd;
    public JSONObject userInfo;
    private CustomImageLoader imageLoader;
    ImageView avatar;
    TextView userCode;
    TextView fullName;
    TextView birthDate;
    TextView specialty;
    MyDatePicker mdp;


    public UserProfile() {
        //empty constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.medical_records, container, false);
        mViewPager = (ViewPager) rootView.findViewById(R.id.viewpager);
        mTabHost = (TabHost) rootView.findViewById(android.R.id.tabhost);
        mHorizontalScroll = (HorizontalScrollView) rootView.findViewById(R.id.horizontal_scroll_view);
        mTabHost.setCurrentTab(1);

        avatar = (ImageView) rootView.findViewById(R.id.user_avatar);
        userCode = (TextView) rootView.findViewById(R.id.user_code);
        fullName = (TextView) rootView.findViewById(R.id.full_name);
        birthDate = (TextView) rootView.findViewById(R.id.birth_date);
        specialty = (TextView) rootView.findViewById(R.id.specialty2);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        parent = (MainActivity) getActivity();
        parent.refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        // Initialise the TabHost
        mTabHost.setup();
        mTabHost.setOnTabChangedListener(this);
        AddTab(this, getActivity(), this.mTabHost, this.mTabHost.newTabSpec("Tab1").setIndicator(createTabView(getString(R.string.profile))));
//        AddTab(this, getActivity(), this.mTabHost, this.mTabHost.newTabSpec("Tab2").setIndicator(createTabView(getString(R.string.medical_history))));

        MainActivity parent = (MainActivity) getActivity();
        parent.imageLoader.DisplayImage(parent.sp.getFromPreferences("avatar"), R.drawable.profile_avatar, avatar);
        fullName.setText(parent.sp.getFromPreferences("name") + " " + parent.sp.getFromPreferences("family"));
        userCode.setText(parent.sp.getFromPreferences("id"));
        birthDate.setText(parent.sp.getFromPreferences("birth_date"));
        specialty.setText(parent.sp.getFromPreferences("specialty"));
        initializeViewPager();
    }

    public void onTabChanged(String tag) {
        int pos = this.mTabHost.getCurrentTab();
        this.mViewPager.setCurrentItem(pos);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
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
        this.mTabHost.setCurrentTab(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    private static void AddTab(UserProfile ref, Activity activity, TabHost tabHost, TabHost.TabSpec tabSpec) {
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
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.tab_with_text, null);
        TextView tv = (TextView) view.findViewById(R.id.tab_text);
        tv.setText(text);
        return view;
    }


    private void initializeViewPager() {
        List<Fragment> fragments = new Vector<>();
        try {
            fragments.add(Fragment.instantiate(getActivity(), DoctorHome.class.getName()));
//            fragments.add(Fragment.instantiate(getActivity(), MedicalHistory.class.getName()));
            PagerAdapter mPagerAdapter = new PagerAdapter(getChildFragmentManager(), fragments);

            this.mViewPager.setAdapter(mPagerAdapter);
            this.mViewPager.setOnPageChangeListener(this);
        } catch (Exception e) {
            Log.i("F------------K-->", e.getMessage());
        }
    }

}
