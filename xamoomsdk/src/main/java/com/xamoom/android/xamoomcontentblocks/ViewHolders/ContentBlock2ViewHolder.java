package com.xamoom.android.xamoomcontentblocks.ViewHolders;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

import com.google.android.youtube.player.YouTubeIntents;
import com.xamoom.android.xamoomsdk.R;
import com.xamoom.android.xamoomsdk.Resource.ContentBlock;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * VideoBlock
 */
public class ContentBlock2ViewHolder extends RecyclerView.ViewHolder {

  final static String youtubeRegex = "(?:youtube(?:-nocookie)?\\.com\\/(?:[^\\/\\n\\s]+\\/\\S+\\/|(?:v|e(?:mbed)?)\\/|\\S*?[?&]v=)|youtu\\.be\\/)([a-zA-Z0-9_-]{11})";
  final static String vimeoRegex = "^.*(?:vimeo.com)\\/(?:channels\\/|groups\\/[^\\/]*\\/videos\\/|album\\/\\d+\\/video\\/|video\\/|)(\\d+)(?:$|\\/|\\?)";

  private Fragment mFragment;
  private TextView mTitleTextView;
  private WebView mVideoWebView;
  private View mWebViewOverlay;
  private String mYoutubeVideoCode;

  public ContentBlock2ViewHolder(View itemView, Fragment fragment) {
    super(itemView);
    mFragment = fragment;
    mTitleTextView = (TextView) itemView.findViewById(R.id.titleTextView);
    mVideoWebView = (WebView) itemView.findViewById(R.id.videoWebView);
    mWebViewOverlay = (View) itemView.findViewById(R.id.webViewOverlay);

    WebSettings webSettings = mVideoWebView.getSettings();
    webSettings.setJavaScriptEnabled(true);
  }

  public void setupContentBlock(ContentBlock contentBlock) {
    mTitleTextView.setVisibility(View.VISIBLE);
    if(contentBlock.getTitle() != null)
      mTitleTextView.setText(contentBlock.getTitle());
    else {
      mTitleTextView.setVisibility(View.GONE);
    }

    if(getYoutubeVideoId(contentBlock.getVideoUrl()) != null) {
      setupYoutube(contentBlock);
    } else {
      setupHTMLPlayer(contentBlock);
    }
  }

  public void setupHTMLPlayer(final ContentBlock contentBlock) {
    if (contentBlock.getVideoUrl().contains("vimeo.com/")) {
      String vimeoEmbed = "<iframe src=\"https://player.vimeo.com/video/"
          + getVimeoVideoId(contentBlock.getVideoUrl()) + "?badge=0\" width=\"500\" " +
          "height=\"281\" frameborder=\"0\" webkitallowfullscreen mozallowfullscreen " +
          "allowfullscreen></iframe>";
      mVideoWebView.loadUrl(vimeoEmbed);
    } else {
      mVideoWebView.loadUrl(contentBlock.getVideoUrl());
      mWebViewOverlay.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(contentBlock.getVideoUrl()));
          intent.setDataAndType(Uri.parse(contentBlock.getVideoUrl()), "video/mp4");
          mFragment.getActivity().startActivity(intent);
        }
      });
    }
  }
  public void setupYoutube(ContentBlock contentBlock) {
    mYoutubeVideoCode = getYoutubeVideoId(contentBlock.getVideoUrl());

    String html = "<iframe style=\"display:block; margin:auto;\" src=\"https://www.youtube.com/embed/"+mYoutubeVideoCode+"\" frameborder=\"0\" allowfullscreen></iframe>";
    mVideoWebView.loadData(html, "text/html", "utf-8");

    mWebViewOverlay.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = YouTubeIntents.createPlayVideoIntent(mFragment.getActivity(), mYoutubeVideoCode);
        mFragment.getActivity().startActivity(intent);
      }
    });

  }

  public String getYoutubeVideoId(String videoUrl) {
    if (videoUrl == null || videoUrl.trim().length() <= 0) {
      return null;
    }

    Pattern pattern = Pattern.compile(youtubeRegex, Pattern.CASE_INSENSITIVE);
    Matcher matcher = pattern.matcher(videoUrl);

    if (matcher.find()) {
      return matcher.group(1);
    }

    return null;
  }

  public String getVimeoVideoId(String videoUrl) {
    if (videoUrl == null || videoUrl.trim().length() <= 0) {
      return null;
    }

    Pattern pattern = Pattern.compile(vimeoRegex, Pattern.CASE_INSENSITIVE);
    Matcher matcher = pattern.matcher(videoUrl);

    if (matcher.find()) {
      return matcher.group(1);
    }

    return null;
  }
}
