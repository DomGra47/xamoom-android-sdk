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
import android.text.TextUtils;
import android.util.Log;

import com.xamoom.android.xamoomsdk.Resource.Content;
import com.xamoom.android.xamoomsdk.Resource.ContentBlock;
import com.xamoom.android.xamoomsdk.Storage.TableContracts.OfflineEnduserContract;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;

public class ContentDatabaseAdapter extends DatabaseAdapter {
  private static ContentDatabaseAdapter mSharedInstance;
  private ContentBlockDatabaseAdapter mContentBlockDatabaseAdapter;
  private SystemDatabaseAdapter mSystemDatabaseAdapter;

  public static ContentDatabaseAdapter getInstance(Context context) {
    if (mSharedInstance == null) {
      mSharedInstance = new ContentDatabaseAdapter(context);
    }
    return mSharedInstance;
  }

  private ContentDatabaseAdapter(Context context) {
    super(context);
  }

  public Content getContent(String jsonId) {
    String selection = OfflineEnduserContract.ContentEntry.COLUMN_NAME_JSON_ID + " = ?";
    String[] selectionArgs = { jsonId };

    return getContent(selection, selectionArgs);
  }

  public Content getContent(long row) {
    String selection = OfflineEnduserContract.ContentEntry._ID + " = ?";
    String[] selectionArgs = { String.valueOf(row) };

    return getContent(selection, selectionArgs);
  }

  private Content getContent(String selection, String[] selectionArgs) {
    open();
    Cursor cursor = queryContent(selection, selectionArgs);
    ArrayList<Content> contents = cursorToContents(cursor);
    close();

    if (contents.size() > 0) {
      return contents.get(0);
    }
    return null;
  }

  public ArrayList<Content> getAllContents() {
    open();
    Cursor cursor = queryContent(null, null);

    ArrayList<Content> contents = cursorToContents(cursor);

    close();
    return contents;
  }

  public ArrayList<Content> getContents(String name) {
    String selection = "LOWER(" + OfflineEnduserContract.ContentEntry.COLUMN_NAME_TITLE + ") LIKE LOWER(?)";
    String[] selectionArgs = { "%"+name+"%" };

    open();
    Cursor cursor = queryContent(selection, selectionArgs);

    ArrayList<Content> contents = cursorToContents(cursor);

    close();

    return contents;
  }

  public ArrayList<Content> getContentsWithFileId(String urlString) {
    String selection = OfflineEnduserContract.ContentEntry.COLUMN_NAME_PUBLIC_IMAGE_URL + " = ?";
    String[] selectionArgs = { urlString };

    open();
    Cursor cursor = queryContent(selection, selectionArgs);

    ArrayList<Content> contents = cursorToContents(cursor);

    close();

    return contents;
  }

  public ArrayList<Content> getRelatedContents(long menuRow) {
    String selection = OfflineEnduserContract.ContentEntry.COLUMN_NAME_MENU_RELATION + " = ?";
    String[] selectionArgs = { String.valueOf(menuRow) };

    open();
    Cursor cursor = queryContent(selection, selectionArgs);
    ArrayList<Content> contents = cursorToContents(cursor);

    close();
    return contents;
  }

  public long insertOrUpdateContent(Content content, boolean hasMenu, long menuRow) {
    ContentValues values = new ContentValues();
    values.put(OfflineEnduserContract.ContentEntry.COLUMN_NAME_JSON_ID, content.getId());
    values.put(OfflineEnduserContract.ContentEntry.COLUMN_NAME_TITLE, content.getTitle());
    values.put(OfflineEnduserContract.ContentEntry.COLUMN_NAME_DESCRIPTION, content.getDescription());
    values.put(OfflineEnduserContract.ContentEntry.COLUMN_NAME_LANGUAGE, content.getLanguage());
    values.put(OfflineEnduserContract.ContentEntry.COLUMN_NAME_CATEGORY, content.getCategory());
    values.put(OfflineEnduserContract.ContentEntry.COLUMN_NAME_PUBLIC_IMAGE_URL, content.getPublicImageUrl());
    if (content.getTags() != null) {
      values.put(OfflineEnduserContract.ContentEntry.COLUMN_NAME_TAGS,
          TextUtils.join(",", content.getTags()));
    }

    if (content.getCustomMeta() != null) {
      String json = new JSONObject(content.getCustomMeta()).toString();
      values.put(OfflineEnduserContract.ContentEntry.COLUMN_NAME_CUSTOM_META,
          json);
    }

    if (hasMenu) {
      values.put(OfflineEnduserContract.ContentEntry.COLUMN_NAME_MENU_RELATION, menuRow);
    }

    if (content.getSystem() != null) {
      long systemRow = getSystemDatabaseAdapter().insertOrUpdateSystem(content.getSystem());
      if (systemRow != -1) {
        values.put(OfflineEnduserContract.ContentEntry.COLUMN_NAME_SYSTEM_RELATION,
            systemRow);
      }
    }

    long row = getPrimaryKey(content.getId());
    if (row != -1) {
      updateContent(row, values);
    } else {
      open();
      row = mDatabase.insert(OfflineEnduserContract.ContentEntry.TABLE_NAME,
          null, values);
      close();
    }

    // inserts contentBlocks to database with foreign key to content
    if (content.getContentBlocks() != null) {
      for (ContentBlock cb : content.getContentBlocks()) {
        getContentBlockDatabaseAdapter().insertOrUpdate(cb, row);
      }
    }

    return row;
  }

  public boolean deleteContent(String jsonId) {
    String selection = OfflineEnduserContract.ContentEntry.COLUMN_NAME_JSON_ID + " = ?";
    String[] selectionArgs = { jsonId };

    open();
    int rowsAffected = mDatabase.delete(OfflineEnduserContract.ContentEntry.TABLE_NAME, selection,
        selectionArgs);
    close();

    return rowsAffected >= 1;
  }

  private int updateContent(long row, ContentValues values) {
    String selection = OfflineEnduserContract.ContentEntry._ID + " = ?";
    String[] selectionArgs = { String.valueOf(row) };

    open();
    int rowsUpdated = mDatabase.update(
        OfflineEnduserContract.ContentEntry.TABLE_NAME,
        values,
        selection,
        selectionArgs);
    close();

    return rowsUpdated;
  }

  public long getPrimaryKey(String jsonId) {
    String selection = OfflineEnduserContract.ContentBlockEntry.COLUMN_NAME_JSON_ID + " = ?";
    String[] selectionArgs = { jsonId };

    open();
    Cursor cursor = queryContent(selection, selectionArgs);
    if (cursor != null) {
      if (cursor.moveToFirst()) {
        long id = cursor.getInt(cursor.getColumnIndex(OfflineEnduserContract.ContentBlockEntry._ID));
        close();
        return id;
      }
    }
    close();
    return -1;
  }

  private Cursor queryContent(String selection, String[] selectionArgs) {
    Cursor cursor = mDatabase.query(
        OfflineEnduserContract.ContentEntry.TABLE_NAME,
        OfflineEnduserContract.ContentEntry.PROJECTION,
        selection,
        selectionArgs,
        null,
        null,
        null
    );

    return cursor;
  }

  private ArrayList<Content> cursorToContents(Cursor cursor) {
    ArrayList<Content> contents = new ArrayList<>();

    while (cursor.moveToNext()) {
      Content content = new Content();
      content.setId(cursor.getString(cursor.getColumnIndex(
          OfflineEnduserContract.ContentEntry.COLUMN_NAME_JSON_ID)));
      content.setTitle(cursor.getString(cursor.getColumnIndex(
          OfflineEnduserContract.ContentEntry.COLUMN_NAME_TITLE)));
      content.setDescription(cursor.getString(cursor.getColumnIndex(
          OfflineEnduserContract.ContentEntry.COLUMN_NAME_DESCRIPTION)));
      content.setLanguage(cursor.getString(cursor.getColumnIndex(
          OfflineEnduserContract.ContentEntry.COLUMN_NAME_LANGUAGE)));
      content.setCategory(cursor.getInt(cursor.getColumnIndex(
          OfflineEnduserContract.ContentEntry.COLUMN_NAME_CATEGORY)));
      String tags = cursor.getString(cursor
          .getColumnIndex(OfflineEnduserContract.ContentEntry.COLUMN_NAME_TAGS));

      if (tags != null) {
        content.setTags(Arrays.asList(tags.split(",")));
      }

      String customMetaJson = cursor.getString(cursor.getColumnIndex(
          OfflineEnduserContract.ContentEntry.COLUMN_NAME_CUSTOM_META));
      if (customMetaJson != null) {
        try {
          JSONObject jsonData = new JSONObject(customMetaJson);
          HashMap<String, String> outMap = new HashMap<String, String>();
          Iterator<String> iter = jsonData.keys();
          while (iter.hasNext()) {
            String name = iter.next();
            outMap.put(name, jsonData.getString(name));
          }
          content.setCustomMeta(outMap);
        } catch (JSONException e) {
          // customMeta will be null
          Log.e(ContentDatabaseAdapter.class.getSimpleName(),
              "Cannot parse customMetaJson from sqlite. Error: " + e.toString());
        }
      }
      content.setPublicImageUrl(cursor.getString(cursor.getColumnIndex(
          OfflineEnduserContract.ContentEntry.COLUMN_NAME_PUBLIC_IMAGE_URL)));
      content.setContentBlocks(relatedBlocks(cursor
          .getLong(cursor.getColumnIndex(OfflineEnduserContract.ContentEntry._ID))));
      content.setSystem(getSystemDatabaseAdapter().getSystem(cursor.getLong(
          cursor.getColumnIndex(OfflineEnduserContract.ContentEntry.COLUMN_NAME_SYSTEM_RELATION))));
      contents.add(content);
    }

    return contents;
  }

  public ArrayList<ContentBlock> relatedBlocks(long id) {
    ArrayList<ContentBlock> blocks = getContentBlockDatabaseAdapter().getRelatedContentBlocks(id);
    Collections.sort(blocks, new Comparator<ContentBlock>() {
      @Override
      public int compare(ContentBlock cb1, ContentBlock cb2) {
        return cb1.getId().compareTo(cb2.getId());
      }
    });
    return blocks;
  }

  // setter

  public ContentBlockDatabaseAdapter getContentBlockDatabaseAdapter() {
    if (mContentBlockDatabaseAdapter == null) {
      mContentBlockDatabaseAdapter = ContentBlockDatabaseAdapter.getInstance(mContext);
    }
    return mContentBlockDatabaseAdapter;
  }

  public SystemDatabaseAdapter getSystemDatabaseAdapter() {
    if (mSystemDatabaseAdapter == null) {
      mSystemDatabaseAdapter = SystemDatabaseAdapter.getInstance(mContext);
    }
    return mSystemDatabaseAdapter;
  }

  public void setContentBlockDatabaseAdapter(ContentBlockDatabaseAdapter contentBlockDatabaseAdapter) {
    mContentBlockDatabaseAdapter = contentBlockDatabaseAdapter;
  }
}
