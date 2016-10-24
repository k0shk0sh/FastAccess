package com.fastaccess.data.dao;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fastaccess.helper.PrefHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
import java.util.Map;

/**
 * Created by Kosh on 23 Oct 2016, 8:51 PM
 */

public class BackupRestoreModel {

    private String uid;
    private List<FolderModel> folders;
    private List<AppsModel> appsModels;
    private Map<String, Object> settings;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public List<FolderModel> getFolders() {
        return folders;
    }

    public void setFolders(List<FolderModel> folders) {
        this.folders = folders;
    }

    public List<AppsModel> getAppsModels() {
        return appsModels;
    }

    public void setAppsModels(List<AppsModel> appsModels) {
        this.appsModels = appsModels;
    }

    public Map<String, Object> getSettings() {
        return settings;
    }

    public void setSettings(Map<String, Object> settings) {
        this.settings = settings;
    }

    @Nullable public static BackupRestoreModel backup() {
        BackupRestoreModel model = new BackupRestoreModel();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser == null) return null;
        model.setUid(firebaseUser.getUid());
        model.setFolders(FolderModel.getFolders());
        model.setSettings(PrefHelper.getAll());
        model.setAppsModels(AppsModel.getApps());
        return model;
    }

    public static void restore(@NonNull BackupRestoreModel model) {
        if (model.getFolders() != null) {
            FolderModel.saveInTx(model.getFolders());
            EventBus.getDefault().post(new FolderEventModel());
        }
        if (model.getAppsModels() != null) {
            AppsModel.saveInTx(model.getAppsModels());
            EventBus.getDefault().post(new FloatingEventModel());
        }
        if (model.getSettings() != null) {
            for (String key : model.getSettings().keySet()) {
                if (key != null && !key.equalsIgnoreCase("null")) {
                    PrefHelper.set(key, model.getSettings().get(key));
                }
            }
        }
    }

    @Override public String toString() {
        return "BackupRestoreModel{" +
                "uid='" + uid + '\'' +
                ", settings='" + settings + '\'' +
                '}';
    }
}
