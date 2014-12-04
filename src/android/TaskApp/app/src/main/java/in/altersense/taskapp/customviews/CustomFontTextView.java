package in.altersense.taskapp.customviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

import in.altersense.taskapp.R;

/**
 * Created by mahesmohan on 12/4/14.
 */
public class CustomFontTextView extends TextView {
    private String TAG = "CustomFontTextView";

    public CustomFontTextView(Context context) {
        super(context);
    }

    public CustomFontTextView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setCustomFont(context, attributeSet);
    }

    public CustomFontTextView(Context context, AttributeSet attributeSet, int defStyle) {
        super(context, attributeSet, defStyle);
        setCustomFont(context, attributeSet);
    }

    private void setCustomFont(Context context, AttributeSet attributeSet) {
        TypedArray a = context.obtainStyledAttributes(attributeSet, R.styleable.CustomFontTextView);
        String customFont = a.getString(R.styleable.CustomFontTextView_customFont);
        setCustomFont(context, customFont);
        a.recycle();
    }

    public boolean setCustomFont(Context context, String asset) {
        Typeface tf = null;
        try {
            tf = Typeface.createFromAsset(context.getAssets(), asset);
        } catch (Exception e) {
            Log.e(TAG, "Could not fet typeface: "+e.getMessage());
            e.printStackTrace();
            return false;
        }
        setTypeface(tf);
        return true;
    }
}
