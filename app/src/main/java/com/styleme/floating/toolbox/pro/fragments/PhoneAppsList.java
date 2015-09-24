package com.styleme.floating.toolbox.pro.fragments;

import android.app.LoaderManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.styleme.floating.toolbox.pro.AppController;
import com.styleme.floating.toolbox.pro.R;
import com.styleme.floating.toolbox.pro.activities.Home;
import com.styleme.floating.toolbox.pro.global.adapter.AppsAdapter;
import com.styleme.floating.toolbox.pro.global.helper.AppHelper;
import com.styleme.floating.toolbox.pro.global.loader.AppsLoader;
import com.styleme.floating.toolbox.pro.global.model.AppsModel;
import com.styleme.floating.toolbox.pro.global.model.EventType;
import com.styleme.floating.toolbox.pro.global.model.EventsModel;
import com.styleme.floating.toolbox.pro.global.receiver.MyAppsReceiver;
import com.styleme.floating.toolbox.pro.global.service.FloatingService;
import com.styleme.floating.toolbox.pro.widget.EmptyRecyclerView;
import com.styleme.floating.toolbox.pro.widget.FontTextView;
import com.styleme.floating.toolbox.pro.widget.impl.OnItemClickListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Kosh on 9/3/2015. copyrights are reserved
 */
public class PhoneAppsList extends Fragment implements OnItemClickListener,
        LoaderManager.LoaderCallbacks<List<AppsModel>>,
        SearchView.OnQueryTextListener, ActionMode.Callback {

    @Bind(R.id.recycler)
    EmptyRecyclerView recycler;
    @Bind(R.id.progressBar)
    ProgressBar progressBar;
    @Bind(R.id.emptyText)
    FontTextView emptyText;
    private AppsAdapter adapter;
    private HashMap<Integer, AppsModel> selectedApps = new LinkedHashMap<>();
    private ActionMode actionMode;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        adapter = new AppsAdapter(this, new ArrayList<AppsModel>());
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
        GridLayoutManager manager = new GridLayoutManager(getContext(), getResources().getInteger(R.integer.num_row));
        recycler.setEmptyView(emptyText);
        recycler.setItemAnimator(new DefaultItemAnimator());
        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(manager);
        recycler.setAdapter(adapter);
        getActivity().getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onItemClickListener(View view, int position) {

    }

    @Override
    public void onItemLongClickListener(RecyclerView.ViewHolder viewHolder, View view, int position) {
        if (selectedApps.get(position) == null) {
            selectedApps.put(position, adapter.getModelList().get(position));
            adapter.setItemChecked(position, true);
        } else {
            selectedApps.remove(position);
            adapter.setItemChecked(position, false);
        }
        if (selectedApps.size() != 0) {
            if (actionMode == null) {
                actionMode = getActivity().findViewById(R.id.toolbar).startActionMode(this);
            }
            if (selectedApps.size() > 1) {
                actionMode.setTitle("Add ( " + selectedApps.size() + " Apps )");
            } else {
                actionMode.setTitle("Add ( " + selectedApps.size() + " App )");
            }
        } else {
            actionMode.finish();
            actionMode = null;
        }
    }

    @Override
    public Loader<List<AppsModel>> onCreateLoader(int id, Bundle args) {
        progressBar.setVisibility(View.VISIBLE);
        return new AppsLoader(getActivity());
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

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        mode.getMenuInflater().inflate(R.menu.menu_action, menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // i f***ing  hate android sometimes.
            getActivity().getWindow().setStatusBarColor(AppHelper.getPrimaryDarkColor(AppHelper.getPrimaryColor(getActivity())));
        }
        return true;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        if (item.getItemId() == R.id.addApp) {
            for (Map.Entry<Integer, AppsModel> apps : selectedApps.entrySet()) {
                apps.getValue().setAppPosition(new AppsModel().lastPosition() + apps.getKey() + 1);
                apps.getValue().save();
                adapter.remove(apps.getValue());
            }
            Intent intent = new Intent(MyAppsReceiver.DATA_ADDED);
            AppController.getController().sendBroadcast(intent);
            mode.finish();
            startFloating();
            postCount();
            return true;
        }
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        actionMode = null;
        selectedApps.clear();
        adapter.clearSelection();

    }

    private void postCount() {
        EventsModel eventsModel = new EventsModel();
        eventsModel.setEventType(EventType.APPS_COUNT);
        eventsModel.setAppsCount(adapter != null ? adapter.getItemCount() : 0);
        AppController.getController().eventBus().post(eventsModel);
    }

    private void startFloating() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (!Settings.canDrawOverlays(getActivity())) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getActivity().getPackageName()));
                startActivityForResult(intent, Home.OVERLAY_PERMISSION_REQ_CODE);
            }
        } else {
            AppController.getController().startService(new Intent(getActivity(), FloatingService.class));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Home.OVERLAY_PERMISSION_REQ_CODE) {
            if (!Settings.canDrawOverlays(getActivity())) {
                Toast.makeText(getActivity(), "FA Wont Work Unless This Permission is granted", Toast.LENGTH_LONG).show();
            } else {
                AppController.getController().startService(new Intent(getActivity(), FloatingService.class));
            }
        }
    }


}
