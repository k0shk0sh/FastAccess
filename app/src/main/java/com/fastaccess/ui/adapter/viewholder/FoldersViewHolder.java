package com.fastaccess.ui.adapter.viewholder;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.fastaccess.R;
import com.fastaccess.data.dao.FolderModel;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.helper.ViewHelper;
import com.fastaccess.ui.widgets.FontTextView;
import com.fastaccess.ui.widgets.recyclerview.BaseRecyclerAdapter;
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder;

import butterknife.BindView;

/**
 * Created by Kosh on 11 Oct 2016, 7:47 PM
 */

public class FoldersViewHolder extends BaseViewHolder<FolderModel> implements ViewHelper.OnTooltipDismissListener {

    @BindView(R.id.folderImage) ImageView folderImage;
    @BindView(R.id.folderName) FontTextView folderName;
    @BindView(R.id.appsCount) FontTextView appsCount;
    @BindView(R.id.addApps) View addApps;
    @BindView(R.id.delete) View delete;
    @BindView(R.id.editFolder) View editFolder;

    public static FoldersViewHolder newInstance(@NonNull ViewGroup parent, @NonNull BaseRecyclerAdapter adapter) {
        return new FoldersViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.folder_row_item, parent, false), adapter);
    }

    public FoldersViewHolder(@NonNull View itemView, @Nullable BaseRecyclerAdapter adapter) {
        super(itemView, adapter);
        folderImage.setOnClickListener(this);
        folderImage.setOnLongClickListener(this);
        addApps.setOnClickListener(this);
        addApps.setOnLongClickListener(this);
        editFolder.setOnClickListener(this);
        editFolder.setOnLongClickListener(this);
        delete.setOnClickListener(this);
        delete.setOnLongClickListener(this);
    }

    @Override public void bind(@NonNull FolderModel folderModel) {
        folderName.setText(folderModel.getFolderName());
        appsCount.setText(String.valueOf(folderModel.getAppsCount()));
        folderImage.setContentDescription(folderModel.getFolderName());
        TextDrawable.IBuilder builder = TextDrawable.builder()
                .beginConfig()
                .endConfig()
                .round();
        String letter = InputHelper.getTwoLetters(folderModel.getFolderName());
        int color = folderModel.getColor() == 0 ? ColorGenerator.MATERIAL.getRandomColor() : folderModel.getColor();
        folderImage.setImageDrawable(builder.build(letter.toUpperCase(), color));
        if (getAdapterPosition() == 0) {
            ViewHelper.showTooltip(delete, R.string.delete_folder_hint, this);
        }
    }

    @Override public void onDismissed(@StringRes int resId) {
        if (resId == R.string.delete_folder_hint) {
            ViewHelper.showTooltip(addApps, R.string.add_folder_apps_hint, this);
        } else {
            ViewHelper.showTooltip(editFolder, R.string.edit_folder_hint);
        }
    }
}
