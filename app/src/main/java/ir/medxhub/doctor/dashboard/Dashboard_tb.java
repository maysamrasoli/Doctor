package ir.medxhub.doctor.dashboard;

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
import android.widget.TabHost;
import android.widget.TextView;

import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import ir.medxhub.doctor.MainActivity;
import ir.medxhub.doctor.R;
import ir.medxhub.doctor.adapter.PagerAdapter;
import ir.medxhub.doctor.network.AppController;
import ir.medxhub.doctor.network.CustomRequest;
import ir.medxhub.doctor.util.Tools;
import ir.medxhub.doctor.util.snack_bar.SnackBar;

/**
 * Created by mohammad on 12/30/2015.
 */

public class Dashboard_tb extends Fragment implements TabHost.OnTabChangeListener, ViewPager.OnPageChangeListener {
    public static final String TAG = "Dashboard_tb";
    private TabHost mTabHost;
    private ViewPager mViewPager;
    private HorizontalScrollView mHorizontalScroll;
    protected List<DashboardListItem> articleListItem = new ArrayList<>();
    private MainActivity parent;

    public Dashboard_tb() {
        //empty constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tabhost_activity, container, false);
        mViewPager = (ViewPager) rootView.findViewById(R.id.viewpager);
        mTabHost = (TabHost) rootView.findViewById(android.R.id.tabhost);
        mHorizontalScroll = (HorizontalScrollView) rootView.findViewById(R.id.horizontal_scroll_view);


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
        AddTab(this, getActivity(), this.mTabHost, this.mTabHost.newTabSpec("Tab1").setIndicator(createTabView(getString(R.string.doctors_feed))));
        AddTab(this, getActivity(), this.mTabHost, this.mTabHost.newTabSpec("Tab2").setIndicator(createTabView(getString(R.string.dashboard))));

        loadArticles();
    }

    private void loadArticles() {
        Tools.showLoadingDialog(getActivity());
        if (parent.cd.isConnectingToInternet()) {
            Map<String, String> params = new HashMap<>();
            CustomRequest customRequest = new CustomRequest(getActivity(), "patient_get_new_article", params, new CustomRequest.ResponseAction() {
                @Override
                public void onResponseAction(JSONObject data) {
                    Tools.hideLoadingDialog();
                    try {
                        if (data.getBoolean("status")) {
                            Log.i("-------", "true");
                            try {
                                JSONArray doctorList = data.getJSONArray("value");
                                for (int i = 0; i < doctorList.length(); i++) {
                                    DashboardListItem doctor = new DashboardListItem(doctorList.getJSONObject(i));
                                    articleListItem.add(doctor);
                                }
                                // Initialise ViewPager
                                initializeViewPager();

                                mTabHost.setCurrentTab(2);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            if (data.has("message"))
                                Tools.showSnack(getActivity(), data.getString("message"), SnackBar.MED_SNACK, -1);
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
            Tools.showSnack(getActivity(), getString(R.string.connection_error), SnackBar.MED_SNACK, -1);
        }
    }

    private static void AddTab(Dashboard_tb ref, Activity activity, TabHost tabHost, TabHost.TabSpec tabSpec) {
        tabSpec.setContent(ref.new TabFactory(activity));
        tabHost.addTab(tabSpec);
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

    private void initializeViewPager() {
        List<Fragment> fragments = new Vector<>();
//        fragments.add(Fragment.instantiate(getActivity(), Articles.class.getName()));
//        fragments.add(Fragment.instantiate(getActivity(), Home.class.getName()));
//        fragments.add(Fragment.instantiate(getActivity(), PastAppointments.class.getName()));
        PagerAdapter mPagerAdapter = new PagerAdapter(getChildFragmentManager(), fragments);

        this.mViewPager.setAdapter(mPagerAdapter);
        this.mViewPager.setOnPageChangeListener(this);
    }

    public View createTabView(final String text) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.tab_with_text, null);
        TextView tv = (TextView) view.findViewById(R.id.tab_text);
        tv.setText(text);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mTabHost = null;
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
}
