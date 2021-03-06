/*
* Copyright 2017 by xamoom GmbH <apps@xamoom.com>
*
* This file is part of some open source application.
*
* Some open source application is free software: you can redistribute
* it and/or modify it under the terms of the GNU General Public
* License as published by the Free Software Foundation, either
* version 2 of the License, or (at your option) any later version.
*
* Some open source application is distributed in the hope that it will
* be useful, but WITHOUT ANY WARRANTY; without even the implied warranty
* of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with xamoom-android-sdk. If not, see <http://www.gnu.org/licenses/>.
*
* author: Raphael Seher <raphael@xamoom.com>
*/

package com.xamoom.android.xamoomsdk.Storage.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.xamoom.android.xamoomsdk.Resource.SystemSetting;
import com.xamoom.android.xamoomsdk.Storage.TableContracts.OfflineEnduserContract;

public class SettingDatabaseAdapter extends DatabaseAdapter {
  private static SettingDatabaseAdapter mSharedInstance;

  public static SettingDatabaseAdapter getInstance(Context context) {
    if (mSharedInstance == null) {
      mSharedInstance = new SettingDatabaseAdapter(context);
    }
    return mSharedInstance;
  }

  private SettingDatabaseAdapter(Context context) {
    super(context);
  }

  public SystemSetting getSystemSetting(String jsonId) {
    String selection = OfflineEnduserContract.SettingEntry.
        COLUMN_NAME_JSON_ID + " = ?";
    String[] selectionArgs = {jsonId};

    return getSystemSetting(selection, selectionArgs);
  }

  public SystemSetting getSystemSetting(long systemRow) {
    String selection = OfflineEnduserContract.SettingEntry.
        _ID + " = ?";
    String[] selectionArgs = {String.valueOf(systemRow)};

    return getSystemSetting(selection, selectionArgs);
  }

  private SystemSetting getSystemSetting(String selection, String[] selectionArgs) {
    open();
    Cursor cursor = querySettings(selection, selectionArgs);
    SystemSetting setting = cursorToSetting(cursor);
    close();
    return setting;
  }

  public long insertOrUpdateSetting(SystemSetting setting, long systemRow) {
    ContentValues values = new ContentValues();
    values.put(OfflineEnduserContract.SettingEntry.COLUMN_NAME_JSON_ID,
        setting.getId());
    values.put(OfflineEnduserContract.SettingEntry.COLUMN_NAME_APPSTORE_ID,
        setting.getItunesAppId());
    values.put(OfflineEnduserContract.SettingEntry.COLUMN_NAME_PLAYSTORE_ID,
        setting.getGooglePlayAppId());
    if (systemRow != -1) {
      values.put(OfflineEnduserContract.SettingEntry.COLUMN_NAME_SYSTEM_RELATION,
          systemRow);
    }

    long row = getPrimaryKey(setting.getId());
    if (row != -1) {
      updateSetting(row, values);
    } else {
      open();
      row = mDatabase.insert(OfflineEnduserContract.SettingEntry.TABLE_NAME,
          null, values);
      close();
    }

    return row;
  }

  public boolean deleteSetting(String jsonId) {
    String selection = OfflineEnduserContract.SettingEntry.COLUMN_NAME_JSON_ID + " = ?";
    String[] selectionArgs = { jsonId };

    open();
    int rowsAffected = mDatabase.delete(OfflineEnduserContract.SettingEntry.COLUMN_NAME_JSON_ID, selection,
        selectionArgs);
    close();

    return rowsAffected >= 1;
  }


  private void updateSetting(long row, ContentValues values) {
    String selection = OfflineEnduserContract.
        SettingEntry._ID + " = ?";
    String[] selectionArgs = {String.valueOf(row)};

    open();
    int rows = mDatabase.update(
        OfflineEnduserContract.SettingEntry.TABLE_NAME,
        values, selection, selectionArgs);
    close();
  }

  private long getPrimaryKey(String jsonId) {
    String selection = OfflineEnduserContract.
        SettingEntry.COLUMN_NAME_JSON_ID + " = ?";
    String[] selectionArgs = {jsonId};

    open();
    Cursor cursor = querySettings(selection, selectionArgs);
    if (cursor != null && cursor.moveToFirst()) {
      long id = cursor.getLong(cursor.getColumnIndex(
          OfflineEnduserContract.SettingEntry._ID));
      close();
      return id;
    }
    return -1;
  }

  private SystemSetting cursorToSetting(Cursor cursor) {
    if (cursor.moveToFirst()) {
      SystemSetting setting = new SystemSetting();
      setting.setId(cursor.getString(cursor.getColumnIndex(
          OfflineEnduserContract.StyleEntry.COLUMN_NAME_JSON_ID)));
      setting.setItunesAppId(cursor.getString(cursor.getColumnIndex(
          OfflineEnduserContract.SettingEntry.COLUMN_NAME_APPSTORE_ID)));
      setting.setGooglePlayAppId(cursor.getString(
          cursor.getColumnIndex(OfflineEnduserContract.SettingEntry.COLUMN_NAME_PLAYSTORE_ID)));
      return setting;
    }
    return null;
  }

  private Cursor querySettings(String selection, String[] selectionArgs) {
    Cursor cursor = mDatabase.query(
        OfflineEnduserContract.SettingEntry.TABLE_NAME,
        OfflineEnduserContract.SettingEntry.PROJECTION,
        selection,
        selectionArgs,
        null,
        null,
        null
    );
    return cursor;
  }
}
