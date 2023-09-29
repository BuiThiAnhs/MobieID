package com.ITS.mobieid.custom_text;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

public class RobotoBoldTextView extends AppCompatTextView {
    public RobotoBoldTextView(@NonNull Context context) {
        super(context);
        setFontTextView();
    }

    public RobotoBoldTextView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setFontTextView();
    }

    public RobotoBoldTextView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setFontTextView();
    }
    private void setFontTextView()
    {
        Typeface typeface= Utils.getRobotoLightTypeface(getContext());
        setTypeface(typeface);
    }
}
