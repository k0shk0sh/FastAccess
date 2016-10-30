package com.fastaccess.data.dao;

import android.content.ComponentName;
import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.firebase.database.Exclude;
import com.orm.SugarRecord;
import com.orm.dsl.Ignore;
import com.orm.query.Condition;
import com.orm.query.Select;
import com.orm.util.NamingHelper;

import java.util.Comparator;
import java.util.List;

/**
 * Created by Kosh on 10 Oct 2016, 10:23 PM
 */

public class AppsModel extends SugarRecord implements Parcelable {
    private long folderId;
    private String appName;
    private String packageName;
    private String iconPath;
    private String activityInfoName;
    private int indexPosition;
    private int countEntry;
    @Exclude @Ignore private Bitmap bitmap;

    public AppsModel() {}//default constructor.

    public long getFolderId() {
        return folderId;
    }

    public void setFolderId(long folderId) {
        this.folderId = folderId;
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

    public String getActivityInfoName() {
        return activityInfoName;
    }

    public void setActivityInfoName(String activityInfoName) {
        this.activityInfoName = activityInfoName;
    }

    public int getIndexPosition() {
        return indexPosition;
    }

    public void setIndexPosition(int indexPosition) {
        this.indexPosition = indexPosition;
    }

    public int getCountEntry() {
        return countEntry;
    }

    public void setCountEntry(int countEntry) {
        this.countEntry = countEntry;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    @Exclude public ComponentName getComponentName() {
        return new ComponentName(packageName, activityInfoName);
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public static List<AppsModel> getApps() {
        return Select.from(AppsModel.class)
                .where(Condition.prop(NamingHelper.toSQLNameDefault("folderId")).eq(0))
                .orderBy(NamingHelper.toSQLNameDefault("indexPosition") + " ASC")
                .list();
    }

    public static List<AppsModel> getApps(long folderId) {
        return Select.from(AppsModel.class)
                .where(Condition.prop(NamingHelper.toSQLNameDefault("folderId")).eq(folderId))
                .list();
    }

    public static int lastPosition() {
        AppsModel appsModel = Select.from(AppsModel.class).orderBy(NamingHelper.toSQLNameDefault("indexPosition") + " DESC").first();
        if (appsModel != null) {
            return appsModel.getIndexPosition();
        }
        return 0;
    }

    public static long countApps(long folderId) {
        return count(AppsModel.class, NamingHelper.toSQLNameDefault("folderId") + " = ?", new String[]{String.valueOf(folderId)});
    }

    public static void deleteAllByFolder(@NonNull FolderModel folder) {
        deleteAll(AppsModel.class, NamingHelper.toSQLNameDefault("folderId") + " = ?", String.valueOf(folder.getId()));
    }

    public static void deleteAll() {
        deleteAll(AppsModel.class);
    }

    public static boolean exists(@NonNull String activityInfoName, @NonNull String packageName) {
        return Select.from(AppsModel.class)
                .where(Condition.prop(NamingHelper.toSQLNameDefault("activityInfoName")).eq(activityInfoName))
                .and(Condition.prop(NamingHelper.toSQLNameDefault("packageName")).eq(packageName))
                .and(Condition.prop(NamingHelper.toSQLNameDefault("folderId")).eq(0))
                .first() != null;
    }

    public static Comparator<AppsModel> sortApps() {
        return new Comparator<AppsModel>() {
            @Override public int compare(AppsModel one, AppsModel two) {
                return one.getAppName().compareTo(two.getAppName());
            }
        };
    }

    @Override public int describeContents() { return 0; }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.folderId);
        dest.writeString(this.appName);
        dest.writeString(this.packageName);
        dest.writeString(this.iconPath);
        dest.writeString(this.activityInfoName);
        dest.writeInt(this.indexPosition);
        dest.writeInt(this.countEntry);
        dest.writeValue(this.getId());
    }

    protected AppsModel(Parcel in) {
        this.folderId = in.readLong();
        this.appName = in.readString();
        this.packageName = in.readString();
        this.iconPath = in.readString();
        this.activityInfoName = in.readString();
        this.indexPosition = in.readInt();
        this.countEntry = in.readInt();
        this.setId((Long) in.readValue(Long.class.getClassLoader()));
    }

    public static final Creator<AppsModel> CREATOR = new Creator<AppsModel>() {
        @Override public AppsModel createFromParcel(Parcel source) {return new AppsModel(source);}

        @Override public AppsModel[] newArray(int size) {return new AppsModel[size];}
    };
}
