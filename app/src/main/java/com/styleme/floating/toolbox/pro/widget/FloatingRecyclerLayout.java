package com.styleme.floating.toolbox.pro.widget;

import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.CountDownTimer;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

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
import static android.view.WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
import static android.view.WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
import static android.view.WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
import static android.view.WindowManager.LayoutParams.TYPE_PHONE;

/**
 * Created by Kosh on 9/4/2015. copyrights are reserved
 * experiment
 * usage( init it instead of {@link FloatingLayout} in {@link FloatingService}
 */
public class FloatingRecyclerLayout implements OnFloatingTouchListener, OnItemClickListener {

    private WindowManager.LayoutParams mParams;
    private WindowManager windowManager;
    private Context context;
    private WindowManager.LayoutParams paramsF;
    private int initialX;
    private int initialY;
    private float initialTouchX;
    private float initialTouchY;
    private GestureDetector gestureDetector;
    private Point szWindow = new Point();
    private ImageView floatingImage;
    private RecyclerFloatingAdapter myAppsAdapter;
    private WindowManager.LayoutParams inflatorParams;
    private View v;
    private MyPopupAppsLoader onMyAppsLoader;

    public FloatingRecyclerLayout(Context context) {
        this.context = context;
        if (!AppController.getController().eventBus().isRegistered(this)) {
            AppController.getController().eventBus().register(this);
        }
        if (myAppsAdapter == null) myAppsAdapter = new RecyclerFloatingAdapter(this, new ArrayList<AppsModel>());
        gestureDetector = new GestureDetector(context, new GestureListener(this));
        initWindows();
    }

    private void initWindows() {
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getSize(szWindow);
        mParams = new WindowManager.LayoutParams(TYPE_PHONE, FLAG_NOT_FOCUSABLE | FLAG_LAYOUT_NO_LIMITS | FLAG_NOT_TOUCH_MODAL, PixelFormat
                .TRANSLUCENT);
        setupParams();
        inflatorParams = new WindowManager.LayoutParams(TYPE_PHONE, FLAG_NOT_FOCUSABLE | FLAG_LAYOUT_NO_LIMITS | FLAG_NOT_TOUCH_MODAL, PixelFormat
                .TRANSLUCENT);
        mParams.gravity = Gravity.LEFT | Gravity.TOP;
        initView();
    }

    private void setupParams() {
        String size = AppHelper.getIconSize(context);
        int gapSize = WRAP_CONTENT;
        if (size.equalsIgnoreCase("small")) {
            gapSize = context.getResources().getDimensionPixelSize(R.dimen.fa_size_small);
        } else if (size.equalsIgnoreCase("medium")) {
            gapSize = context.getResources().getDimensionPixelSize(R.dimen.fa_size_medium);
        } else if (size.equalsIgnoreCase("large")) {
            gapSize = context.getResources().getDimensionPixelSize(R.dimen.fa_size_large);
        }
        mParams.width = WRAP_CONTENT;
        mParams.height = WRAP_CONTENT;
        if (AppHelper.isSavePositionEnabled(context)) {
            mParams.x = AppHelper.getPositionX(context);
            mParams.y = AppHelper.getPositionY(context);
        } else {
            mParams.x = 0;
            mParams.y = 100;
        }
    }

    private void initView() {
        floatingImage = new ImageView(context);
        floatingImage.setAdjustViewBounds(true);
        setupFloatingImage(false);
        floatingImage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureDetector.onTouchEvent(event);
                return FloatingRecyclerLayout.this.onTouch(v, event);
            }
        });
        v = LayoutInflater.from(context).inflate(R.layout.floating_layout_list, null);
        v.setVisibility(View.GONE);
        RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.recycler);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(myAppsAdapter);
        inflatorParams = mParams;
        windowManager.addView(v, inflatorParams);
        windowManager.addView(floatingImage, mParams);
        onMyAppsLoader = new MyPopupAppsLoader(context, this);
        onMyAppsLoader.registerListener(3, onLoadCompleteListener);
        onMyAppsLoader.startLoading();
    }

    private void setupFloatingImage(boolean update) {
        if (AppHelper.getImage(context) != null && new File(AppHelper.getImage(context)).exists()) {
            floatingImage.setImageBitmap(BitmapFactory.decodeFile(AppHelper.getImage(context)));
        } else {
            floatingImage.setImageResource(R.mipmap.ic_launcher);
        }
        if (update) {
            if (windowManager != null)
                windowManager.updateViewLayout(floatingImage, mParams);
        }
    }

    public void onDestroy() {
        if (windowManager != null && floatingImage != null) {
            windowManager.removeViewImmediate(floatingImage);
        }
        if (windowManager != null && v != null) {
            if (v.isShown() || v.isDirty()) {
                windowManager.removeViewImmediate(v);
            }
        }
        if (onMyAppsLoader != null) {
            onMyAppsLoader.unregisterListener(onLoadCompleteListener);
            onMyAppsLoader.cancelLoad();
            onMyAppsLoader.stopLoading();
        }
        if (AppController.getController().eventBus().isRegistered(this)) {
            AppController.getController().eventBus().unregister(this);
        }
    }

    @Override
    public void onLongClick() {
        if (new AppsModel().getAll() != null)
            Notifier.createNotification(context, new AppsModel().getAll().size());
        else
            Notifier.createNotification(context, 0);
        if (v.isShown()) v.setVisibility(View.GONE);
    }

    @Override
    public void onDoubleClick() {
        if (context != null) {
            Intent startMain = new Intent(Intent.ACTION_MAIN);
            startMain.addCategory(Intent.CATEGORY_HOME);
            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(startMain);
        }
        if (v.isShown()) v.setVisibility(View.GONE);
    }

    @Override
    public void onClick() {
        if (!v.isShown()) {
            LinearLayout.LayoutParams param = (LinearLayout.LayoutParams) ((LinearLayout) v).getChildAt(0).getLayoutParams();
            param.topMargin = floatingImage.getHeight();
            v.setVisibility(View.VISIBLE);
        } else {
            v.setVisibility(View.GONE);
        }
        animateShowing();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        paramsF = mParams;
        inflatorParams = paramsF;
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
                if (paramsF.y > (szWindow.y - floatingImage.getHeight())) {
                    paramsF.y = (szWindow.y - mParams.height);
                } else if (paramsF.y < 0) {
                    paramsF.y = 1;
                } else if (paramsF.x > (szWindow.x - floatingImage.getWidth())) {
                    paramsF.x = szWindow.x - floatingImage.getWidth();
                } else if (paramsF.x < 0) {
                    paramsF.x = 1;
                }
                windowManager.updateViewLayout(floatingImage, paramsF);
                inflatorParams = paramsF;
                windowManager.updateViewLayout(FloatingRecyclerLayout.this.v, paramsF);
                animateShowing();
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
                        paramsF.x = (int) (double) bounceValue(step, x);
                        windowManager.updateViewLayout(floatingImage, paramsF);
                        windowManager.updateViewLayout(v, paramsF);
                    }

                    public void onFinish() {
                        paramsF.x = 0;
                        inflatorParams = paramsF;
                        windowManager.updateViewLayout(floatingImage, paramsF);
                        windowManager.updateViewLayout(v, paramsF);
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
                        paramsF.x = szWindow.x + (int) (double) bounceValue(step, x) - floatingImage.getWidth();
                        windowManager.updateViewLayout(floatingImage, paramsF);
                        windowManager.updateViewLayout(v, paramsF);
                    }

                    public void onFinish() {
                        paramsF.x = szWindow.x - floatingImage.getWidth();
                        inflatorParams = paramsF;
                        windowManager.updateViewLayout(floatingImage, paramsF);
                        windowManager.updateViewLayout(v, paramsF);
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onReset() {
        if (myAppsAdapter != null) myAppsAdapter.clearAll();
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

    @Override
    public void onItemClickListener(View view, int position) {
        try {
            PackageManager manager = context.getPackageManager();
            Intent intent = manager.getLaunchIntentForPackage(myAppsAdapter.getModelList().get(position).getPackageName());
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (v.isShown()) v.setVisibility(View.GONE);
    }

    @Override
    public void onItemLongClickListener(RecyclerView.ViewHolder viewHolder, View view, int position) {

    }

    private Loader.OnLoadCompleteListener<List<AppsModel>> onLoadCompleteListener = new Loader.OnLoadCompleteListener<List<AppsModel>>() {
        @Override
        public void onLoadComplete(Loader<List<AppsModel>> loader, List<AppsModel> data) {
            if (data == null || data.size() == 0) {
                context.stopService(new Intent(context, FloatingService.class));
                return;
            }
            if (myAppsAdapter != null) myAppsAdapter.insert(data);
        }
    };

    public void onEvent(EventsModel eventsModel) {
        if (eventsModel != null && eventsModel.getEventType() == EventType.SETTINGS_CHANGE) {
            setupParams();
            setupFloatingImage(true);
            animateHidden();
        }
    }

}
