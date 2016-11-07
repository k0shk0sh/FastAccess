package com.fastaccess.ui.modules.apps.folders.create;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.fastaccess.R;
import com.fastaccess.data.dao.FolderModel;
import com.fastaccess.helper.Bundler;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.helper.ViewHelper;
import com.fastaccess.ui.base.BaseBottomSheetDialog;
import com.fastaccess.ui.modules.apps.folders.create.CreateFolderMvp.OnNotifyFoldersAdapter;
import com.fastaccess.ui.widgets.FontButton;

import org.xdty.preference.colorpicker.ColorPickerDialog;

import java.util.Date;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import icepick.State;

/**
 * Created by Kosh on 11 Oct 2016, 8:27 PM
 */

public class CreateFolderView extends BaseBottomSheetDialog implements CreateFolderMvp.View {
    private long folderId;

    @State int selectedColor = Color.parseColor("#FF2A456B");
    @State String fName;

    @BindView(R.id.folderImage) ImageView folderImage;
    @BindView(R.id.folderName) TextInputLayout folderName;
    @BindView(R.id.cancel) FontButton cancel;
    @BindView(R.id.save) FontButton save;
    @BindView(R.id.folderNameEditText) EditText folderNameEditText;
    private FolderModel folderModel;
    private OnNotifyFoldersAdapter callback;

    private CreateFolderPresenter presenter;

    public static CreateFolderView newInstance(long folderId) {
        CreateFolderView view = new CreateFolderView();
        view.setArguments(Bundler.start().put("folderId", folderId).end());
        return view;
    }

    @OnClick(R.id.cancel) void onCancel() {
        dismiss();
    }

    @OnClick(R.id.save) void onSave() {
        boolean isEmpty = InputHelper.isEmpty(folderName);
        folderName.setError(isEmpty ? getString(R.string.required_field) : null);
        if (!isEmpty) {
            FolderModel draft = FolderModel.getFolder(fName);
            if (draft != null) {
                draft.setColor(selectedColor);
                draft.setFolderName(fName);
                draft.save();
            } else {
                getFolderModel().setColor(selectedColor);
                getFolderModel().setFolderName(fName);
                getFolderModel().setCreatedDate(new Date().getTime());
                getFolderModel().save();
            }
            callback.onNotifyChanges();
            dismiss();
        }
    }

    @OnClick(R.id.folderImage) void onChooseColor() {
        ColorPickerDialog.newInstance(R.string.color_picker_default_title,
                getResources().getIntArray(R.array.colors_primary),
                selectedColor != 0 ? selectedColor : ContextCompat.getColor(getContext(), R.color.primary),
                (getResources().getInteger(R.integer.num_columns) + 1), ColorPickerDialog.SIZE_SMALL)
                .show(getChildFragmentManager(), "ColorPickerDialog");
    }

    @OnTextChanged(value = R.id.folderNameEditText, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED) void onTextChanged(Editable editable) {
        boolean isEmpty = InputHelper.isEmpty(editable);
        if (!isEmpty) {
            setupDrawable(editable.toString());
        }
        folderName.setError(isEmpty ? getString(R.string.required_field) : null);
        fName = InputHelper.toString(editable);
    }

    @Override public void onAttach(Context context) {
        super.onAttach(context);
        if (getPresenter() != null && getParentFragment() instanceof OnNotifyFoldersAdapter) {
            callback = (OnNotifyFoldersAdapter) getParentFragment();
        } else if (context instanceof OnNotifyFoldersAdapter) {
            callback = (OnNotifyFoldersAdapter) context;
        } else {
            throw new RuntimeException("Activity/Fragment must implement OnNotifyFoldersAdapter");
        }
    }

    @Override public void onDetach() {
        super.onDetach();
    }

    @Override protected int layoutRes() {
        return R.layout.create_edit_folder;
    }

    @Override public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        folderId = getArguments().getLong("folderId", -1);
        if (savedInstanceState == null) {
            selectedColor = getFolderModel().getColor() != 0 ? getFolderModel().getColor() : selectedColor;
            fName = getFolderModel().getFolderName();
        }
    }

    @Override protected void onViewCreated(@NonNull View view) {
        folderNameEditText.setText(fName);
        setupDrawable(fName);
        ViewHelper.showTooltip(folderImage, R.string.color_picker_hint_folder, Gravity.TOP, null);
    }

    @Override public void onColorSelected(int color) {
        selectedColor = color;
        setupDrawable(InputHelper.toString(folderNameEditText));
    }

    @NonNull private FolderModel getFolderModel() {
        if (folderModel == null) {
            folderModel = FolderModel.findById(FolderModel.class, folderId);
            if (folderModel == null) {
                folderModel = new FolderModel();
            }
        }
        return folderModel;
    }

    private CreateFolderPresenter getPresenter() {
        if (presenter == null) presenter = CreateFolderPresenter.with(this);
        return presenter;
    }

    private void setupDrawable(@NonNull String upDrawable) {
        TextDrawable.IBuilder builder = TextDrawable.builder()
                .beginConfig()
                .endConfig()
                .round();
        String letter = InputHelper.isEmpty(upDrawable) ? "N/A" : InputHelper.getTwoLetters(upDrawable);
        folderImage.setImageDrawable(builder.build(letter.toUpperCase(), selectedColor));
    }
}
