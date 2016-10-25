package com.xamoom.android.xamoomsdk.Storage.TableContracts;

import android.provider.BaseColumns;

import com.xamoom.android.xamoomsdk.Resource.Style;

/**
 * Created by raphaelseher on 24/10/2016.
 */

public class OfflineEnduserContract {
  private OfflineEnduserContract() {}
  public static final int DATABASE_VERSION = 1;
  public static final String DATABASE_NAME = "OfflineEnduser.db";
  public static final String TEXT_TYPE = " TEXT";
  public static final String COMMA_SEP = ",";

  /**
   * System
   */
  public static class SystemEntry implements BaseColumns {
    public static final String TABLE_NAME = "System";
    public static final String COLUMN_NAME_JSON_ID = "json_id";
    public static final String COLUMN_NAME_NAME = "name";
    public static final String COLUMN_NAME_URL = "url";
    public static final String COLUMN_NAME_STYLE = "style";
    public static final String COLUMN_NAME_SYSTEMSETTING = "setting";
    public static final String COLUMN_NAME_MENU = "menu";

    public static final String SYSTEM_CREATE_TABLE =
        "CREATE TABLE " + SystemEntry.TABLE_NAME + " (" +
            SystemEntry._ID + " INTEGER PRIMARY KEY," +
            SystemEntry.COLUMN_NAME_JSON_ID + TEXT_TYPE + COMMA_SEP +
            SystemEntry.COLUMN_NAME_NAME + TEXT_TYPE + COMMA_SEP +
            SystemEntry.COLUMN_NAME_URL + TEXT_TYPE + COMMA_SEP +
            SystemEntry.COLUMN_NAME_STYLE + TEXT_TYPE + COMMA_SEP +
            SystemEntry.COLUMN_NAME_SYSTEMSETTING + TEXT_TYPE + COMMA_SEP +
            SystemEntry.COLUMN_NAME_MENU + TEXT_TYPE + " )";

    public static  final String[] PROJECTION = {
          SystemEntry._ID,
          SystemEntry.COLUMN_NAME_JSON_ID,
          SystemEntry.COLUMN_NAME_NAME,
          SystemEntry.COLUMN_NAME_URL,
          SystemEntry.COLUMN_NAME_STYLE,
          SystemEntry.COLUMN_NAME_SYSTEMSETTING,
          SystemEntry.COLUMN_NAME_MENU
    };
  }

  /**
   * Style
   */
  public static class StyleEntry implements BaseColumns {
    public static final String TABLE_NAME = "Style";
    public static final String COLUMN_NAME_SYSTEM_ID = "system_ID";
    public static final String COLUMN_NAME_JSON_ID = "json_id";
    public static final String COLUMN_NAME_BACKGROUND_COLOR = "background_color";
    public static final String COLUMN_NAME_HIGHLIGHT_COLOR = "highlight_color";
    public static final String COLUMN_NAME_FOREGROUND_COLOR = "foreground_color";
    public static final String COLUMN_NAME_CHROME_HEADER_COLOR = "chrome_header_color";
    public static final String COLUMN_NAME_MAP_PIN = "map_pin";
    public static final String COLUMN_NAME_ICON = "icon";

    public static final String STYLE_CREATE_TABLE =
        "CREATE TABLE " + StyleEntry.TABLE_NAME + " (" +
            StyleEntry._ID + " INTEGER PRIMARY KEY," +
            StyleEntry.COLUMN_NAME_SYSTEM_ID + " INTEGER" + COMMA_SEP +
            StyleEntry.COLUMN_NAME_JSON_ID + TEXT_TYPE + COMMA_SEP +
            StyleEntry.COLUMN_NAME_BACKGROUND_COLOR + TEXT_TYPE + COMMA_SEP +
            StyleEntry.COLUMN_NAME_HIGHLIGHT_COLOR + TEXT_TYPE + COMMA_SEP +
            StyleEntry.COLUMN_NAME_FOREGROUND_COLOR + TEXT_TYPE + COMMA_SEP +
            StyleEntry.COLUMN_NAME_CHROME_HEADER_COLOR + TEXT_TYPE + COMMA_SEP +
            StyleEntry.COLUMN_NAME_MAP_PIN + TEXT_TYPE + COMMA_SEP +
            StyleEntry.COLUMN_NAME_ICON + TEXT_TYPE + " )";

    public static  final String[] PROJECTION = {
        StyleEntry._ID,
        StyleEntry.COLUMN_NAME_SYSTEM_ID,
        StyleEntry.COLUMN_NAME_JSON_ID,
        StyleEntry.COLUMN_NAME_BACKGROUND_COLOR,
        StyleEntry.COLUMN_NAME_HIGHLIGHT_COLOR,
        StyleEntry.COLUMN_NAME_FOREGROUND_COLOR,
        StyleEntry.COLUMN_NAME_CHROME_HEADER_COLOR,
        StyleEntry.COLUMN_NAME_MAP_PIN,
        StyleEntry.COLUMN_NAME_ICON,
    };
  }
}