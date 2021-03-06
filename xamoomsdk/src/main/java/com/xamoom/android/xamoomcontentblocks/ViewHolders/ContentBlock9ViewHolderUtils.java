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

package com.xamoom.android.xamoomcontentblocks.ViewHolders;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.Base64;

import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.xamoom.android.xamoomsdk.R;

import java.io.UnsupportedEncodingException;
import java.util.Set;

/**
 * Image and map utils for the spotmap block.
 */
public class ContentBlock9ViewHolderUtils {

  /**
   * Returns camera update for all markers on map with specific padding.
   *
   * @param markers Set of markers.
   * @param padding Padding for LatLngBounds.
   * @return Camera update to see all markers on map.
   */
  public static CameraUpdate zoomToDisplayAllMarker(Set<Marker> markers, int padding) {
    if (markers.size() == 0) {
      return null;
    }
    
    LatLngBounds.Builder builder = new LatLngBounds.Builder();
    for (Marker marker :markers) {
      builder.include(marker.getPosition());
    }

    LatLngBounds bounds = builder.build();
    return CameraUpdateFactory.newLatLngBounds(bounds, padding);
  }

  /**
   * Returns the icon for mapMarker.
   *
   * @param customMarker base64 custom marker (mappin) from xamoom
   * @param context Android context.
   * @return icon Bitmap of custom marker
   */
  public static Bitmap getIcon(String customMarker, Context context) {
    Bitmap icon;
    if (customMarker != null) {
      icon = ContentBlock9ViewHolderUtils.getIconFromBase64(customMarker, context);
    } else {
      icon = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_default_map_marker);
    }

    return icon;
  }

  /**
   * Decodes a base64 string to an icon for mapMarkers.
   * Can handle normal image formats and also svgs.
   * The icon will be resized to width: 70, height will be resized to maintain imageRatio.
   *
   * @param base64String Base64 string that will be resized. Must start with "data:image/"
   * @return icon as BitMap, or null if there was a problem
   */
  private static Bitmap getIconFromBase64(String base64String, Context context) {
    Bitmap icon = null;
    byte[] data1;
    String decodedString1 = null;
    float newImageWidth = 25.0f;

    newImageWidth = newImageWidth * context.getResources().getDisplayMetrics().density;

    if (base64String == null)
      return null;

    try {
      int index = base64String.indexOf("base64,");
      String decodedString1WithoutPrefix = base64String.substring(index + 7);
      data1 = Base64.decode(decodedString1WithoutPrefix, Base64.DEFAULT);
      decodedString1 = new String(data1, "UTF-8");

      if (base64String.contains("data:image/svg+xml")) {
        //svg stuff
        SVG svg = SVG.getFromString(decodedString1);

        if (svg != null) {
          //resize svg
          float imageRatio = svg.getDocumentWidth() / svg.getDocumentHeight();
          svg.setDocumentWidth(newImageWidth);
          svg.setDocumentHeight(newImageWidth / imageRatio);

          icon = Bitmap.createBitmap((int) svg.getDocumentWidth(), (int) svg.getDocumentHeight(), Bitmap.Config.ARGB_8888);
          Canvas canvas1 = new Canvas(icon);
          svg.renderToCanvas(canvas1);
        }
      } else if (base64String.contains("data:image/")) {
        //normal image stuff
        icon = BitmapFactory.decodeByteArray(data1, 0, data1.length);
        //resize the icon
        double imageRatio = (double) icon.getWidth() / (double) icon.getHeight();
        double newHeight = newImageWidth / imageRatio;
        icon = Bitmap.createScaledBitmap(icon, (int) newImageWidth, (int) newHeight, false);
      }
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    } catch (SVGParseException e) {
      e.printStackTrace();
    }

    return icon;
  }
}
