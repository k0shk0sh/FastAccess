package com.fastaccess.ui.modules.settings;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v14.preference.SwitchPreference;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ShareCompat;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceGroupAdapter;
import android.view.View;
import android.widget.Toast;

import com.fastaccess.App;
import com.fastaccess.BuildConfig;
import com.fastaccess.R;
import com.fastaccess.data.dao.events.FloatingEventModel;
import com.fastaccess.helper.ActivityHelper;
import com.fastaccess.helper.AppHelper;
import com.fastaccess.helper.FileHelper;
import com.fastaccess.helper.IconPackHelper;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.helper.Logger;
import com.fastaccess.helper.PrefConstant;
import com.fastaccess.helper.PrefHelper;
import com.fastaccess.ui.modules.about.AboutMeView;
import com.fastaccess.ui.modules.intro.IntroPagerView;
import com.fastaccess.ui.modules.settings.dialogs.CustomIconChooserDialog;
import com.fastaccess.ui.modules.settings.dialogs.IconSizeTransparencyDialog;
import com.fastaccess.ui.modules.whats_new.WhatsNewView;
import com.google.common.io.Files;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.IOException;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Kosh on 15 Oct 2016, 10:49 PM
 */

public class SettingsFragmentView extends PreferenceFragmentCompat implements Preference.OnPreferenceClickListener, SharedPreferences
        .OnSharedPreferenceChangeListener, CustomIconChooserDialog.OnCustomIconChooseCallback, EasyPermissions.PermissionCallbacks {

    private Toast toast;

    private final static String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};

    private void showToast(@StringRes int resId) {
        if (toast != null) toast.cancel();
        toast = Toast.makeText(App.getInstance(), resId, Toast.LENGTH_LONG);//getContext() might be null when onSharedPreferenceChanged, weird
        // behavior
        toast.show();
    }

    @Override public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.fa_settings);
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setDivider(ActivityCompat.getDrawable(getActivity(), R.drawable.list_divider));
        setDividerHeight(1);
        PreferenceGroupAdapter adapter = (PreferenceGroupAdapter) getListView().getAdapter();
        for (int i = 0; i < getListView().getAdapter().getItemCount(); i++) {//lazy global setOnPreferenceClickListener
            Preference preference = adapter.getItem(i);
            if (preference != null && !InputHelper.isEmpty(preference.getKey())) {
                if (preference.getKey().equalsIgnoreCase("version")) {
                    preference.setSummary(BuildConfig.VERSION_NAME);
                } else if (!(preference instanceof SwitchPreference) && !(preference instanceof ListPreference)) {
                    preference.setOnPreferenceClickListener(this);
                }
            }
        }
    }

    @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == IconPackHelper.PICK_ICON) {
                Bitmap bitmap = data.getParcelableExtra("icon");
                if (bitmap == null) {
                    Uri uri = data.getData();
                    if (uri != null) {
                        File file = FileHelper.generateFile("fa_image_icon");
                        try {
                            Files.copy(new File(uri.getPath()), file);
                        } catch (IOException e) {
                            e.printStackTrace();
                            showToast(R.string.error_retrieving_icon);
                            return;
                        }
                        PrefHelper.set(PrefConstant.CUSTOM_ICON, file.getPath());
                        EventBus.getDefault().post(new FloatingEventModel(true, PrefConstant.CUSTOM_ICON));
                    } else {
                        showToast(R.string.error_retrieving_icon);
                    }
                } else {
                    String path = AppHelper.saveBitmap(bitmap);
                    if (path == null) {
                        showToast(R.string.write_sdcard_explanation);
                        return;
                    }
                    PrefHelper.set(PrefConstant.CUSTOM_ICON, path);
                    EventBus.getDefault().post(new FloatingEventModel(true, PrefConstant.CUSTOM_ICON));
                    if (!bitmap.isRecycled()) {
                        bitmap.recycle();
                    }
                }
            } else if (requestCode == ActivityHelper.SELECT_PHOTO_REQUEST) {
                CropImage.activity(data.getData())
                        .setCropShape(CropImageView.CropShape.OVAL)
                        .setOutputUri(Uri.fromFile(FileHelper.generateFile("fa_image_icon")))
                        .setAspectRatio(5, 5)
                        .setFixAspectRatio(true)
                        .setOutputCompressFormat(Bitmap.CompressFormat.PNG)
                        .start(getContext(), this);
            } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                Uri resultUri = result.getUri();
                PrefHelper.set(PrefConstant.CUSTOM_ICON, new File(resultUri.getPath()).getPath());
                EventBus.getDefault().post(new FloatingEventModel(true, PrefConstant.CUSTOM_ICON));
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            if (requestCode == IconPackHelper.PICK_ICON) {
                if (data != null) {
                    boolean defaultIcon = data.getBooleanExtra("default", false);
                    if (defaultIcon) {
                        PrefHelper.set(PrefConstant.CUSTOM_ICON, "");
                        EventBus.getDefault().post(new FloatingEventModel(true, PrefConstant.CUSTOM_ICON));
                    }
                }
            }
        }

    }

    @Override public boolean onPreferenceClick(Preference preference) {
        switch (preference.getKey()) {
            case "fa_background_alpha":
                IconSizeTransparencyDialog.newInstance(true).show(getChildFragmentManager(), "IconSizeTransparencyDialog");
                return true;
            case "icon_pack":
                IconPackHelper.pickIconPack(this, false);
                return true;
            case "custom_icon":
                new CustomIconChooserDialog().show(getChildFragmentManager(), "CustomIconChooserDialog");
                return true;
            case "icon_alpha":
                IconSizeTransparencyDialog.newInstance(false).show(getChildFragmentManager(), "IconSizeTransparencyDialog");
                return true;
            case "manual_size":
                IconSizeTransparencyDialog.newInstance(false, true).show(getChildFragmentManager(), "IconSizeTransparencyDialog");
                return true;
            case "version":
                return true;
            case "sourceCode":
                ActivityHelper.startCustomTab(getActivity());
                return true;
            case "libraries":
                ActivityHelper.startLibs(getActivity());
                return true;
            case "about_me":
                startActivity(new Intent(getContext(), AboutMeView.class));
                return true;
            case "email_us":
                ShareCompat.IntentBuilder.from(getActivity())
                        .setType("message/rfc822")
                        .setEmailTo(new String[]{"fastaccessapps@gmail.com"})
                        .setSubject(getString(R.string.email_subject))
                        .setText(AppHelper.getEmailBody())
                        .setChooserTitle(getString(R.string.choose_email))
                        .startChooser();
                return true;
            case "whats_new":
                startActivity(new Intent(getContext(), WhatsNewView.class));
                return true;
            case "intro":
                startActivity(new Intent(getContext(), IntroPagerView.class));
                return true;
        }
        return false;
    }

    @Override public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key == null) return;
        if (key.equalsIgnoreCase(PrefConstant.STATUS_BAR_HIDDEN) || key.equalsIgnoreCase(PrefConstant.FA_IS_HORIZONTAL)) {
            showToast(R.string.required_restart);
        } else if (key.equalsIgnoreCase(PrefConstant.ICON_SIZE)) {
            sharedPreferences.edit().putInt(PrefConstant.MANUAL_SIZE, 0).apply();
        }
        EventBus.getDefault().post(new FloatingEventModel(true, key));
    }

    @Override public void onResume() {
        super.onResume();
        PreferenceManager.getDefaultSharedPreferences(getContext()).registerOnSharedPreferenceChangeListener(this);
    }

    @Override public void onPause() {
        super.onPause();
        PreferenceManager.getDefaultSharedPreferences(getContext()).registerOnSharedPreferenceChangeListener(this);
    }

    @Override public void onUserChoose(boolean isFromGallery) {
        if (!isFromGallery) {
            pickIcon();
        } else {
            pickImage();
        }
    }

    @Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override public void onPermissionsGranted(int requestCode, List<String> perms) {
        Logger.e();
    }

    @Override public void onPermissionsDenied(int requestCode, List<String> perms) {
        Logger.e();
    }

    @AfterPermissionGranted(IconPackHelper.PICK_ICON) private void pickIcon() {
        if (EasyPermissions.hasPermissions(getContext(), permissions)) {
            IconPackHelper.pickIconPack(this, true);
        } else {
            EasyPermissions.requestPermissions(this, getString(R.string.write_sdcard_explanation),
                    IconPackHelper.PICK_ICON, permissions);
        }
    }

    @AfterPermissionGranted(ActivityHelper.SELECT_PHOTO_REQUEST) private void pickImage() {
        if (EasyPermissions.hasPermissions(getContext(), permissions)) {
            ActivityHelper.startGalleryIntent(this);
        } else {
            EasyPermissions.requestPermissions(this, getString(R.string.write_sdcard_explanation),
                    ActivityHelper.SELECT_PHOTO_REQUEST, permissions);
        }
    }

}
