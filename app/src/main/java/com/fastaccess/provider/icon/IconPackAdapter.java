package com.fastaccess.provider.icon;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.fastaccess.R;
import com.fastaccess.helper.PrefConstant;
import com.fastaccess.helper.PrefHelper;
import com.fastaccess.provider.icon.model.IconPackInfo;
import com.fastaccess.ui.widgets.FontRadioButton;
import com.fastaccess.ui.widgets.FontTextView;
import com.fastaccess.ui.widgets.ForegroundImageView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Kosh on 13/12/15 1:21 AM
 */
public class IconPackAdapter extends BaseAdapter {

    private ArrayList<IconPackInfo> mSupportedPackages;
    private String mCurrentIconPack;
    private int mCurrentIconPackPosition = -1;
    private boolean isPickIcon;

    public IconPackAdapter(Context ctx, Map<String, IconPackInfo> supportedPackages, boolean isPickIcon) {
        mSupportedPackages = new ArrayList<>(supportedPackages.values());
        this.isPickIcon = isPickIcon;
        Collections.sort(mSupportedPackages, new Comparator<IconPackInfo>() {
            @Override public int compare(IconPackInfo lhs, IconPackInfo rhs) {
                return lhs.label.toString().compareToIgnoreCase(rhs.label.toString());
            }
        });
        String defaultLabel = ctx.getString(R.string.default_theme);
        Drawable icon = ContextCompat.getDrawable(ctx, R.mipmap.ic_launcher);
        mSupportedPackages.add(0, new IconPackInfo(defaultLabel, icon, ""));
        mCurrentIconPack = PrefHelper.getString(PrefConstant.ICON_PACK);
    }

    @Override public int getCount() {
        return mSupportedPackages.size();
    }

    @Override public String getItem(int position) {
        return mSupportedPackages.get(position).packageName;
    }

    @Override public long getItemId(int position) {
        return 0;
    }

    public boolean isCurrentIconPack(int position) {
        return mCurrentIconPackPosition == position;
    }

    @Override public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder viewHolder;
        if (view == null) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.icon_pack_layout, parent, false);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        IconPackInfo info = mSupportedPackages.get(position);
        viewHolder.title.setText(info.label);
        viewHolder.icon.setImageDrawable(info.icon);
        if (!isPickIcon) {
            boolean isCurrentIconPack = info.packageName.equals(mCurrentIconPack);
            viewHolder.radio.setChecked(isCurrentIconPack);
            if (isCurrentIconPack) {
                mCurrentIconPackPosition = position;
            }
        } else {
            viewHolder.radio.setVisibility(View.GONE);
        }
        return view;
    }

    static class ViewHolder {
        @BindView(R.id.icon) ForegroundImageView icon;
        @BindView(R.id.title) FontTextView title;
        @BindView(R.id.radio) FontRadioButton radio;

        ViewHolder(View view) {ButterKnife.bind(this, view);}
    }
}
