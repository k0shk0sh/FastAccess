package com.styleme.floating.toolbox.pro.widget.menu;

import android.content.Context;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.styleme.floating.toolbox.pro.global.model.AppsModel;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by Kosh on 9/12/2015. copyrights are reserved
 */
public class Popup implements OnMenuItemsChanged, PopupMenu.OnMenuItemClickListener {

    private Context context;
    private PopupMenu popupMenu;
    private ImageView view;
    private PopupMenuAdapter adapter;

    public Popup(Context context, ImageView view) {
        this.context = context;
        this.view = view;
        setupMenu();
    }

    private void setupMenu() {
        if (popupMenu == null) popupMenu = new PopupMenu(context, view);
        popupMenu.setOnMenuItemClickListener(this);
        try {
            Field[] fields = popupMenu.getClass().getDeclaredFields();
            for (Field field : fields) {
                if ("mPopup".equals(field.getName())) {
                    field.setAccessible(true);
                    Object menuPopupHelper = field.get(popupMenu);
                    Class<?> classPopupHelper = Class.forName(menuPopupHelper
                            .getClass().getName());
                    Method setForceIcons = classPopupHelper.getMethod("setForceShowIcon", boolean.class);
                    setForceIcons.invoke(menuPopupHelper, true);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (adapter == null) adapter = new PopupMenuAdapter(context, this);
    }

    public Menu getMenu() {
        return popupMenu.getMenu();
    }

    public PopupMenu getPopupMenu() {
        return popupMenu;
    }

    private void insertAll(List<AppsModel> appsModelList) {
        if (adapter != null) {
            adapter.clearAll();
            adapter.insertNew(appsModelList);
        }
    }

    private void clearAll() {
        if (adapter != null) {
            adapter.clearAll();
        }
    }

    @Override
    public void onMenuChanged(List<AppsModel> appsModelList) {
        insertAll(appsModelList);
    }

    @Override
    public void onMenuReset() {
        clearAll();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        AppsModel appsModel = new AppsModel().getById(item.getItemId());
        if (appsModel != null) {
            Toast.makeText(context, appsModel.getAppName(), Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }
}
