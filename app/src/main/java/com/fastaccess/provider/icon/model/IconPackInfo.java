package com.fastaccess.provider.icon.model;

import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;

/**
 * Created by Kosh on 13/12/15 1:21 AM
 */
public class IconPackInfo {
    public String packageName;
    public CharSequence label;
    public Drawable icon;

    private IconPackInfo() {}

    public IconPackInfo(ResolveInfo r, PackageManager packageManager) {
        packageName = r.activityInfo.packageName;
        icon = r.loadIcon(packageManager);
        label = r.loadLabel(packageManager);
    }

    public IconPackInfo(String label, Drawable icon, String packageName) {
        this.label = label;
        this.icon = icon;
        this.packageName = packageName;
    }
}
