package com.styleme.floating.toolbox.pro.global.helper;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.styleme.floating.toolbox.pro.AppController;

/**
 * Created by Kosh on 9/25/2015. copyrights are reserved
 */
public class EventTrackerHelper {

    public static void sendEvent(Object className, String category, String action) {
        HitBuilders.EventBuilder eventBuilder = new HitBuilders.EventBuilder();
        eventBuilder.setCategory(category).setAction(action);
        Tracker tracker = AppController.getController().tracker();
        tracker.setScreenName((String) className);
        tracker.send(eventBuilder.build());
    }

    public static void sendEvent(Object className, String category, String action, String label) {
        HitBuilders.EventBuilder eventBuilder = new HitBuilders.EventBuilder();
        eventBuilder.setCategory(category).setAction(action).setLabel(label);
        Tracker tracker = AppController.getController().tracker();
        tracker.setScreenName((String) className);
        tracker.send(eventBuilder.build());
    }

}
