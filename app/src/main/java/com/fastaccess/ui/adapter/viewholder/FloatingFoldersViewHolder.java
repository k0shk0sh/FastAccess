package com.fastaccess.ui.adapter.viewholder;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.fastaccess.R;
import com.fastaccess.data.dao.FolderModel;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.helper.PrefConstant;
import com.fastaccess.ui.widgets.recyclerview.BaseRecyclerAdapter;
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder;

import butterknife.BindView;

/**
 * Created by Kosh on 11 Oct 2016, 7:47 PM
 */

public class FloatingFoldersViewHolder extends BaseViewHolder<FolderModel> {

    @BindView(R.id.imageIcon) ImageView imageIcon;
    @BindView(R.id.iconHolder) RelativeLayout iconHolder;
    private boolean isHorizontal;

    public static FloatingFoldersViewHolder newInstance(@NonNull ViewGroup parent, @NonNull BaseRecyclerAdapter adapter) {
        return new FloatingFoldersViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.floating_apps_row_item, parent, false),
                adapter);
    }

    public FloatingFoldersViewHolder(@NonNull View itemView, @Nullable BaseRecyclerAdapter adapter) {
        super(itemView, adapter);
        iconHolder.setOnClickListener(null);
        iconHolder.setOnLongClickListener(null);
        imageIcon.setOnClickListener(this);
        imageIcon.setOnLongClickListener(this);
    }

    public void bind(@NonNull FolderModel model, boolean isHorizontal) {
        this.isHorizontal = isHorizontal;
        bind((model));
    }

    @Override public void bind(@NonNull FolderModel folderModel) {
        TextDrawable.IBuilder builder = TextDrawable.builder()
                .beginConfig()
                .endConfig()
                .round();
        String letter = InputHelper.getTwoLetters(folderModel.getFolderName());
        int color = folderModel.getColor() == 0 ? ColorGenerator.MATERIAL.getRandomColor() : folderModel.getColor();
        imageIcon.setImageDrawable(builder.build(letter.toUpperCase(), color));
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) imageIcon.getLayoutParams();
        int gap = PrefConstant.getGapSize(imageIcon.getResources());
        if (!isHorizontal) params.setMargins(0, 0, 0, gap);
        else params.setMargins(0, 0, gap, 0);
    }
}
