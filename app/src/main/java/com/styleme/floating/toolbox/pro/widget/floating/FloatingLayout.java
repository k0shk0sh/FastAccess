package com.styleme.floating.toolbox.pro.widget.floating;

import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.widget.ListPopupWindow;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ListAdapter;
import android.widget.PopupWindow;

import com.styleme.floating.toolbox.pro.AppController;
import com.styleme.floating.toolbox.pro.R;
import com.styleme.floating.toolbox.pro.global.adapter.FloatingAdapter;
import com.styleme.floating.toolbox.pro.global.helper.AppHelper;
import com.styleme.floating.toolbox.pro.global.helper.EventTrackerHelper;
import com.styleme.floating.toolbox.pro.global.helper.Notifier;
import com.styleme.floating.toolbox.pro.global.loader.MyPopupAppsLoader;
import com.styleme.floating.toolbox.pro.global.model.AppsModel;
import com.styleme.floating.toolbox.pro.global.model.EventType;
import com.styleme.floating.toolbox.pro.global.model.EventsModel;
import com.styleme.floating.toolbox.pro.global.service.FloatingService;
import com.styleme.floating.toolbox.pro.widget.FloatingImage;
import com.styleme.floating.toolbox.pro.widget.impl.GestureListener;
import com.styleme.floating.toolbox.pro.widget.impl.OnFloatingTouchListener;

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
public class FloatingLayout implements OnFloatingTouchListener {

    private LayoutParams mParams;
    private WindowManager windowManager;
    private Context context;
    private LayoutParams paramsF;
    private int initialX;
    private int initialY;
    private float initialTouchX;
    private float initialTouchY;
    private ListPopupWindow popupWindow;
    private FloatingAdapter adapter;
    private GestureDetector gestureDetector;
    private Point szWindow = new Point();
    private FloatingImage floatingImage;
    private MyPopupAppsLoader onMyAppsLoader;
    private boolean isShowed = true;

    public FloatingLayout(Context context) {
        this.context = context;
        if (!AppController.getController().eventBus().isRegistered(this)) {
            AppController.getController().eventBus().register(this);
        }
        adapter = new FloatingAdapter(new ArrayList<AppsModel>(), this);
        gestureDetector = new GestureDetector(context, new GestureListener(this));
        initWindows();
        EventTrackerHelper.sendEvent("FloatingLayout", "Init()", "Init()");
    }

    @Override
    public void onLongClick() {
        Notifier.createNotification(context, adapter.getCount());
        EventTrackerHelper.sendEvent("FloatingLayout", "onLongClick()", "onLongClick()");
    }

    @Override
    public void onDoubleClick() {
        if (context != null) {
            Intent startMain = new Intent(Intent.ACTION_MAIN);
            startMain.addCategory(Intent.CATEGORY_HOME);
            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(startMain);
        }
        EventTrackerHelper.sendEvent("FloatingLayout", "onDoubleClick()", "onDoubleClick()");
    }

    @Override
    public void onClick() {
        animateShowing();
        try {
            if (popupWindow.isShowing()) {
                popupWindow.dismiss();
                EventTrackerHelper.sendEvent("FloatingLayout", "onClick()", "hide()");
            } else {
                popupWindow.show();
                EventTrackerHelper.sendEvent("FloatingLayout", "onClick()", "show()");
            }
            configure();
            isShowed = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
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
                paramsF.x = initialX + (int) (event.getRawX() - initialTouchX);
                paramsF.y = initialY + (int) (event.getRawY() - initialTouchY);
                /* no longer needed since the FLAG_NO_LIMITS was causing this issue */
//                if (paramsF.y > (szWindow.y - floatingImage.getHeight())) {
//                    paramsF.y = (szWindow.y - mParams.height);
//                } else if (paramsF.y < 0) {
//                    paramsF.y = 1;
//                } else if (paramsF.x > (szWindow.x - floatingImage.getWidth())) {
//                    paramsF.x = szWindow.x - floatingImage.getWidth();
//                } else if (paramsF.x < 0) {
//                    paramsF.x = 1;
//                }
                try {
                    windowManager.updateViewLayout(floatingImage, paramsF);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                animateShowing();
                break;
        }
        return false;
    }

    @Override
    public void onAppClick(AppsModel appsModel) {
        try {
            PackageManager manager = context.getPackageManager();
            Intent intent = manager.getLaunchIntentForPackage(appsModel.getPackageName());
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            context.startActivity(intent);
            appsModel.updateEntry(appsModel.getPackageName());
        } catch (Exception e) {// app uninstalled/not found
            e.printStackTrace();
        }
        if (popupWindow != null) {
            if (!AppHelper.isAlwaysShowing(context)) {
                if (popupWindow.isShowing()) {
                    popupWindow.dismiss();
                }
            }
        }
    }

    @Override
    public void onOrientationChanged(int orientation) {
        if (AppHelper.isEdged(context)) {
            windowManager.getDefaultDisplay().getSize(szWindow); // update szWindow to reflect to orientation changes
            moveToEdge();// update windowManager params
        }
    }

    @Override
    public void onReset() {
        adapter.clear();
        EventTrackerHelper.sendEvent("FloatingLayout", "onReset()", "onReset()");
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
        mParams.width = gapSize;
        mParams.height = gapSize;
        if (AppHelper.isSavePositionEnabled(context)) {
            mParams.x = AppHelper.getPositionX(context);
            mParams.y = AppHelper.getPositionY(context);
        } else {
            mParams.x = 0;
            mParams.y = 100;
        }
    }

    private void initView() {
        if (floatingImage == null) floatingImage = new FloatingImage(context, this);
        setupFloatingImage(false);
        floatingImage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureDetector.onTouchEvent(event);
                return FloatingLayout.this.onTouch(v, event);
            }
        });
        windowManager.addView(floatingImage, mParams);
        popUp();
        onMyAppsLoader = new MyPopupAppsLoader(context, this);
        onMyAppsLoader.registerListener(2, onLoadCompleteListener);
        onMyAppsLoader.startLoading();
        animateHidden();
    }

    private void setupFloatingImage(boolean update) {
        if (AppHelper.getImage(context) != null && new File(AppHelper.getImage(context)).exists()) {
            floatingImage.setImageBitmap(BitmapFactory.decodeFile(AppHelper.getImage(context)));
        } else {
            floatingImage.setImageResource(R.mipmap.ic_launcher);
        }
        if (update) {
            if (windowManager != null)
                try {
                    windowManager.updateViewLayout(floatingImage, mParams);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            if (popupWindow != null) {
                if (adapter != null) {
                    popupWindow.setContentWidth(measureContentWidth(adapter));
                    adapter.notifyDataSetChanged();
                }
            }
        }
    }

    private void configure() {
        if (isShowed) {
            if (popupWindow.getListView() != null) {
                popupWindow.getListView().setVerticalScrollBarEnabled(false);
                popupWindow.getListView().setBackgroundColor(context.getResources().getColor(R.color.transparent));
                popupWindow.getListView().setDividerHeight(0);
                popupWindow.getListView().setDivider(new ColorDrawable(context.getResources().getColor(R.color.transparent)));
            }
        }
    }

    private void popUp() {
        if (popupWindow == null) {
            popupWindow = new ListPopupWindow(context, null, R.attr.listPopupWindowStyle);
            popupWindow.setWidth(WRAP_CONTENT);
            popupWindow.setHeight(WRAP_CONTENT);
            Drawable drawable = AppHelper.getColorDrawable(AppHelper.getFABackground(context));
            drawable.setAlpha(AppHelper.getBackgroundAlpha(context));
            popupWindow.setBackgroundDrawable(drawable);
            popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    animateHidden();
                }
            });
            if (AppHelper.isAlwaysShowing(context)) {
                popupWindow.setForceIgnoreOutsideTouch(true);
            } else {
                popupWindow.setModal(true);
            }
            popupWindow.setAdapter(adapter);
            popupWindow.setAnimationStyle(android.R.style.Animation_Dialog);
            popupWindow.setAnchorView(floatingImage);
        }
    }

    public void onDestroy() {
        if (windowManager != null && floatingImage != null) {
            windowManager.removeViewImmediate(floatingImage);
        }
        if (onMyAppsLoader != null) {
            onMyAppsLoader.unregisterListener(onLoadCompleteListener);
            onMyAppsLoader.cancelLoad();
            onMyAppsLoader.stopLoading();
        }
        if (AppController.getController().eventBus().isRegistered(this)) {
            AppController.getController().eventBus().unregister(this);
        }
        EventTrackerHelper.sendEvent("FloatingLayout", "onDestroy()", "onDestroy()");

    }

    private void animateHidden() {
        if (AppHelper.isAutoTransparent(context))
            floatingImage.setImageAlpha(AppHelper.getIconTransparency(context));
    }

    private void animateShowing() {
        floatingImage.setImageAlpha(255);
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
                            windowManager.updateViewLayout(floatingImage, paramsF);
                        } catch (Exception ignored) {}
                    }

                    public void onFinish() {
                        paramsF.x = 0;
                        try {
                            windowManager.updateViewLayout(floatingImage, paramsF);
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
                            windowManager.updateViewLayout(floatingImage, paramsF);
                        } catch (Exception ignored) {}
                    }

                    public void onFinish() {
                        paramsF.x = szWindow.x - floatingImage.getWidth();
                        try {
                            windowManager.updateViewLayout(floatingImage, paramsF);
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

    private double bounceValue(long step, long scale) {
        return scale * Math.exp(-0.055 * step) * Math.cos(0.08 * step);
    }

    private int measureContentWidth(ListAdapter listAdapter) {
        ViewGroup mMeasureParent = null;
        int maxWidth = 0;
        View itemView = null;
        int itemType = 0;
        final ListAdapter adapter = listAdapter;
        int gapSize = AppHelper.getFinalSize(context); // get the width so the icons adjust themselves base on adjustviewbounds.
        final int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(gapSize, View.MeasureSpec.AT_MOST);
        final int heightMeasureSpec = View.MeasureSpec.UNSPECIFIED;
        final int count = adapter.getCount();
        for (int i = 0; i < count; i++) {
            final int positionType = adapter.getItemViewType(i);
            if (positionType != itemType) {
                itemType = positionType;
                itemView = null;
            }
            if (mMeasureParent == null) {
                mMeasureParent = new FrameLayout(context);
            }
            itemView = adapter.getView(i, itemView, mMeasureParent);
            itemView.measure(widthMeasureSpec, heightMeasureSpec);
            final int itemWidth = itemView.getMeasuredWidth();
            if (itemWidth > maxWidth) {
                maxWidth = itemWidth;
            }
        }
        return maxWidth;
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
                popupWindow.setContentWidth(measureContentWidth(adapter));
            }
            EventTrackerHelper.sendEvent("FloatingLayout", "onLoadCompleteListener()", "Data Size", "" + data.size());
        }
    };

    public void onEvent(EventsModel eventsModel) {
        if (eventsModel != null && eventsModel.getEventType() == EventType.SETTINGS_CHANGE) {
            setupParams();
            setupFloatingImage(true);
            animateHidden();
            EventTrackerHelper.sendEvent("FloatingLayout", "onEvent()", "EventType.SETTINGS_CHANGE");
        } else if (eventsModel != null && eventsModel.getEventType() == EventType.PREVIEW) {
            mParams.height = AppHelper.toPx(context, eventsModel.getPreviewSize());
            mParams.width = AppHelper.toPx(context, eventsModel.getPreviewSize());
            try {
                EventTrackerHelper.sendEvent("FloatingLayout", "onEvent()", "EventType.PREVIEW");
                if (windowManager != null) {
                    windowManager.updateViewLayout(floatingImage, mParams);
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
            if (popupWindow != null) {
                Drawable drawable = AppHelper.getColorDrawable(AppHelper.getFABackground(context));
                drawable.setAlpha(AppHelper.getBackgroundAlpha(context));
                popupWindow.setBackgroundDrawable(drawable);
                if (!popupWindow.isShowing()) {
                    popupWindow.show();
                }
            }
        } else if (eventsModel != null && eventsModel.getEventType() == EventType.ICON_ALPHA) {
            animateHidden();
        }
    }
}
