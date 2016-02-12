package ir.medxhub.doctor.util.glider;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import static android.view.ViewGroup.LayoutParams;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class ParallaxPagerAdapter extends PagerAdapter {
    private int count = 0;
    private final Context context;
    private final ArrayList<View> recycleBin = new ArrayList<>();

    public ParallaxPagerAdapter(Context context) {
        this.context = context;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public int getCount() {
        return count;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view;
        if (!recycleBin.isEmpty()) {
            view = recycleBin.get(recycleBin.size() - 1);
            recycleBin.remove(recycleBin.size() - 1);
        } else {
            view = new View(context);
            view.setLayoutParams(new LayoutParams(MATCH_PARENT, MATCH_PARENT));
        }
        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        View view = (View) object;
        container.removeView(view);
        recycleBin.remove(view);

    }


    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }
}
