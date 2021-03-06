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
import android.content.CursorLoader;
import android.database.Cursor;

import com.xamoom.android.xamoomsdk.Resource.Content;
import com.xamoom.android.xamoomsdk.Resource.Menu;
import com.xamoom.android.xamoomsdk.Storage.TableContracts.OfflineEnduserContract;

public class MenuDatabaseAdapter extends DatabaseAdapter {
  private static MenuDatabaseAdapter mSharedInstance;
  private ContentDatabaseAdapter mContentDatabaseAdapter;

  public static MenuDatabaseAdapter getInstance(Context context) {
    if (mSharedInstance == null) {
      mSharedInstance = new MenuDatabaseAdapter(context);
    }
    return mSharedInstance;
  }

  private MenuDatabaseAdapter(Context context) {
    super(context);
  }

  public Menu getMenu(String jsonId) {
    String selection = OfflineEnduserContract.MenuEntry.COLUMN_NAME_JSON_ID  + " = ?";
    String[] selectionArgs = {jsonId};

    return getMenu(selection, selectionArgs);
  }

  public Menu getMenu(long row) {
    String selection = OfflineEnduserContract.MenuEntry._ID  + " = ?";
    String[] selectionArgs = {String.valueOf(row)};

    return getMenu(selection, selectionArgs);
  }

  public Menu getRelatedMenu(long systemRow) {
    String selection = OfflineEnduserContract.MenuEntry.
        COLUMN_NAME_SYSTEM_RELATION  + " = ?";
    String[] selectionArgs = {String.valueOf(systemRow)};

    return getMenu(selection, selectionArgs);
  }

  public Menu getMenu(String selection, String[] selectionArgs) {
    open();
    Cursor cursor = queryMenu(selection, selectionArgs);
    Menu menu = cursorToMenu(cursor);

    return menu;
  }

  public long insertOrUpdate(Menu menu, long systemRow) {
    ContentValues values = new ContentValues();
    values.put(OfflineEnduserContract.MenuEntry.COLUMN_NAME_JSON_ID, menu.getId());
    if (systemRow != -1) {
      values.put(OfflineEnduserContract.MenuEntry.COLUMN_NAME_SYSTEM_RELATION, systemRow);
    }

    long row = getPrimaryKey(menu.getId());
    if (row != -1) {
      updateMenu(row, values);
    } else {
      open();
      row = mDatabase.insert(OfflineEnduserContract.MenuEntry.TABLE_NAME,
          null, values);
      close();
    }

    if (menu.getItems() != null) {
      for (Content content : menu.getItems()) {
        getContentDatabaseAdapter().insertOrUpdateContent(content, true, row);
      }
    }

    return row;
  }

  public boolean deleteMenu(String jsonId) {
    String selection = OfflineEnduserContract.MenuEntry.COLUMN_NAME_JSON_ID + " = ?";
    String[] selectionArgs = { jsonId };

    open();
    int rowsAffected = mDatabase.delete(OfflineEnduserContract.MenuEntry.COLUMN_NAME_JSON_ID, selection,
        selectionArgs);
    close();

    return rowsAffected >= 1;
  }

  private int updateMenu(long row, ContentValues values) {
    String selection = OfflineEnduserContract.MenuEntry._ID + " = ?";
    String[] selectionArgs = {String.valueOf(row)};

    open();
    int rowsUpdated = mDatabase.update(OfflineEnduserContract.MenuEntry.TABLE_NAME, values,
        selection, selectionArgs);
    close();

    return rowsUpdated;
  }

  private long getPrimaryKey(String jsonId) {
    String selection = OfflineEnduserContract.MenuEntry.COLUMN_NAME_JSON_ID + " = ?";
    String[] selectionArgs = {jsonId};

    open();
    Cursor cursor = queryMenu(selection, selectionArgs);

    if (cursor != null) {
      if (cursor.moveToFirst()) {
        long id = cursor.getInt(cursor.getColumnIndex(OfflineEnduserContract.MenuEntry._ID));
        close();
        return id;
      }
    }
    close();
    return -1;
  }

  private Menu cursorToMenu(Cursor cursor) {
    if (cursor.moveToFirst()) {
      Menu menu = new Menu();
      menu.setId(cursor.getString(cursor
          .getColumnIndex(OfflineEnduserContract.MenuEntry.COLUMN_NAME_JSON_ID)));
      menu.setItems(getContentDatabaseAdapter().getRelatedContents(cursor.getLong(
          cursor.getColumnIndex(OfflineEnduserContract.MenuEntry._ID)
      )));
      return menu;
    }
    return null;
  }

  private Cursor queryMenu(String selection, String[] selectionArgs) {
    Cursor cursor = mDatabase.query(OfflineEnduserContract.MenuEntry.TABLE_NAME,
        OfflineEnduserContract.MenuEntry.PROJECTION,
        selection,
        selectionArgs,
        null, null, null);

    return cursor;
  }

  // setter


  public ContentDatabaseAdapter getContentDatabaseAdapter() {
    if (mContentDatabaseAdapter == null) {
      mContentDatabaseAdapter = ContentDatabaseAdapter.getInstance(mContext);
    }
    return mContentDatabaseAdapter;
  }

  public void setContentDatabaseAdapter(ContentDatabaseAdapter contentDatabaseAdapter) {
    mContentDatabaseAdapter = contentDatabaseAdapter;
  }
}
