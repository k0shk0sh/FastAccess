package com.styleme.floating.toolbox.pro.global.loader;

import android.content.Context;

import com.styleme.floating.toolbox.pro.global.model.AppsModel;

import java.util.List;


/**
 * Created by Kosh on 8/19/2015. copyrights are reserved
 */
public class AppListCreator {
    private Context context;

    public AppListCreator(Context context) {
        this.context = context;
    }

    public List<AppsModel> getAppList() {
        List<AppsModel> appsModel = new AppsModel().getAll();
        return appsModel;
    }

}
