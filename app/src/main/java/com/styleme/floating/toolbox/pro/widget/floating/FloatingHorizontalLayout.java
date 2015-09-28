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

import com.styleme.floating.toolbox.pro.AppController;
import com.styleme.floating.toolbox.pro.R;
import com.styleme.floating.toolbox.pro.global.adapter.RecyclerFloatingAdapter;
import com.styleme.floating.toolbox.pro.global.helper.AppHelper;
import com.styleme.floating.toolbox.pro.global.helper.EventTrackerHelper;
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

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
import static android.view.WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
import static android.view.WindowManager.LayoutParams.TYPE_PRIORITY_PHONE;

/**
 * Created by Kosh on 9/4/2015. copyrights are reserved
 */
public class FloatingHorizontalLayout implements OnFloatingTouchListener, OnItemClickListener {

    private LayoutParams mParams, rParams;
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
    private View view;

    public FloatingHorizontalLayout(Context context) {
        this.context = context;
        if (!AppController.getController().eventBus().isRegistered(this)) {
            AppController.getController().eventBus().register(this);
        }
        adapter = new RecyclerFloatingAdapter(this, new ArrayList<AppsModel>());
        gestureDetector = new GestureDetector(context, new GestureListener(this));
        initWindows();
        EventTrackerHelper.sendEvent("FloatingHorizontalLayout", "FloatingHorizontalLayout", "Init");
    }

    private void initWindows() {
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getSize(szWindow);
        mParams = new LayoutParams(TYPE_PRIORITY_PHONE /* to appear on top of keyboard */, FLAG_NOT_FOCUSABLE | FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT);
        rParams = new LayoutParams(TYPE_PRIORITY_PHONE /* to appear on top of keyboard */, FLAG_NOT_FOCUSABLE | FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT);
        setupParams();
        mParams.gravity = Gravity.LEFT | Gravity.TOP;
        rParams.gravity = Gravity.LEFT | Gravity.TOP;
        initView();
    }

    private void setupParams() {
        int iconSize = AppHelper.getFinalSize(context);
        int pX = AppHelper.getPositionX(context);
        int pY = AppHelper.getPositionY(context);
        rParams.width = MATCH_PARENT;
        rParams.height = iconSize;
        mParams.width = iconSize;
        mParams.height = iconSize;
        if (AppHelper.isSavePositionEnabled(context)) {
            mParams.x = pX;
            mParams.y = pY;
            rParams.y = pY + iconSize;
            rParams.x = pX;
        } else {
            mParams.x = 0;
            mParams.y = 100;
            rParams.x = 0;
            rParams.y = 100 + iconSize;
        }
    }

    private void initView() {
        view = LayoutInflater.from(context).inflate(R.layout.floating_layout_list, null);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        setupBackground();
        hideRecycler();
        recyclerView.setAdapter(adapter);
        windowManager.addView(view, rParams);
        if (floatingImage == null) floatingImage = new ImageView(context);
        setupFloatingImage(false);
        floatingImage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureDetector.onTouchEvent(event);
                return FloatingHorizontalLayout.this.onTouch(v, event);
            }
        });
        windowManager.addView(floatingImage, mParams);
        onMyAppsLoader = new MyPopupAppsLoader(context, this);
        onMyAppsLoader.registerListener(3, onLoadCompleteListener);
        onMyAppsLoader.startLoading();
        animateHidden();
    }

    private void hideRecycler() {
        view.setVisibility(View.GONE);
    }

    private void showRecycler() {
        int iconSize = AppHelper.getFinalSize(context);
        if (paramsF != null) {
            rParams.x = paramsF.x;
            if (paramsF.y + iconSize / 2 > szWindow.y / 2) {
                rParams.y = paramsF.y - iconSize;
            } else if (paramsF.y + iconSize / 2 <= szWindow.y / 2) {
                rParams.y = paramsF.y + iconSize;
            }
            windowManager.updateViewLayout(view, rParams);
        }
        view.setVisibility(View.VISIBLE);
    }

    private void setupBackground() {
        Drawable drawable = AppHelper.getColorDrawable(AppHelper.getFABackground(context));
        drawable.setAlpha(AppHelper.getBackgroundAlpha(context));
        if (view != null) {
            view.setBackground(drawable);
            showRecycler();
        }
    }

    private void setupFloatingImage(boolean update) {
        if (AppHelper.getImage(context) != null && new File(AppHelper.getImage(context)).exists()) {
            floatingImage.setImageBitmap(BitmapFactory.decodeFile(AppHelper.getImage(context)));
        } else {
            floatingImage.setImageResource(R.mipmap.ic_launcher);
        }
        if (adapter != null) adapter.notifyDataSetChanged();
        if (update) {
            if (windowManager != null)
                try {
                    windowManager.updateViewLayout(FloatingHorizontalLayout.this.floatingImage, mParams);
                    windowManager.updateViewLayout(FloatingHorizontalLayout.this.view, rParams);
                } catch (Exception e) {
                    e.printStackTrace();
                }
        }
    }

    public void onDestroy() {
        if (windowManager != null && floatingImage != null) {
            windowManager.removeViewImmediate(floatingImage);
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
        EventTrackerHelper.sendEvent("FloatingHorizontalLayout", "onDestroy", "onDestroy");
    }

    @Override
    public void onClick() {
        if (view != null) {
            animateShowing();
            if (view.isShown()) {
                hideRecycler();
                EventTrackerHelper.sendEvent("FloatingHorizontalLayout", "OnClick", "Hide");
            } else {
                showRecycler();
                EventTrackerHelper.sendEvent("FloatingHorizontalLayout", "onClick", "Show");
            }
        }
    }

    @Override
    public void onLongClick() {
        Notifier.createNotification(context, adapter.getItemCount());
        EventTrackerHelper.sendEvent("FloatingHorizontalLayout", "onLongClick", "onLongClick");
    }

    @Override
    public void onDoubleClick() {
        if (context != null) {
            Intent startMain = new Intent(Intent.ACTION_MAIN);
            startMain.addCategory(Intent.CATEGORY_HOME);
            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(startMain);
        }
        EventTrackerHelper.sendEvent("FloatingHorizontalLayout", "onDoubleClick", "onDoubleClick");
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
                break;
            case MotionEvent.ACTION_UP:
                if (AppHelper.isEdged(context)) {
                    moveToEdge();
                } else {
                    if (AppHelper.isSavePositionEnabled(context)) {
                        AppHelper.savePosition(context, paramsF.y, paramsF.x);
                    }
                }
                animateHidden();
                break;
            case MotionEvent.ACTION_MOVE:
                if (paramsF.y < 0) {
                    paramsF.y = 0;
                }
                if (paramsF.y >= 0 || paramsF.y <= (szWindow.y - AppHelper.getFinalSize(context))) {
                    paramsF.x = initialX + (int) (event.getRawX() - initialTouchX);
                    paramsF.y = initialY + (int) (event.getRawY() - initialTouchY);
                    try {
                        windowManager.updateViewLayout(FloatingHorizontalLayout.this.floatingImage, paramsF);
                        if (view.isShown()) {
                            showRecycler();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    animateShowing();
                } else if (paramsF.y > (szWindow.y - AppHelper.getFinalSize(context))) {
                    paramsF.y = (szWindow.y - AppHelper.getFinalSize(context));
                }
                break;
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
                            windowManager.updateViewLayout(FloatingHorizontalLayout.this.floatingImage, paramsF);
                        } catch (Exception ignored) {}
                    }

                    public void onFinish() {
                        paramsF.x = 0;
                        try {
                            windowManager.updateViewLayout(FloatingHorizontalLayout.this.floatingImage, paramsF);
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
                            windowManager.updateViewLayout(FloatingHorizontalLayout.this.floatingImage, paramsF);
                        } catch (Exception ignored) {}
                    }

                    public void onFinish() {
                        paramsF.x = szWindow.x - floatingImage.getWidth();
                        try {
                            windowManager.updateViewLayout(FloatingHorizontalLayout.this.floatingImage, paramsF);
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
            EventTrackerHelper.sendEvent("FloatingHorizontalLayout", "onAppClick", "onAppClick", appsModel.getAppName() == null ? appsModel
                    .getPackageName() : appsModel.getAppName());

        } catch (Exception e) {
            e.printStackTrace();
            EventTrackerHelper.sendEvent("FloatingHorizontalLayout", "onAppClick", "Crash!!!", e.getMessage());
        }
        if (!AppHelper.isAlwaysShowing(context)) {
            hideRecycler();
        }
    }

    @Override
    public void onReset() {
        adapter.clear();
        EventTrackerHelper.sendEvent("FloatingHorizontalLayout", "onReset", "onReset");
    }

    private void animateHidden() {
        if (AppHelper.isAutoTransparent(context))
            floatingImage.setImageAlpha(AppHelper.getIconTransparency(context));
    }

    private void animateShowing() {
        floatingImage.setImageAlpha(255);
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
            EventTrackerHelper.sendEvent("FloatingHorizontalLayout", "onLoadCompleteListener", "onLoadCompleteListener", "new Data: " + data.size());
        }
    };

    public void onEvent(EventsModel eventsModel) {
        if (eventsModel != null && eventsModel.getEventType() == EventType.SETTINGS_CHANGE) {
            setupParams();
            setupFloatingImage(true);
            animateHidden();
            EventTrackerHelper.sendEvent("onEvent", "onLoadCompleteListener", "EventType.SETTINGS_CHANGE");
        } else if (eventsModel != null && eventsModel.getEventType() == EventType.PREVIEW) {
            mParams.height = AppHelper.toPx(context, eventsModel.getPreviewSize());
            mParams.width = AppHelper.toPx(context, eventsModel.getPreviewSize());
            try {
                EventTrackerHelper.sendEvent("onEvent", "onLoadCompleteListener", "EventType.PREVIEW");
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
        } else if (eventsModel != null && eventsModel.getEventType() == EventType.ICON_ALPHA) {
            animateHidden();
        }
    }

    @Override
    public void onItemClickListener(View view, int position) {
        onAppClick(adapter.getModelList().get(position));
    }

    @Override
    public void onItemLongClickListener(RecyclerView.ViewHolder viewHolder, View view, int position) {}
}
