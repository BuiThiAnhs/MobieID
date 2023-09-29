package com.ITS.mobieid.custom_text;

import android.content.Context;
import android.graphics.Typeface;

public class Utils {
    private static Typeface robotoBoldTypeface;
    private static Typeface robotoLightTypeface;
    private static Typeface robotoItalicTypeface;

    public static Typeface getRobotoBoldTypeface(Context context) {
        if (robotoBoldTypeface == null)
        {
            robotoBoldTypeface = Typeface.createFromAsset(context.getAssets(),"font/Roboto-Bold.ttf");
        }
        return robotoBoldTypeface;
    }

    public static Typeface getRobotoLightTypeface(Context context) {
        if (robotoLightTypeface == null)
        {
            robotoLightTypeface = Typeface.createFromAsset(context.getAssets(),"font/Roboto-Light.ttf");
        }
        return robotoLightTypeface;
    }

    public static Typeface getRobotoItalicTypeface(Context context) {
        if (robotoItalicTypeface == null)
        {
            robotoItalicTypeface = Typeface.createFromAsset(context.getAssets(),"font/Roboto-Italic.ttf");
        }
        return robotoItalicTypeface;
    }
}
