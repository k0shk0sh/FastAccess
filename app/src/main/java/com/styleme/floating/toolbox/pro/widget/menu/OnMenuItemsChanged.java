package com.styleme.floating.toolbox.pro.widget.menu;

import com.styleme.floating.toolbox.pro.global.model.AppsModel;

import java.util.List;

/**
 * Created by Kosh on 9/12/2015. copyrights are reserved
 */
public interface OnMenuItemsChanged {
    void onMenuChanged(List<AppsModel> appsModelList);

    void onMenuReset();
}
