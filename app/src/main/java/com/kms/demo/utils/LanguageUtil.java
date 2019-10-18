package com.kms.demo.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.text.TextUtils;

import com.kms.demo.component.ui.view.MainActivity;
import com.kms.demo.config.AppSettings;

import java.util.Locale;

/**
 * @author matrixelement
 */
public class LanguageUtil {


    private LanguageUtil() {

    }

    public static Locale getLocale(Context context) {

        Resources resources = context.getResources();

        Configuration configuration = resources.getConfiguration();

        String language = AppSettings.getInstance().getLanguage();

        if (TextUtils.isEmpty(language)) {
            Locale locale = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                locale = configuration.getLocales().get(0);
            } else {
                locale = configuration.locale;
            }
            if (locale.getLanguage().equals(Locale.US.getLanguage())) {
                return Locale.US;
            } else {
                return Locale.CHINA;
            }

        } else {
            if (language.equals(Locale.ENGLISH.getLanguage())) {
                return Locale.US;
            } else {
                return Locale.CHINA;
            }

        }
    }

    public static void switchLanguage(Context context, Locale locale) {

        AppSettings.getInstance().setLanguage(locale.getLanguage());

        MainActivity.restart(context);
    }

}
