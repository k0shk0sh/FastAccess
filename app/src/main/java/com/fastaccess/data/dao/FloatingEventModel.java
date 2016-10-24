package com.fastaccess.data.dao;

/**
 * Created by Kosh on 15 Oct 2016, 8:57 PM
 */

public class FloatingEventModel {

    private boolean settingsChanged;
    private String key;

    public FloatingEventModel() {}

    public FloatingEventModel(boolean settingsChanged) {
        this.settingsChanged = settingsChanged;
    }

    public FloatingEventModel(boolean settingsChanged, String key) {
        this.settingsChanged = settingsChanged;
        this.key = key;
    }

    public boolean isSettingsChanged() {
        return settingsChanged;
    }

    public String getKey() {
        return key;
    }
}
