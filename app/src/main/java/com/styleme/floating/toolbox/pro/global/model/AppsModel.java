package com.styleme.floating.toolbox.pro.global.model;

import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.google.gson.annotations.Expose;
import com.styleme.floating.toolbox.pro.global.helper.IconCache;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Kosh on 9/3/2015. copyrights are reserved
 */
public class AppsModel extends Model {
    @Column
    @Expose
    private String appName;
    @Column(unique = true, onUniqueConflicts = Column.ConflictAction.REPLACE, onUniqueConflict = Column.ConflictAction.REPLACE)
    @Expose
    private String packageName;
    @Column
    @Expose
    private String iconPath;
    @Column
    @Expose
    private String activityInfoName;
    @Column
    @Expose
    private int appPosition;
    @Column
    private int countEntry;
    private Bitmap bitmap;
    private ComponentName componentName;
    private IconCache iconCache;
    private PackageManager pm;
    private ResolveInfo info;
    private HashMap<Object, CharSequence> labelCache;

    public AppsModel() {}

    public AppsModel(PackageManager pm, ResolveInfo info, IconCache iconCache, HashMap<Object, CharSequence> labelCache) {
        this.packageName = info.activityInfo.applicationInfo.packageName;
        this.componentName = new ComponentName(packageName, info.activityInfo.name);
        this.activityInfoName = info.activityInfo.name;
        this.appName = info.loadLabel(pm).toString();
        iconCache.getTitleAndIcon(this, info, labelCache);
    }

    public AppsModel DoIt(PackageManager pm, ResolveInfo info, IconCache iconCache, HashMap<Object, CharSequence> labelCache) {
        this.packageName = info.activityInfo.applicationInfo.packageName;
        this.componentName = new ComponentName(packageName, info.activityInfo.name);
        this.activityInfoName = info.activityInfo.name;
        this.appName = info.loadLabel(pm).toString();
        iconCache.getTitleAndIcon(this, info, labelCache);
        return this;
    }

    public PackageManager getPm() {
        return pm;
    }

    public void setPm(PackageManager pm) {
        this.pm = pm;
    }

    public ResolveInfo getInfo() {
        return info;
    }

    public void setInfo(ResolveInfo info) {
        this.info = info;
    }

    public HashMap<Object, CharSequence> getLabelCache() {
        return labelCache;
    }

    public void setLabelCache(HashMap<Object, CharSequence> labelCache) {
        this.labelCache = labelCache;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getIconPath() {
        return iconPath;
    }

    public void setIconPath(String iconPath) {
        this.iconPath = iconPath;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public ComponentName getComponentName() {
        return componentName;
    }

    public void setComponentName(ComponentName componentName) {
        this.componentName = componentName;
    }

    public IconCache getIconCache() {
        return iconCache;
    }

    public void setIconCache(IconCache iconCache) {
        this.iconCache = iconCache;
    }

    public String getActivityInfoName() {
        return activityInfoName;
    }

    public void setActivityInfoName(String activityInfoName) {
        this.activityInfoName = activityInfoName;
    }

    public void add(List<AppsModel> modelList) {
        if (modelList != null && modelList.size() != 0) {
            for (AppsModel model : modelList) {
                model.save();
            }
        }
    }

    public boolean deleteByPackageName(String packageName) {
        return new Delete().from(AppsModel.class).where("packageName = ?", packageName).execute() != null;
    }

    public boolean deleteById(long id) {
        return new Delete().from(AppsModel.class).where("id = ?", id).execute() != null;
    }

    public AppsModel getAppByPackage(String packageName) {
        return new Select().from(AppsModel.class).where("packageName = ?", packageName).executeSingle();
    }

    public List<AppsModel> getAll() {
        return new Select().from(AppsModel.class).orderBy("appPosition ASC").execute();
    }

    public List<AppsModel> getAllByUsage() {
        return new Select().from(AppsModel.class).orderBy("countEntry DESC").execute();
    }


    public void deleteAll() {
        new Delete().from(AppsModel.class).execute();
    }

    public int getAppPosition() {
        return appPosition;
    }

    public void setAppPosition(int appPosition) {
        this.appPosition = appPosition;
    }

    public int lastPosition() {
        AppsModel appsModel = new Select().from(AppsModel.class).orderBy("appPosition DESC").limit(1).executeSingle();
        if (appsModel != null) {
            return appsModel.getAppPosition();
        }
        return 0;
    }

    public AppsModel getById(int id) {
        return new Select().from(AppsModel.class).where("id = ?", id).executeSingle();
    }

    public int countAll() {
        return new Select().from(AppsModel.class).count();
    }

    public int getCountEntry() {
        return countEntry;
    }

    public void setCountEntry(int countEntry) {
        this.countEntry = countEntry;
    }

    public void updateEntry(String packageName) {
        if (packageName != null && !packageName.isEmpty()) {
            AppsModel app = getAppByPackage(packageName);
            if (app != null) {
                app.setCountEntry(app.getCountEntry() + 1);
                app.save();
            }
        }
    }

    public Comparator<AppsModel> sortApps = new Comparator<AppsModel>() {
        @Override
        public int compare(AppsModel one, AppsModel two) {
            return one.getAppName().compareTo(two.getAppName());
        }
    };
}
