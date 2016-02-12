package ir.medxhub.doctor.advertisement;

/**
 * Created by Maysam on 09/02/2016.
 */

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ir.medxhub.doctor.M;
import ir.medxhub.doctor.R;


public class ActivityAllAds extends AppCompatActivity
        implements OnClickListener {

    private static final String TAG_POSTS = "posts";
    private ArrayList<ListStructure> mCommentList = new ArrayList<>();
    FloatingActionButton fab;
    SwipeRefreshLayout swipe;
    TextView txtFilter, txtGroup, txtCityFilter;
    View popUpLayout;
    LinearLayoutManager manager;
    RecyclerView recyclerView;
    PersonAdapter adapter;
    LinearLayout viewGroup;
    String action = "all";
    String city;
    boolean isSwiped, filterByCity, selectCity;
    private boolean loading = true;
    static int part, previousTotal, firstVisibleItem, visibleItemCount, totalItemCount, currentRowId, position;
    private int visibleThreshold = 3;


    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.lytCall) {

        } else if (view.getId() == R.id.txtFilter) {
            int[] location = new int[2];
            currentRowId = position;
            View currentRow = view;
            view.getLocationOnScreen(location);
            Point point = new Point();
            point.x = location[0];
            point.y = location[1];
            showStatusPopup(ActivityAllAds.this, point);
        } else if (view.getId() == R.id.txtCityFilter) {
            filterByCity = true;
            View currentRow = view;
            int[] location = new int[2];
            view.getLocationOnScreen(location);
            Point point = new Point();
            point.x = location[0];
            point.y = location[1];
            showStatusPopup(ActivityAllAds.this, point);
        } else if (view.getId() == R.id.fab) {
            startActivity(new Intent(ActivityAllAds.this, ActivitySendAds.class));
        }
    }

    @TargetApi(VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ads_reas_all);
        this.getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        //initialize Views
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        txtCityFilter = (TextView) findViewById(R.id.txtCityFilter);
        txtFilter = (TextView) findViewById(R.id.txtFilter);
        txtGroup = (TextView) findViewById(R.id.txtGroup);
        swipe = (SwipeRefreshLayout) findViewById(R.id.swipe);
        fab = (FloatingActionButton) findViewById(R.id.fab);

        //initialize RecyclerView
        recyclerView = (RecyclerView) findViewById(R.id.recycleView);
        recyclerView.setHasFixedSize(true);
        adapter = new PersonAdapter(mCommentList);
        manager = new WrapContentLinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);

        txtCityFilter.setOnClickListener(this);
        txtFilter.setOnClickListener(this);
        fab.setOnClickListener(this);


        swipe.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                recyclerView.setClickable(false);
                //   mCommentList.clear();
                isSwiped = true;
                previousTotal = 0;
                part = 0;
                updateJSONdata();
            }
        });


          /*
          visibleItemCount  --->>  how many item can show in screen
          totalItemCount    --->>  number of items that load in memory
          firstVisibleItem  --->>  first item that show in list
          part              --->>  how many item get from server in one step

           */

        recyclerView.addOnScrollListener(new OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerVie, int dx, int dy) {
                super.onScrolled(recyclerVie, dx, dy);

                visibleItemCount = recyclerView.getChildCount();
                totalItemCount = manager.getItemCount();
                firstVisibleItem = manager.findFirstVisibleItemPosition();

                if (loading) {
                    if (totalItemCount > previousTotal) {
                        loading = false;
                        previousTotal = totalItemCount;
                    }
                }
                if (!loading && (totalItemCount - visibleItemCount)
                        <= (firstVisibleItem + visibleThreshold)) {
                    part++;
                    updateJSONdata();
                    loading = true;
                }
            }
        });


        M.setCustomFont((ViewGroup) (getWindow().getDecorView()));

    }


    @Override
    protected void onResume() {
        super.onResume();
        M.currentActivity = this;
        part = 0;
        previousTotal = 0;
        isSwiped = true;
        updateJSONdata();

    }


    public void updateJSONdata() {
        swipe.setRefreshing(true);

        if (selectCity) {
            action = "filter";
        }

        String READ_COMMENTS_URL = "http://adinapp.ir/webservice/comments.php";
        StringRequest stringRequest = new StringRequest(Method.POST, READ_COMMENTS_URL,
                new Listener<String>() {
                    @Override
                    public void onResponse(String string) {
                        try {
                            JSONObject response = new JSONObject(string);
                            JSONArray mComments = response.getJSONArray(TAG_POSTS);
                            if (isSwiped) {
                                mCommentList.clear();
                                isSwiped = false;
                            }
                            for (int i = 0; i < mComments.length(); i++) {
                                JSONObject c = mComments.getJSONObject(i);

                                String postId = c.getString("post_id");
                                String city = c.getString("city");
                                String title = c.getString("title");
                                String price = c.getString("price");
                                String img_count = c.getString("img_count");

                                if (price.equals("0")) {
                                    price = "توافقی";
                                } else {
                                    price = price + " تومان ";
                                }
                                String time = M.timeDifferenceCalculator(c.getString("time"));

                                ListStructure struct = new ListStructure();

                                struct.postId = postId;
                                struct.title = title;
                                struct.city = city;
                                struct.time = time;
                                struct.price = price;
                                struct.img_count = img_count;
                                struct.url = "http://adinapp.ir/webservice/uploads/" + postId + "_1.png";

                                mCommentList.add(struct);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        swipe.setRefreshing(false);
                        adapter.notifyDataSetChanged();
                    }

                }, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();
                params.put("charset", "utf-8");
                params.put("action", action);
                params.put("part", "" + part);
                if (selectCity) {
                    params.put("city", city);
                }
                if (action.equals("fav")) {
                    params.put("username", M.preference.getString("username", ""));
                }
                return params;
            }
        };

        //for avoid twice data send in volley
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        M.getInstance().addToRequestQueue(stringRequest);

    }


    private void showStatusPopup(final Activity context, Point p) {

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int height = displaymetrics.heightPixels;
        int width = displaymetrics.widthPixels;

        // Inflate the popup_layout.xml
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        popUpLayout = layoutInflater.inflate(R.layout.ads_filter_popup, null);
        viewGroup = (LinearLayout) popUpLayout.findViewById(R.id.llSortChangePopup);
        TextView txtTitle = (TextView) popUpLayout.findViewById(R.id.txtTitle);
        txtTitle.setTypeface(M.typeFace);
        if (filterByCity) {
            fillPopUpCityLayout();
            txtTitle.setText("شهر مورد نظر را انتخاب کنید.");
        } else {
            txtTitle.setText("فیلتر مورد نظر را انتخاب کنبد.");
            fillPopUpLayout();
        }
        // Creating the PopupWindow
        PopupWindow changeStatusPopUp = new PopupWindow(context);
        changeStatusPopUp.setContentView(popUpLayout);
        changeStatusPopUp.setWidth(width);
        changeStatusPopUp.setHeight(height);
        changeStatusPopUp.setFocusable(true);
        //  changeStatusPopUp.setBackgroundDrawable(new BitmapDrawable());
        changeStatusPopUp.showAtLocation(popUpLayout, Gravity.CENTER, 0, 0);

    }

    //for test
    public void fillPopUpLayout() {


        String[] City = (getResources().getStringArray(R.array.city));
        LayoutInflater layoutInflater = (LayoutInflater) M.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        for (int i = 0; i < City.length; i++) {
            View popUpCityLayout = layoutInflater.inflate(R.layout.ads_filtter_single, null);
            TextView txt = (TextView) popUpCityLayout.findViewById(R.id.txt);
            txt.setTypeface(M.typeFace);
            txt.setText(City[i]);
            viewGroup.addView(popUpCityLayout);


        }

    }


    //for test
    public void fillPopUpCityLayout() {
        filterByCity = false;

        String[] City = (getResources().getStringArray(R.array.city));
        LayoutInflater layoutInflater = (LayoutInflater) M.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        for (int i = 0; i < City.length; i++) {
            View popUpCityLayout = layoutInflater.inflate(R.layout.ads_filtter_single, null);
            TextView txt = (TextView) popUpCityLayout.findViewById(R.id.txt);
            txt.setTypeface(M.typeFace);
            txt.setText(City[i]);
            viewGroup.addView(popUpCityLayout);


        }
    }


    public class ListStructure {

        public String title;
        public String price;
        public String city;
        public String time;
        public String url;
        public String postId;
        public String img_count;


    }


    // for avoid recycle view crash index out of bound
    public class WrapContentLinearLayoutManager extends LinearLayoutManager {
        public WrapContentLinearLayoutManager(Context context) {
            super(context);

        }

        //... constructor
        @Override
        public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
            try {
                super.onLayoutChildren(recycler, state);
            } catch (IndexOutOfBoundsException e) {
                Log.e("probe", "meet a IOOBE in RecyclerView");
            }
        }
    }


    public class PersonAdapter extends RecyclerView.Adapter<PersonAdapter.PersonViewHolder> {

        public class PersonViewHolder extends RecyclerView.ViewHolder {

            public ViewGroup lytRoot;
            public TextView txtTitle;
            public TextView txtCity;
            public TextView txtTime;
            public TextView txtPrice;
            public ImageView img;

            public PersonViewHolder(final View view) {
                super(view);
                lytRoot = (ViewGroup) view.findViewById(R.id.lytRoot);
                txtTitle = (TextView) view.findViewById(R.id.txtTitle);
                txtCity = (TextView) view.findViewById(R.id.txtCity);
                txtTime = (TextView) view.findViewById(R.id.txtTime);
                txtPrice = (TextView) view.findViewById(R.id.txtPrice);
                img = (ImageView) view.findViewById(R.id.img);


                M.setCustomFont((lytRoot));
            }
        }


        private ArrayList<ListStructure> names = new ArrayList<>();

        public PersonAdapter(ArrayList<ListStructure> names) {
            this.names = names;
        }

        @Override
        public PersonViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.ads_single_ads, viewGroup, false);

            return new PersonViewHolder(view);

        }

        @Override
        public void onBindViewHolder(PersonViewHolder personViewHolder, final int i) {
            final ActivityAllAds.ListStructure item = names.get(i);
            personViewHolder.txtTitle.setText(item.title);
            personViewHolder.txtCity.setText(item.city);
            personViewHolder.txtTime.setText(item.time);
            personViewHolder.txtPrice.setText(item.price);
            Glide.with(M.context).
                    load(item.url).
                    crossFade().
                    centerCrop().
                    override(128, 96).
                    placeholder(R.drawable.loading).
                    error(R.drawable.failed).
                    into(personViewHolder.img);


            personViewHolder.lytRoot.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    Intent intent = new Intent(M.context, ActivityViewAds.class);
                    intent.putExtra("postId", item.postId);
                    intent.putExtra("img_count", item.img_count);
                    M.currentActivity.startActivity(intent);
                }
            });

        }

        @Override
        public int getItemCount() {
            return names.size();
        }

        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);

        }
    }

}
