package com.cocosw.xteam.ui;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;

import com.cocosw.xteam.R;
import com.cocosw.xteam.data.Emotion;
import com.jakewharton.u2020.ui.misc.Truss;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.Locale;

public final class EmotionItemView extends LinearLayout {
  @Bind(R.id.trending_repository_name) TextView nameView;
  @Bind(R.id.trending_repository_description) TextView descriptionView;

  public EmotionItemView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  @Override protected void onFinishInflate() {
    super.onFinishInflate();
    ButterKnife.bind(this);
  }

  public void bindTo(Emotion emotion) {
    nameView.setText(emotion.face);
    descriptionView.setText(NumberFormat.getCurrencyInstance(Locale.US).format(emotion.price));
  }
}
