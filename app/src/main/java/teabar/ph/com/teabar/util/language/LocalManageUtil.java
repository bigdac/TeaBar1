package teabar.ph.com.teabar.util.language;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.LocaleList;
import android.util.DisplayMetrics;
import android.util.Log;


import java.util.Locale;

import teabar.ph.com.teabar.R;

public class LocalManageUtil {

    private static final String TAG = "LocalManageUtil";

    /**
     * 获取系统的locale
     *
     * @return Locale对象
     */
    public static Locale getSystemLocale(Context context) {
        return SPUtil.getInstance(context).getSystemCurrentLocal();
    }

    public static String getSelectLanguage(Context context) {
        switch (SPUtil.getInstance(context).getSelectLanguage()) {
            case 0:
                return context.getString(R.string.language_auto);
            case 1:
//                return context.getString(R.string.language_cn);
            case 2:
                return context.getString(R.string.language_traditional);
            case 3:
            default:
                return context.getString(R.string.language_en);
        }
    }

    /**
     * 获取选择的语言设置
     *
     * @param context
     * @return
     */
    public static Locale getSetLanguageLocale(Context context) {

        switch (SPUtil.getInstance(context).getSelectLanguage()) {
            case 0:
                return getSystemLocale(context);
            case 1:
                return Locale.CHINA;
            case 2:
                return Locale.TAIWAN;
            case 3:
            default:
                return Locale.ENGLISH;
        }
    }

    public static void saveSelectLanguage(Context context, int select) {
        SPUtil.getInstance(context).saveLanguage(select);
        setApplicationLanguage(context);
    }

    public static Context setLocal(Context context) {
        return updateResources(context, getSetLanguageLocale(context));
    }

    private static Context updateResources(Context context, Locale locale) {
        Locale.setDefault(locale);

        Resources res = context.getResources();
        Configuration config = new Configuration(res.getConfiguration());
        if (Build.VERSION.SDK_INT >= 17) {
            config.setLocale(locale);
            context = context.createConfigurationContext(config);
        } else {
            config.locale = locale;
            res.updateConfiguration(config, res.getDisplayMetrics());
        }
        return context;
    }

    /**
     * 设置语言类型
     */
    public static void setApplicationLanguage(Context context) {
        Resources resources = context.getApplicationContext().getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        Configuration config = resources.getConfiguration();
        Locale locale = getSetLanguageLocale(context);
        Log.e("DDDDDDDDFFFFFFFF", "setApplicationLanguage: -->"+locale.toLanguageTag()+"...."+locale.toString()+"...."+locale.getDisplayLanguage() );
        config.locale = locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            LocaleList localeList = new LocaleList(locale);
            LocaleList.setDefault(localeList);
            config.setLocales(localeList);
            context.getApplicationContext().createConfigurationContext(config);
            Locale.setDefault(locale);
        }
        resources.updateConfiguration(config, dm);
    }

    public static void saveSystemCurrentLanguage(Context context) {
        Locale locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            locale = LocaleList.getDefault().get(0);
        } else {
            locale = Locale.getDefault();
        }
        Log.d(TAG, locale.getLanguage());
        SPUtil.getInstance(context).setSystemCurrentLocal(locale);
    }

    public static void onConfigurationChanged(Context context){
        saveSystemCurrentLanguage(context);
        setLocal(context);
        setApplicationLanguage(context);
    }
    public static int getLanguage(Context context) {

        String language = getLanguageEnv();
        if (language.equals("zh-CN"))
            return 1;
        else if (language.equals("TW"))
            return 2;
        else
            return 3;
    }
    private static String getLanguageEnv() {
        Locale l = Locale.getDefault();
        String language = l.getLanguage();
        String country = l.getCountry().toLowerCase();
        if ("zh".equals(language)) {
            if ("cn".equals(country)) {
                language = "zh-CN";
            } else if ("tw".equals(country)) {
                language = "zh-TW";
            }
        } else if ("pt".equals(language)) {
            if ("br".equals(country)) {
                language = "pt-BR";
            } else if ("pt".equals(country)) {
                language = "pt-PT";
            }
        }
        return language;
    }
}
