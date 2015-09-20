package com.styleme.floating.toolbox.pro;

import com.activeandroid.app.Application;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.facebook.stetho.Stetho;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.styleme.floating.toolbox.pro.activities.Home;
import com.styleme.floating.toolbox.pro.global.helper.IconCache;

import cat.ereza.customactivityoncrash.CustomActivityOnCrash;
import de.greenrobot.event.EventBus;
import io.fabric.sdk.android.Fabric;

/**
 * Created by Kosh on 9/3/2015. copyrights are reserved
 */
public class AppController extends Application {
    private static AppController appController;
    private IconCache mIconCache;
    private static GoogleAnalytics analytics;
    private static Tracker tracker;

    public GoogleAnalytics analytics() {
        if (analytics == null) analytics = GoogleAnalytics.getInstance(this);
        return analytics;
    }

    public Tracker tracker() {
        if (analytics == null) analytics = analytics();
        if (tracker == null) tracker = analytics.newTracker(R.xml.app_tracker);
        return tracker;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        appController = this;
        mIconCache = new IconCache(this);
        CustomActivityOnCrash.setRestartActivityClass(Home.class);
        if (BuildConfig.DEBUG) {
            CustomActivityOnCrash.setShowErrorDetails(false);
        }
        CustomActivityOnCrash.install(this);
        CrashlyticsCore core = new CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build();
        Fabric.with(this, new Crashlytics.Builder().core(core).build());
        Stetho.initialize(Stetho.newInitializerBuilder(this)
                .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
                .build());
        tracker = analytics().newTracker(R.xml.app_tracker);//create your own app_tracker.xml in xml folder
        tracker.enableAutoActivityTracking(true);
    }

    public static AppController getController() {
        return appController;
    }

    public EventBus eventBus() {
        return EventBus.getDefault();
    }

    public IconCache getIconCache(boolean recreate) {
        if (mIconCache != null) mIconCache.flush();
        if (recreate) mIconCache = new IconCache(this);
        return mIconCache;
    }

}
