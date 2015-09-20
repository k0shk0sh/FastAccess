package com.styleme.floating.toolbox.pro.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.styleme.floating.toolbox.pro.AppController;
import com.styleme.floating.toolbox.pro.BuildConfig;
import com.styleme.floating.toolbox.pro.R;
import com.styleme.floating.toolbox.pro.global.helper.AppHelper;
import com.styleme.floating.toolbox.pro.global.helper.IconPackHelper;
import com.styleme.floating.toolbox.pro.global.model.EventType;
import com.styleme.floating.toolbox.pro.global.model.EventsModel;
import com.styleme.floating.toolbox.pro.global.tasks.BackupTask;
import com.styleme.floating.toolbox.pro.global.tasks.RestoreTask;
import com.styleme.floating.toolbox.pro.widget.colorpicker.dashclockpicker.ColorPreference;
import com.styleme.floating.toolbox.pro.widget.colorpicker.dashclockpicker.ColorSelector;

/**
 * Created by Kosh on 8/22/2015. copyrights are reserved
 */
public class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener, ColorSelector,
        SharedPreferences.OnSharedPreferenceChangeListener {

    public static final int REQUEST_STORAGE = 1;
    private CheckBoxPreference customImage;
    private Preference customIcon;
    private int PICK_IMAGE = 2000;
    public static int PICK_ICON = 2001;
    private static String[] EXTERNAL_STORAGE = {Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.general_settings);
        findPreference("version").setSummary(BuildConfig.VERSION_NAME);
        getPreferenceManager().findPreference("dark_theme").setOnPreferenceClickListener(this);
        ColorPreference primary = (ColorPreference) getPreferenceManager().findPreference("primary_color");
        ColorPreference accent = (ColorPreference) getPreferenceManager().findPreference("accent_color");
        getPreferenceScreen().findPreference("icon_pack").setOnPreferenceClickListener(this);
        getPreferenceScreen().findPreference("backup").setOnPreferenceClickListener(this);
        getPreferenceScreen().findPreference("restore").setOnPreferenceClickListener(this);
        if (AppHelper.getBackupFile().exists()) {
            getPreferenceScreen().findPreference("restore").setEnabled(true);
        } else {
            getPreferenceScreen().findPreference("restore").setSummary(getString(R.string.restore_summery) + " " + getString(R.string
                    .no_backup_found));
        }
        getPreferenceScreen().findPreference("manualSize").setOnPreferenceClickListener(this);
        customImage = (CheckBoxPreference) getPreferenceManager().findPreference("customImage");
        customImage.setOnPreferenceClickListener(this);
        customIcon = getPreferenceManager().findPreference("customIcon");
        customIcon.setOnPreferenceClickListener(this);
        primary.onColorSelect(this);
        accent.onColorSelect(this);
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        String saveKey = preference.getKey();
        if (saveKey.equalsIgnoreCase("dark_theme")) {
            post();
            getActivity().recreate();
            return true;
        } else if (preference.getKey().equals(customImage.getKey())) {
            if (customImage.isChecked()) {
                pickImage();
            } else {
                AppHelper.clearImage(getActivity());
                postToService();
            }
            return true;
        } else if (preference.getKey().equals(customIcon.getKey())) {
            pickIcon();
            return true;
        } else if (preference.getKey().equals("icon_pack")) {
            pickPackage();
            return true;
        } else if (preference.getKey().equalsIgnoreCase("backup")) {
            doBackup();
            return true;
        } else if (preference.getKey().equalsIgnoreCase("restore")) {
            doRestore();
            return true;
        } else if (preference.getKey().equalsIgnoreCase("manualSize")) {
            if (((CheckBoxPreference) getPreferenceScreen().findPreference("manualSize")).isChecked()) {
                IconSizeFragment iconSizeFragment = new IconSizeFragment();
                iconSizeFragment.setCancelable(false);
                iconSizeFragment.show(((AppCompatActivity) getActivity()).getSupportFragmentManager(), "FAICON");
            }
            return true;
        }
        return false;
    }

    private void doRestore() {
        if (!doNeedPermission()) {
            new RestoreTask(this).execute();
        }
    }

    private void doBackup() {
        if (!doNeedPermission()) {
            new BackupTask(getActivity()).execute();
        }
    }

    private void pickImage() {
        if (!doNeedPermission()) {
            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, getString(R.string.select_image)), PICK_IMAGE);
        }
    }

    private void pickIcon() {
        IconPackHelper.pickIconPack(this, true);
    }

    private void pickPackage() {
        IconPackHelper.pickIconPack(this, false);
    }

    private boolean doNeedPermission() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermission();
            return true;
        }
        return false;
    }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
                || ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Snackbar.make(getView() != null ? getView() : getActivity().findViewById(R.id.toolbar), "Permission is required to continue using this " +
                    "function!", Snackbar.LENGTH_INDEFINITE).setAction("Okay", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ActivityCompat.requestPermissions(getActivity(), EXTERNAL_STORAGE, REQUEST_STORAGE);
                }
            }).show();
        } else {
            ActivityCompat.requestPermissions(getActivity(), EXTERNAL_STORAGE, REQUEST_STORAGE);
        }
    }

    @Override
    public void onColorSelected(int color) {
        post();
        getActivity().recreate();
    }

    public void post() {
        EventsModel eventsModel = new EventsModel();
        eventsModel.setEventType(EventType.THEME);
        AppController.getController().eventBus().post(eventsModel);
    }

    public void postToService() {
        EventsModel eventsModel = new EventsModel();
        eventsModel.setEventType(EventType.SETTINGS_CHANGE);
        AppController.getController().eventBus().post(eventsModel);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            customImage.setDefaultValue(false);
            customImage.setChecked(false);
        }
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PICK_IMAGE) {
                Uri uri = data.getData();
                try {
                    BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
                    bmpFactoryOptions.inScaled = false;
                    bmpFactoryOptions.inDither = false;
                    bmpFactoryOptions.inSampleSize = 5;
                    Bitmap bmp = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(uri), null, bmpFactoryOptions);
                    if (bmp != null) {
                        Bitmap resizedbitmap = Bitmap.createScaledBitmap(bmp, 200, 200, true);
                        AppHelper.setImage(getActivity(), AppHelper.saveBitmap(resizedbitmap));
                        if (!resizedbitmap.isRecycled()) {
                            resizedbitmap.recycle();
                        }
                        if (!bmp.isRecycled()) {
                            bmp.recycle();
                        }
                        customIcon.setDefaultValue(false);
                        postToService();
                    } else {
                        Snackbar.make(getActivity().findViewById(R.id.toolbar), R.string.error_bitmap, Snackbar.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    customImage.setChecked(false);
                }
            } else if (requestCode == PICK_ICON) {
                Bitmap bitmap = data.getParcelableExtra("icon");
                if (bitmap != null) {
                    AppHelper.setImage(getActivity(), AppHelper.saveBitmap(bitmap));
                    customImage.setDefaultValue(false);
                    customImage.setChecked(false);
                    if (!bitmap.isRecycled()) {
                        bitmap.recycle();
                    }
                    postToService();
                } else {
                    Toast.makeText(getActivity(), "Can not get the icon from the icon-pack.", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

    }

    @Override
    public void onPause() {
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equalsIgnoreCase("size") || key.equalsIgnoreCase("gap") || key.equalsIgnoreCase("autoTrans")) {
            postToService();
        }
    }

}
