package com.styleme.floating.toolbox.pro.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.styleme.floating.toolbox.pro.AppController;
import com.styleme.floating.toolbox.pro.R;
import com.styleme.floating.toolbox.pro.global.helper.AppHelper;
import com.styleme.floating.toolbox.pro.global.model.EventType;
import com.styleme.floating.toolbox.pro.global.model.EventsModel;
import com.styleme.floating.toolbox.pro.widget.FontTextView;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Kosh on 9/16/2015. copyrights are reserved
 */
public class IconSizeFragment extends DialogFragment implements DiscreteSeekBar.OnProgressChangeListener {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.appbar)
    AppBarLayout appbar;
    @Bind(R.id.sizeValue)
    FontTextView sizeValue;
    @Bind(R.id.seek)
    DiscreteSeekBar seek;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (AppHelper.isDarkTheme(getActivity())) {
            setStyle(DialogFragment.STYLE_NORMAL, R.style.FloatingTheme);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.icon_size_popup, container, false);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.done) {
                    AppHelper.setFaIconSize(getActivity(), seek.getProgress());
                    EventsModel eventsModel = new EventsModel();
                    eventsModel.setEventType(EventType.SETTINGS_CHANGE);
                    AppController.getController().eventBus().post(eventsModel);
                    dismiss();
                    return true;
                } else if (item.getItemId() == R.id.sneak) {
                    EventsModel eventsModel = new EventsModel();
                    eventsModel.setPreviewSize(seek.getProgress());
                    eventsModel.setEventType(EventType.PREVIEW);
                    AppController.getController().eventBus().post(eventsModel);
                    return true;
                }
                return true;
            }
        });
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.inflateMenu(R.menu.fragment_menu);
        toolbar.setTitle("FA Icon Size");
        toolbar.setBackgroundColor(AppHelper.getPrimaryColor(getActivity()));
        seek.setTrackColor(AppHelper.getPrimaryColor(getActivity()));
        seek.setOnProgressChangeListener(this);
        seek.setScrubberColor(AppHelper.getAccentColor(getActivity()));
        seek.setThumbColor(AppHelper.getPrimaryColor(getActivity()), AppHelper.getAccentColor(getActivity()));
        seek.setProgress(AppHelper.getFaIconSize(getActivity()));
        sizeValue.setTextColor(AppHelper.getAccentColor(getActivity()));
        sizeValue.setText(String.format("%d", AppHelper.getFaIconSize(getActivity())));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
        sizeValue.setText(String.format("%d", value));
    }

    @Override
    public void onStartTrackingTouch(DiscreteSeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(DiscreteSeekBar seekBar) {
    }
}
