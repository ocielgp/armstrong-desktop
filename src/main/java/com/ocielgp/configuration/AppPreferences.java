package com.ocielgp.configuration;

import java.util.HashMap;
import java.util.prefs.Preferences;

public class AppPreferences {
    private static final Preferences PREFERENCES = Preferences.userNodeForPackage(AppPreferences.class);
    private static final HashMap<String, Object> DEFAULT_PREFERENCES = new HashMap<>();

    static {
        // database: 0 = local, 1 = remote
        DEFAULT_PREFERENCES.put("DB_SOURCE", 1);
        DEFAULT_PREFERENCES.put("DB_NAME", "gym");
        // database: local
        DEFAULT_PREFERENCES.put("DB_HOST_0", "127.0.0.1");
        DEFAULT_PREFERENCES.put("DB_PORT_0", "3306");
        DEFAULT_PREFERENCES.put("DB_USER_0", "root");
        DEFAULT_PREFERENCES.put("DB_PASSWORD_0", "280580");
        // database: - remote
        DEFAULT_PREFERENCES.put("DB_HOST_1", "142.93.81.100");
        DEFAULT_PREFERENCES.put("DB_PORT_1", "3306");
        DEFAULT_PREFERENCES.put("DB_USER_1", "ociel");
        DEFAULT_PREFERENCES.put("DB_PASSWORD_1", "Ociel4580");

        // system
        DEFAULT_PREFERENCES.put("THEME", "day-theme"); // day-theme | night theme

        DEFAULT_PREFERENCES.put("LAST_GYM", 1);
        DEFAULT_PREFERENCES.put("MAX_DAYS_FINGERPRINTS", 7); // fingerprint scanner query
        DEFAULT_PREFERENCES.put("PAGINATION_MAX_ROWS", 20); // pagination

        // member filters
        DEFAULT_PREFERENCES.put("FILTER_MEMBER_ALL_GYMS", false);
        DEFAULT_PREFERENCES.put("FILTER_MEMBER_ACTIVE_MEMBERS", false);
        DEFAULT_PREFERENCES.put("FILTER_MEMBER_DEBTORS", false);
        DEFAULT_PREFERENCES.put("FILTER_MEMBER_GENDERS", 0); // 0 all, 1 = male, 2 = female
        DEFAULT_PREFERENCES.put("FILTER_MEMBER_ORDER_BY", 0); // 0 = id desc, 1 = id asc
    }

    public static void setPreference(String preference, String value) {
        PREFERENCES.put(preference, value);
    }

    public static void setPreference(String preference, int value) {
        PREFERENCES.putInt(preference, value);
    }

    public static void setPreference(String preference, boolean value) {
        PREFERENCES.putBoolean(preference, value);
    }

    public static String getPreferenceString(String preference) {
        return PREFERENCES.get(preference, (String) DEFAULT_PREFERENCES.get(preference));
    }

    public static int getPreferenceInt(String preference) {
        return PREFERENCES.getInt(preference, (int) DEFAULT_PREFERENCES.get(preference));
    }

    public static boolean getPreferenceBool(String preference) {
        return PREFERENCES.getBoolean(preference, (boolean) DEFAULT_PREFERENCES.get(preference));
    }
}
