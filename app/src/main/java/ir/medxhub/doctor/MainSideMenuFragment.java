package ir.medxhub.doctor;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainSideMenuFragment extends Fragment {
    private DrawerLayout mDrawerLayout;
    private NavigationDrawerCallbacks mCallbacks;
    private View mFragmentContainerView, currentSelectedItem, menuHeader, menuFooter;
    private LinearLayout menuItems;
    private LayoutInflater inflater;
    private Resources resources;
    private ImageView avatar;
    private TextView fullName, userCode;
    private View mainMenuItem;

    public MainSideMenuFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.inflater = inflater;
        View mDrawerView = inflater.inflate(R.layout.fragment_side_menu, container, false);
        avatar = (ImageView) mDrawerView.findViewById(R.id.user_avatar);
        fullName = (TextView) mDrawerView.findViewById(R.id.full_name);
        userCode = (TextView) mDrawerView.findViewById(R.id.user_code);
        menuItems = (LinearLayout) mDrawerView.findViewById(R.id.menu_items);
        menuHeader = mDrawerView.findViewById(R.id.menu_header);
        menuFooter = mDrawerView.findViewById(R.id.visit_website);
        menuFooter.findViewById(R.id.visit_website).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Globals.siteUrl)));
                closeDrawer();
            }
        });

        return mDrawerView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        resources = getResources();

        MainActivity parent = (MainActivity) getActivity();
        parent.imageLoader.DisplayImage(parent.sp.getFromPreferences("avatar"), R.drawable.profile_avatar, avatar);
        fullName.setText(parent.sp.getFromPreferences("name") + " " + parent.sp.getFromPreferences("family"));
        userCode.setText(getString(R.string.your_code) + parent.sp.getFromPreferences("id"));

        mainMenuItem = addMenuItem(getString(R.string.dashboard), R.mipmap.ic_home, R.mipmap.ic_home_red, R.string.dashboard);
        addMenuItem(getString(R.string.profile), R.mipmap.ic_medical_file, R.mipmap.ic_medical_file_red, R.string.profile);
        addMenuItem(getString(R.string.exit), R.mipmap.ic_off, R.mipmap.ic_off_red, R.string.exit);

        selectMainItem();

        checkOrientation();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (NavigationDrawerCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    private void closeDrawer() {
        if (mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView)) {
            mDrawerLayout.closeDrawer(Gravity.RIGHT);
        }
    }

    public void setUp(int fragmentId, DrawerLayout drawerLayout) {
        mFragmentContainerView = getActivity().findViewById(fragmentId);
        getActivity().findViewById(R.id.sidebar_toggle_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleDrawer();
            }
        });
        mDrawerLayout = drawerLayout;

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener
    }

    public void toggleDrawer() {
        if (mDrawerLayout.isDrawerOpen(Gravity.RIGHT)) {
            mDrawerLayout.closeDrawer(Gravity.RIGHT);
        } else {
            mDrawerLayout.openDrawer(Gravity.RIGHT);
        }
    }

    private void selectItem(final View item, final int itemId) {
        if (mDrawerLayout != null && mDrawerLayout.isDrawerOpen(Gravity.END)) {
            mDrawerLayout.closeDrawer(mFragmentContainerView);
//            mDrawerLayout.setDrawerListener(new DrawerLayout.SimpleDrawerListener() {
//                @Override
//                public void onDrawerClosed(DoctorView drawerView) {
//                    super.onDrawerClosed(drawerView);
//                    setItemSelected(item, itemId);
//                }
//            });
            setItemSelected(item, itemId);
        } else {
            setItemSelected(item, itemId);
        }
    }

    private void setItemSelected(final View item, final int itemId) {
        if (mCallbacks != null) {
            mCallbacks.onNavigationDrawerItemSelected(itemId);
            if (currentSelectedItem != null) {
                currentSelectedItem.setEnabled(true);
                currentSelectedItem.findViewById(R.id.icon_red).setVisibility(View.GONE);
                currentSelectedItem.findViewById(R.id.icon_white).setVisibility(View.VISIBLE);
                ((TextView) currentSelectedItem.findViewById(R.id.item_text)).setTextColor(resources.getColor(android.R.color.white));
            }
            item.setEnabled(false);
            item.findViewById(R.id.icon_red).setVisibility(View.VISIBLE);
            item.findViewById(R.id.icon_white).setVisibility(View.GONE);
            ((TextView) item.findViewById(R.id.item_text)).setTextColor(resources.getColor(R.color.main_color));
            currentSelectedItem = item;
        }
    }

    public void selectMainItem() {
        selectItem(mainMenuItem, R.string.dashboard);
    }

    private View addMenuItem(String itemText, int iconWhite, int iconRed, final int itemId) {
        final View menuItem = inflater.inflate(R.layout.side_menu_item, null);
        menuItem.findViewById(R.id.icon_white).setBackgroundResource(iconWhite);
        menuItem.findViewById(R.id.icon_red).setBackgroundResource(iconRed);
        ((TextView) menuItem.findViewById(R.id.item_text)).setText(itemText);
        menuItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectItem(menuItem, itemId);
            }
        });

        menuItems.addView(menuItem);
        return menuItem;
    }

    public interface NavigationDrawerCallbacks {
        /**
         * Called when an item in the navigation drawer is selected.
         */
        void onNavigationDrawerItemSelected(int position);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        checkOrientation();
    }

    private void checkOrientation() {
        if (getResources().getConfiguration().orientation == 2) {
            menuHeader.setVisibility(View.GONE);
            menuFooter.setVisibility(View.GONE);
        } else {
            menuHeader.setVisibility(View.VISIBLE);
            menuFooter.setVisibility(View.VISIBLE);
        }
    }
}