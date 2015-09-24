package com.styleme.floating.toolbox.pro.widget.floating;

import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.styleme.floating.toolbox.pro.AppController;
import com.styleme.floating.toolbox.pro.R;
import com.styleme.floating.toolbox.pro.global.adapter.RecyclerFloatingAdapter;
import com.styleme.floating.toolbox.pro.global.helper.AppHelper;
import com.styleme.floating.toolbox.pro.global.helper.Notifier;
import com.styleme.floating.toolbox.pro.global.loader.MyPopupAppsLoader;
import com.styleme.floating.toolbox.pro.global.model.AppsModel;
import com.styleme.floating.toolbox.pro.global.model.EventType;
import com.styleme.floating.toolbox.pro.global.model.EventsModel;
import com.styleme.floating.toolbox.pro.global.service.FloatingService;
import com.styleme.floating.toolbox.pro.widget.impl.GestureListener;
import com.styleme.floating.toolbox.pro.widget.impl.OnFloatingTouchListener;
import com.styleme.floating.toolbox.pro.widget.impl.OnItemClickListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static android.view.WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
import static android.view.WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
import static android.view.WindowManager.LayoutParams.TYPE_PRIORITY_PHONE;

/**
 * Created by Kosh on 9/4/2015. copyrights are reserved
 */
public class FloatingHorizontalLayout implements OnFloatingTouchListener {

    private LayoutParams mParams;
    private WindowManager windowManager;
    private Context context;
    private LayoutParams paramsF;
    private int initialX;
    private int initialY;
    private float initialTouchX;
    private float initialTouchY;
    private RecyclerFloatingAdapter adapter;
    private GestureDetector gestureDetector;
    private Point szWindow = new Point();
    private ImageView floatingImage;
    private MyPopupAppsLoader onMyAppsLoader;
    private Tracker tracker = AppController.getController().tracker();
    private View view;
    private RecyclerView recyclerView;

    public FloatingHorizontalLayout(Context context) {
        this.context = context;
        if (!AppController.getController().eventBus().isRegistered(this)) {
            AppController.getController().eventBus().register(this);
        }
        adapter = new RecyclerFloatingAdapter(new OnItemClickListener() {
            @Override
            public void onItemClickListener(View view, int position) {

            }

            @Override
            public void onItemLongClickListener(RecyclerView.ViewHolder viewHolder, View view, int position) {

            }
        }, new ArrayList<AppsModel>());
        gestureDetector = new GestureDetector(context, new GestureListener(this));
        initWindows();
        tracker.setScreenName("FloatingHorizontalLayout");
        HitBuilders.EventBuilder eventBuilder = new HitBuilders.EventBuilder();
        eventBuilder.setCategory("FloatingHorizontalLayout")
                .setAction("Init")
                .setLabel("FloatingLayout");
        tracker.send(eventBuilder.build());
    }

    private void initWindows() {
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getSize(szWindow);
        mParams = new LayoutParams(TYPE_PRIORITY_PHONE /* to appear on top of keyboard */, FLAG_NOT_FOCUSABLE | FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT);
        setupParams();
        mParams.gravity = Gravity.LEFT | Gravity.TOP;
        initView();
    }

    private void setupParams() {
        int gapSize = AppHelper.getFinalSize(context);
        mParams.width = WRAP_CONTENT;
        mParams.height = gapSize * 2;
        if (AppHelper.isSavePositionEnabled(context)) {
            mParams.x = AppHelper.getPositionX(context);
            mParams.y = AppHelper.getPositionY(context);
        } else {
            mParams.x = 0;
            mParams.y = 100;
        }
    }

    private void initView() {
        view = LayoutInflater.from(context).inflate(R.layout.floating_layout_list, null);
        floatingImage = (ImageView) view.findViewById(R.id.floatingImage);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        setupBackground();
        recyclerView.setAdapter(adapter);
        setupFloatingImage(false);
        windowManager.addView(view, mParams);
        floatingImage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureDetector.onTouchEvent(event);
                return FloatingHorizontalLayout.this.onTouch(v, event);
            }
        });
        onMyAppsLoader = new MyPopupAppsLoader(context, this);
        onMyAppsLoader.registerListener(2, onLoadCompleteListener);
        onMyAppsLoader.startLoading();
    }

    private void hideRecycler() {
        recyclerView.setVisibility(View.GONE);
    }

    private void showRecycler() {
        recyclerView.setVisibility(View.VISIBLE);
    }

    private void setupBackground() {
        Drawable drawable = AppHelper.getColorDrawable(AppHelper.getFABackground(context));
        drawable.setAlpha(AppHelper.getBackgroundAlpha(context));
        if (recyclerView != null) {
            recyclerView.setBackground(drawable);
            if (!view.isShown()) view.setVisibility(View.VISIBLE);
        }
    }

    private void setupFloatingImage(boolean update) {
        if (AppHelper.getImage(context) != null && new File(AppHelper.getImage(context)).exists()) {
            floatingImage.setImageBitmap(BitmapFactory.decodeFile(AppHelper.getImage(context)));
        } else {
            floatingImage.setImageResource(R.mipmap.ic_launcher);
        }
        int gapSize = AppHelper.getFinalSize(context);
        floatingImage.getLayoutParams().width = gapSize;
        floatingImage.requestLayout();
        if (adapter != null) adapter.notifyDataSetChanged();
        if (update) {
            if (windowManager != null)
                try {
                    windowManager.updateViewLayout(FloatingHorizontalLayout.this.view, mParams);
                } catch (Exception e) {
                    e.printStackTrace();
                }
        }
    }

    public void onDestroy() {
        if (windowManager != null && floatingImage != null) {
            windowManager.removeViewImmediate(view);
        }
        if (onMyAppsLoader != null) {
            onMyAppsLoader.unregisterListener(onLoadCompleteListener);
            onMyAppsLoader.cancelLoad();
            onMyAppsLoader.stopLoading();
        }
        if (AppController.getController().eventBus().isRegistered(this)) {
            AppController.getController().eventBus().unregister(this);
        }
        HitBuilders.EventBuilder eventBuilder = new HitBuilders.EventBuilder();
        eventBuilder
                .setCategory("FloatingHorizontalLayout")
                .setAction("onDestroy")
                .setLabel("onDestroy");
        tracker.send(eventBuilder.build());
    }

    @Override
    public void onClick() {
        animateShowing();
        try {
            HitBuilders.EventBuilder eventBuilder = new HitBuilders.EventBuilder();
            if (recyclerView.isShown()) {
                hideRecycler();
                eventBuilder.setCategory("FloatingHorizontalLayout")
                        .setAction("hide")
                        .setLabel("onClick");
            } else {
                showRecycler();
                eventBuilder.setCategory("FloatingHorizontalLayout")
                        .setAction("show")
                        .setLabel("onClick");

            }
            tracker.send(eventBuilder.build());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLongClick() {
        Notifier.createNotification(context, adapter.getItemCount());
        HitBuilders.EventBuilder eventBuilder = new HitBuilders.EventBuilder();
        eventBuilder
                .setCategory("FloatingHorizontalLayout")
                .setAction("onLongClick")
                .setLabel("onLongClick");
        tracker.send(eventBuilder.build());
    }

    @Override
    public void onDoubleClick() {
        if (context != null) {
            Intent startMain = new Intent(Intent.ACTION_MAIN);
            startMain.addCategory(Intent.CATEGORY_HOME);
            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(startMain);
        }
        HitBuilders.EventBuilder eventBuilder = new HitBuilders.EventBuilder();

        eventBuilder
                .setCategory("FloatingHorizontalLayout")
                .setAction("onDoubleClick")
                .setLabel("onDoubleClick");
        tracker.send(eventBuilder.build());
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        paramsF = mParams;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                initialX = paramsF.x;
                initialY = paramsF.y;
                initialTouchX = event.getRawX();
                initialTouchY = event.getRawY();
                animateShowing();
                return true;
            case MotionEvent.ACTION_UP:
                if (AppHelper.isEdged(context)) {
                    moveToEdge();
                } else {
                    if (AppHelper.isSavePositionEnabled(context)) {
                        AppHelper.savePosition(context, paramsF.y, paramsF.x);
                    }
                }
                animateHidden();
                return true;
            case MotionEvent.ACTION_MOVE:
                paramsF.x = initialX + (int) (event.getRawX() - initialTouchX);
                paramsF.y = initialY + (int) (event.getRawY() - initialTouchY);
                try {
                    windowManager.updateViewLayout(FloatingHorizontalLayout.this.view, paramsF);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                animateShowing();
                return true;
        }
        return false;
    }

    private void moveToEdge() {
        try {
            int w = floatingImage.getWidth();
            if (paramsF.x + w / 2 <= szWindow.x / 2) {
                final int x = paramsF.x;
                new CountDownTimer(500, 5) {
                    public void onTick(long t) {
                        long step = (500 - t) / 5;
                        paramsF.x = (int) (double) bounceValue(step, x) - floatingImage.getWidth();
                        try {
                            windowManager.updateViewLayout(FloatingHorizontalLayout.this.view, paramsF);
                        } catch (Exception ignored) {}
                    }

                    public void onFinish() {
                        paramsF.x = 0;
                        try {
                            windowManager.updateViewLayout(FloatingHorizontalLayout.this.view, paramsF);
                        } catch (Exception ignored) {}
                        if (AppHelper.isSavePositionEnabled(context)) {
                            AppHelper.savePosition(context, paramsF.y, paramsF.x);
                        }
                    }
                }.start();

            } else if (paramsF.x + w / 2 > szWindow.x / 2) {
                final int x = paramsF.x;
                new CountDownTimer(500, 5) {
                    public void onTick(long t) {
                        long step = (500 - t) / 5;
                        paramsF.x = szWindow.x + (int) (double) bounceValue(step, x);
                        try {
                            windowManager.updateViewLayout(FloatingHorizontalLayout.this.view, paramsF);
                        } catch (Exception ignored) {}
                    }

                    public void onFinish() {
                        paramsF.x = szWindow.x - floatingImage.getWidth();
                        try {
                            windowManager.updateViewLayout(FloatingHorizontalLayout.this.view, paramsF);
                        } catch (Exception ignored) {}
                        if (AppHelper.isSavePositionEnabled(context)) {
                            AppHelper.savePosition(context, paramsF.y, paramsF.x);
                        }
                    }
                }.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onAppClick(AppsModel appsModel) {
        try {
            PackageManager manager = context.getPackageManager();
            Intent intent = manager.getLaunchIntentForPackage(appsModel.getPackageName());
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            context.startActivity(intent);
            appsModel.updateEntry(appsModel.getPackageName());
            HitBuilders.EventBuilder eventBuilder = new HitBuilders.EventBuilder();
            eventBuilder
                    .setCategory("FloatingHorizontalLayout")
                    .setAction("Open App")
                    .setLabel(appsModel.getAppName() == null ? appsModel.getPackageName() : appsModel.getAppName());
            tracker.send(eventBuilder.build());
        } catch (Exception e) {
            e.printStackTrace();
            HitBuilders.EventBuilder eventBuilder = new HitBuilders.EventBuilder();
            eventBuilder
                    .setCategory("FloatingHorizontalLayout")
                    .setAction("Open App Exception")
                    .setLabel(e.getMessage() == null ? "Open App Crash" : e.getMessage());
            tracker.send(eventBuilder.build());
        }
    }

    @Override
    public void onReset() {
        adapter.clear();
        HitBuilders.EventBuilder eventBuilder = new HitBuilders.EventBuilder();
        eventBuilder
                .setCategory("FloatingHorizontalLayout")
                .setAction("onReset")
                .setLabel("Adapter Reset");
        tracker.send(eventBuilder.build());
    }

    private void animateHidden() {
        if (AppHelper.isAutoTransparent(context))
            floatingImage.setAlpha(0.3F);
    }

    private void animateShowing() {
        floatingImage.setAlpha(1.0F);
    }

    private double bounceValue(long step, long scale) {
        return scale * Math.exp(-0.055 * step) * Math.cos(0.08 * step);
    }

    private Loader.OnLoadCompleteListener<List<AppsModel>> onLoadCompleteListener = new Loader.OnLoadCompleteListener<List<AppsModel>>() {
        @Override
        public void onLoadComplete(Loader<List<AppsModel>> loader, List<AppsModel> data) {
            if (data == null || data.size() == 0) {
                context.stopService(new Intent(context, FloatingService.class));
                Notifier.cancelNotification(context);//in case if the service is stopped previously and its in the notification bar
                return;
            }
            if (adapter != null) {
                adapter.insert(data);
            }
            HitBuilders.EventBuilder eventBuilder = new HitBuilders.EventBuilder();
            eventBuilder
                    .setCategory("FloatingHorizontalLayout")
                    .setAction("onLoadCompleteListener")
                    .setLabel("new Data: " + data.size());
            tracker.send(eventBuilder.build());
        }
    };

    public void onEvent(EventsModel eventsModel) {
        if (eventsModel != null && eventsModel.getEventType() == EventType.SETTINGS_CHANGE) {
            setupParams();
            setupFloatingImage(true);
            animateHidden();
            HitBuilders.EventBuilder eventBuilder = new HitBuilders.EventBuilder();
            eventBuilder
                    .setCategory("FloatingHorizontalLayout")
                    .setAction("onEvent")
                    .setLabel("EventType.SETTINGS_CHANGE");
            tracker.send(eventBuilder.build());
        } else if (eventsModel != null && eventsModel.getEventType() == EventType.PREVIEW) {
            mParams.height = AppHelper.toPx(context, eventsModel.getPreviewSize());
            mParams.width = AppHelper.toPx(context, eventsModel.getPreviewSize());
            try {
                HitBuilders.EventBuilder eventBuilder = new HitBuilders.EventBuilder();

                eventBuilder
                        .setCategory("FloatingHorizontalLayout")
                        .setAction("onEvent")
                        .setLabel("EventType.PREVIEW");
                if (windowManager != null) {
                    windowManager.updateViewLayout(FloatingHorizontalLayout.this.view, mParams);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            setupParams();
                            setupFloatingImage(true);
                        }
                    }, 1500);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (eventsModel != null && eventsModel.getEventType() == EventType.FA_BACKGROUND) {
            setupBackground();
        }
    }
}
