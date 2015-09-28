package com.styleme.floating.toolbox.pro.global.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

/**
 * Created by Kosh on 9/6/2015. copyrights are reserved
 */
public class BackupModel {
    @Expose
    @SerializedName("selectedApps")
    private List<AppsModel> selectedApps;
    @Expose
    @SerializedName("prefs")
    private Map<String, ?> prefs;

    public List<AppsModel> getSelectedApps() {
        return selectedApps;
    }

    public void setSelectedApps(List<AppsModel> selectedApps) {
        this.selectedApps = selectedApps;
    }

    public Map<String, ?> getPrefs() {
        return prefs;
    }

    public void setPrefs(Map<String, ?> prefs) {
        this.prefs = prefs;
    }
}
