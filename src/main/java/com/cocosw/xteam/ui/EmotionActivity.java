package com.cocosw.xteam.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.style.RelativeSizeSpan;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.cocosw.accessory.views.Toaster;
import com.cocosw.accessory.views.textview.StyledText;
import com.cocosw.xteam.R;
import com.cocosw.xteam.data.Const;
import com.cocosw.xteam.data.Emotion;
import com.jakewharton.u2020.data.Injector;

import java.text.NumberFormat;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import dagger.ObjectGraph;

/**
 * Coco studio
 * <p>
 * Created by kai on 31/12/2015.
 */
public class EmotionActivity extends AppCompatActivity {

    @Bind(R.id.trending_toolbar)
    Toolbar trendingToolbar;
    @Bind(R.id.face)
    TextView face;
    @Bind(R.id.stock)
    Button stock;
    @Bind(R.id.price)
    TextView price;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Explicitly reference the application object since we don't want to match our own injector.
        ObjectGraph appGraph = Injector.obtain(getApplication());
//        appGraph.inject(this);
        setContentView(R.layout.emotion_activity);
        ButterKnife.bind(this);

        setSupportActionBar(trendingToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("");
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(Fragment.instantiate(this, EmotionFragment.class.getName()), EmotionFragment.class.getName()).commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else
            return super.onOptionsItemSelected(item);
    }

    public static class EmotionFragment extends Fragment implements View.OnClickListener {

        private Emotion emotion;

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setRetainInstance(true);
            emotion = (Emotion) getActivity().getIntent().getSerializableExtra(Const.Extra.EMOTION);
        }

        @Override
        public void onActivityCreated(@Nullable Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            requestRender();
        }


        private EmotionActivity target() {
            return (EmotionActivity) getActivity();
        }

        private void requestRender() {
            if (getActivity() != null && !getActivity().isFinishing() && emotion != null) {
                target().price.setText(NumberFormat.getCurrencyInstance(Locale.US).format(emotion.price));
                target().face.setText(emotion.face);
                target().stock.setOnClickListener(this);
                if (emotion.stock == 0)
                    target().stock.setEnabled(false);
                else {
                    if (emotion.stock == 1) {
                        StyledText span = new StyledText(getContext()).append(R.string.buy).append("\n").append(getString(R.string.limit_stock), new RelativeSizeSpan(0.7f));
                        target().stock.setText(span, TextView.BufferType.SPANNABLE);
                    }
                }
            }
        }

        @Override
        public void onClick(View v) {
            Toaster.showShort(getActivity(), "Buy emotion id:" + emotion.id);
        }
    }


}
