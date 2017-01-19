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

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.PictureDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.GenericRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.StreamEncoder;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.load.resource.file.FileToStreamDecoder;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.caverock.androidsvg.SVG;
import com.xamoom.android.xamoomcontentblocks.Helper.SvgDecoder;
import com.xamoom.android.xamoomcontentblocks.Helper.SvgDrawableTranscoder;
import com.xamoom.android.xamoomcontentblocks.Helper.SvgSoftwareLayerSetter;
import com.xamoom.android.xamoomsdk.R;
import com.xamoom.android.xamoomsdk.Resource.ContentBlock;
import com.xamoom.android.xamoomsdk.Resource.Style;
import com.xamoom.android.xamoomsdk.Storage.FileManager;

import java.io.InputStream;

/**
 * ImageBlock
 */
public class ContentBlock3ViewHolder extends RecyclerView.ViewHolder {
  private Context mContext;
  private TextView mTitleTextView;
  private TextView mCopyrightTextView;
  private ProgressBar mImageProgressBar;
  private ImageView mImageView;
  private int mTextColor = Color.BLACK;
  private GenericRequestBuilder<Uri, InputStream, SVG, PictureDrawable> requestBuilder;
  private FileManager mFileManager;

  private OnContentBlock3ViewHolderInteractionListener mListener;

  public ContentBlock3ViewHolder(View itemView, Context context,
                                 OnContentBlock3ViewHolderInteractionListener listener) {
    super(itemView);
    mContext = context;
    mTitleTextView = (TextView) itemView.findViewById(R.id.titleTextView);
    mCopyrightTextView = (TextView) itemView.findViewById(R.id.copyrightTextView);
    mImageView = (ImageView) itemView.findViewById(R.id.imageImageView);
    mImageProgressBar = (ProgressBar) itemView.findViewById(R.id.imageProgressBar);
    mListener = listener;

    mFileManager = FileManager.getInstance(context);

    SvgDrawableTranscoder svgDrawableTranscoder =  new SvgDrawableTranscoder();
    svgDrawableTranscoder.setmDeviceWidth(mContext.getResources().getDisplayMetrics().widthPixels);

    requestBuilder = Glide.with(mContext)
        .using(Glide.buildStreamModelLoader(Uri.class, mContext), InputStream.class)
        .from(Uri.class)
        .as(SVG.class)
        .transcode(svgDrawableTranscoder, PictureDrawable.class)
        .sourceEncoder(new StreamEncoder())
        .cacheDecoder(new FileToStreamDecoder<>(new SvgDecoder()))
        .decoder(new SvgDecoder())
        .listener(new SvgSoftwareLayerSetter<Uri>());
  }

  public void setupContentBlock(final ContentBlock contentBlock, boolean offline) {
    mTitleTextView.setTextColor(mTextColor);
    mCopyrightTextView.setTextColor(mTextColor);

    mTitleTextView.setVisibility(View.VISIBLE);
    mCopyrightTextView.setVisibility(View.VISIBLE);

    if(contentBlock.getTitle() != null && !contentBlock.getTitle().equalsIgnoreCase("")) {
      mTitleTextView.setText(contentBlock.getTitle());
    } else {
      mTitleTextView.setVisibility(View.GONE);
      LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mImageView.getLayoutParams();
      params.setMargins(0,0,0,0);
      mImageView.setLayoutParams(params);
    }

    setImageViewContentDescription(contentBlock);
    setCopyright(contentBlock.getCopyright());

    String fileId = null;
    if (offline) {
      fileId = mFileManager.getFilePath(contentBlock.getFileId());
    } else {
      fileId = contentBlock.getFileId();
    }

    if(fileId != null) {
      final double scaleX;
      //checking scale and divide it by 100.0f
      if(contentBlock.getScaleX() != 0) {
        scaleX = contentBlock.getScaleX()/100.0f;
      } else {
        scaleX = 100.0f/100;
      }

      if (fileId.contains(".svg")) {
        Uri uri = Uri.parse(fileId);
        requestBuilder
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .load(uri)
            .into(mImageView);
        mImageProgressBar.setVisibility(View.GONE);

      } else {
        //making the scaleX to a factor scaleX
        Glide.with(mContext)
            .load(fileId)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .dontAnimate()
            .into(new GlideDrawableImageViewTarget(mImageView) {
              public void onResourceReady(GlideDrawable drawable, GlideAnimation anim) {
                super.onResourceReady(drawable, anim);
                mImageProgressBar.setVisibility(View.GONE);
              }
            });
      }
      resizeImageViewWithScaling(mImageView, scaleX);
    }

    if(contentBlock.getLinkUrl() != null && !contentBlock.getLinkUrl().equals("")) {
      mImageView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(contentBlock.getLinkUrl()));
          mContext.startActivity(intent);
        }
      });
    }

    mImageView.setOnLongClickListener(new View.OnLongClickListener() {
      @Override
      public boolean onLongClick(View v) {
        if (contentBlock.getFileId().contains(".svg") || contentBlock.getFileId().contains(".gif")) {
          Toast.makeText(mContext, mContext.getString(R.string.cannot_save_image), Toast.LENGTH_SHORT).show();
          return false;
        }
        try {
          if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Glide.with(mContext)
                .load(contentBlock.getFileId())
                .asBitmap()
                .into(new SimpleTarget<Bitmap>() {
                  @Override
                  public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    MediaStore.Images.Media.insertImage(mContext.getContentResolver(), resource, contentBlock.getTitle(), "");
                    Toast.makeText(mContext, mContext.getString(R.string.image_saved_text), Toast.LENGTH_SHORT).show();
                  }
                });
          } else {
            mListener.needExternalStoragePermission();
          }
        } catch (Exception e) {
          e.printStackTrace();
        }

        return false;
      }
    });
  }

  private void setImageViewContentDescription(ContentBlock contentBlock) {
    if (contentBlock.getAltText() != null && !contentBlock.getAltText().equalsIgnoreCase("")) {
      mImageView.setContentDescription(contentBlock.getAltText());
    } else if (contentBlock.getTitle() != null && !contentBlock.getTitle().equalsIgnoreCase("")) {
      mImageView.setContentDescription(contentBlock.getTitle());
    }
  }

  private void setCopyright(String copyright) {
    if (copyright != null && !copyright.equalsIgnoreCase("")) {
      mCopyrightTextView.setText(copyright);
    } else {
      mCopyrightTextView.setVisibility(View.GONE);
    }
  }

  private void resizeImageViewWithScaling(ImageView imageView, double scaleX) {
    int deviceWidth = mContext.getResources().getDisplayMetrics().widthPixels;
    double margin = 0;

    //calculate the diff between imageSize and scaledImageSize
    double deviceWidthWithMargins =  (deviceWidth - (margin * 2));
    double diff = deviceWidthWithMargins - (deviceWidthWithMargins*scaleX);
    //set left and right margin to the half of the difference
    //so the imageView is doing all the resizing
    mImageView.setPadding((int)(diff/2),0,(int)(diff/2),0);
  }

  public interface OnContentBlock3ViewHolderInteractionListener {
    void needExternalStoragePermission();
  }

  public void setStyle(Style style) {
    if (style != null && style.getForegroundFontColor() != null) {
      mTextColor = Color.parseColor(style.getForegroundFontColor());
    }
  }

  public TextView getTitleTextView() {
    return mTitleTextView;
  }

  public TextView getCopyrightTextView() {
    return mCopyrightTextView;
  }

  public ProgressBar getImageProgressBar() {
    return mImageProgressBar;
  }

  public ImageView getImageView() {
    return mImageView;
  }

  public void setFileManager(FileManager fileManager) {
    mFileManager = fileManager;
  }
}