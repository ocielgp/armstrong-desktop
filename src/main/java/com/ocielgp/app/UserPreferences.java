package com.ocielgp.app;

import com.jfoenix.controls.JFXRadioButton;
import com.ocielgp.utilities.Pagination;
import javafx.beans.value.ChangeListener;
import javafx.scene.control.ToggleGroup;

import java.io.File;
import java.util.HashMap;
import java.util.prefs.Preferences;

public class UserPreferences {
    private static final Preferences PREFERENCES = Preferences.userNodeForPackage(UserPreferences.class);
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
        // database: remote
        DEFAULT_PREFERENCES.put("DB_HOST_1", "db-gym-dev.ctxmkfz7w6pq.us-east-2.rds.amazonaws.com");
        DEFAULT_PREFERENCES.put("DB_PORT_1", "3306");
        DEFAULT_PREFERENCES.put("DB_USER_1", "ociel");
        DEFAULT_PREFERENCES.put("DB_PASSWORD_1", "Ociel4580");

        // system
        DEFAULT_PREFERENCES.put("THEME", "day-theme"); // day-theme | night theme

        DEFAULT_PREFERENCES.put("FOLDER_PATH", "");
        DEFAULT_PREFERENCES.put("LAST_GYM", 1);
        DEFAULT_PREFERENCES.put("MAX_DAYS_FINGERPRINTS", 7); // fingerprint scanner query
        DEFAULT_PREFERENCES.put("PAGINATION_MAX_ROWS", 20); // pagination

        // member filters
        DEFAULT_PREFERENCES.put("FILTER_MEMBER_ALL_GYMS", false);
        DEFAULT_PREFERENCES.put("FILTER_MEMBER_ACTIVE_MEMBERS", false);
        DEFAULT_PREFERENCES.put("FILTER_MEMBER_DEBTORS", false);
        DEFAULT_PREFERENCES.put("FILTER_MEMBER_GENDERS", 0); // 0 all, 1 = male, 2 = female
        DEFAULT_PREFERENCES.put("FILTER_MEMBER_ORDER_BY", 0); // 0 = id desc, 1 = id asc

        String test = PREFERENCES.get("THEME", null);
        if (test == null) {
            UserPreferences.CreatePreferences();
        }
    }

    private static void CreatePreferences() {
        DEFAULT_PREFERENCES.forEach((key, value) -> {
            if (value.getClass() == String.class) {
                PREFERENCES.put(key, (String) value);

            } else if (value.getClass() == Integer.class) {
                PREFERENCES.putInt(key, (int) value);
            } else {
                PREFERENCES.putBoolean(key, (boolean) value);
            }
        });
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

    public static void setFolderPath(File file) {
        File parentFile = file.getParentFile();
        if (parentFile != null && file.exists() && parentFile.isDirectory()) {
            PREFERENCES.put("FOLDER_PATH", parentFile.toString());
        }
    }

    public static File getFolderPath() {
        String path = PREFERENCES.get("FOLDER_PATH", DEFAULT_PREFERENCES.get("FOLDER_PATH").toString());
        File folder = new File(path);
        return (folder.exists() && folder.isDirectory()) ? folder : null;
    }

    public static void createSelectedToggleProperty(ToggleGroup toggle, String radioButtonPrefix, String preference, Pagination pagination) {
        int selectPreviousState = getPreferenceInt(preference);
        for (byte i = 0; i < toggle.getToggles().size(); i++) {
            if (((JFXRadioButton) toggle.getToggles().get(i)).getId().equals(radioButtonPrefix + selectPreviousState)) {
                toggle.getToggles().get(i).setSelected(true);
            }
        }
        toggle.selectedToggleProperty().addListener((observableValue, oldValue, newValue) -> {
            if (oldValue != null && oldValue != newValue) {
                JFXRadioButton selected = (JFXRadioButton) newValue;
                setPreference(preference, String.valueOf(selected.getId().charAt(selected.getId().length() - 1)));
                pagination.restartTable();
            }
        });
    }

    public static ChangeListener<Object> listenerSaver(String preference, Pagination pagination) {
        return (observableValue, oldValue, newValue) -> {
            setPreference(preference, Boolean.parseBoolean(newValue.toString()));
            pagination.restartTable();
        };
    }

}
