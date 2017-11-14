package com.themakersdirectory;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.preference.PreferenceManager;

import java.util.Locale;

/**
 * Created by xlethal on 3/29/16.
 */
public class AppManager {

    public static String kLanguageKey = "kLanguageKey";

    public static void initLanguage() {
        String defaultLanguage = Locale.getDefault().getLanguage();

        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(MyApplication.getAppContext());

        String languageToLoad;

        if (sharedPreferences.getString(kLanguageKey, "null").equals("null")) {
            languageToLoad = defaultLanguage;
            SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences
                    .edit();
            sharedPreferencesEditor.putString(kLanguageKey,
                    languageToLoad);
            sharedPreferencesEditor.apply();

        } else {
            languageToLoad = sharedPreferences.getString(kLanguageKey, "null");

        }

        Locale appLocale = new Locale(languageToLoad);
        Locale.setDefault(appLocale);
        Configuration config = new Configuration();
        config.locale = appLocale;
        MyApplication.getAppContext().getResources()
                .updateConfiguration(config, null);

    }

    public static boolean isArabic() {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(MyApplication.getAppContext());
        return sharedPreferences.getString(kLanguageKey, "null").equals("ar");
    }
}
