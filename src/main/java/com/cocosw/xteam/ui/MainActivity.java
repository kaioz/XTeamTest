package com.cocosw.xteam.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.ViewGroup;

import com.cocosw.xteam.R;
import com.cocosw.xteam.data.Const;
import com.cocosw.xteam.data.Emotion;
import com.cocosw.xteam.data.WarehouseService;
import com.jakewharton.u2020.data.Injector;
import com.jakewharton.u2020.ui.AppContainer;
import com.jakewharton.u2020.ui.MainActivityModule;
import com.jakewharton.u2020.ui.misc.BetterViewAnimator;
import com.jakewharton.u2020.util.Intents;


import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import dagger.ObjectGraph;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;



public final class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, SearchView.OnQueryTextListener,ListAdapter.RepositoryClickListener, Toolbar.OnMenuItemClickListener {

    private static final int SIZE = 40;
    @Bind(R.id.trending_toolbar)
    Toolbar toolbarView;
    @Bind(R.id.trending_animator)
    BetterViewAnimator animatorView;
    @Bind(R.id.trending_swipe_refresh)
    SwipeRefreshLayout swipeRefreshView;
    @Bind(R.id.trending_list)
    RecyclerView trendingView;
    @Bind(R.id.search_view)
    SearchView searchView;

    private StaggeredGridLayoutManager mLinearLayoutManager;

    private static final String FRAGMENT_NAME = "_modelview";

    @Inject
    AppContainer appContainer;

    private ObjectGraph activityGraph;
    private MainFragment fragment;

    private ListAdapter trendingAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = getLayoutInflater();

        // Explicitly reference the application object since we don't want to match our own injector.
        ObjectGraph appGraph = Injector.obtain(getApplication());
        appGraph.inject(this);
        activityGraph = appGraph.plus(new MainActivityModule(this));

        ViewGroup container = appContainer.bind(this);

        inflater.inflate(R.layout.main_activity, container);
        ButterKnife.bind(this, container);

        swipeRefreshView.setColorSchemeResources(R.color.accent);
        swipeRefreshView.setOnRefreshListener(this);

        toolbarView.inflateMenu(R.menu.debug_external_intent);
        toolbarView.setOnMenuItemClickListener(this);

        trendingView.setLayoutManager(mLinearLayoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));

        trendingAdapter = new ListAdapter(this);
        trendingView.setAdapter(trendingAdapter);

        trendingView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (fragment.loadDone)
                    return;
                int totalItem = mLinearLayoutManager.getItemCount();
                int[] lastVisibleItem = mLinearLayoutManager.findLastVisibleItemPositions(null);

                if (!fragment.mLoading && lastVisibleItem[0] >= totalItem - 3) {
                    fragment.loadMore();
                }
            }
        });

        fragment = (MainFragment) getSupportFragmentManager().findFragmentByTag(FRAGMENT_NAME);
        if (fragment == null) {
            fragment = (MainFragment) Fragment.instantiate(this, MainFragment.class.getName());
            Injector.obtain(this).inject(fragment);
            getSupportFragmentManager().beginTransaction().add(fragment, FRAGMENT_NAME).commit();
        }
    }

    @Override
    public Object getSystemService(@NonNull String name) {
        if (Injector.matchesService(name)) {
            return activityGraph;
        }
        return super.getSystemService(name);
    }

    @Override
    protected void onDestroy() {
        activityGraph = null;
        searchView.setOnQueryTextListener(null);
        super.onDestroy();
    }


    @Override
    public void onRefresh() {
        fragment.refresh();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        fragment.keyword = query;
        fragment.refresh();
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        fragment.keyword = newText;
        if (TextUtils.isEmpty(newText))
            fragment.refresh();
        return false;
    }

    @Override
    public void onRepositoryClick(Emotion repository) {
        Intents.maybeStartActivity(this, new Intent(this, EmotionActivity.class).putExtra(Const.Extra.EMOTION, repository));
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.stock) {
            item.setChecked(!item.isChecked());
            fragment.stockOnly = item.isChecked();
            fragment.refresh();
        }
        return true;
    }

    public static class MainFragment extends Fragment {
        private boolean mLoading = false;
        private boolean loadDone;
        private String keyword;
        private boolean stockOnly;
        private int displayId = R.id.trending_swipe_refresh;

        private List<Emotion> repositories = new ArrayList<>();

        private final CompositeSubscription subscriptions = new CompositeSubscription();

        @Inject
        WarehouseService githubService;

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setRetainInstance(true);
            refresh();
        }

        private void refresh() {
            loadDone = false;
            refresh(true, keyword, stockOnly);
        }

        @Override
        public void onActivityCreated(@Nullable Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            requestRender();
        }

        @Override
        public void onDestroy() {
            subscriptions.unsubscribe();
            super.onDestroy();
        }

        private MainActivity target() {
            return (MainActivity) getActivity();
        }

        private void requestRender() {
            if (getActivity() != null && !getActivity().isFinishing()) {
                target().animatorView.setDisplayedChildId(displayId);
                target().trendingAdapter.replace(repositories);
                target().searchView.setQuery(keyword,false);
                target().toolbarView.getMenu().findItem(R.id.stock).setChecked(stockOnly);
                target().searchView.setOnQueryTextListener(target());
                showRefreshing();
            }
        }

        private void showRefreshing() {
            if (mLoading) {
                new Handler().post(() -> {
                    target().swipeRefreshView.setRefreshing(true);
                });
            }
        }

        private void loadMore() {
            refresh(false, keyword, stockOnly);
        }

        private void refresh(boolean reset, String keyword, boolean stockOnly) {
            mLoading = true;
            showRefreshing();
            subscriptions.add(githubService.fetch(SIZE, reset ? 0 : repositories.size(), keyword, stockOnly?1:0)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io()) //
                    .subscribe(new Subscriber<List<Emotion>>() {

                        @Override
                        public void onCompleted() {
                            displayId = (repositories.size() == 0 //
                                    ? R.id.trending_empty //
                                    : R.id.trending_swipe_refresh);
                            target().animatorView.setDisplayedChildId(displayId);
                            target().swipeRefreshView.setRefreshing(false);
                            mLoading = false;
                        }

                        @Override
                        public void onError(Throwable e) {
                            mLoading = false;
                            target().swipeRefreshView.setRefreshing(false);
                            displayId = R.id.trending_error;
                            target().animatorView.setDisplayedChildId(displayId);
                            Timber.e(e, "TreadingView");
                        }

                        @Override
                        public void onNext(List<Emotion> emotions) {
                            if (emotions.size() < SIZE)
                                loadDone = true;
                            if (reset)
                                repositories = emotions;
                            else
                                repositories.addAll(emotions);
                            target().trendingAdapter.replace(repositories);
                        }
                    }));
        }
    }
}
