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

package com.xamoom.android.xamoomsdk.xamoomsdk.Resource;

import android.os.Parcel;

import com.xamoom.android.xamoomsdk.BuildConfig;
import com.xamoom.android.xamoomsdk.Resource.Content;
import com.xamoom.android.xamoomsdk.Resource.KeyValueObject;
import com.xamoom.android.xamoomsdk.Resource.Location;
import com.xamoom.android.xamoomsdk.Resource.Marker;
import com.xamoom.android.xamoomsdk.Resource.Spot;
import com.xamoom.android.xamoomsdk.Resource.System;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.HashMap;

import static junit.framework.Assert.assertEquals;
import static junit.framework.TestCase.assertNotNull;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21, manifest = "AndroidManifest.xml")
public class SpotTest {

  private Spot mSpot;

  @Before
  public void setup() {
    mSpot = new Spot();
    mSpot.setId("id");
    mSpot.setName("name");
    mSpot.setPublicImageUrl("www.image.url");
    mSpot.setDescription("description");
    Location location = new Location();
    location.setLongitude(1.0);
    location.setLatitude(2.0);
    mSpot.setLocation(location);
    ArrayList<String> tags = new ArrayList<String>();
    tags.add("tag1");
    tags.add("tag2");
    mSpot.setTags(tags);
    ArrayList<Marker> markers = new ArrayList<>();
    Marker marker = new Marker();
    marker.setId("id");
    markers.add(marker);
    mSpot.setMarkers(markers);
    System system = new System();
    system.setName("SystemName");
    mSpot.setSystem(system);
    Content content = new Content();
    content.setId("id");
    mSpot.setContent(content);
    mSpot.setCategory(11);
  }

  @Test
  public void testConstructor() {
    Spot spot = new Spot();

    assertNotNull(spot);
  }

  @Test
  public void testParcelable() {
    Parcel parcel = Parcel.obtain();
    mSpot.writeToParcel(parcel, 0);
    parcel.setDataPosition(0);
    Spot createdFromParcel = Spot.CREATOR.createFromParcel(parcel);

    assertEquals(mSpot.getId(), createdFromParcel.getId());
    assertEquals(mSpot.getName(), createdFromParcel.getName());
    assertEquals(mSpot.getPublicImageUrl(), createdFromParcel.getPublicImageUrl());
    assertEquals(mSpot.getDescription(), createdFromParcel.getDescription());
    assertEquals(mSpot.getLocation().getLatitude(), createdFromParcel.getLocation().getLatitude());
    assertEquals(mSpot.getLocation().getLongitude(), createdFromParcel.getLocation().getLongitude());
    assertEquals(mSpot.getTags(), createdFromParcel.getTags());
    assertEquals(mSpot.getMarkers().get(0).getId(), createdFromParcel.getMarkers().get(0).getId());
    assertEquals(mSpot.getSystem().getName(), createdFromParcel.getSystem().getName());
    assertEquals(mSpot.getContent().getId(), createdFromParcel.getContent().getId());
    assertEquals(mSpot.getCategory(), createdFromParcel.getCategory());
  }


  @Test
  public void testGetCustomMeta() {
    Content content = new Content();
    ArrayList<KeyValueObject> customMetaList = new ArrayList<>();
    customMetaList.add(new KeyValueObject("key", "value"));
    customMetaList.add(new KeyValueObject("key2", "value2"));
    content.setCustomMeta(customMetaList);

    HashMap<String, String> checkCustomMeta = new HashMap<>();
    checkCustomMeta.put("key", "value");
    checkCustomMeta.put("key2", "value2");

    HashMap<String, String> customMeta = content.getCustomMeta();

    assertEquals(customMeta, checkCustomMeta);
  }

  @Test
  public void testSetCustomMeta() {
    Content content = new Content();

    HashMap<String, String> customMeta = new HashMap<>();
    customMeta.put("key", "value");
    customMeta.put("key2", "value2");

    content.setCustomMeta(customMeta);

    assertEquals(content.getCustomMeta(), customMeta);
  }
}
