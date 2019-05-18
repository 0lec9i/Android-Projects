package com.android.audiorecorder;

import android.content.Context;
import android.content.SharedPreferences;

public class MySharedPreferences {

    private static final String sharedPrefsFile = "AudioRecorderSharedPref";
    static final String spQuality = "Quality";
    static  final String spChannel = "Channel";
    public static final String spStorageLocation = "Storage";

    public static String loadStringSavedPreferences(String key, Context ctx) {
        String name = "";
        try {
            SharedPreferences sp = ctx.getSharedPreferences(sharedPrefsFile,
                    Context.MODE_PRIVATE);
            name = sp.getString(key, "Recorder/");
        } catch (Exception e) {
            //
        }
        return name;
    }
}
