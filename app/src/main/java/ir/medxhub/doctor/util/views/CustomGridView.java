package ir.medxhub.doctor.util.views;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.GridView;

public class CustomGridView extends GridView {
    private int oldCount = 0;

    public CustomGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int itemCount = getColumnsCount();
        if (getCount() != oldCount) {
            oldCount = getCount();
            int columnCount = getCount() % 3 == 0 ? getCount() / itemCount : (getCount() / itemCount) + 1;
            android.view.ViewGroup.LayoutParams params = getLayoutParams();
            params.height = columnCount * (oldCount > 0 ? getChildAt(0).getHeight() : 0);
            setLayoutParams(params);
        }

        super.onDraw(canvas);
    }

    private int getColumnsCount() {
        int count;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            count = getNumColumns();
        } else {
            if (getResources().getConfiguration().orientation == 2) {
                count = 4;
            } else {
                count = 3;
            }
        }

        return count;
    }

}
