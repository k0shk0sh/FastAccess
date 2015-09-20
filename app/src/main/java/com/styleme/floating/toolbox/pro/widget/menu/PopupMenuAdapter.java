package com.styleme.floating.toolbox.pro.widget.menu;

import android.content.Context;
import android.view.MenuItem;

import com.styleme.floating.toolbox.pro.global.model.AppsModel;

import java.util.List;

/**
 * Created by Kosh on 9/12/2015. copyrights are reserved
 */
public class PopupMenuAdapter {

    private Context context;
    private Popup popup;

    public PopupMenuAdapter(Context context, Popup popup) {
        this.context = context;
        this.popup = popup;
    }

    public MenuItem getByPosition(int position) {
        return popup.getMenu().getItem(position);
    }

    public void insertNew(List<AppsModel> appsModels) {
        if (appsModels != null) {
            for (AppsModel app : appsModels) {
                if (app != null) {
                    int id = Integer.parseInt(Long.toString(app.getId()));
                    popup.getMenu().add(0, id, 0, app.getAppName());
                }
            }
        }
    }

    public void remove(long id) {
        if (popup.getMenu() != null) popup.getMenu().removeItem(Integer.parseInt(Long.toString(id)));
    }

    public void clearAll() {
        if (popup != null && popup.getMenu() != null) {
            popup.getMenu().clear();
        }
    }
}
