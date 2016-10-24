package com.fastaccess.ui.modules.apps.folders.create;

import org.xdty.preference.colorpicker.ColorPickerSwatch;

/**
 * Created by Kosh on 11 Oct 2016, 8:26 PM
 */

public interface CreateFolderMvp {

    interface View extends ColorPickerSwatch.OnColorSelectedListener {}//op-out

    interface Presenter {}//op-out

    interface OnNotifyFoldersAdapter {
        void onNotifyChanges();
    }
}
