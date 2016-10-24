package com.fastaccess.ui.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fastaccess.ui.base.mvp.presenter.BasePresenter;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import icepick.Icepick;

/**
 * Created by Kosh on 27 May 2016, 7:54 PM
 */

public abstract class BaseFragment<V, P extends BasePresenter<V>> extends Fragment {

    @Nullable private Unbinder unbinder;

    @LayoutRes protected abstract int fragmentLayout();

    @NonNull protected abstract P getPresenter();

    protected abstract void onFragmentCreated(View view, @Nullable Bundle savedInstanceState);

    @Override public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override public void onDetach() {
        super.onDetach();
    }

    @Override public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }

    @Override public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null && !savedInstanceState.isEmpty()) {
            Icepick.restoreInstanceState(this, savedInstanceState);
        }
    }

    @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (fragmentLayout() != 0) {
            View view = inflater.inflate(fragmentLayout(), container, false);
            unbinder = ButterKnife.bind(this, view);
            return view;
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        onFragmentCreated(view, savedInstanceState);
    }

    @Override public void onDestroyView() {
        getPresenter().onDestroy();
        super.onDestroyView();
        if (unbinder != null) unbinder.unbind();
    }

    protected boolean isSafe() {
        return getView() != null && getActivity() != null && !getActivity().isFinishing();
    }
}
