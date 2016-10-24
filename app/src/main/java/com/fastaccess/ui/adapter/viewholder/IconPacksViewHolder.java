package com.fastaccess.ui.adapter.viewholder;

import android.view.View;

import com.fastaccess.R;
import com.fastaccess.ui.widgets.FontRadioButton;
import com.fastaccess.ui.widgets.FontTextView;
import com.fastaccess.ui.widgets.ForegroundImageView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class IconPacksViewHolder {
    @BindView(R.id.icon) public ForegroundImageView icon;
    @BindView(R.id.title) public FontTextView title;
    @BindView(R.id.radio) public FontRadioButton radio;

    public IconPacksViewHolder(View view) {ButterKnife.bind(this, view);}
}
