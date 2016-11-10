package com.xamoom.android.xamoomsdk.xamoomsdk.Offline;

import android.location.Location;

import com.xamoom.android.xamoomsdk.APICallback;
import com.xamoom.android.xamoomsdk.APIListCallback;
import com.xamoom.android.xamoomsdk.BuildConfig;
import com.xamoom.android.xamoomsdk.Enums.ContentSortFlags;
import com.xamoom.android.xamoomsdk.Offline.OfflineEnduserApi;
import com.xamoom.android.xamoomsdk.Resource.Content;
import com.xamoom.android.xamoomsdk.Resource.Spot;
import com.xamoom.android.xamoomsdk.Storage.OfflineStorageManager;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import at.rags.morpheus.Error;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.contains;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.matches;
import static org.mockito.Matchers.notNull;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class OfflineEnduserApiTest {

  private OfflineEnduserApi mOfflineEnduserApi;
  private OfflineStorageManager mMockedOfflineStorageManager;

  @Before
  public void setup() {
    mOfflineEnduserApi = new OfflineEnduserApi(RuntimeEnvironment.application);
    mMockedOfflineStorageManager = Mockito.mock(OfflineStorageManager.class);

    mOfflineEnduserApi.setOfflineStorageManager(mMockedOfflineStorageManager);
  }

  @Test
  public void testGetContent() {
    final Content savedContent = new Content();
    savedContent.setId("1");

    Mockito.stub(mMockedOfflineStorageManager.getContent(eq("1234"))).toReturn(savedContent);

    mOfflineEnduserApi.getContent("1234", new APICallback<Content, List<Error>>() {
      @Override
      public void finished(Content result) {
        Assert.assertEquals(result, savedContent);
      }

      @Override
      public void error(List<Error> error) {

      }
    });

    Mockito.verify(mMockedOfflineStorageManager).getContent(eq("1234"));
  }

  @Test
  public void testGetContentByLocationIdentifier() {
    final Content savedContent = new Content();
    savedContent.setId("1");

    Mockito.stub(mMockedOfflineStorageManager.getContentWithLocationIdentifier(anyString()))
        .toReturn(savedContent);

    mOfflineEnduserApi.getContentByLocationIdentifier("locId", new APICallback<Content, List<Error>>() {
      @Override
      public void finished(Content result) {
        Assert.assertEquals(savedContent, result);
      }

      @Override
      public void error(List<Error> error) {
        Assert.fail();
      }
    });
  }

  @Test
  public void testGetContentByLocationIdentifierBeacon() {
    final Content savedContent = new Content();
    savedContent.setId("1");

    Mockito.stub(mMockedOfflineStorageManager.getContentWithLocationIdentifier(anyString()))
        .toReturn(savedContent);

    mOfflineEnduserApi.getContentByBeacon(1, 2, new APICallback<Content, List<Error>>() {
      @Override
      public void finished(Content result) {
        Assert.assertEquals(savedContent, result);
      }

      @Override
      public void error(List<Error> error) {
        Assert.fail();
      }
    });

    Mockito.verify(mMockedOfflineStorageManager).getContentWithLocationIdentifier(eq(String.format("%d|%d", 1, 2)));
  }

  @Test
  public void testGetContentsByLocation() {
    mOfflineEnduserApi.getContentsByLocation(null, 1, null, null, new APIListCallback<List<Content>, List<Error>>() {
      @Override
      public void finished(List<Content> result, String cursor, boolean hasMore) {
        Assert.assertNotNull(result);
      }

      @Override
      public void error(List<Error> error) {

      }
    });

    Mockito.verify(mMockedOfflineStorageManager).getContentsByLocation(any(Location.class),
        anyInt(), anyString(), any(EnumSet.class), (APIListCallback<List<Content>, List<Error>>) any(APICallback.class));
  }

  @Test
  public void testGetContentsByTags() {
    mOfflineEnduserApi.getContentsByTags(null, 1, null, null, new APIListCallback<List<Content>, List<Error>>() {
      @Override
      public void finished(List<Content> result, String cursor, boolean hasMore) {
        Assert.assertNotNull(result);
      }

      @Override
      public void error(List<Error> error) {

      }
    });

    Mockito.verify(mMockedOfflineStorageManager).getContentByTags(any(List.class), anyInt(),
        anyString(), any(EnumSet.class), any (APIListCallback.class));
  }

  @Test
  public void testSearchContentsByName() {
    mOfflineEnduserApi.searchContentsByName("test", 1, null, null, new APIListCallback<List<Content>, List<Error>>() {
      @Override
      public void finished(List<Content> result, String cursor, boolean hasMore) {
        Assert.assertNotNull(result);
      }

      @Override
      public void error(List<Error> error) {

      }
    });

    Mockito.verify(mMockedOfflineStorageManager).searchContentsByName(eq("test"), anyInt(), anyString(),
        any(EnumSet.class), (APIListCallback<List<Content>, List<Error>>) any(APICallback.class));
  }
}