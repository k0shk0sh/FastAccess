package com.fastaccess.data.dao;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.orm.SugarRecord;
import com.orm.dsl.Ignore;
import com.orm.dsl.Unique;
import com.orm.query.Select;
import com.orm.util.NamingHelper;

import java.util.List;

/**
 * Created by Kosh on 10 Oct 2016, 10:21 PM
 */

public class FolderModel extends SugarRecord implements Parcelable {

    @Unique private String folderName;
    private long createdDate;
    private int orderIndex;
    private int color;
    private int appsCount;
    @Ignore private List<AppsModel> folderApps;

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public long getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(long createdDate) {
        this.createdDate = createdDate;
    }

    public int getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(int orderIndex) {
        this.orderIndex = orderIndex;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getAppsCount() {
        appsCount = (int) AppsModel.countApps(getId());
        return appsCount;
    }

    @Nullable public static FolderModel getFolder(@NonNull String folderName) {
        return Select.from(FolderModel.class).where(NamingHelper.toSQLNameDefault("folderName") + " = ? COLLATE NOCASE", new String[]{folderName})
                .first();
    }

    public static List<FolderModel> getFolders() {
        return Select.from(FolderModel.class)
                .orderBy(NamingHelper.toSQLNameDefault("createdDate") + " DESC")
                .list();
    }

    public static void deleteFolder(@NonNull FolderModel model) {
        model.delete();
        AppsModel.deleteAll(AppsModel.class, NamingHelper.toSQLNameDefault("folderId") + " = ?", String.valueOf(model.getId()));
    }

    public FolderModel() {}

    @Override public int describeContents() { return 0; }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.folderName);
        dest.writeLong(this.createdDate);
        dest.writeInt(this.orderIndex);
        dest.writeInt(this.color);
        dest.writeInt(this.appsCount);
        dest.writeValue(this.getId());
    }

    protected FolderModel(Parcel in) {
        this.folderName = in.readString();
        this.createdDate = in.readLong();
        this.orderIndex = in.readInt();
        this.color = in.readInt();
        this.appsCount = in.readInt();
        this.setId((Long) in.readValue(Long.class.getClassLoader()));
    }

    public static final Creator<FolderModel> CREATOR = new Creator<FolderModel>() {
        @Override public FolderModel createFromParcel(Parcel source) {return new FolderModel(source);}

        @Override public FolderModel[] newArray(int size) {return new FolderModel[size];}
    };

    public List<AppsModel> getFolderApps() {
        return AppsModel.getApps(getId());
    }

    public void setFolderApps(List<AppsModel> folderApps) {
        this.folderApps = folderApps;
        if (folderApps != null && !folderApps.isEmpty()) {
            for (AppsModel app : folderApps) {
                if (app.getFolderId() == 0) {
                    app.setFolderId(getId());
                }
                if (app.getFolderId() != 0) app.save();
            }
        }
    }
}
