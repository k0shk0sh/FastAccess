package com.styleme.floating.toolbox.pro.global.model;

/**
 * Created by Kosh on 9/4/2015. copyrights are reserved
 */
public class FloatingModel {

    public static int HEADER = 1;
    public static int APPS = 2;
    private int type;
    private AppsModel appsModel;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public AppsModel getAppsModel() {
        return appsModel;
    }

    public void setAppsModel(AppsModel appsModel) {
        this.appsModel = appsModel;
    }
}
