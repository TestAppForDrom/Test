package com.drom.test.features.search;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.drom.test.R;
import com.drom.test.beans.SessionManager;
import com.drom.test.features.start.ActivityStart;
import com.drom.test.utils.CommonUtils;
import com.drom.test.utils.PaginationAdapterWrapper;
import com.drom.test.utils.PaginationProgressCreator;
import com.drom.test.utils.RvWrapperPaginator;
import dagger.android.AndroidInjection;
import io.reactivex.disposables.Disposable;

import javax.inject.Inject;

public class SearchActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    private static final String ARG_QUERY = "ARG_QUERY";
    private static final int LOGIN_MENU_CODE = 101;
    @Inject SessionManager sessionManager;
    @BindView(R.id.progress_bar) View progressBar;
    @BindView(R.id.recycler_view) RecyclerView recyclerView;
    @BindView(R.id.text_empty_message) View emptyMessage;
    private SearchViewModel viewModel;
    private SearchView searchView;
    private Disposable disposable;
    private RvWrapperPaginator paginator;
    private PaginationAdapterWrapper adapterWrapper;
    private RecyclerView.AdapterDataObserver adapterDataObserver;
    private String savedSearchQuery;
    private boolean isUserUnauthorized;


    public static Intent createIntent(Context callingContext) {
        return new Intent(callingContext, SearchActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);

        isUserUnauthorized = sessionManager.isUnauthorizedAccount();
        viewModel = ViewModelProviders.of(this).get(SearchViewModel.class);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initRecyclerView();

        listenForViewStateUpdates();

        if (savedInstanceState != null) {
            savedSearchQuery = savedInstanceState.getString(ARG_QUERY);
        }
    }

    private void initRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(new SpaceLineItemDecoration(CommonUtils.dp2px(8), 0, Color.LTGRAY));
        createPaginator();
        viewModel.getReposData().observe(this, repos -> {
            if (repos == null) {
                return;
            }
            RecyclerView.Adapter inner = new RepositoryAdapter(repos);
            recyclerView.setAdapter(adapterWrapper = PaginationAdapterWrapper.wrap(inner, PaginationProgressCreator.DEFAULT));
            adapterDataObserver = new RecyclerView.AdapterDataObserver() {
                @Override
                public void onChanged() {
                    checkEmptyMessage();
                }

                @Override
                public void onItemRangeChanged(int positionStart, int itemCount) {
                    checkEmptyMessage();
                }

                @Override
                public void onItemRangeChanged(int positionStart, int itemCount, @Nullable Object payload) {
                    checkEmptyMessage();
                }

                @Override
                public void onItemRangeInserted(int positionStart, int itemCount) {
                    checkEmptyMessage();
                }

                @Override
                public void onItemRangeRemoved(int positionStart, int itemCount) {
                    checkEmptyMessage();
                }

                @Override
                public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
                    checkEmptyMessage();
                }

                private void checkEmptyMessage() {
                    if (inner.getItemCount() == 0) {
                        CommonUtils.visible(emptyMessage);
                    } else {
                        CommonUtils.gone(emptyMessage);
                    }
                }
            };
            inner.registerAdapterDataObserver(adapterDataObserver);

            if (inner.getItemCount() == 0) {
                CommonUtils.visible(emptyMessage);
            } else {
                CommonUtils.gone(emptyMessage);
            }
        });
    }

    private void createPaginator() {
        paginator = new RvWrapperPaginator(recyclerView, 0);

        disposable = paginator.getObservable().filter(ignored -> recyclerView != null && searchView != null)
                .subscribe(count -> viewModel.loadNextPage(searchView.getQuery().toString(), count));
    }

    private void listenForViewStateUpdates() {
        viewModel.getObservable().observe(this, repos -> {
            if (repos == null) {
                return;
            }
            if (adapterWrapper != null) {
                adapterWrapper.displayPaginationProgressBar(repos.isPaginationProgress);
            }
            if (repos.inProgress) {
                CommonUtils.visible(progressBar);
                CommonUtils.gone(emptyMessage);
            } else if (repos.error != null) {
                CommonUtils.gone(progressBar);
                if (disposable != null) {
                    disposable.dispose();
                }
                createPaginator();
                if (recyclerView != null) {
                    Snackbar.make(recyclerView, repos.error, Snackbar.LENGTH_SHORT).show();
                }
            } else {
                CommonUtils.gone(progressBar);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        MenuItem searchItem = menu.findItem(R.id.search);
        searchView = (SearchView) searchItem.getActionView();
        if (savedSearchQuery != null) {
            searchItem.expandActionView();
            searchView.setIconified(false);
            searchView.setQuery(savedSearchQuery, false);
        }
        EditText searchEditText = searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        if (searchEditText != null) {
            searchEditText.setTextColor(Color.WHITE);
            searchEditText.setHintTextColor(Color.LTGRAY);
        }
        searchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (menu.findItem(LOGIN_MENU_CODE) == null) {
            menu.add(Menu.NONE, LOGIN_MENU_CODE, 2, isUserUnauthorized ? getString(R.string.menu_login) : getString(R.string.menu_logout));
        }
        super.onPrepareOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case LOGIN_MENU_CODE:
                sessionManager.logOut().subscribe(this::doLogOut, error -> doLogOut());
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void doLogOut() {
        startActivity(new Intent(this, ActivityStart.class));
        finish();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (viewModel != null) {
            viewModel.search(newText);
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        if (disposable != null) {
            disposable.dispose();
            disposable = null;
        }
        if (adapterWrapper != null && adapterWrapper.getWrappedAdapter() != null && adapterDataObserver != null) {
            adapterWrapper.getWrappedAdapter().unregisterAdapterDataObserver(adapterDataObserver);
        }
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (searchView != null) {
            outState.putString(ARG_QUERY, searchView.getQuery().toString());
        }
    }
}
