package com.styleme.floating.toolbox.pro.fragments;

import android.app.LoaderManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.styleme.floating.toolbox.pro.AppController;
import com.styleme.floating.toolbox.pro.R;
import com.styleme.floating.toolbox.pro.global.adapter.MyAppsAdapter;
import com.styleme.floating.toolbox.pro.global.helper.AppHelper;
import com.styleme.floating.toolbox.pro.global.loader.MyAppsLoader;
import com.styleme.floating.toolbox.pro.global.model.AppsModel;
import com.styleme.floating.toolbox.pro.global.model.EventType;
import com.styleme.floating.toolbox.pro.global.model.EventsModel;
import com.styleme.floating.toolbox.pro.global.receiver.ApplicationsReceiver;
import com.styleme.floating.toolbox.pro.global.receiver.MyAppsReceiver;
import com.styleme.floating.toolbox.pro.widget.EmptyRecyclerView;
import com.styleme.floating.toolbox.pro.widget.FontTextView;
import com.styleme.floating.toolbox.pro.widget.impl.MyAppsOnItemClickListener;
import com.styleme.floating.toolbox.pro.widget.impl.SimpleItemTouchHelperCallback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Kosh on 9/3/2015. copyrights are reserved
 */

public class MyAppsList extends Fragment implements MyAppsOnItemClickListener, LoaderManager.LoaderCallbacks<List<AppsModel>>,
        SearchView.OnQueryTextListener {

    @Bind(R.id.recycler)
    EmptyRecyclerView recycler;
    @Bind(R.id.progressBar)
    ProgressBar progressBar;
    @Bind(R.id.emptyText)
    FontTextView emptyText;
    private MyAppsAdapter adapter;
    private ItemTouchHelper mItemTouchHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        adapter = new MyAppsAdapter(this, new ArrayList<AppsModel>());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recycler_view, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        recycler.setEmptyView(emptyText);
        recycler.setItemAnimator(new DefaultItemAnimator());
        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(manager);
        recycler.setAdapter(adapter);
        getActivity().getLoaderManager().initLoader(1, null, this);
        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(recycler);
    }

    @Override
    public void onItemClickListener(View view, int position) {

    }

    @Override
    public void onItemLongClickListener(RecyclerView.ViewHolder h, View view, int position) {
        if (h != null) {
            mItemTouchHelper.startDrag(h);
        }
    }

    boolean canDelete = true;

    @Override
    public void onItemDismissed(final int position) {
        if (adapter.getModelList().get(position) != null) {
            final AppsModel model = adapter.getModelList().get(position);
            adapter.getModelList().remove(position);
            adapter.notifyItemRemoved(position);
            Snackbar.make(recycler, "App Removed", Snackbar.LENGTH_LONG).setAction("Undo", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    adapter.getModelList().add(position, model);
                    adapter.notifyItemInserted(position);
                    canDelete = false;
                }
            }).setCallback(new Snackbar.Callback() {
                @Override
                public void onDismissed(Snackbar snackbar, int event) {
                    super.onDismissed(snackbar, event);
                    if (canDelete) {
                        model.delete();
                    } else {
                        canDelete = true;
                    }
                    notifyChange();
                    postCount();
                }
            }).show();
        }
    }

    @Override
    public void onItemPositionChange(int fromPosition, int toPosition) {
        if (!AppHelper.isAutoOrder(getActivity())) {
            AppsModel old = adapter.getModelList().get(fromPosition);
            old.setAppPosition(toPosition);
            old.save();
            AppsModel newModel = adapter.getModelList().get(toPosition);
            newModel.setAppPosition(fromPosition);
            newModel.save();
            Collections.swap(adapter.getModelList(), fromPosition, toPosition);
            adapter.notifyItemMoved(fromPosition, toPosition);
            Intent intent = new Intent(MyAppsReceiver.REARRANGED);
            AppController.getController().sendBroadcast(intent);
        } else {
            Snackbar.make(recycler, "This feature does not work when auto ordering enabled.", Snackbar.LENGTH_LONG).show();
        }
    }

    private void notifyChange() {
        try {
            Intent intent = new Intent(ApplicationsReceiver.DATA_CHANGED);
            AppController.getController().sendBroadcast(intent);
            Intent intent2 = new Intent(MyAppsReceiver.DATE_DELETE);
            AppController.getController().sendBroadcast(intent2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Loader<List<AppsModel>> onCreateLoader(int id, Bundle args) {
        progressBar.setVisibility(View.VISIBLE);
        return new MyAppsLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<List<AppsModel>> loader, List<AppsModel> data) {
        progressBar.setVisibility(View.GONE);
        adapter.insert(data);
        postCount();
    }

    @Override
    public void onLoaderReset(Loader<List<AppsModel>> loader) {
        adapter.clearAll();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(this);
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setSubmitButtonEnabled(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);

    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (newText.isEmpty()) {
            adapter.getFilter().filter("");
        } else {
            adapter.getFilter().filter(newText.toLowerCase());
        }
        return false;
    }

    private void postCount() {
        EventsModel eventsModel = new EventsModel();
        eventsModel.setEventType(EventType.MY_APPS_COUNT);
        AppController.getController().eventBus().post(eventsModel);
    }

    public void deleteAll() {
        if (adapter != null) {
            Snackbar.make(recycler, "Confirm To Remove All Selected Apps", Snackbar.LENGTH_LONG)
                    .setAction("Confirm", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            new AppsModel().deleteAll();
                            adapter.clearAll();
                        }
                    })
                    .setCallback(new Snackbar.Callback() {
                        @Override
                        public void onDismissed(Snackbar snackbar, int event) {
                            super.onDismissed(snackbar, event);
                            notifyChange();
                            postCount();
                        }
                    })
                    .show();
        }
    }

}
