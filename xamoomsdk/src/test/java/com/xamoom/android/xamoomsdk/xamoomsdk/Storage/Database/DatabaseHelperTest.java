package com.xamoom.android.xamoomsdk.xamoomsdk.Storage.Database;

import android.database.sqlite.SQLiteDatabase;

import com.xamoom.android.xamoomsdk.BuildConfig;
import com.xamoom.android.xamoomsdk.Storage.Database.DatabaseHelper;
import com.xamoom.android.xamoomsdk.Storage.TableContracts.OfflineEnduserContract;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class DatabaseHelperTest {

  private DatabaseHelper databaseHelper;

  @Before
  public void setup() {
    databaseHelper = new DatabaseHelper(RuntimeEnvironment.application);
  }

  @Test
  public void testConstructor() {
    DatabaseHelper databaseHelper = new DatabaseHelper(RuntimeEnvironment.application);

    Assert.assertNotNull(databaseHelper.getReadableDatabase());
    Assert.assertNotNull(databaseHelper);
  }

  @Test
  public void testOnCreate() {
    SQLiteDatabase mockedDb = Mockito.mock(SQLiteDatabase.class);

    databaseHelper.onCreate(mockedDb);

    Mockito.verify(mockedDb).execSQL(OfflineEnduserContract.SystemEntry.CREATE_TABLE);
    Mockito.verify(mockedDb).execSQL(OfflineEnduserContract.StyleEntry.CREATE_TABLE);
    Mockito.verify(mockedDb).execSQL(OfflineEnduserContract.SettingEntry.CREATE_TABLE);
    Mockito.verify(mockedDb).execSQL(OfflineEnduserContract.ContentBlockEntry.CREATE_TABLE);
  }
}
