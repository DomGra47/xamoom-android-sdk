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

package com.xamoom.android.xamoomsdk.xamoomsdk.Storage;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;

import com.xamoom.android.xamoomsdk.BuildConfig;
import com.xamoom.android.xamoomsdk.Storage.SimpleTagStorage;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class SimpleTagStorageTest {
  private SimpleTagStorage mSimpleTagStorage;
  private SharedPreferences mMockedPreferences;
  private SharedPreferences.Editor mMockedEditor;

  @SuppressLint("CommitPrefEdits")
  @Before
  public void setup() {
    mMockedPreferences = mock(SharedPreferences.class);
    mMockedEditor = mock(SharedPreferences.Editor.class);
    Mockito.stub(mMockedPreferences.edit()).toReturn(mMockedEditor);

    mSimpleTagStorage =
        new SimpleTagStorage(RuntimeEnvironment.application.getApplicationContext());
    mSimpleTagStorage.setSharedPreferences(mMockedPreferences);
  }

  @Test
  public void testSaveTags() {
    ArrayList<String> tags = new ArrayList<>();
    tags.add("tag1");
    tags.add("tag2");

    mSimpleTagStorage.saveTags(tags);

    Mockito.verify(mMockedEditor).putStringSet(eq("com.xamoom.android.sdk.offlineTags"),
        eq(new HashSet<String>(tags)));

  }

  @Test
  public void testGetTags() {
    ArrayList<String> tags = new ArrayList<>();
    tags.add("tag1");
    tags.add("tag2");

    Mockito.stub(mMockedPreferences.getStringSet(eq("com.xamoom.android.sdk.offlineTags"), (Set<String>) any()))
        .toReturn(new HashSet<String>(tags));

    ArrayList<String> savedTags = mSimpleTagStorage.getTags();

    Assert.assertEquals(tags, savedTags);
  }

}
