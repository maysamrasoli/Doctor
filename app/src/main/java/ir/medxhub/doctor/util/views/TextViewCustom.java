package ir.medxhub.doctor.util.views;
/**
 * Created by Ali Arasteh on 8/25/14.
 */

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.text.Html;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.widget.TextView;
import ir.medxhub.doctor.R;

public class TextViewCustom extends TextView {
    private static final String TAG = "TextView";

    public TextViewCustom(Context context) {
        super(context);
        init();
    }

    public TextViewCustom(Context context, AttributeSet attrs) {
        super(context, attrs);
        setCustomFont(context, attrs);
        setDefaultGravity();
        init();
    }

    public TextViewCustom(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setCustomFont(context, attrs);
        setDefaultGravity();
        init();
    }

    private void setCustomFont(Context ctx, AttributeSet attrs) {
        TypedArray a = ctx.obtainStyledAttributes(attrs, R.styleable.TextViewCustom);
        String customFont = a.getString(R.styleable.TextViewCustom_customFont);
        setCustomFont(ctx, customFont);
        a.recycle();
    }

    public boolean setCustomFont(Context ctx, String asset) {
        Typeface tf = null;
        try {
            tf = Typeface.createFromAsset(ctx.getAssets(), asset);
        } catch (Exception e) {
            Log.e(TAG, "Could not get typeface: " + e.getMessage());
            return false;
        }

        setTypeface(tf);
        return true;
    }

    public void setDefaultGravity() {
        if (getGravity() != Gravity.CENTER && getGravity() != Gravity.CENTER_HORIZONTAL && getGravity() != Gravity.CENTER_VERTICAL && getGravity() != Gravity.LEFT) {
            setGravity(Gravity.RIGHT);
        }
    }

    private void init() {
        setText(Html.fromHtml(getText().toString()));
    }
}