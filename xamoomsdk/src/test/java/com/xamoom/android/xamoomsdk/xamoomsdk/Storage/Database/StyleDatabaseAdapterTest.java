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

package com.xamoom.android.xamoomsdk.xamoomsdk.Storage.Database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.xamoom.android.xamoomsdk.BuildConfig;
import com.xamoom.android.xamoomsdk.Resource.Style;
import com.xamoom.android.xamoomsdk.Storage.Database.DatabaseHelper;
import com.xamoom.android.xamoomsdk.Storage.Database.StyleDatabaseAdapter;
import com.xamoom.android.xamoomsdk.Storage.Database.SystemDatabaseAdapter;
import com.xamoom.android.xamoomsdk.Storage.TableContracts.OfflineEnduserContract;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.verification.VerificationMode;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.stub;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class StyleDatabaseAdapterTest {

  private StyleDatabaseAdapter mStyleDatabaseAdapter;
  private DatabaseHelper mMockedDatabaseHelper;
  private SQLiteDatabase mMockedDatabase;
  private Style mStyle;

  @Before
  public void setup() {
    mStyleDatabaseAdapter =
        StyleDatabaseAdapter.getInstance(RuntimeEnvironment.application);

    mMockedDatabaseHelper = Mockito.mock(DatabaseHelper.class);
    mMockedDatabase = Mockito.mock(SQLiteDatabase.class);
    mStyleDatabaseAdapter.setDatabaseHelper(mMockedDatabaseHelper);

    stub(mMockedDatabaseHelper.getWritableDatabase())
        .toReturn(mMockedDatabase);
    stub(mMockedDatabaseHelper.getReadableDatabase())
        .toReturn(mMockedDatabase);

    mStyle = new Style();
    mStyle.setId("1");
    mStyle.setBackgroundColor("back");
    mStyle.setForegroundFontColor("fore");
    mStyle.setHighlightFontColor("high");
    mStyle.setChromeHeaderColor("chrome");
    mStyle.setIcon("icon");
    mStyle.setCustomMarker("marker");
  }

  @Test
  public void testGetStyle() {
    Cursor mockCursor = Mockito.mock(Cursor.class);
    stub(mMockedDatabase.query(
        anyString(),
        Mockito.any(String[].class),
        anyString(),
        Mockito.any(String[].class),
        anyString(),
        anyString(),
        anyString()
    )).toReturn(mockCursor);
    stub(mockCursor.getCount()).toReturn(1);
    stub(mockCursor.moveToFirst()).toReturn(true);

    Style savedStyle = mStyleDatabaseAdapter.getStyle("1");

    Assert.assertNotNull(savedStyle);
    verify(mockCursor, times(7)).getString(Mockito.anyInt());
  }

  @Test
  public void testInserOrUpdateStyleInsertNew() {
    mStyleDatabaseAdapter.insertOrUpdateStyle(mStyle, 0);

    verify(mMockedDatabase).insert(
        eq(OfflineEnduserContract.StyleEntry.TABLE_NAME), anyString(), any(ContentValues.class));
  }

  @Test
  public void testInserOrUpdateWithUpdate() {
    Cursor mockCursor = Mockito.mock(Cursor.class);
    stub(mMockedDatabase.query(
        anyString(),
        Mockito.any(String[].class),
        anyString(),
        Mockito.any(String[].class),
        anyString(),
        anyString(),
        anyString()
    )).toReturn(mockCursor);
    stub(mockCursor.moveToFirst()).toReturn(true);

    mStyleDatabaseAdapter.insertOrUpdateStyle(mStyle, 0);

    verify(mMockedDatabase).update(
        eq(OfflineEnduserContract.StyleEntry.TABLE_NAME), any(ContentValues.class),
        anyString(), any(String[].class));
  }

  @Test
  public void testDelete() {
    Mockito.stub(mMockedDatabase.delete(anyString(), anyString(), any(String[].class))).toReturn(1);

    boolean deleted = mStyleDatabaseAdapter.deleteStyle("1");

    junit.framework.Assert.assertTrue(deleted);
  }

  @Test
  public void testDeleteFail() {
    Mockito.stub(mMockedDatabase.delete(anyString(), anyString(), any(String[].class))).toReturn(0);

    boolean deleted = mStyleDatabaseAdapter.deleteStyle("1");

    junit.framework.Assert.assertFalse(deleted);
  }
}
