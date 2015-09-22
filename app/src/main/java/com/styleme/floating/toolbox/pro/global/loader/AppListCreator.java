package com.styleme.floating.toolbox.pro.global.loader;

import android.content.Context;

import com.styleme.floating.toolbox.pro.global.helper.AppHelper;
import com.styleme.floating.toolbox.pro.global.model.AppsModel;

import java.util.List;


/**
 * Created by Kosh on 8/19/2015. copyrights are reserved
 */
public class AppListCreator {

    public List<AppsModel> getAppList(Context context) {
        if (!AppHelper.isAutoOrder(context)) {
            return new AppsModel().getAll();
        } else {
            return new AppsModel().getAllByUsage();
        }
    }

}
