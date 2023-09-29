package com.ITS.mobieid.custom_text;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;

public class RobotoLightEditText extends AppCompatEditText {
    public RobotoLightEditText(@NonNull Context context) {
        super(context);
        setFontTextView();
    }

    public RobotoLightEditText(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setFontTextView();
    }

    public RobotoLightEditText(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setFontTextView();
    }
    private void setFontTextView()
    {
        Typeface typeface= Utils.getRobotoItalicTypeface(getContext());
        setTypeface(typeface);
    }
}
