package com.fastaccess.ui.modules.floating.apps;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.View;

import com.fastaccess.data.dao.AppsModel;
import com.fastaccess.ui.modules.floating.BaseFloatingMvp;
import com.fastaccess.ui.modules.floating.BaseFloatingPresenter;

/**
 * Created by Kosh on 14 Oct 2016, 9:00 PM
 */

public class FloatingVHPresenter extends BaseFloatingPresenter<AppsModel, BaseFloatingMvp.BaseView<AppsModel>> implements FloatingHVMvp.Presenter {


    public FloatingVHPresenter(@NonNull BaseFloatingMvp.BaseView<AppsModel> view) {
        super(view);
    }

    public static FloatingVHPresenter with(@NonNull BaseFloatingMvp.BaseView<AppsModel> view) {
        return new FloatingVHPresenter(view);
    }

    @Override public void onItemClick(int position, View v, AppsModel item) {
        try {
            Context context = v.getContext();
            Intent intent = new Intent();
            intent.setComponent(new ComponentName(item.getPackageName(), item.getActivityInfoName()));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {// app uninstalled/not found
            e.printStackTrace();
            item.delete();
        }
        super.onItemClick(position, v, item);
    }
}
