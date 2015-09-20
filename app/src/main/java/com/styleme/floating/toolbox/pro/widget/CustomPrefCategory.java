package com.styleme.floating.toolbox.pro.widget;

import android.content.Context;
import android.preference.PreferenceCategory;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.styleme.floating.toolbox.pro.R;
import com.styleme.floating.toolbox.pro.global.helper.AppHelper;

/**
 * Created by kosh20111 on 5/27/2015. CopyRights @ Innov8tif
 */
public class CustomPrefCategory extends PreferenceCategory {

    public CustomPrefCategory(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setLayoutResource(R.layout.preference_category);
        setSelectable(false);
    }

    public CustomPrefCategory(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomPrefCategory(Context context) {
        this(context, null, 0);
    }

    @Override
    protected void onBindView(@NonNull View view) {
        super.onBindView(view);
        // TODO comeback here
        ((TextView) view.findViewById(android.R.id.title)).setTextColor(AppHelper.getAccentColor(getContext()));
    }
}
