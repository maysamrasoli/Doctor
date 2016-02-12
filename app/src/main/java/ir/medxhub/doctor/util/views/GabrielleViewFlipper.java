package ir.medxhub.doctor.util.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ViewFlipper;

public class GabrielleViewFlipper extends ViewFlipper {
    public GabrielleViewFlipper(Context context) {
        super(context);
    }

    public GabrielleViewFlipper(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDetachedFromWindow() {
        try {
            super.onDetachedFromWindow();
        } catch (IllegalArgumentException e) {
            stopFlipping();
        }
    }
}