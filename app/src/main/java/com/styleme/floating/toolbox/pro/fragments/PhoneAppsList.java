package com.styleme.floating.toolbox.pro.fragments;

import android.animation.Animator;
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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.styleme.floating.toolbox.pro.AppController;
import com.styleme.floating.toolbox.pro.R;
import com.styleme.floating.toolbox.pro.activities.Home;
import com.styleme.floating.toolbox.pro.global.adapter.AppsAdapter;
import com.styleme.floating.toolbox.pro.global.loader.AppsLoader;
import com.styleme.floating.toolbox.pro.global.model.AppsModel;
import com.styleme.floating.toolbox.pro.global.model.EventType;
import com.styleme.floating.toolbox.pro.global.model.EventsModel;
import com.styleme.floating.toolbox.pro.global.receiver.MyAppsReceiver;
import com.styleme.floating.toolbox.pro.global.service.FloatingService;
import com.styleme.floating.toolbox.pro.widget.EmptyRecyclerView;
import com.styleme.floating.toolbox.pro.widget.FontTextView;
import com.styleme.floating.toolbox.pro.widget.impl.OnItemClickListener;
import com.styleme.floating.toolbox.pro.widget.impl.OnScrollListener;

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
        LoaderManager.LoaderCallbacks<List<AppsModel>>, SearchView.OnQueryTextListener {

    @Bind(R.id.recycler)
    EmptyRecyclerView recycler;
    @Bind(R.id.progressBar)
    ProgressBar progressBar;
    @Bind(R.id.emptyText)
    FontTextView emptyText;
    ImageView back;
    ImageView addApps;
    FrameLayout actionmode;
    TextView appCount;
    private AppsAdapter adapter;
    public HashMap<Integer, AppsModel> selectedApps = new LinkedHashMap<>();


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
        actionmode = (FrameLayout) getActivity().findViewById(R.id.actionmode);
        back = (ImageView) getActivity().findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearSelection();
            }
        });
        addApps = (ImageView) getActivity().findViewById(R.id.addApps);
        addApps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (Map.Entry<Integer, AppsModel> apps : selectedApps.entrySet()) {
                    apps.getValue().setAppPosition(new AppsModel().lastPosition() + apps.getKey() + 1);
                    apps.getValue().save();
                    adapter.remove(apps.getValue());
                }
                Intent intent = new Intent(MyAppsReceiver.DATA_ADDED);
                AppController.getController().sendBroadcast(intent);
                startFloating();
                postCount();
                clearSelection();
            }
        });
        appCount = (TextView) getActivity().findViewById(R.id.appCount);
        GridLayoutManager manager = new GridLayoutManager(getContext(), getResources().getInteger(R.integer.num_row));
        recycler.setEmptyView(emptyText);
        recycler.setItemAnimator(new DefaultItemAnimator());
        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(manager);
        recycler.setAdapter(adapter);
        recycler.addOnScrollListener(onScrollListener);
        getActivity().getLoaderManager().initLoader(0, null, this);
    }

    private void clearSelection() {
        selectedApps.clear();
        adapter.clearSelection();
        appCount.setText("");
        onScrollListener.onHide();
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
            if (selectedApps.size() > 1) {
                appCount.setText("Add ( " + selectedApps.size() + " Apps )");
            } else {
                appCount.setText("Add ( " + selectedApps.size() + " App )");
            }
            onScrollListener.onShow();
        } else {
            appCount.setText("");
            onScrollListener.onHide();
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

    public OnScrollListener onScrollListener = new OnScrollListener() {
        @Override
        public void onHide() {
            actionmode.animate().translationY(actionmode.getHeight()).setInterpolator(new AccelerateInterpolator())
                    .setListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {}

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            actionmode.setVisibility(View.GONE);
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    });
        }

        @Override
        public void onShow() {
            if (selectedApps != null && selectedApps.size() > 0) {
                actionmode.animate().translationY(0).setInterpolator(new AccelerateInterpolator())
                        .setListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {
                                actionmode.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {

                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {

                            }
                        });
            }
        }
    };
}
