package com.styleme.floating.toolbox.pro.global.model;

/**
 * Created by Kosh on 8/21/2015. copyrights are reserved
 */
public class EventsModel {
    private String packageName;
    private EventType eventType;
    private int appsCount;
    private int previewSize;

    public EventsModel() {}

    public EventsModel(EventType eventType) {
        this.eventType = eventType;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public int getAppsCount() {
        return appsCount;
    }

    public void setAppsCount(int appsCount) {
        this.appsCount = appsCount;
    }

    public int getPreviewSize() {
        return previewSize;
    }

    public void setPreviewSize(int previewSize) {
        this.previewSize = previewSize;
    }
}
