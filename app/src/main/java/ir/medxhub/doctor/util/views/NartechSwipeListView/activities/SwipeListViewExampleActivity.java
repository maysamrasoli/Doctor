package ir.medxhub.doctor.util.views.NartechSwipeListView.activities;///*
// * Copyright (C) 2013 47 Degrees, LLC
// *  http://47deg.com
// *  hello@47deg.com
// *
// *  Licensed under the Apache License, Version 2.0 (the "License");
// *  you may not use this file except in compliance with the License.
// *  You may obtain a copy of the License at
// *
// *      http://www.apache.org/licenses/LICENSE-2.0
// *
// *  Unless required by applicable law or agreed to in writing, software
// *  distributed under the License is distributed on an "AS IS" BASIS,
// *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// *  See the License for the specific language governing permissions and
// *  limitations under the License.
// */
//
//package ir.medxhub.doctor.util.views.NartechSwipeListView.activities;
//
//import android.app.ProgressDialog;
//import android.content.pm.ApplicationInfo;
//import android.content.pm.PackageManager;
//import android.os.AsyncTask;
//import android.os.Bundle;
//import android.support.v4.app.FragmentActivity;
//import android.util.DisplayMetrics;
//import android.util.Log;
//import android.view.*;
//import android.widget.ListView;
//import ir.medxhub.doctor.util.views.NartechSwipeListView.BaseSwipeListViewListener;
//import ir.medxhub.doctor.util.views.NartechSwipeListView.SwipeListView;
//import ir.medxhub.doctor.R;
//import ir.medxhub.doctor.util.views.NartechSwipeListView.adapters.PackageAdapter;
//import ir.medxhub.doctor.util.views.NartechSwipeListView.adapters.PackageItem;
//import ir.medxhub.doctor.util.views.NartechSwipeListView.dialogs.AboutDialog;
//import ir.medxhub.doctor.util.views.NartechSwipeListView.utils.PreferencesManager;
//import ir.medxhub.doctor.util.views.NartechSwipeListView.utils.SettingsManager;
//
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//
//public class SwipeListViewExampleActivity extends FragmentActivity {
//
//    private static final int REQUEST_CODE_SETTINGS = 0;
//    private PackageAdapter adapter;
//    private List<PackageItem> data;
//
//    private SwipeListView swipeListView;
//
//    private ProgressDialog progressDialog;
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        setContentView(R.layout.swipe_list_view_activity);
//
//        data = new ArrayList<PackageItem>();
//
//        adapter = new PackageAdapter(this, data);
//
//        swipeListView = (SwipeListView) findViewById(R.id.example_lv_list);
//
////        swipeListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
////        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
////            swipeListView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
////
////                @Override
////                public void onItemCheckedStateChanged(ActionMode mode, int position,
////                                                      long id, boolean checked) {
////                    mode.setTitle("Selected (" + swipeListView.getCountSelected() + ")");
////                }
////
////                @Override
////                public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
////                    switch (item.getItemId()) {
////                        case R.id.menu_delete:
////                            swipeListView.dismissSelected();
////                            mode.finish();
////                            return true;
////                        default:
////                            return false;
////                    }
////                }
////
////                @Override
////                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
////                    MenuInflater inflater = mode.getMenuInflater();
////                    inflater.inflate(R.menu.menu_choice_items, menu);
////                    return true;
////                }
////
////                @Override
////                public void onDestroyActionMode(ActionMode mode) {
////                    swipeListView.unselectedChoiceStates();
////                }
////
////                @Override
////                public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
////                    return false;
////                }
////            });
////        }
//
//        swipeListView.setSwipeListViewListener(new BaseSwipeListViewListener() {
//            @Override
//            public void onOpened(int position, boolean toRight) {
//
//            }
//
//            @Override
//            public void onClosed(int position, boolean fromRight) {
//                DoctorView temp = getViewByPosition(position, swipeListView);
//                temp.findViewById(R.id.b_1).setVisibility(DoctorView.GONE);
//                temp.findViewById(R.id.b_2).setVisibility(DoctorView.GONE);
//            }
//
//            @Override
//            public void onListChanged() {
//            }
//
//            @Override
//            public void onMove(int position, float x) {
//
//            }
//
//            @Override
//            public void onStartOpen(int position, int action, boolean right) {
//                Log.d("swipe", String.format("onStartOpen %d - action %d", position, action));
//                DoctorView temp = getViewByPosition(position, swipeListView);
//                if (right) {
//                    temp.findViewById(R.id.b_1).setVisibility(DoctorView.GONE);
//                    temp.findViewById(R.id.b_2).setVisibility(DoctorView.VISIBLE);
//
//                } else {
//                    temp.findViewById(R.id.b_1).setVisibility(DoctorView.VISIBLE);
//                    temp.findViewById(R.id.b_2).setVisibility(DoctorView.GONE);
//
//                }
//            }
//
//            @Override
//            public void onStartClose(int position, boolean right) {
//                Log.d("swipe", String.format("onStartClose %d", position));
//            }
//
//            @Override
//            public void onClickFrontView(int position) {
//                Log.d("swipe", String.format("onClickFrontView %d", position));
//            }
//
//            @Override
//            public void onClickBackView(int position) {
//                Log.d("swipe", String.format("onClickBackView %d", position));
//            }
//
//            @Override
//            public void onDismiss(int[] reverseSortedPositions) {
//                for (int position : reverseSortedPositions) {
//                    data.remove(position);
//                }
//                adapter.notifyDataSetChanged();
//            }
//
//        });
//
//        swipeListView.setAdapter(adapter);
//
//        reload();
//
//        new ListAppTask().execute();
//
//        progressDialog = new ProgressDialog(this);
//        progressDialog.setMessage(getString(R.string.loading));
//        progressDialog.setCancelable(false);
//        progressDialog.show();
//
//
//    }
//
//    public static DoctorView getViewByPosition(int pos, ListView listView) {
//        final int firstListItemPosition = listView.getFirstVisiblePosition();
//        final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;
//
//        if (pos < firstListItemPosition || pos > lastListItemPosition) {
//            return listView.getAdapter().getView(pos, null, listView);
//        } else {
//            final int childIndex = pos - firstListItemPosition;
//            return listView.getChildAt(childIndex);
//        }
//    }
//    public int convertDpToPixel(float dp) {
//        DisplayMetrics metrics = getResources().getDisplayMetrics();
//        float px = dp * (metrics.densityDpi / 160f);
//        return (int) px;
//    }
//    private void reload() {
//        SettingsManager settings = SettingsManager.getInstance();
//        swipeListView.setSwipeMode(settings.getSwipeMode());
//        swipeListView.setSwipeActionLeft(settings.getSwipeActionLeft());
//        swipeListView.setSwipeActionRight(settings.getSwipeActionRight());
//        swipeListView.setOffsetLeft(convertDpToPixel(settings.getSwipeOffsetLeft()));
//        swipeListView.setOffsetRight(convertDpToPixel(settings.getSwipeOffsetRight()));
//        swipeListView.setAnimationTime(settings.getSwipeAnimationTime());
//        swipeListView.setSwipeOpenOnLongPress(settings.isSwipeOpenOnLongPress());
//    }
//
//
//
////    @Override
////    public boolean onCreateOptionsMenu(Menu menu) {
////        MenuInflater inflater = getMenuInflater();
////        inflater.inflate(R.menu.menu_app, menu);
////        return true;
////    }
////
////    @Override
////    public boolean onMenuItemSelected(int featureId, MenuItem item) {
////        boolean handled = false;
////        switch (item.getItemId()) {
////            case android.R.id.home: //Actionbar home/up icon
////                finish();
////                break;
////            case R.id.menu_settings:
////                Intent intent = new Intent(this, SettingsActivity.class);
////                startActivityForResult(intent, REQUEST_CODE_SETTINGS);
////                break;
////        }
////        return handled;
////    }
////
////    @Override
////    public void onActivityResult(int requestCode, int resultCode, Intent data) {
////        super.onActivityResult(requestCode, resultCode, data);
////        switch (requestCode) {
////            case REQUEST_CODE_SETTINGS:
////                reload();
////        }
////    }
//
//    public class ListAppTask extends AsyncTask<Void, Void, List<PackageItem>> {
//
//        protected List<PackageItem> doInBackground(Void... args) {
//            PackageManager appInfo = getPackageManager();
//            List<ApplicationInfo> listInfo = appInfo.getInstalledApplications(0);
//            Collections.sort(listInfo, new ApplicationInfo.DisplayNameComparator(appInfo));
//
//            List<PackageItem> data = new ArrayList<PackageItem>();
//
//            for (int index = 0; index < listInfo.size(); index++) {
//                try {
//                    ApplicationInfo content = listInfo.get(index);
//                    if ((content.flags != ApplicationInfo.FLAG_SYSTEM) && content.enabled) {
//                        if (content.icon != 0) {
//                            PackageItem item = new PackageItem();
//                            item.setName(getPackageManager().getApplicationLabel(content).toString());
//                            item.setPackageName(content.packageName);
//                            item.setIcon(getPackageManager().getDrawable(content.packageName, content.icon, content));
//                            data.add(item);
//                        }
//                    }
//                } catch (Exception e) {
//
//                }
//            }
//
//            return data;
//        }
//
//        protected void onPostExecute(List<PackageItem> result) {
//            data.clear();
//            data.addAll(result);
//            adapter.notifyDataSetChanged();
//            if (progressDialog != null) {
//                progressDialog.dismiss();
//                progressDialog = null;
//            }
//            if (PreferencesManager.getInstance(SwipeListViewExampleActivity.this).getShowAbout()) {
//                AboutDialog logOutDialog = new AboutDialog();
//                logOutDialog.show(getSupportFragmentManager(), "dialog");
//            }
//        }
//    }
//
//}
